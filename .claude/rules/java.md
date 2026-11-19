---
paths:
  - "src/main/java/**/*.java"
  - "src/test/java/**/*.java"
---

# Java rules

- Следуй стилю соседних классов: 4 пробела, существующие соглашения по именованию, nullable-аннотациям и структуре пакетов.
- Не вводи новый framework, utility layer или абстракцию ради единичного использования.
- Используй существующие compat-интерфейсы и platform seams; не обходи их прямым импортом внутренних API.
- Проверяй dedicated-server safety: общие классы не должны ссылаться на client-only типы.
- Для persisted/networked state используй существующие data components, attachments, codecs и stream codecs. Проверяй defaults, старые/неполные данные, round-trip и invalid input.
- Проверяй стороны выполнения: authoritative mutations выполняются на сервере; клиентские изменения — только prediction/UI там, где это предусмотрено.
- Для entity/block-entity/menu logic учитывай save/load, chunk unload/reload, tracking, ownership и multiplayer.
- Комментарий должен объяснять причину, инвариант или ограничение, а не повторять следующую строку.
- Новые тесты на чистые преобразования и сериализацию пиши на JUnit 5. Не подменяй runtime-тесты утверждением о прохождении JUnit.
- Перед изменением интерфейса найди все реализации, ServiceLoader entries, тесты и потребителей. При изменении API compat/platform проверь и обнови соответствующие contract tests.
