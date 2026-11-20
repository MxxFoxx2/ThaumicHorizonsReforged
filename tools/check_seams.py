import argparse
import re
import sys
from pathlib import Path

SOURCE_DIR = Path("src") / "main" / "java" / "dev" / "visfox" / "thaumichorizons"

PLATFORM = ("platform/", "client/platform/")
CLIENT_PLATFORM = ("client/platform/",)
CLIENT = "client/"
COMPAT = "compat/"

OUTSIDE_PLATFORM = [
    "net.neoforged.neoforge.network.*",
    "net.neoforged.neoforge.client.network.*",
    "net.neoforged.neoforge.attachment.*",
    "net.neoforged.neoforge.transfer.*",
    "net.neoforged.neoforge.capabilities.*",
    "net.minecraft.core.component.DataComponentType",
    "net.minecraft.world.level.portal.TeleportTransition",
    "net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket",
    "net.minecraft.world.item.ToolMaterial",
    "net.minecraft.world.item.equipment.*",
    "net.minecraft.world.item.component.Consumable",
    "net.minecraft.world.item.component.Consumables",
    "net.minecraft.world.item.consume_effects.*",
]

OUTSIDE_CLIENT_PLATFORM = [
    "com.mojang.renderpearl.*",
    "net.minecraft.client.renderer.RenderPipelines",
    "net.minecraft.client.renderer.rendertype.*",
]

OUTSIDE_CLIENT = [
    "net.minecraft.client.*",
    "dev.visfox.thaumichorizons.client.*",
    "com.mojang.blaze3d.*",
    "net.neoforged.neoforge.client.*",
]
OUTSIDE_CLIENT_EXCEPT_FILES = ("ThaumicHorizonsClient.java",)

THAUMCRAFT_API = [
    "thaumcraft.Thaumcraft",
        "thaumcraft.aspect.Aspect",
    "thaumcraft.aspect.AspectList",
    "thaumcraft.aspect.AspectRegistry",
    "thaumcraft.aspect.AspectSync",
    "thaumcraft.aspect.AddonAspects",
    "thaumcraft.essentia.Essentia",
    "thaumcraft.essentia.EssentiaTransport",
    "thaumcraft.essentia.EssentiaHandler",
    "thaumcraft.node.Node",
    "thaumcraft.node.NodeType",
    "thaumcraft.node.NodeModifier",
        "thaumcraft.node.EnergizedNodeBlockEntity",
    "thaumcraft.vis.VisNode",
    "thaumcraft.vis.VisNet",
    "thaumcraft.wand.WandItem",
    "thaumcraft.wand.WandInteractable",
    "thaumcraft.wand.WandCap",
    "thaumcraft.wand.WandRod",
    "thaumcraft.wand.WandRegistry",
    "thaumcraft.wand.WandKeys",
    "thaumcraft.wand.WandParts",
    "thaumcraft.wand.WandVis",
    "thaumcraft.wand.WandManager",
    "thaumcraft.wand.WandTriggers",
    "thaumcraft.wand.Wands",
    "thaumcraft.wand.focus.FocusItem",
    "thaumcraft.wand.focus.FocusUpgradeType",
    "thaumcraft.entity.golem.GolemBase",
    "thaumcraft.entity.golem.GolemStats",
    "thaumcraft.entity.golem.GolemType",
    "thaumcraft.client.render.entity.golem.GolemRenderer",
    "thaumcraft.client.geom.CubeGeometry",
    "thaumcraft.client.geom.QuadSink",
    "thaumcraft.client.geom.FacingQuads",
    "thaumcraft.client.geom.ModelBoxGeometry",
    "thaumcraft.client.geom.InfusionHaloGeometry",
    "thaumcraft.client.platform.TcRenderTypes",
    "thaumcraft.client.platform.EntityQuadSink",
            "thaumcraft.client.render.device.WardedRendering",
    "thaumcraft.entity.golem.GolemKeys",
    "thaumcraft.entity.golem.GolemSnapshot",
    "thaumcraft.item.golem.GolemPlacerItem",
    "thaumcraft.item.golem.GolemBellItem",
    "thaumcraft.item.golem.GolemItems",
    "thaumcraft.wand.WandCap",
    "thaumcraft.wand.WandRod",
    "thaumcraft.aspect.AspectHelper",
    "thaumcraft.aspect.AspectKeys",
    "thaumcraft.item.tool.SpecialMining",
    "thaumcraft.entity.CombatUtils",
    "thaumcraft.entity.TaintedMob",
    "thaumcraft.taint.TaintBiomes",
    "thaumcraft.research.ResearchCategories",
    "thaumcraft.research.ResearchItem",
    "thaumcraft.research.ResearchGate",
    "thaumcraft.research.ResearchManager",
    "thaumcraft.research.ResearchPage",
    "thaumcraft.research.Warp",
    "thaumcraft.research.WarpType",
    "thaumcraft.research.Knowledge",
    "thaumcraft.research.ScanManager",
    "thaumcraft.research.ScanKind",
    "thaumcraft.crafting.ArcaneCrafting",
    "thaumcraft.crafting.TcRecipes",
        "thaumcraft.crafting.InfusionRecipe",
    "thaumcraft.crafting.TcStack",
    "thaumcraft.crafting.TcIngredient",
    "thaumcraft.crafting.CrucibleRecipe",
    "thaumcraft.crafting.ShapedArcaneRecipe",
    "thaumcraft.crafting.ShapelessArcaneRecipe",
    "thaumcraft.crafting.CraftingRecipeRef",
            "thaumcraft.block.InfusionStabiliser",
    "thaumcraft.damage.ModDamageTypes",
    "thaumcraft.device.BrainJarBlockEntity",
    "thaumcraft.device.HandMirrorItem",
    "thaumcraft.device.MirrorBlock",
    "thaumcraft.device.MirrorBlockEntity",
    "thaumcraft.device.MirrorKeys",
    "thaumcraft.device.MirrorLinks",
    "thaumcraft.device.SmeltingBonuses",
    "thaumcraft.entity.AspectOrb",
    "thaumcraft.entity.monster.Wisp",
    "thaumcraft.item.AspectTooltips",
    "thaumcraft.network.NodeZapPayload",
    "thaumcraft.node.NodeBlockEntity",
    "thaumcraft.node.NodeHooks",
    "thaumcraft.node.NodeTransducerBlockEntity",
    "thaumcraft.platform.Baubles",
    "thaumcraft.platform.ModNetwork",
    "thaumcraft.registry.ModBlocks",
    "thaumcraft.registry.ModEntities",
    "thaumcraft.registry.ModSounds",
    "thaumcraft.research.PlayerKnowledge",
"thaumcraft.platform.Teleport",
]

IMPORT_PATTERN = re.compile(r"^\s*import\s+(?:static\s+)?([\w.]+(?:\.\*)?)\s*;")


def matches(imported, pattern):
    if pattern.endswith(".*"):
        return imported.startswith(pattern[:-1])
    return imported == pattern or imported.startswith(pattern + ".")


def matches_any(imported, patterns):
    return any(matches(imported, pattern) for pattern in patterns)


def violated_rule(relative, imported):
    in_platform = relative.startswith(PLATFORM)
    in_client_platform = relative.startswith(CLIENT_PLATFORM)
    in_client = relative.startswith(CLIENT)
    in_compat = relative.startswith(COMPAT)
    if not in_compat and matches_any(imported, ["thaumcraft.*"]) and not matches_any(imported, THAUMCRAFT_API):
        return "thaumcraft.* outside compat/ is limited to the API allowlist (THAUMCRAFT_API in check_seams.py)"
    if not in_platform and matches_any(imported, OUTSIDE_PLATFORM):
        return "outside platform/ and client/platform/"
    if not in_client_platform and matches_any(imported, OUTSIDE_CLIENT_PLATFORM):
        return "outside client/platform/"
    if not in_client and relative not in OUTSIDE_CLIENT_EXCEPT_FILES and matches_any(imported, OUTSIDE_CLIENT):
        return "outside client/"
    return None


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", default=".")
    arguments = parser.parse_args()
    source = Path(arguments.root) / SOURCE_DIR
    violations = []
    files = 0
    for path in sorted(source.rglob("*.java")):
        files += 1
        relative = path.relative_to(source).as_posix()
        for number, line in enumerate(path.read_text(encoding="utf-8").splitlines(), 1):
            found = IMPORT_PATTERN.match(line)
            if not found:
                continue
            imported = found.group(1)
            rule = violated_rule(relative, imported)
            if rule:
                violations.append(f"{path}:{number}: {imported} ({rule})")
    for violation in violations:
        print(violation)
    if violations:
        print(f"{len(violations)} seam violations in {files} files")
        return 1
    print(f"seams ok: {files} files checked")
    return 0


if __name__ == "__main__":
    sys.exit(main())
