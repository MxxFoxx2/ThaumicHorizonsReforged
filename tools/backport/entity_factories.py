#!/usr/bin/env python3
"""Fix entity constructor type parameters and 1.21.1 builder calls."""
import re
import sys
from pathlib import Path


def rewrite(text):
    text = re.sub(
        r"((?:public|protected)\s+(\w+)\(\s*EntityType)<\?\s+extends\s+\2>(\s+\w+\s*,\s*Level)",
        lambda m: m.group(1) + "<" + m.group(2) + ">" + m.group(3),
        text,
    )
    text = re.sub(r"\.notInPeaceful\(\)", "", text)
    return text


def main():
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("src/main/java")
    count = 0
    for path in sorted(root.rglob("*.java")):
        original = path.read_text(encoding="utf-8")
        updated = rewrite(original)
        if updated != original:
            path.write_text(updated, encoding="utf-8", newline="")
            count += 1
    print("entity_factories rewritten " + str(count) + " files", file=sys.stderr)


if __name__ == "__main__":
    main()
