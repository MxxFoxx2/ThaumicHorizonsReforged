#!/usr/bin/env python3
"""1.21.1 block-property and recipe constructor fixes."""
import re
import sys
from pathlib import Path


def rewrite(text):
    text = re.sub(r"\.noCollision\(\)", ".noCollission()", text)
    text = re.sub(r"extends CustomRecipe \{", "extends CustomRecipe {\n    public FocusLikeRecipeMarker() {\n    }", text) if False else text
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
    print("block_props rewritten " + str(count) + " files", file=sys.stderr)


if __name__ == "__main__":
    main()
