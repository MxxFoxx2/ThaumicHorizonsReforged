"""Convert 26.x data pack files to the Minecraft 1.21.1 formats.

The script is idempotent: running it on already converted files changes nothing.
"""
import argparse
import json
import sys
from pathlib import Path

DEFAULT_DATA = Path("src") / "main" / "resources" / "data"

DIMENSION_TYPE_REMOVED = (
    "attributes",
    "cardinal_light",
    "has_ender_dragon_fight",
    "has_fixed_time",
    "skybox",
)
DIMENSION_TYPE_DEFAULTS = {
    "has_raids": False,
    "piglin_safe": False,
    "effects": "minecraft:the_nether",
}
BIOME_EFFECT_DEFAULTS = {
    "fog_color": 12638463,
    "water_color": 4159204,
    "water_fog_color": 329011,
    "sky_color": 0,
}
BIOME_FEATURE_STEPS = 12


def write_json(path, payload):
    path.write_text(json.dumps(payload, indent=2) + "\n", encoding="utf-8")


def convert_condition(condition):
    if isinstance(condition, dict) and "type" in condition and "condition" not in condition:
        converted = dict(condition)
        converted["condition"] = converted.pop("type")
        return converted
    return condition


def convert_function(function):
    if isinstance(function, dict) and "type" in function and "function" not in function:
        converted = dict(function)
        converted["function"] = converted.pop("type")
        return converted
    return function


def convert_conditions(payload):
    conditions = payload.get("conditions")
    if isinstance(conditions, list):
        payload["conditions"] = [convert_condition(condition) for condition in conditions]
    elif isinstance(conditions, dict):
        payload["conditions"] = [convert_condition(conditions)]
    condition = payload.pop("condition", None)
    if condition is not None:
        payload.setdefault("conditions", []).append(convert_condition(condition))


def convert_functions(payload):
    functions = payload.get("functions")
    if isinstance(functions, list):
        payload["functions"] = [convert_function(function) for function in functions]
    modifier = payload.pop("modifier", None)
    if modifier is not None:
        payload.setdefault("functions", []).extend(
            convert_function(function) for function in modifier
        )


def convert_entry(entry):
    if not isinstance(entry, dict):
        return
    convert_conditions(entry)
    convert_functions(entry)
    for child in entry.get("children", []) or []:
        convert_entry(child)


def convert_loot_table(payload):
    convert_functions(payload)
    for pool in payload.get("pools", []) or []:
        if not isinstance(pool, dict):
            continue
        convert_conditions(pool)
        convert_functions(pool)
        for entry in pool.get("entries", []) or []:
            convert_entry(entry)


def convert_dimension_type(payload):
    for key in DIMENSION_TYPE_REMOVED:
        payload.pop(key, None)
    if "fixed_time" not in payload:
        payload["fixed_time"] = 18000
    for key, value in DIMENSION_TYPE_DEFAULTS.items():
        payload.setdefault(key, value)


def convert_biome(payload):
    effects = payload.setdefault("effects", {})
    for key, value in BIOME_EFFECT_DEFAULTS.items():
        effects.setdefault(key, value)
    features = payload.get("features")
    if isinstance(features, list):
        while len(features) < BIOME_FEATURE_STEPS:
            features.append([])


def convert_ingredient(ingredient):
    if isinstance(ingredient, str):
        return {"item": ingredient}
    if isinstance(ingredient, list):
        return [convert_ingredient(element) for element in ingredient]
    return ingredient


def convert_recipe(payload):
    ingredients = payload.get("ingredients")
    if isinstance(ingredients, list):
        payload["ingredients"] = [convert_ingredient(ingredient) for ingredient in ingredients]
    key = payload.get("key")
    if isinstance(key, dict):
        payload["key"] = {
            symbol: convert_ingredient(ingredient) for symbol, ingredient in key.items()
        }
    ingredient = payload.get("ingredient")
    if ingredient is not None:
        payload["ingredient"] = convert_ingredient(ingredient)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", default=str(DEFAULT_DATA))
    arguments = parser.parse_args()
    root = Path(arguments.root)
    changed = 0

    for path in sorted(root.rglob("*.json")):
        relative = path.relative_to(root).as_posix()
        parts = relative.split("/")
        registry = parts[1] if len(parts) > 2 else ""
        if registry not in ("loot_table", "dimension_type", "worldgen", "recipe"):
            continue
        payload = json.loads(path.read_text(encoding="utf-8"))
        before = json.dumps(payload, sort_keys=True)
        if registry == "loot_table":
            convert_loot_table(payload)
        elif registry == "dimension_type":
            convert_dimension_type(payload)
        elif registry == "recipe":
            convert_recipe(payload)
        elif len(parts) > 2 and parts[2] == "biome":
            convert_biome(payload)
        after = json.dumps(payload, sort_keys=True)
        if before != after:
            write_json(path, payload)
            changed += 1
            print(f"converted {relative}")

    print(f"data pack conversion done: {changed} file(s) changed")
    return 0


if __name__ == "__main__":
    sys.exit(main())
