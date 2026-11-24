#!/usr/bin/env python3
"""Small post-rewrite fixes for 26.x -> 1.21.1 API differences."""
import re
import sys
from pathlib import Path

RULES = [
    (re.compile(r"org\.jspecify\.annotations\.Nullable"), lambda m: "javax.annotation.Nullable"),
    (re.compile(r"net\.minecraft\.world\.entity\.animal\.golem\.(AbstractGolem|IronGolem|SnowGolem)"),
     lambda m: "net.minecraft.world.entity.animal." + m.group(1)),
    (re.compile(r"net\.minecraft\.world\.entity\.EntityTypes"), lambda m: "net.minecraft.world.entity.EntityType"),
    (re.compile(r"Items\.WOOL\.white\(\)"), lambda m: "Items.WHITE_WOOL"),
    (re.compile(r"Items\.WOOL\.black\(\)"), lambda m: "Items.BLACK_WOOL"),
    (re.compile(r"Items\.DYE\.black\(\)"), lambda m: "Items.BLACK_DYE"),
    (re.compile(r"Items\.DYE\.red\(\)"), lambda m: "Items.RED_DYE"),
    (re.compile(r"Items\.DYE\.yellow\(\)"), lambda m: "Items.YELLOW_DYE"),
    (re.compile(r"Blocks\.DYED_TERRACOTTA\.pick\(DyeColor\.PURPLE\)"), lambda m: "Blocks.PURPLE_TERRACOTTA"),
    (re.compile(r"Blocks\.WOOL\.pick\(DyeColor\.RED\)"), lambda m: "Blocks.RED_WOOL"),
    (re.compile(r"Blocks\.WOOL\.pick\(DyeColor\.PURPLE\)"), lambda m: "Blocks.PURPLE_WOOL"),
    (re.compile(r"Blocks\.WOOL\.pick\(DyeColor\.WHITE\)"), lambda m: "Blocks.WHITE_WOOL"),
    (re.compile(r"(\b\w+)\.getBlockState\(([^()]+(?:\([^()]*\))?[^()]*)\)\.isSolidRender\(\)"),
     lambda m: m.group(1) + ".getBlockState(" + m.group(2) + ").isSolidRender(" + m.group(1) + ", " + m.group(2) + ")"),
    (re.compile(r"\.create\((\w+),\s*MobSpawnType\.[A-Z_]+\)"), lambda m: ".create(" + m.group(1) + ")"),
    (re.compile(r"\.spawnAtLocation\((\w+),\s*(new ItemStack\([^;]*?\)|\w+),\s*([0-9.]+)F\)"),
     lambda m: ".spawnAtLocation(" + m.group(2) + ", " + m.group(3) + "F)"),
    (re.compile(r"\.spawnAtLocation\((\w+),\s*(new ItemStack\([^;]*?\)|\w+)\)"),
     lambda m: ".spawnAtLocation(" + m.group(2) + ")"),
    (re.compile(r"\.coldEnoughToSnow\(\)"), lambda m: ".coldEnoughToSnow(pos)"),
    (re.compile(r"new PotionContents\((.+?),\s*java\.util\.Optional(?:\.<\w+>)?\.empty\(\)\)", re.S),
     lambda m: "new PotionContents(" + m.group(1) + ")"),
]


def rewrite(text):
    for pattern, builder in RULES:
        text = pattern.sub(builder, text)
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
    print("fixups rewritten " + str(count) + " files", file=sys.stderr)


if __name__ == "__main__":
    main()
