---
name: port-content
description: Use when porting a gameplay feature or content from the Thaumic Horizons 1.7.10 reference into this repository.
---

# Port content from 1.7.10

1. Найди исходную реализацию в `reference/1.7.10/` и связанные регистрации, вызовы, рецепты, исследования, аспекты, локализацию, модели и текстуры.
2. Сверь старые ID с таблицей `../thaumcraft-4-reforged-26.1.2/tools/data/id_map.json` и реализацией TC4R из pinned checkout. Не угадывай замену по названию.
3. Изучи существующие TH-реализации и используй текущие registry, compat, platform и rendering patterns.
4. Сохраняй серверную авторитетность, persistence, synchronization, ownership, drops и multiplayer-поведение там, где они применимы.
5. Сохраняй оригинальные числа, вероятности, стоимости аспектов, ингредиенты и research gates. Необходимые отступления фиксируй в `docs/fidelity.md`.
6. Добавляй JUnit-тесты для детерминированных преобразований, codecs и data contracts, когда это полезно. Они не заменяют runtime-проверки.
7. Запускай релевантные seam/build/asset checks. Непроверенные игровые сценарии записывай в `docs/verification.md`.
8. В отчёте указывай изученные исходники, объём реализации, проверки и оставшиеся неизвестные.

Не объявляй порт завершённым только потому, что он компилируется или появился в creative tab.
