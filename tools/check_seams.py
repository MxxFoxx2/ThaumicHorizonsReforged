import argparse
import json
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
    "thaumcraft.client.recipeview.AspectGrid",
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
    "thaumcraft.entity.golem.GolemType",
    "thaumcraft.client.render.entity.golem.GolemRenderer",
    "thaumcraft.client.render.entity.golem.GolemRenderState",
    "thaumcraft.client.geom.CubeGeometry",
    "thaumcraft.client.geom.QuadSink",
    "thaumcraft.client.geom.FacingQuads",
    "thaumcraft.client.geom.ModelBoxGeometry",
    "thaumcraft.client.geom.InfusionHaloGeometry",
    "thaumcraft.client.platform.TcRenderTypes",
    "thaumcraft.client.platform.ItemGeometry",
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



def check_item_model_definitions(root):
    assets = Path(root) / "src" / "main" / "resources" / "assets" / "thaumichorizons"
    source_root = Path(root) / SOURCE_DIR
    client_sources = sorted((source_root / "client").rglob("*.java"))
    errors = []

    if (assets / "items").is_dir():
        errors.append(
            f"{assets / 'items'}: 26.x item definitions are not used by Minecraft 1.21.1; "
            "item models belong in models/item"
        )

    registry = parse_registry(source_root / "registry")
    item_ids = registry["items"]
    if not item_ids:
        errors.append(f"no registered items found under {source_root / 'registry'}")

    item_model_dir = assets / "models/item"
    if not item_model_dir.is_dir():
        print(f"item models: item model directory not found: {item_model_dir}")
        return 1

    model_files = sorted((assets / "models").glob("**/*.json"))
    checked_models = set()
    visiting_models = set()
    checked_textures = set()
    referenced_models = set()

    def check_texture_reference(reference, source):
        if not isinstance(reference, str) or not reference or reference.startswith("#"):
            return
        namespace, resource = reference.split(":", 1) if ":" in reference else ("thaumichorizons", reference)
        if namespace != "thaumichorizons":
            return
        if resource.endswith(".png"):
            resource = resource[:-4]
        texture_path = assets.parent / namespace / "textures" / (resource + ".png")
        key = str(texture_path)
        if key in checked_textures:
            return
        checked_textures.add(key)
        if not texture_path.is_file():
            errors.append(f"{source}: texture reference {reference} does not resolve to {texture_path}")

    def check_model_file(model_path):
        if model_path in checked_models or model_path in visiting_models:
            return
        visiting_models.add(model_path)
        try:
            model = json.loads(model_path.read_text(encoding="utf-8"))
        except (OSError, json.JSONDecodeError) as exception:
            errors.append(f"{model_path}: invalid model JSON: {exception}")
            visiting_models.remove(model_path)
            checked_models.add(model_path)
            return

        parent = model.get("parent") if isinstance(model, dict) else None
        if isinstance(parent, str):
            check_model_reference(parent, model_path)

        textures = model.get("textures", {}) if isinstance(model, dict) else {}
        if isinstance(textures, dict):
            for texture in textures.values():
                check_texture_reference(texture, model_path)

        visiting_models.remove(model_path)
        checked_models.add(model_path)

    def check_model_reference(reference, source):
        if not isinstance(reference, str) or ":" not in reference:
            return
        namespace, resource = reference.split(":", 1)
        if namespace != "thaumichorizons":
            return

        if resource.startswith("item/"):
            model_path = assets / "models" / "item" / (resource.removeprefix("item/") + ".json")
        elif resource.startswith("block/"):
            model_path = assets / "models" / "block" / (resource.removeprefix("block/") + ".json")
        else:
            return

        referenced_models.add(model_path)
        if not model_path.is_file():
            errors.append(f"{source}: model reference {reference} does not resolve to {model_path}")
        else:
            check_model_file(model_path)

    for model_path in model_files:
        check_model_file(model_path)

    for item_id in sorted(item_ids):
        model_path = item_model_dir / f"{item_id}.json"
        if not model_path.is_file():
            errors.append(f"item {item_id}: no models/item/{item_id}.json")

    for model_path in sorted(item_model_dir.glob("*.json")):
        if model_path in referenced_models:
            continue
        if model_path.stem not in item_ids:
            errors.append(
                f"{model_path}: model does not belong to a registered item "
                "and is not used as a parent"
            )

    for error in errors:
        print(error)

    if errors:
        print(f"item model validation failed: {len(errors)} error(s)")
        return 1

    print(
        f"item models ok: {len(item_ids)} registered items, {len(model_files)} models, "
        f"{len(checked_textures)} local textures"
    )
    return 0


REGISTRY_ITEM_PATTERNS = (
    re.compile(r'DeferredItem<[^>]*>\s+(\w+)\s*=\s*ITEMS\.registerItem\(\s*"([^"]+)"'),
    re.compile(r'DeferredItem<[^>]*>\s+(\w+)\s*=\s*spawnEgg\(\s*"([^"]+)"'),
    re.compile(r'DeferredItem<[^>]*>\s+(\w+)\s*=\s*cloudItem\(\s*"([^"]+)"'),
)

REGISTRY_BLOCK_PATTERN = re.compile(
    r'DeferredBlock<[^>]*>\s+(\w+)\s*=\s*(withItem\()?\s*BLOCKS\.registerBlock\(\s*"([^"]+)"'
)

REGISTRY_HELPER_PATTERNS = (
    (re.compile(r'DeferredItem<[^>]*>\s+(\w+)\s*=\s*infusionCheat\((\d+)\)'), "infusioncheat"),
    (re.compile(r'DeferredItem<[^>]*>\s+(\w+)\s*=\s*selfInfusionCheat\((\d+)\)'), "infusionselfcheat"),
)


def parse_registry(registry_dir):
    """Collect registered item ids and the constants that reference them."""
    items = {}
    blocks = {}

    for source_path in sorted(registry_dir.glob("*.java")):
        text = source_path.read_text(encoding="utf-8")
        for pattern in REGISTRY_ITEM_PATTERNS:
            for constant, name in pattern.findall(text):
                if pattern is REGISTRY_ITEM_PATTERNS[1]:
                    name = name + "_spawn_egg"
                items[constant] = name
        for pattern, prefix in REGISTRY_HELPER_PATTERNS:
            for constant, number in pattern.findall(text):
                suffix = "" if int(number) == 1 else number
                items[constant] = prefix + suffix
        for constant, has_item, name in REGISTRY_BLOCK_PATTERN.findall(text):
            blocks[constant] = (name, bool(has_item))

    item_ids = set(items.values())
    for name, has_item in blocks.values():
        if has_item:
            item_ids.add(name)

    return {"items": item_ids, "constants": items, "blocks": blocks}

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

    item_model_status = check_item_model_definitions(arguments.root)
    if violations or item_model_status:
        return 1

    print(f"seams ok: {files} files checked")
    return 0


if __name__ == "__main__":
    sys.exit(main())
