"""Generates the TH block-golem textures (textures/model/golem_<material>.png).

Each material's texture is the canonical block texture of that material category
(GolemMaterialTH.fromBlock) tiled over the TC4R golem sheet, masked by the stock
golem template's alpha so only the model's UV regions are filled. The void
material reuses the designed 1.7.10 texture (same 128x128 sheet layout).

Usage: python tools/gen_golem_textures.py <path-to-minecraft-client.jar>
"""

import argparse
import shutil
import zipfile
from pathlib import Path

from PIL import Image, ImageChops

REPO = Path(__file__).resolve().parent.parent
TEMPLATE = REPO.parent / "thaumcraft-4-reforged" / "src/main/resources/assets/thaumcraft/textures/model/golem_straw.png"
OUT = REPO / "src/main/resources/assets/thaumichorizons/textures/model"
VOID_LEGACY = REPO / "reference/1.7.10/src/main/resources/assets/thaumichorizons/textures/models/golem_void.png"

# material id -> canonical vanilla block texture (GolemMaterialTH.fromBlock)
MATERIALS = {
    "grass": "grass_block_side",
    "dirt": "dirt",
    "wood": "oak_planks",
    "plant": "oak_leaves",
    "rock": "cobblestone",
    "metal": "iron_block",
    "cloth": "white_wool",
    "sand": "sand",
    "redstone": "redstone_block",
    "tnt": "tnt_side",
    "ice": "ice",
    "cactus": "cactus_side",
    "clay": "clay",
    "cake": "cake_side",
    "web": "cobweb",
}
# textures that ship untinted and expect a biome colormap
TINTS = {
    "oak_leaves": (0x48, 0xB5, 0x18),  # oak foliage
}


def tinted(tile: Image.Image, color: tuple) -> Image.Image:
    r, g, b, a = tile.split()
    return Image.merge("RGBA", (
        r.point(lambda v: v * color[0] // 255),
        g.point(lambda v: v * color[1] // 255),
        b.point(lambda v: v * color[2] // 255),
        a))


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("client_jar", type=Path, help="Minecraft client jar (assets/minecraft/textures/block)")
    args = parser.parse_args()

    template = Image.open(TEMPLATE).convert("RGBA")
    mask = template.split()[3]
    OUT.mkdir(parents=True, exist_ok=True)

    with zipfile.ZipFile(args.client_jar) as jar:
        for material, texture in MATERIALS.items():
            entry = f"assets/minecraft/textures/block/{texture}.png"
            try:
                with jar.open(entry) as handle:
                    tile = Image.open(handle).convert("RGBA")
            except KeyError as error:
                raise SystemExit(f"texture {entry} not found in {args.client_jar}") from error
            if texture in TINTS:
                tile = tinted(tile, TINTS[texture])

            sheet = Image.new("RGBA", template.size, (0, 0, 0, 0))
            for x in range(0, template.size[0], tile.size[0]):
                for y in range(0, template.size[1], tile.size[1]):
                    sheet.paste(tile, (x, y))
            # keep only the model's UV regions; block-texture holes (cobweb) stay translucent
            sheet.putalpha(ImageChops.multiply(sheet.split()[3], mask))

            target = OUT / f"golem_{material}.png"
            sheet.save(target)
            print(f"{target.name}: {tile.size[0]}x{tile.size[1]} tile from {texture}")

    shutil.copyfile(VOID_LEGACY, OUT / "golem_void.png")
    print("golem_void.png: reused the designed 1.7.10 texture")


if __name__ == "__main__":
    main()
