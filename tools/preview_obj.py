"""Offline preview of an OBJ model textured with a PNG atlas.

Renders the OBJ triangles with a flat-shaded painter's algorithm so the UV
layout can be checked without launching the game.
"""
import argparse
import struct
import sys


def load_png(path):
    import zlib

    data = open(path, "rb").read()
    assert data[:8] == b"\x89PNG\r\n\x1a\n", "not a png"
    pos = 8
    idat = b""
    width = height = 0
    bitdepth = colortype = 0
    palette = b""
    trns = b""
    while pos < len(data):
        length = struct.unpack(">I", data[pos:pos + 4])[0]
        ctype = data[pos + 4:pos + 8]
        chunk = data[pos + 8:pos + 8 + length]
        if ctype == b"IHDR":
            width, height, bitdepth, colortype = struct.unpack(">IIBB", chunk[:10])
        elif ctype == b"PLTE":
            palette = chunk
        elif ctype == b"tRNS":
            trns = chunk
        elif ctype == b"IDAT":
            idat += chunk
        elif ctype == b"IEND":
            break
        pos += 12 + length
    raw = zlib.decompress(idat)
    channels = {0: 1, 2: 3, 3: 1, 4: 2, 6: 4}[colortype]
    bpp = channels * bitdepth // 8
    stride = (width * channels * bitdepth + 7) // 8
    out = bytearray(height * stride)
    prev = bytearray(stride)
    pos = 0
    for y in range(height):
        f = raw[pos]
        pos += 1
        line = bytearray(raw[pos:pos + stride])
        pos += stride
        for i in range(stride):
            a = line[i - bpp] if i >= bpp else 0
            b = prev[i]
            c = prev[i - bpp] if i >= bpp else 0
            if f == 1:
                line[i] = (line[i] + a) & 255
            elif f == 2:
                line[i] = (line[i] + b) & 255
            elif f == 3:
                line[i] = (line[i] + (a + b) // 2) & 255
            elif f == 4:
                p = a + b - c
                pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                pr = a if (pa <= pb and pa <= pc) else (b if pb <= pc else c)
                line[i] = (line[i] + pr) & 255
        out[y * stride:(y + 1) * stride] = line
        prev = line
    pixels = []
    for y in range(height):
        row = []
        for x in range(width):
            if colortype == 6:
                row.append(tuple(out[y * stride + x * 4:y * stride + x * 4 + 4]))
            elif colortype == 2:
                row.append(tuple(out[y * stride + x * 3:y * stride + x * 3 + 3]) + (255,))
            elif colortype == 3:
                idx = out[y * stride + x]
                row.append((palette[idx * 3], palette[idx * 3 + 1], palette[idx * 3 + 2], 255))
            else:
                v = out[y * stride + x]
                row.append((v, v, v, 255))
        pixels.append(row)
    return width, height, pixels


def load_obj(path):
    verts, uvs, faces = [], [], []
    for line in open(path, encoding="utf-8", errors="ignore"):
        t = line.split()
        if not t:
            continue
        if t[0] == "v":
            verts.append(tuple(float(v) for v in t[1:4]))
        elif t[0] == "vt":
            uvs.append(tuple(float(v) for v in t[1:3]))
        elif t[0] == "f":
            faces.append(t[1:])
    return verts, uvs, faces


def render(obj_path, png_path, out_path, size=600):
    from PIL import Image

    verts, uvs, faces = load_obj(obj_path)
    tw, th, pixels = load_png(png_path)
    img = Image.new("RGBA", (size, size), (255, 0, 255, 255))
    tris = []
    for f in faces:
        pts = []
        for tok in f:
            p = tok.split("/")
            vi = int(p[0]) - 1
            ti = int(p[1]) - 1 if len(p) > 1 and p[1] else None
            pts.append((verts[vi], uvs[ti] if ti is not None else (0.0, 0.0)))
        for i in range(1, len(pts) - 1):
            tris.append((pts[0], pts[i], pts[i + 1]))
    minx = min(t[0][0][0] for t in tris)
    maxx = max(t[0][0][0] for t in tris)
    minz = min(t[0][0][2] for t in tris)
    maxz = max(t[0][0][2] for t in tris)
    span = max(maxx - minx, maxz - minz) or 1.0
    scale = size * 0.8 / span
    cx = (minx + maxx) / 2
    cz = (minz + maxz) / 2
    depth = [(a[0][1] + b[0][1] + c[0][1]) / 3 for a, b, c in tris]
    order = sorted(range(len(tris)), key=lambda i: depth[i])
    for i in order:
        a, b, c = tris[i]
        pts = []
        for v, uv in (a, b, c):
            x = (v[0] - cx) * scale + size / 2
            y = (v[2] - cz) * scale + size / 2
            px = int(uv[0] * tw)
            py = int((1.0 - uv[1]) * th)
            px = max(0, min(tw - 1, px))
            py = max(0, min(th - 1, py))
            pts.append((x, y, pixels[py][px]))
        minx2 = min(p[0] for p in pts)
        maxx2 = max(p[0] for p in pts)
        miny2 = min(p[1] for p in pts)
        maxy2 = max(p[1] for p in pts)
        area = (maxx2 - minx2) * (maxy2 - miny2)
        if area < 1:
            continue
        buf = Image.new("RGBA", (int(maxx2 - minx2) + 1, int(maxy2 - miny2) + 1), (0, 0, 0, 0))
        bp = buf.load()
        for yy in range(buf.height):
            for xx in range(buf.width):
                u = (xx + minx2 - minx2) / (maxx2 - minx2)
                v = (yy + miny2 - miny2) / (maxy2 - miny2)
                if _bary(pts, minx2 + xx, miny2 + yy) is None:
                    continue
                w0, w1, w2 = _bary(pts, minx2 + xx, miny2 + yy)
                weights = (w0, w1, w2)
                r = sum(p[2][0] * w for p, w in zip(pts, weights))
                g = sum(p[2][1] * w for p, w in zip(pts, weights))
                bl = sum(p[2][2] * w for p, w in zip(pts, weights))
                a = sum(p[2][3] * w for p, w in zip(pts, weights))
                if a > 16:
                    bp[xx, yy] = (int(r), int(g), int(bl), int(a))
        img.alpha_composite(buf, (int(minx2), int(miny2)))
    img.save(out_path)
    print(f"{out_path}: {len(tris)} triangles")


def _bary(pts, x, y):
    (x0, y0, _), (x1, y1, _), (x2, y2, _) = pts
    d = (y1 - y2) * (x0 - x2) + (x2 - x1) * (y0 - y2)
    if abs(d) < 1e-9:
        return None
    l0 = ((y1 - y2) * (x - x2) + (x2 - x1) * (y - y2)) / d
    l1 = ((y2 - y0) * (x - x2) + (x0 - x2) * (y - y2)) / d
    l2 = 1.0 - l0 - l1
    if l0 < 0 or l1 < 0 or l2 < 0:
        return None
    return l0, l1, l2


if __name__ == "__main__":
    ap = argparse.ArgumentParser()
    ap.add_argument("obj")
    ap.add_argument("png")
    ap.add_argument("out")
    ap.add_argument("--size", type=int, default=600)
    args = ap.parse_args()
    render(args.obj, args.png, args.out, args.size)
