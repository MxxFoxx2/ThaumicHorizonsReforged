import sys
from PIL import Image

SRC_DIR = sys.argv[1]
OUT_DIR = sys.argv[2]
NAMES = sys.argv[3:]

def faces(u, v, w, h, d):
    return {
        'top':    (u + d, v, w, d),
        'bottom': (u + d + w, v, w, d),
        'right':  (u, v + d, d, h),
        'front':  (u + d, v + d, w, h),
        'left':   (u + d + w, v + d, d, h),
        'back':   (u + d + w + d, v + d, w, h),
    }

def crop(img, rect):
    x, y, w, h = rect
    return img.crop((x, y, x + w, y + h))

def put(img, rect, piece):
    x, y, w, h = rect
    if piece.size != (w, h):
        piece = piece.resize((w, h), Image.NEAREST)
    img.paste(piece, (x, y))

def simple(img, out, legacy, modern, solidify=False):
    lf, mf = faces(*legacy), faces(*modern)
    for name in mf:
        piece = crop(img, lf[name])
        if solidify:
            piece = solid(piece)
        put(out, mf[name], piece)

def solid(piece):
    # legacy tail art is a thin plume with transparent margins; the modern tail is a
    # solid box, so carry those margins as opaque base colour instead of holes
    bg = Image.new('RGBA', piece.size, (0, 0, 0, 255))
    bg.alpha_composite(piece)
    return bg

def stack_sides(img, out, segs, modern, heights, solidify=False):
    # segs: list of legacy (u,v,w,h,d) top->bottom; heights: target side heights
    mf = faces(*modern)
    for name in ('right', 'front', 'left', 'back'):
        x, y, w, h = mf[name]
        col = Image.new('RGBA', (w, h), (0, 0, 0, 0))
        cy = 0
        for seg, sh in zip(segs, heights):
            piece = crop(img, faces(*seg)[name]).resize((w, sh), Image.NEAREST)
            col.paste(piece, (0, cy))
            cy += sh
        if solidify:
            col = solid(col)
        put(out, mf[name], col)
    top = crop(img, faces(*segs[0])['top'])
    bottom = crop(img, faces(*segs[-1])['bottom'])
    if solidify:
        top, bottom = solid(top), solid(bottom)
    put(out, mf['top'], top)
    put(out, mf['bottom'], bottom)

# legacy 1.7.10 ModelHorse (128x128)
L_BODY = (0, 34, 10, 10, 24)
L_HEAD = (0, 0, 5, 5, 7)
L_NECK = (0, 12, 4, 14, 8)
L_MANE = (58, 0, 2, 16, 4)
L_MOUTH = (24, 18, 4, 3, 6)
L_TAIL = [(24, 3, 3, 4, 7), (38, 7, 3, 4, 7), (44, 0, 2, 2, 3)]
L_LEG = [(96, 29, 4, 9, 5), (96, 43, 3, 5, 3), (96, 51, 4, 3, 4)]
L_EAR_BLOCK = (0, 0, 6, 4)

# modern 26.3 AbstractEquineModel (64x64)
M_BODY = (0, 32, 10, 10, 22)
M_HEAD = (0, 13, 6, 5, 7)
M_NECK = (0, 35, 4, 12, 7)
M_MANE = (56, 36, 2, 16, 2)
M_MOUTH = (0, 25, 4, 5, 5)
M_TAIL = (42, 36, 3, 14, 4)
M_LEG = (48, 21, 4, 11, 4)
M_EAR = (19, 16, 2, 3, 1)

# modern BabyHorseModel (64x64, separate file)
B_BODY = (0, 13, 8, 7, 14)
B_TAIL = (24, 34, 3, 3, 8)
B_LEGS = [(0, 34, 3, 9, 3), (12, 34, 3, 9, 3), (0, 46, 3, 9, 3), (12, 46, 3, 9, 3)]
B_NECK = (30, 0, 4, 8, 4)
B_HEAD = (0, 0, 6, 4, 9)
B_EARS = [(0, 4, 2, 3, 1), (0, 0, 2, 3, 1)]

for name in NAMES:
    img = Image.open(f'{SRC_DIR}/{name}.png').convert('RGBA')
    assert img.size == (128, 128), (name, img.size)

    adult = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
    simple(img, adult, L_BODY, M_BODY)
    simple(img, adult, L_HEAD, M_HEAD)
    simple(img, adult, L_NECK, M_NECK)
    simple(img, adult, L_MANE, M_MANE)
    simple(img, adult, L_MOUTH, M_MOUTH)
    stack_sides(img, adult, L_TAIL, M_TAIL, [6, 6, 2], solidify=True)
    stack_sides(img, adult, L_LEG, M_LEG, [6, 2, 3])
    put(adult, (19, 16, 6, 4), crop(img, L_EAR_BLOCK))
    adult.save(f'{OUT_DIR}/{name}.png')

    baby = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
    simple(img, baby, L_BODY, B_BODY)
    simple(img, baby, L_NECK, B_NECK)
    simple(img, baby, L_HEAD, B_HEAD)
    simple(img, baby, L_TAIL[0], B_TAIL, solidify=True)
    for bl in B_LEGS:
        stack_sides(img, baby, [L_LEG[0], L_LEG[2]], bl, [6, 3])
    # baby left ear shares the head-right-face pixels (vanilla semantics); only the
    # right ear at (0,0) owns a private block copied from the legacy ear region
    put(baby, (0, 0, 6, 4), crop(img, L_EAR_BLOCK))
    baby.save(f'{OUT_DIR}/{name}_baby.png')
    print('remapped', name)