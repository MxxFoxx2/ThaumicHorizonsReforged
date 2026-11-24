#!/usr/bin/env python3
"""Convert ValueInput-style calls on CompoundTag in 1.21.1 saved-data code."""
import re
import sys
from pathlib import Path

RULES = [
    (re.compile(r"\b(\w+)\.getIntOr\(([^;]+?)\)"), r"TagInput.getIntOr(\1, \2)"),
    (re.compile(r"\b(\w+)\.getStringOr\(([^;]+?)\)"), r"TagInput.getStringOr(\1, \2)"),
    (re.compile(r"\b(\w+)\.getBooleanOr\(([^;]+?)\)"), r"TagInput.getBooleanOr(\1, \2)"),
    (re.compile(r"\b(\w+)\.getLongOr\(([^;]+?)\)"), r"TagInput.getLongOr(\1, \2)"),
    (re.compile(r"\b(\w+)\.getDoubleOr\(([^;]+?)\)"), r"TagInput.getDoubleOr(\1, \2)"),
    (re.compile(r"\b(\w+)\.getIntArray\(\"([^\"]+)\"\)\.orElse\(null\)"), r"TagInput.getIntArrayOr(\1, \"\2\")"),
    (re.compile(r"\b(\w+)\.getListOrEmpty\(\"([^\"]+)\"\)"), r"TagInput.getListOrEmpty(\1, \"\2\")"),
    (re.compile(r"\b(\w+)\.getCompoundOrEmpty\(\"([^\"]+)\"\)"), r"TagInput.getCompoundOrEmpty(\1, \"\2\")"),
    (re.compile(r"\b(\w+)\.getCompoundOrEmpty\((\w+)\)"), r"TagInput.getCompoundOrEmpty(\1, \2)"),
    (re.compile(r"\b(\w+)\.keySet\(\)"), r"TagInput.keySet(\1)"),
]


def rewrite(text):
    for pattern, replacement in RULES:
        text = pattern.sub(replacement, text)
    if "TagInput." in text and "import dev.visfox.thaumichorizons.lib.TagInput;" not in text:
        text = re.sub(r"^(package [^;]+;\n)", r"\1\nimport dev.visfox.thaumichorizons.lib.TagInput;\n",
                      text, count=1, flags=re.M)
    return text


def main():
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("src/main/java/dev/visfox/thaumichorizons/world/pocket")
    count = 0
    for path in sorted(root.rglob("*.java")):
        original = path.read_text(encoding="utf-8")
        updated = rewrite(original)
        if updated != original:
            path.write_text(updated, encoding="utf-8", newline="")
            count += 1
    print("tag_input rewritten " + str(count) + " files", file=sys.stderr)


if __name__ == "__main__":
    main()
