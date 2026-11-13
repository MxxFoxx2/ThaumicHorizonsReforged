# Thaumic Horizons: Reforged

Порт Thaumic Horizons (GTNH-форк, MC 1.7.10, Kentington) на современный Minecraft поверх `thaumcraft-4-reforged` (TC4R). Логика, числа, рецепты и ассеты оригинала сохраняются 1:1, кроме явно задокументированных отступлений. Первое намеренное отступление — фикс бага невидимости инфузии игрока (см. ниже), требование сообщества.

TC4R — родственный проект рядом (`../thaumcraft-4-reforged`). Его `CLAUDE.md` обязателен при работе в его репозитории; здесь он источник конвенций, не инструкция.

## Принципы

- Источник истины по поведению: исходники 1.7.10 в `reference/1.7.10/` (GTNH-форк). Логику сверять с ним, не выдумывать.
- Баги оригинала чиним только если они ломают игру (дюп, потеря предметов, NPE, рассинхрон, краш). Каждое отступление от 1:1 — запись в разделе «Намеренные отступления».
- Сервер проверяет всё сам; клиентские классы на сервер не протекают.
- GameTest-ы не пишутся. Логику проверяет пользователь в `runClient` и `runServer`.

## Две ортогональные оси

- Ось «версия MC»: шов `platform/` и `client/platform/` (сеть, StackKey-данные, attachments, рендер-типы, телепорт). Игровая логика не импортирует версиезависимые классы напрямую, `tools/check_seams.py` падает на запрещённых импортах.
- Ось «порт Thaumcraft»: `thaumichorizons.compat/` — интерфейсы (`AspectCompat`, `ResearchCompat`, `WarpCompat`, `EssentiaCompat`, `NodeCompat`, `VisNetCompat`, `GolemCompat`, `InfusionCompat`, `FxCompat`, `ScanCompat`, `PotionCompat`, `ItemCompat`). Игровая логика ходит только через них. Реализация `TcReforgedCompat` выбрана через ServiceLoader (`META-INF/services`).
- Каждый compat-интерфейс имеет `isAvailable()`; контент отсутствующей подсистемы не регистрируется и скрывается, ядро не крашится. Ссылки на предметы TC — только `ItemCompat.lookup(...)`, никаких прямых референсов на классы TC4R из игровой логики.

## Mixin-хуки TC4R

Пока хуки не приняты в TC4R, TH-R несёт их mixin'ами (`thaumichorizons.mixins.json`, `META-INF/accesstransformer.cfg`). Правила:

- Mixin'ы живут только в `thaumichorizons.compat.tc/` и только как реализация `TcReforgedCompat`. Раскидывать цели TC4R по игровому коду запрещено.
- Версия TC4R пинуется range'ом в `neoforge.mods.toml`; при обновлении TC4R mixin'ы проверяются первыми.
- Сигнатуры mixin-хуков совпадают с будущим нативным API TC4R. Когда хук принят upstream — mixin удаляется, `TcReforgedCompat` переключается на нативный вызов, интерфейсы не меняются.

## Процесс хуков в TC4R (двухступенчатый)

1. Ветки хуков в форке `MxxFoxx2/thaumcraft-4-reforged` (`feat/addon-*`), проверка через includeBuild этого репо. По правилам `CLAUDE.md` TC4R: additive-only, поведение без TH не меняется, doc-обновление `docs/ADDON_API.md`.
2. Upstream `notlikethiss/thaumcraft-4-reforged` — только по явному решению владельца, из отработанной ветки (образец: PR #37, стиль коммита f0b847d).

## Ветки

- `main`: MC 26.3, NeoForge 26.3.0.57-beta, Java 25. `mc/26.1.2`: Java 25. `mc/1.21.1`: Java 21.
- Бэкпорт main → `mc/26.1.2` → `mc/1.21.1` по процедуре TC4R: коммиты переносятся по порядку с тем же сообщением, в ветке правятся только швы, `compat/`, регистрация и ресурсы. Правка игровой логики в ветке = на `main` не хватает шва.
- `legacy/1.7.10-gtnh` — снапшот оригинала, не развивается; исходники также лежат в `reference/1.7.10/`.

## Справочники

- `reference/1.7.10/` — исходники и ассеты GTNH-форка (декомпилят, «Decompiled by Procyon» — это норма).
- Ассеты копируются в `src/main/resources/assets/thaumichorizons/` по мере порта контента, lang en_us и ru_ru ведутся руками.

## Ресурсы и регистрация

- Регистрация через `DeferredRegister` в `thaumichorizons/registry/` (`ThItems`, `ThBlocks`, `ThBlockEntities`, `ThEntities`, `ThEffects`, `ThMenus`, `ThPayloads`, `ThCreativeTabs`). Регистрация учитывает capability-флаги compat-слоя.
- Player-состояние — NeoForge attachments (copy-on-death, авто-sync по образцу `BaubleAttachments` TC4R); item-данные — data components (StackKey-стиль TC4R), NBT-хаков нет.
- Рецепты critter/self-infusion — собственный реестр TH, НЕ `TcRecipes` TC4R.

## Инвиз игрока (Chameleon Skin)

- Единственный источник правды — attachment `CreatureInfusionAttachment`; никакого вечного MobEffect.
- Сервер выставляет ванильный `setInvisible` из состояния каждый тик; toggle-payload проверяет наличие infusion на сервере.
- Sync: `sendToPlayer` + broadcast `TRACKING_ENTITY` при каждом изменении + `PlayerEvent.StartTracking` новым наблюдателям + auto-sync на join.
- Клиент: `RenderPlayerEvent.Pre` отменяет рендер игрока целиком (броня и предмет в руке скрыты).
- Любая правка этого узла проходит инвиз-чеклист из «Проверок».

## Проверки

```sh
./gradlew build
python3 tools/check_seams.py
```

- TH-R собирается против includeBuild-копии TC4R (dev) и против официального jar.
- Mixin'ы применяются без ошибок в логе `runClient`.
- Инвиз-чеклист (обязателен после правок инвиз-узла, двумя клиентами): toggle скрывает игрока полностью, включая броню и предмет в руке; зашедший позже игрок видит состояние правильно; состояние переживает релог, респавн, смерть, `/reload`; обратный toggle показывает игрока.
- Прочее: critter-infusion через Vat, self-infusion toggles, шприцы, голем TH, карманное измерение, `/reload`, перезапуск мира.

## Сборка

- `settings.gradle` делает `includeBuild('../thaumcraft-4-reforged')`, зависимость `thaumcraft:thaumcraft-4-reforged:1.1.3`. В composite-режиме репозитории included build применяются не полностью — forge maven для группы `com.github.glitchfiend` (TerraBlender) объявлен и в этом проекте; убирать нельзя.
- На этой машине `maven.neoforged.net` был заблокирован роутером: при падении сборки с таймаутом/сбросом к `maven.neoforged.net` — проверить белый список роутера (домен + `*.rsc.cdn77.org`).

## Код-стайл

- Java: 4 пробела, без комментариев (как в TC4R).
- Python (tools/): 4 пробела, двойные кавычки, без комментариев, `pathlib`.
- Коммиты на английском `feat:`/`fix:`, без строк соавторства.
