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
- Ось «порт Thaumcraft»: `dev.visfox.thaumichorizons.compat/` — интерфейсы (`AspectCompat`, `ResearchCompat`, `WarpCompat`, `EssentiaCompat`, `NodeCompat`, `VisNetCompat`, `GolemCompat`, `InfusionCompat`, `FxCompat`, `ScanCompat`, `PotionCompat`, `ItemCompat`). Реализация `TcReforgedPort` выбрана через ServiceLoader (`META-INF/services`).
- Каждый compat-интерфейс имеет `isAvailable()`; контент отсутствующей подсистемы не регистрируется и скрывается, ядро не крашится. Ссылки на предметы TC — только `ItemCompat.lookup(...)`.
- **Allowlist API TC4R**: наследование классов TC4R не абстрагируется адаптером, поэтому игровому коду разрешён прямой импорт только API-поверхности TC4R — список `THAUMCRAFT_API` в `tools/check_seams.py` (аспекты, `EssentiaTransport`/`EssentiaHandler`, `Node`/`NodeGenerator`/`VisNet`/`VisNode`, wand-классы и фокусы, голем-классы, research/warp, `TcRecipes`, `thaumcraft.api.addon.*`). Внутренности TC4R (`NodeBlockEntity`, `GolemBase` internals, клиент TC4R и т.д.) — только в `compat/`. Добавление в allowlist = осознанное решение расширить точку сопряжения, помечать в `docs/ADDON_API.md` TC4R.

## Mixin-хуки TC4R

Пока хуки не приняты в TC4R, TH-R несёт их mixin'ами (`thaumichorizons.mixins.json`, `META-INF/accesstransformer.cfg`). Правила:

- Mixin'ы живут только в `dev.visfox.thaumichorizons.compat.tc/` и только как реализация `TcReforgedCompat`. Раскидывать цели TC4R по игровому коду запрещено.
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

- Регистрация через `DeferredRegister` в `dev.visfox.thaumichorizons.registry/` (`ThItems`, `ThBlocks`, `ThBlockEntities`, `ThEntities`, `ThEffects`, `ThMenus`, `ThPayloads`, `ThCreativeTabs`). Регистрация учитывает capability-флаги compat-слоя.
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

## Подводные камни 26.3 (первый запуск, выстраданы верификацией)

- Item data components bind'ятся только при загрузке датапаков (`DefaultDataComponentsBoundEvent`) — не создавай `ItemStack`/компоненты в статической инициализации или конструкторе регистрации; ленивое создание при первом обращении в игре.
- Регистрация блоков: `IntegerProperty` требует max > min — одно значение пишется как `(name, 10, 11)`.
- Все пути ресурсов — lowercase; camelCase-имена текстур/моделей из 1.7.10 переименовывать сразу при копировании ассетов, вместе со ссылками в json/java.
- Свои `FocusUpgradeType`-id не должны пересекаться с TC4R (TC4R молча отбрасывает чужой id при конфликте — эффект «исчез апгрейд» без ошибок в логе).
- Lang-ключи сверять с кодом: суффикс `.name` в ключе означает, что код строит ключ иначе, чем ты думаешь.
- Объекты, создаваемые при регистрации блоков (например, дроп-стеки в конструкторе), держать ленивыми (`ItemLike`/Supplier) — регистрация идёт раньше bind'а компонентов.

## Оркестрация агентов (плейбук, выстрадан)

Портирование фаз делается субагентами. Правила брифа и сессии:

- **Бриф обязан содержать**: жёсткий токен-бюджет (тяжёлая дата-фаза ≤8–15M, лёгкая ≤5M); лимит gradle-прогонов (≤6); codegraph-first (`mcp__codegraph__codegraph_explore` с projectPath) вместо чтения файлов целиком; `rtk` для git/rg/read; явный список «DO NOT touch» (зоны других агентов и координатора); источник истины по порядку — **GTNH-референс (что портировать) → Azanor API (оракул семантики) → TC4R (идиома реализации)**; всё нужное лежит на этой машине — gh/web-поиски запрещены.
- **Реестры, lang, main-класс принадлежат координатору.** Агент пишет классы и сдаёт REGISTRATION MANIFEST (точные записи: поле, registry name 1:1 lowercase, класс, properties, lang-ключи) — координатор вносит сам. Параллельным агентам — запрет коммитов и запрета их `git add -A`.
- **Параллельные сессии**: зоны разведены явно; реестр-владение выдаётся SendMessage'ом; у каждого ≤1 собственного GUI-файла-реестра; билд-демон общий — параллельные прогоны медленнее в ~1.5×.
- **Контроль живости**: 10–25 минут без файлов — норма (read-фаза); пинг статусом после 45 минут; TaskOutput block=false показывает текущий инструмент агента. Сетевые смерти (EACCES на API) лечатся резюмом через SendMessage.
- **Wrap-up протокол** (при исчерпании бюджета): приказ «wrap up immediately — manifest + DONE/INCOMPLETE, stop»; in-flight файлы — в `.deferred-<имя>/` + gitignore + RESUME_MANIFEST.md с точкой возобновления; билд после изоляции обязан быть зелёным.
- **Селективные коммиты**: в parallel-режиме координатор коммитит только свои и сданные файлы (перечислением путей), никогда `git add -A` при живых агентах.
- **Calibration**: тяжёлая дата-фаза (существа/мир) 25–60M, машины 20–40M, клиент 4–15M, лёгкая 0.5–5M. Экономия от дисциплины — до 10× (реальные замеры: 57M без дисциплины vs 4.5M с ней на сопоставимой работе).

## Код-стайл

- Java: 4 пробела, без комментариев (как в TC4R).
- Python (tools/): 4 пробела, двойные кавычки, без комментариев, `pathlib`.
- Коммиты на английском `feat:`/`fix:`, без строк соавторства.
