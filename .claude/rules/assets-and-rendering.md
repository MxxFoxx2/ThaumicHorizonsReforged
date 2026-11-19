---
paths:
  - "src/main/java/**/client/**/*.java"
  - "src/main/resources/assets/thaumichorizons/**"
  - "src/main/resources/data/thaumichorizons/**"
---

# Assets and rendering

- При переносе оригинального визуала сверяй геометрию, UV, размеры текстур, alpha, pivot и render transforms с `reference/1.7.10/`.
- Проверяй цепочку registry ID → blockstate → model → parent → texture → renderer; наличие JSON или регистрации само по себе не доказывает корректность.
- Resource locations и имена файлов должны быть lowercase; переименование синхронно отражай во всех JSON и Java-ссылках.
- Не создавай placeholder-текстуры, случайные модели или guessed mappings, чтобы скрыть отсутствующий ресурс.
- Не допускай загрузки client-only renderer-классов на dedicated server.
- Не меняй оригинальную геометрию и внешний вид без запроса или документированного intentional deviation.
- После изменений blockstates/models/items запускай `python3 -I tools/check_assets.py` и релевантные seam/build checks. Этот скрипт проверяет только ссылки, а не внешний вид в игре.
- В отчёте указывай, проверялся ли новый или изменённый renderer в реальном клиенте.
