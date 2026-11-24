#!/usr/bin/env python3
"""Convert 26.x Entity#snapTo calls to the 1.21.1 placement helper."""
import re
import sys
from pathlib import Path


def split_args(text, start):
    depth = 0
    args = []
    current = start
    i = start
    while i < len(text):
        c = text[i]
        if c in "([{":
            depth += 1
        elif c in ")]}":
            if depth == 0:
                args.append(text[current:i])
                return args, i
            depth -= 1
        elif c == "," and depth == 0:
            args.append(text[current:i])
            current = i + 1
        i += 1
    return None, start


def rewrite(text):
    out = []
    pos = 0
    pattern = re.compile(r"(\b[\w.]+)\.snapTo\(")
    changed = False
    while True:
        match = pattern.search(text, pos)
        if not match:
            out.append(text[pos:])
            break
        args, end = split_args(text, match.end())
        if not args or len(args) != 5:
            out.append(text[pos:match.end()])
            pos = match.end()
            continue
        replacement = "EntityPlacement.snapTo(" + match.group(1) + ", " + ", ".join(
            arg.strip() for arg in args) + ")"
        out.append(text[pos:match.start()])
        out.append(replacement)
        pos = end + 1
        changed = True
    if changed and "EntityPlacement.snapTo(" in "".join(out):
        text = "".join(out)
        if "import dev.visfox.thaumichorizons.lib.EntityPlacement;" not in text:
            text = re.sub(r"^(package [^;]+;\n)", r"\1\nimport dev.visfox.thaumichorizons.lib.EntityPlacement;\n",
                          text, count=1, flags=re.M)
        return text, True
    return "".join(out), changed


def main():
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("src/main/java")
    count = 0
    for path in sorted(root.rglob("*.java")):
        original = path.read_text(encoding="utf-8")
        updated, changed = rewrite(original)
        if changed:
            path.write_text(updated, encoding="utf-8", newline="")
            count += 1
    print("snap_to rewritten " + str(count) + " files", file=sys.stderr)


if __name__ == "__main__":
    main()
