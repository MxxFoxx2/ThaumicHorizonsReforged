#!/usr/bin/env python3
"""Fix 26.x BlockBehaviour overrides for 1.21.1 signatures."""
import re
import sys
from pathlib import Path

RULES = [
    (re.compile(r"^\s*import net\.minecraft\.world\.entity\.InsideBlockEffectApplier;\n", re.M), lambda m: ""),
    (re.compile(r"^\s*import net\.minecraft\.world\.level\.redstone\.Orientation;\n", re.M), lambda m: ""),
    (re.compile(r"InsideBlockEffectApplier effectApplier, boolean isPrecise\)"), lambda m: "Entity entity)"),
    (re.compile(r"@Nullable Orientation orientation, boolean movedByPiston\)"), lambda m: "BlockPos sourcePos, boolean movedByPiston)"),
    (re.compile(r"net\.minecraft\.world\.level\.redstone\.@Nullable Orientation orientation, boolean movedByPiston\)"),
     lambda m: "BlockPos sourcePos, boolean movedByPiston)"),
    (re.compile(r"InteractionResult\.SUCCESS_SERVER"), lambda m: "ItemInteractionResult.sidedSuccess(true)"),
    (re.compile(r"\bhand\.asEquipmentSlot\(\)"), lambda m: "InteractionCompat.slot(hand)"),
]


def rewrite(text):
    for pattern, builder in RULES:
        text = pattern.sub(builder, text)
    if "ItemInteractionResult." in text and "import net.minecraft.world.ItemInteractionResult;" not in text:
        text = text.replace("import net.minecraft.world.InteractionResult;",
                            "import net.minecraft.world.InteractionResult;\nimport net.minecraft.world.ItemInteractionResult;", 1)
    needed = []
    if "InteractionCompat.slot(" in text:
        needed.append("dev.visfox.thaumichorizons.lib.InteractionCompat")
    if "BlockPos sourcePos" in text:
        needed.append("net.minecraft.core.BlockPos")
    if "ItemInteractionResult." in text:
        needed.append("net.minecraft.world.ItemInteractionResult")
    for name in needed:
        statement = "import " + name + ";\n"
        if statement in text:
            continue
        text = re.sub(r"^(package [^;]+;\n)", r"\1\n" + statement, text, count=1, flags=re.M)
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
    print("block_signatures rewritten " + str(count) + " files", file=sys.stderr)


if __name__ == "__main__":
    main()
