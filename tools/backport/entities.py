#!/usr/bin/env python3
"""Convert 26.x registerEntityType to the 1.21.1 DeferredRegister API."""
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
    text = text.replace("DeferredRegister.Entities", "DeferredRegister<EntityType<?>>")
    text = text.replace("DeferredRegister.createEntities(ThaumicHorizons.MODID)",
                        "DeferredRegister.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, ThaumicHorizons.MODID)")
    out = []
    pos = 0
    pattern = re.compile(r"\.registerEntityType\(")
    changed = False
    while True:
        match = pattern.search(text, pos)
        if not match:
            out.append(text[pos:])
            break
        args, end = split_args(text, match.end())
        if not args or len(args) < 4:
            out.append(text[pos:match.end()])
            pos = match.end()
            continue
        name = args[0].strip()
        factory = args[1].strip()
        category = args[2].strip()
        tail = ",".join(args[3:]).strip()
        tail = re.sub(r"^\w+\s*->\s*\w+\.", "", tail)
        replacement = (".register(" + name + ", () -> EntityType.Builder.of(" + factory + ", "
                       + category + ")." + tail + ".build(\"thaumichorizons:" + name.strip('"') + "\"))")
        out.append(text[pos:match.start()])
        out.append(replacement)
        pos = end + 1
        changed = True
    return "".join(out), changed


def main():
    path = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("src/main/java/dev/visfox/thaumichorizons/registry/ThEntities.java")
    original = path.read_text(encoding="utf-8")
    updated, changed = rewrite(original)
    if changed:
        path.write_text(updated, encoding="utf-8", newline="")
    print("entities rewritten: " + str(changed), file=sys.stderr)


if __name__ == "__main__":
    main()
