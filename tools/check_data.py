import argparse
import json
import sys
from pathlib import Path

DEFAULT_DATA = Path("src") / "main" / "resources" / "data"

LOOT_TABLE_TYPES = {
    "minecraft:archaeology",
    "minecraft:barter",
    "minecraft:block",
    "minecraft:chest",
    "minecraft:entity",
    "minecraft:equipment",
    "minecraft:fishing",
    "minecraft:gift",
    "minecraft:shearing",
}
LOOT_TABLE_KEYS = {"type", "pools", "functions", "random_sequence"}
LOOT_POOL_KEYS = {"entries", "rolls", "bonus_rolls", "conditions", "functions", "name"}
LOOT_ENTRY_KEYS = {
    "minecraft:item": {"name"},
    "minecraft:tag": {"name", "expand"},
    "minecraft:dynamic": {"name"},
    "minecraft:loot_table": {"value"},
    "minecraft:alternatives": {"children"},
    "minecraft:sequence": {"children"},
    "minecraft:group": {"children"},
    "minecraft:empty": set(),
}
LOOT_ENTRY_COMMON_KEYS = {"type", "conditions", "functions", "weight", "quality"}
LOOT_COMPOSITE_KEYS = {"type", "conditions", "functions"}

CRAFTING_CATEGORIES = {"building", "redstone", "equipment", "misc"}
COOKING_CATEGORIES = {"food", "blocks", "misc"}
CRAFTING_SHAPED_KEYS = {"type", "group", "category", "pattern", "key", "result"}
CRAFTING_SHAPELESS_KEYS = {"type", "group", "category", "ingredients", "result"}
COOKING_KEYS = {"type", "group", "category", "ingredient", "result", "experience", "cookingtime"}
RECIPE_KEYS = {
    "minecraft:crafting_shaped": (CRAFTING_SHAPED_KEYS, CRAFTING_CATEGORIES),
    "minecraft:crafting_shapeless": (CRAFTING_SHAPELESS_KEYS, CRAFTING_CATEGORIES),
    "minecraft:smelting": (COOKING_KEYS, COOKING_CATEGORIES),
    "minecraft:blasting": (COOKING_KEYS, COOKING_CATEGORIES),
    "minecraft:smoking": (COOKING_KEYS, COOKING_CATEGORIES),
    "minecraft:campfire_cooking": (COOKING_KEYS, COOKING_CATEGORIES),
}

DIMENSION_TYPE_KEYS = {
    "ambient_light",
    "bed_works",
    "coordinate_scale",
    "effects",
    "fixed_time",
    "has_ceiling",
    "has_raids",
    "has_skylight",
    "height",
    "infiniburn",
    "logical_height",
    "min_y",
    "monster_spawn_block_light_limit",
    "monster_spawn_light_level",
    "natural",
    "piglin_safe",
    "respawn_anchor_works",
    "ultrawarm",
}
DIMENSION_TYPE_REQUIRED = {
    "ambient_light",
    "bed_works",
    "coordinate_scale",
    "has_ceiling",
    "has_skylight",
    "height",
    "infiniburn",
    "logical_height",
    "min_y",
    "natural",
    "respawn_anchor_works",
    "ultrawarm",
    "has_raids",
    "piglin_safe",
    "monster_spawn_block_light_limit",
    "monster_spawn_light_level",
}
DIMENSION_TYPE_REMOVED = {
    "attributes": "26.x dimension attributes",
    "cardinal_light": "26.x cardinal light setting",
    "has_ender_dragon_fight": "26.x dragon fight flag",
    "has_fixed_time": "26.x fixed time flag; 1.21.1 uses fixed_time",
    "skybox": "26.x skybox setting; 1.21.1 uses effects",
}

BIOME_EFFECT_COLORS = ("fog_color", "water_color", "water_fog_color", "sky_color")
BIOME_KEYS = {
    "temperature",
    "downfall",
    "has_precipitation",
    "effects",
    "carvers",
    "features",
    "spawners",
    "spawn_costs",
}

TAG_KEYS = {"replace", "values"}

INGREDIENT_KEYS = {"item", "tag", "type"}


def read_json(path, errors):
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except (json.JSONDecodeError, OSError, UnicodeError) as error:
        errors.append(f"{path}: cannot read JSON ({error})")
        return None


def report_unknown_keys(path, payload, allowed, label, errors):
    for key in sorted(set(payload) - allowed):
        errors.append(f"{path}: unknown {label} key '{key}'")


def check_condition_list(path, payload, key, errors):
    conditions = payload.get(key)
    if conditions is None:
        return
    if not isinstance(conditions, list):
        errors.append(f"{path}: '{key}' must be a list in Minecraft 1.21.1")
        return
    for index, condition in enumerate(conditions):
        if not isinstance(condition, dict):
            errors.append(f"{path}: {key}[{index}] must be an object")
            continue
        if "condition" not in condition:
            errors.append(
                f"{path}: {key}[{index}] must use the 1.21.1 'condition' discriminator, "
                f"found {sorted(condition)}"
            )


def check_function_list(path, payload, errors):
    functions = payload.get("functions")
    if functions is None:
        return
    if not isinstance(functions, list):
        errors.append(f"{path}: 'functions' must be a list in Minecraft 1.21.1")
        return
    for index, function in enumerate(functions):
        if not isinstance(function, dict):
            errors.append(f"{path}: functions[{index}] must be an object")
            continue
        if "function" not in function:
            errors.append(
                f"{path}: functions[{index}] must use the 1.21.1 'function' discriminator, "
                f"found {sorted(function)}"
            )


def check_loot_entry(path, entry, errors):
    if not isinstance(entry, dict):
        errors.append(f"{path}: loot entry must be an object")
        return
    entry_type = entry.get("type")
    if entry_type not in LOOT_ENTRY_KEYS:
        errors.append(f"{path}: unknown loot entry type {entry_type!r}")
        return
    if entry_type in ("minecraft:alternatives", "minecraft:sequence", "minecraft:group"):
        allowed = LOOT_COMPOSITE_KEYS
    else:
        allowed = LOOT_ENTRY_COMMON_KEYS | LOOT_ENTRY_KEYS[entry_type]
    report_unknown_keys(path, entry, allowed, "loot entry", errors)
    if "condition" in entry:
        errors.append(
            f"{path}: loot entries use the 1.21.1 'conditions' list, "
            "'condition' is the 26.x spelling"
        )
    if "modifier" in entry:
        errors.append(
            f"{path}: loot entries use the 1.21.1 'functions' list, "
            "'modifier' is the 26.x spelling"
        )
    check_condition_list(path, entry, "conditions", errors)
    check_function_list(path, entry, errors)
    for child_index, child in enumerate(entry.get("children", []) or []):
        check_loot_entry(f"{path}.children[{child_index}]", child, errors)


def check_loot_table(path, payload, errors):
    report_unknown_keys(path, payload, LOOT_TABLE_KEYS, "loot table", errors)
    table_type = payload.get("type")
    if table_type not in LOOT_TABLE_TYPES:
        errors.append(f"{path}: unknown loot table type {table_type!r}")
    pools = payload.get("pools")
    if pools is None:
        return
    if not isinstance(pools, list):
        errors.append(f"{path}: 'pools' must be a list")
        return
    for pool_index, pool in enumerate(pools):
        pool_path = f"{path}.pools[{pool_index}]"
        if not isinstance(pool, dict):
            errors.append(f"{pool_path}: loot pool must be an object")
            continue
        report_unknown_keys(pool_path, pool, LOOT_POOL_KEYS, "loot pool", errors)
        if "condition" in pool:
            errors.append(
                f"{pool_path}: loot pools use the 1.21.1 'conditions' list, "
                "'condition' is the 26.x spelling"
            )
        check_condition_list(pool_path, pool, "conditions", errors)
        check_function_list(pool_path, pool, errors)
        entries = pool.get("entries")
        if entries is None:
            continue
        if not isinstance(entries, list):
            errors.append(f"{pool_path}: 'entries' must be a list")
            continue
        for entry_index, entry in enumerate(entries):
            check_loot_entry(f"{pool_path}.entries[{entry_index}]", entry, errors)


def check_ingredient(path, value, errors, allow_list=True):
    if isinstance(value, dict):
        if not set(value) & INGREDIENT_KEYS:
            errors.append(
                f"{path}: ingredient object must use 'item', 'tag' or 'type', found {sorted(value)}"
            )
        return
    if isinstance(value, list):
        if not allow_list:
            errors.append(f"{path}: ingredient list entries must be objects")
            return
        for index, element in enumerate(value):
            check_ingredient(f"{path}[{index}]", element, errors, allow_list=False)
        return
    if isinstance(value, str):
        errors.append(
            f"{path}: ingredient {value!r} must use the 1.21.1 object form "
            "({'item': ...} or {'tag': ...}); the bare string form is 26.x only"
        )
        return
    errors.append(f"{path}: ingredient must be an object or a list of objects")


def check_recipe(path, payload, errors):
    recipe_type = payload.get("type")
    if not isinstance(recipe_type, str):
        errors.append(f"{path}: recipe has no 'type'")
        return
    if recipe_type not in RECIPE_KEYS:
        return
    allowed, categories = RECIPE_KEYS[recipe_type]
    report_unknown_keys(path, payload, allowed, "recipe", errors)
    category = payload.get("category")
    if category is not None and category not in categories:
        errors.append(
            f"{path}: recipe category {category!r} is not valid for {recipe_type}, "
            f"expected one of {sorted(categories)}"
        )
    result = payload.get("result")
    if result is not None:
        if not isinstance(result, dict):
            errors.append(f"{path}: recipe result must be an object")
        elif "id" not in result:
            errors.append(
                f"{path}: recipe result must use the 1.21.1 'id' field, found {sorted(result)}"
            )
    ingredients = payload.get("ingredients")
    if ingredients is not None:
        if not isinstance(ingredients, list):
            errors.append(f"{path}: recipe 'ingredients' must be a list")
        else:
            for index, ingredient in enumerate(ingredients):
                check_ingredient(f"{path}.ingredients[{index}]", ingredient, errors)
    key = payload.get("key")
    if key is not None:
        if not isinstance(key, dict):
            errors.append(f"{path}: recipe 'key' must be an object")
        else:
            for symbol, ingredient in sorted(key.items()):
                check_ingredient(f"{path}.key[{symbol!r}]", ingredient, errors)
    ingredient = payload.get("ingredient")
    if ingredient is not None:
        check_ingredient(f"{path}.ingredient", ingredient, errors)


def check_dimension_type(path, payload, errors):
    report_unknown_keys(path, payload, DIMENSION_TYPE_KEYS, "dimension type", errors)
    for key in sorted(DIMENSION_TYPE_REQUIRED - set(payload)):
        errors.append(f"{path}: dimension type is missing required key '{key}'")
    for key, description in sorted(DIMENSION_TYPE_REMOVED.items()):
        if key in payload:
            errors.append(f"{path}: {description} ('{key}') does not exist in Minecraft 1.21.1")
    for key in ("ambient_light", "fixed_time", "height", "logical_height", "min_y",
                "monster_spawn_block_light_limit", "monster_spawn_light_level"):
        if key in payload and not isinstance(payload[key], (int, float)):
            errors.append(f"{path}: dimension type key '{key}' must be a number")
    for key in ("bed_works", "has_ceiling", "has_raids", "has_skylight", "natural",
                "piglin_safe", "respawn_anchor_works", "ultrawarm"):
        if key in payload and not isinstance(payload[key], bool):
            errors.append(f"{path}: dimension type key '{key}' must be a boolean")


def check_biome(path, payload, errors):
    report_unknown_keys(path, payload, BIOME_KEYS, "biome", errors)
    effects = payload.get("effects")
    if not isinstance(effects, dict):
        errors.append(f"{path}: biome is missing the 'effects' object")
        return
    for color in BIOME_EFFECT_COLORS:
        if color not in effects:
            errors.append(f"{path}: biome effects are missing '{color}'")
        elif not isinstance(effects[color], int):
            errors.append(f"{path}: biome effect '{color}' must be an integer colour")
    features = payload.get("features")
    if features is not None and not isinstance(features, list):
        errors.append(f"{path}: biome 'features' must be a list")


def check_tag(path, payload, errors):
    report_unknown_keys(path, payload, TAG_KEYS, "tag", errors)
    values = payload.get("values")
    if values is not None and not isinstance(values, list):
        errors.append(f"{path}: tag 'values' must be a list")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", default=str(DEFAULT_DATA))
    arguments = parser.parse_args()
    root = Path(arguments.root)
    errors = []
    files = 0

    if not root.is_dir():
        print(f"data: directory not found: {root}")
        return 1

    for path in sorted(root.rglob("*.json")):
        files += 1
        payload = read_json(path, errors)
        if payload is None:
            continue
        relative = path.relative_to(root).as_posix()
        parts = relative.split("/")
        registry = parts[1] if len(parts) > 2 else ""
        if registry == "loot_table":
            check_loot_table(path, payload, errors)
        elif registry == "recipe":
            check_recipe(path, payload, errors)
        elif registry == "dimension_type":
            check_dimension_type(path, payload, errors)
        elif registry == "worldgen":
            if len(parts) > 2 and parts[2] == "biome":
                check_biome(path, payload, errors)
        elif registry == "tags":
            check_tag(path, payload, errors)

    for error in errors:
        print(error)
    if errors:
        print(f"{len(errors)} data pack errors in {files} files")
        return 1
    print(f"data ok: {files} files checked")
    return 0


if __name__ == "__main__":
    sys.exit(main())
