import argparse
import json
import sys
from pathlib import Path

DEFAULT_ASSETS = Path("src") / "main" / "resources" / "assets"
NAMESPACE = "thaumichorizons"
DIRECTORIES = ("blockstates", "models", "items")


def check_reference(assets, kind, reference, source, checked, errors):
    if reference.startswith("#"):
        return
    namespace, separator, path = reference.partition(":")
    if not separator:
        namespace, path = NAMESPACE, namespace
    if namespace != NAMESPACE or not path:
        return
    checked.append(reference)
    directory = "models" if kind == "model" else "textures"
    extension = ".json" if kind == "model" else ".png"
    target = assets / namespace / directory / (path + extension)
    if not target.is_file():
        errors.append(f"{source}: missing {kind} reference {reference} ({target.as_posix()})")


def walk(value, source, assets, checked, errors):
    if isinstance(value, dict):
        for key, child in value.items():
            if key in ("model", "parent") and isinstance(child, str):
                check_reference(assets, "model", child, source, checked, errors)
            elif key in ("texture", "particle") and isinstance(child, str):
                check_reference(assets, "texture", child, source, checked, errors)
            elif key == "textures" and isinstance(child, dict):
                for texture in child.values():
                    if isinstance(texture, str):
                        check_reference(assets, "texture", texture, source, checked, errors)
            else:
                walk(child, source, assets, checked, errors)
    elif isinstance(value, list):
        for child in value:
            walk(child, source, assets, checked, errors)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", default=str(DEFAULT_ASSETS))
    arguments = parser.parse_args()
    assets = Path(arguments.root)
    checked = []
    errors = []
    files = 0
    for directory in DIRECTORIES:
        base = assets / NAMESPACE / directory
        if not base.is_dir():
            continue
        for path in sorted(base.rglob("*.json")):
            files += 1
            try:
                data = json.loads(path.read_text(encoding="utf-8"))
            except (json.JSONDecodeError, OSError, UnicodeError) as error:
                errors.append(f"{path}: cannot read JSON ({error})")
                continue
            walk(data, path, assets, checked, errors)
    if not files or not checked:
        errors.append(f"no asset references found under {assets}")
    for error in errors:
        print(error)
    if errors:
        print(f"{len(errors)} asset reference errors")
        return 1
    print(f"assets ok: {len(checked)} references checked")
    return 0


if __name__ == "__main__":
    sys.exit(main())
