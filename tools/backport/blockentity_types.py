#!/usr/bin/env python3
"""Convert 26.x BlockEntityType constructors to the 1.21.1 Builder API."""
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


def strip_set(arg):
    stripped = arg.strip()
    match = re.match(r"(?:java\.util\.)?Set\.of\(", stripped)
    if not match:
        return [arg.strip()]
    args, _ = split_args(stripped, match.end())
    return [item.strip() for item in args] if args else [arg.strip()]


def rewrite(text):
    out = []
    pos = 0
    pattern = re.compile(r"new BlockEntityType<>\(")
    changed = False
    while True:
        match = pattern.search(text, pos)
        if not match:
            out.append(text[pos:])
            break
        args, end = split_args(text, match.end())
        if not args:
            out.append(text[pos:match.end()])
            pos = match.end()
            continue
        factory = args[0].strip()
        blocks = []
        for arg in args[1:]:
            blocks.extend(strip_set(arg))
        replacement = "BlockEntityType.Builder.of(" + factory + ", " + ", ".join(blocks) + ").build(null)"
        out.append(text[pos:match.start()])
        out.append(replacement)
        pos = end + 1
        changed = True
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
    print("blockentity_types rewritten " + str(count) + " files", file=sys.stderr)


if __name__ == "__main__":
    main()
