"""Convert 26.x item definitions into 1.21.1 models/item entries.

Minecraft 1.21.1 has no ``assets/<namespace>/items`` directory: that format
arrived with the 26.x item model rework. On 1.21.1 every item is rendered from
``assets/<namespace>/models/item/<item_id>.json``.

The conversion rules are:

* ``minecraft:model`` definitions become a model whose parent is the referenced
  model. The referenced model must not be the item itself.
* Definitions that used a custom special renderer (``minecraft:special``,
  ``thaumcraft:special_owner``) keep the hand written fallback model in
  ``models/item``: on 1.21.1 those meshes are drawn by the
  ``BlockEntityWithoutLevelRenderer`` registered in
  ``client/render/item/ThItemRenderers.java``.

Usage: python -I tools/backport/item_models.py [assets_dir]
"""

import json
import sys
from pathlib import Path

NAMESPACE = "thaumichorizons"
MODEL_TYPES = {"minecraft:model"}


def referenced_model(definition):
    model = definition.get("model")
    if not isinstance(model, dict) or model.get("type") not in MODEL_TYPES:
        return None
    reference = model.get("model")
    return reference if isinstance(reference, str) else None


def main():
    root = Path(sys.argv[1] if len(sys.argv) > 1 else "src/main/resources/assets")
    assets = root / NAMESPACE
    definitions = assets / "items"
    models = assets / "models/item"
    if not definitions.is_dir():
        print(f"no 26.x item definitions found at {definitions}")
        return 0

    written = 0
    skipped = 0
    for path in sorted(definitions.glob("*.json")):
        name = path.stem
        target = models / f"{name}.json"
        if target.is_file():
            skipped += 1
            continue
        definition = json.loads(path.read_text(encoding="utf-8"))
        reference = referenced_model(definition)
        if reference is None:
            print(f"{path.name}: no plain model reference, needs a hand written fallback model")
            continue
        namespace, separator, location = reference.partition(":")
        if not separator:
            namespace, location = NAMESPACE, reference
        if namespace == NAMESPACE and location == f"item/{name}":
            print(f"{path.name}: model references itself, needs a hand written fallback model")
            continue
        target.write_text(json.dumps({"parent": reference}, indent=2) + "\n", encoding="utf-8")
        written += 1
        print(f"wrote {target.as_posix()} (parent {reference})")

    print(f"item models: {written} written, {skipped} already present")
    return 0


if __name__ == "__main__":
    sys.exit(main())
