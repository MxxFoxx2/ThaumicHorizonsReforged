#!/usr/bin/env python3
"""Convert NeoForge 26.x registerBlock/registerItem property lambdas to 1.21.1 properties."""
import re
import sys
from pathlib import Path


def split_call(text, start):
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
    pattern = re.compile(r"\.(registerBlock|registerItem)\(")
    changed = False
    while True:
        match = pattern.search(text, pos)
        if not match:
            out.append(text[pos:])
            break
        args, end = split_call(text, match.end())
        if args is None or len(args) != 3:
            out.append(text[pos:match.end()])
            pos = match.end()
            continue
        third = args[2]
        lambda_match = re.match(r"\s*(\w+)\s*->\s*(.*?)\s*$", third, re.S)
        if not lambda_match:
            out.append(text[pos:match.end()])
            pos = match.end()
            continue
        kind = "block" if match.group(1) == "registerBlock" else "item"
        new_third = "THRegistryProps." + kind + "(" + third.strip() + ")"
        replacement = "." + match.group(1) + "(" + args[0] + "," + args[1] + "," + new_third + ")"
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
    print("registry_args rewritten " + str(count) + " files", file=sys.stderr)


if __name__ == "__main__":
    main()
