"""Generate static JSON block models from 1.7.10 ModelRenderer box specs.

Reads reference Model*.java, extracts addBox/setRotationPoint/texture offsets,
and emits Minecraft JSON block models with per-face UVs into the machine atlas.
Only static geometry is ported; animated TESR parts are excluded by name.
"""
import argparse
import json
import re
from pathlib import Path

REF = Path("reference/1.7.10/src/main/java/com/kentington/thaumichorizons/client/renderer/model")
OUT = Path("src/main/resources/assets/thaumichorizons/models/block")

BOX = re.compile(
    r"new ModelRenderer\(this,\s*(\d+),\s*(\d+)\)\)\.addBox\(\s*"
    r"([-\d.]+)f?,\s*([-\d.]+)f?,\s*([-\d.]+)f?,\s*(\d+),\s*(\d+),\s*(\d+)\)",
)
ROT = re.compile(r"this\.(\w+)\.setRotationPoint\(\s*([-\d.]+)f?,\s*([-\d.]+)f?,\s*([-\d.]+)f?\)")


def parse(model):
    text = (REF / f"{model}.java").read_text(encoding="utf-8")
    rot = {m.group(1): tuple(float(m.group(i)) for i in (2, 3, 4)) for m in ROT.finditer(text)}
    boxes = []
    # pair each ModelRenderer assignment with its variable name
    for m in re.finditer(r"this\.(\w+)\s*=\s*new ModelRenderer\(this,\s*(\d+),\s*(\d+)\)\)\.addBox\(\s*"
                         r"([-\d.]+)f?,\s*([-\d.]+)f?,\s*([-\d.]+)f?,\s*(\d+),\s*(\d+),\s*(\d+)\)", text):
        name = m.group(1)
        u, v = int(m.group(2)), int(m.group(3))
        x, y, z = (float(m.group(i)) for i in (4, 5, 6))
        w, h, d = (int(m.group(i)) for i in (7, 8, 9))
        rx, ry, rz = rot.get(name, (0.0, 0.0, 0.0))
        boxes.append(dict(name=name, u=u, v=v, x=x + rx, y=y + ry, z=z + rz, w=w, h=h, d=d))
    return boxes


def faces_uv(u, v, w, h, d, tw, th):
    sx, sy = 16.0 / tw, 16.0 / th

    def sc(x1, y1, x2, y2):
        return [round(x1 * sx, 4), round(y1 * sy, 4), round(x2 * sx, 4), round(y2 * sy, 4)]

    return {
        "up": sc(u + d, v, u + d + w, v + d),
        "down": sc(u + d + w, v, u + d + w + w, v + d),
        "north": sc(u + d, v + d, u + d + w, v + d + h),
        "south": sc(u + 2 * d + w, v + d, u + 2 * d + w + w, v + d + h),
        "east": sc(u, v + d, u + d, v + d + h),
        "west": sc(u + d + w, v + d, u + d + w + d, v + d + h),
    }


def element(box, tw, th, flip):
    x0, y0, z0 = box["x"], box["y"], box["z"]
    x1, y1, z1 = x0 + box["w"], y0 + box["h"], z0 + box["d"]
    if flip:
        fx0, fx1 = 8 + x0, 8 + x1
        fy0, fy1 = 24 - y1, 24 - y0
        fz0, fz1 = 8 - z1, 8 - z0
    else:
        fx0, fx1, fy0, fy1, fz0, fz1 = x0, x1, y0, y1, z0, z1
    uv = faces_uv(box["u"], box["v"], box["w"], box["h"], box["d"], tw, th)
    return {
        "from": [round(fx0, 3), round(fy0, 3), round(fz0, 3)],
        "to": [round(fx1, 3), round(fy1, 3), round(fz1, 3)],
        "faces": {k: {"uv": val, "texture": "#all"} for k, val in uv.items()},
    }


# machine -> (model class, texture, texW, texH, flip, excluded boxes)
SPECS = {
    "visdynamo": ("ModelQuarterBlock", "dynamobase", 64, 32, False, []),
    "essentiadynamo": ("ModelQuarterBlock", "dynamoessentiabase", 64, 32, False, []),
    "inspiratron": ("ModelInspiratron", "inspiratron", 64, 64, True, []),
    "soulforge": ("ModelSoulforge", "soulforge", 64, 64, True, ["Brine"]),
    "bloodinfuser": ("ModelBloodInfuser", "bloodinfuser", 64, 64, True, []),
    "soulbeacon": ("ModelSoulBeacon", "soulbeacon", 64, 64, True, []),
    "soulsieve": ("ModelSoulSieve", "soulsieve", 128, 64, True, []),
    "recombinator": ("ModelRecombinator", "recombinator", 64, 64, True, ["Pearl"]),
    "recombinator_item": ("ModelRecombinator", "recombinator", 64, 64, True, []),
    "vortexstabilizer": ("ModelVortexAttenuator", "attenuator", 64, 64, True, []),
}


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--only", nargs="*")
    args = ap.parse_args()
    for machine, (model, tex, tw, th, flip, exclude) in SPECS.items():
        if args.only and machine not in args.only:
            continue
        boxes = [b for b in parse(model) if b["name"] not in exclude]
        elements = [element(b, tw, th, flip) for b in boxes]
        doc = {
            "parent": "minecraft:block/block",
            "textures": {"all": f"thaumichorizons:block/{tex}", "particle": f"thaumichorizons:block/{tex}"},
            "elements": elements,
        }
        (OUT / f"{machine}.json").write_text(json.dumps(doc, indent=2) + "\n", encoding="utf-8")
        print(f"{machine}: {len(elements)} elements")


if __name__ == "__main__":
    main()