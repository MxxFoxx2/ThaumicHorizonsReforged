---
paths:
  - "src/main/java/**/client/**/*.java"
  - "src/main/resources/assets/thaumichorizons/**"
  - "src/main/resources/data/thaumichorizons/**"
---

# Assets and rendering rules

- Сверяй геометрию, UV, размеры текстуры, alpha, pivot, animation timing и render transforms с ассетами/кодом \`reference/1.7.10/\`, если переносишь оригинальный визуал.
- Не считай наличие JSON или регистрацию renderer достаточным доказательством: проверяй связи между registry ID, blockstate, model, parent, texture и renderer.
- Resource locations и имена файлов должны быть lowercase; при переименовании синхронно обновляй все JSON и Java-ссылки.
- Не создавай placeholder-текстуры, случайные модели или guessed mappings, чтобы скрыть отсутствующий ресурс.
- Проверяй client-only загрузку и render registration на правильной стороне. Не добавляй ссылки на client renderer в общий/server class.
- Сохраняй оригинальные текстуры и модельную геометрию, когда это соответствует лицензии и структуре проекта; не «улучшай» дизайн без запроса или записи intentional deviation.
- После изменений запускай \`python3 -I tools/check_assets.py\` (если затронуты blockstates/models/items) и релевантные seam/build checks. Помни: asset checker — локальный статический чекер, его прохождение не означает визуальную проверку в игре.
- Для каждого нового или изменённого renderer указывай в отчёте, был ли он реально проверен в клиенте.
