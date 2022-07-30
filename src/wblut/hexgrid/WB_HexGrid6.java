package wblut.hexgrid;

import java.util.ArrayList;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.isogrid.WB_IsoGridLine;
import wblut.isogrid.WB_IsoGridSegment;
import wblut.isogrid.WB_IsoHexGridData6;
import wblut.isogrid.WB_IsoGridLine.HexLineSort;

public class WB_HexGrid6 extends WB_HexGrid implements WB_IsoHexGridData6 {

	public WB_HexGrid6() {
		super();
		offsets = new double[14];
		for (int i = 0; i < 7; i++) {
			offsets[2 * i] = vertexOffsets[2 * i] * s60;
			offsets[2 * i + 1] = (vertexOffsets[2 * i + 1] - vertexOffsets[2 * i] * c60);
		}

	}

	public void setTriangle(int q, int r, int t, int colors, int palette, int part, int region, int z) {
		int layer = q + r;
		layer = layer - 3 * ((layer + 1) / 3);
		while (layer > 1) {
			layer -= 3;
		}

		while (layer < -1) {
			layer += 3;
		}

		int ns;
		switch (layer) {

		case 0:
			addTriangleRaw(q, r, t, colors, palette, part, region, z);
			break;

		case +1:

			ns = mapTrianglesFromPosOne[t];
			addTriangleRaw(q + mapQFromPosOne[t], r + mapRFromPosOne[t], ns, colors, palette, part, region, z);

			break;
		case -1:

			ns = mapTrianglesFromNegOne[t];
			addTriangleRaw(q + mapQFromNegOne[t], r + mapRFromNegOne[t], ns, colors, palette, part, region, z);

			break;

		default:

		}

	}

	public void setTriangle(int q, int r, int t, int colors, int palette, int part, int region, int z, double[] UV) {
		int layer = q + r;
		layer = layer - 3 * ((layer + 1) / 3);
		while (layer > 1) {
			layer -= 3;
		}

		while (layer < -1) {
			layer += 3;
		}

		int ns;
		switch (layer) {

		case 0:
			addTriangleRaw(q, r, t, colors, palette, part, region, z, UV);

			break;

		case +1:

			ns = mapTrianglesFromPosOne[t];

			addTriangleRaw(q + mapQFromPosOne[t], r + mapRFromPosOne[t], ns, colors, palette, part, region, z, UV);

			break;
		case -1:

			ns = mapTrianglesFromNegOne[t];
			addTriangleRaw(q + mapQFromNegOne[t], r + mapRFromNegOne[t], ns, colors, palette, part, region, z, UV);

			break;

		default:

		}

	}

	void addTriangleRaw(int q, int r, int t, int colors, int palette, int part, int region, int z) {
		if (t >= 0 && t < 6) {
			long key = getCellKey(q, r);
			WB_HexGridCell cell = cells.get(key);

			if (cell == null) {
				cell = new WB_HexGridCell(q, r, 6);
				cells.put(key, cell);

			}
			cell.triangleColor[t] = colors;
			cell.triangleColorSourceIndex[t] = palette;
			cell.part[t] = part;
			cell.region[t] = region;
			cell.z[t] = z;
			cell.occupied[t] = true;
		}

	}

	void addTriangleRaw(int q, int r, int t, int colors, int palette, int part, int region, int z, double[] UV) {
		if (t >= 0 && t < 6) {
			long key = getCellKey(q, r);
			WB_HexGridCell cell = cells.get(key);

			if (cell == null) {
				cell = new WB_HexGridCell(q, r, 6);
				cells.put(key, cell);

			}
			cell.triangleColor[t] = colors;
			cell.triangleColorSourceIndex[t] = palette;
			cell.part[t] = part;
			cell.region[t] = region;
			cell.z[t] = z;
			for (int c = 0; c < 6; c++) {
				cell.triangleUV[t][c] = UV[c];
			}
			cell.occupied[t] = true;
		}

	}

	public void clearTriangle(int q, int r, int t) {
		int layer = q + r;
		layer = layer - 3 * ((layer + 1) / 3);
		while (layer > 1) {
			layer -= 3;
		}

		while (layer < -1) {
			layer += 3;
		}

		int ns;
		switch (layer) {

		case 0:

			clearTriangleRaw(q, r, t);

			break;
		case +1:

			ns = mapTrianglesFromPosOne[t];

			clearTriangleRaw(q + mapQFromPosOne[t], r + mapRFromPosOne[t], ns);

			break;
		case -1:

			ns = mapTrianglesFromNegOne[t];

			clearTriangleRaw(q + mapQFromNegOne[t], r + mapRFromNegOne[t], ns);

		default:

		}

	}

	void clearTriangleRaw(int q, int r, int t) {
		if (t >= 0 && t < 6) {
			long key = getCellKey(q, r);
			WB_HexGridCell cell = cells.get(key);
			if (cell != null) {
				cell.clear(t);
			}

		}

	}

	private WB_HexGridCell getNeighbor(int q, int r, int i) {
		return cells.get(getCellKey(q + interHexNeighborQ[i], r + interHexNeighborR[i]));
	}

	public void collectLines() {

		outlinesMap.clear();
		collectInterHexSegmentsOutline(outlinesMap);
		collectInterTriangleSegmentsOutline(outlinesMap);

		outlines = new ArrayList<WB_IsoGridLine>();
		outlines.addAll(outlinesMap.values());
		outlines.sort(new WB_IsoGridLine.HexLineSort());
		int i = 0;
		for (WB_IsoGridLine line : outlines) {
			line.sort();
			line.optimize();
			if (i % 2 == 0)
				line.reverse();
			i++;
		}

		linesMap.clear();
		collectInterHexSegmentsInterior(linesMap);
		collectInterTriangleSegmentsInterior(linesMap);

		lines = new ArrayList<WB_IsoGridLine>();
		lines.addAll(linesMap.values());
		lines.sort(new WB_IsoGridLine.HexLineSort());
		i = 0;
		for (WB_IsoGridLine line : lines) {
			line.sort();
			line.optimize();
			if (i % 2 == 0)
				line.reverse();
			i++;
		}
		
		gridlinesMap.clear();
		collectInterHexSegmentsGrid(gridlinesMap);
		collectInterTriangleSegmentsGrid(gridlinesMap);

		gridlines = new ArrayList<WB_IsoGridLine>();
		gridlines.addAll(gridlinesMap.values());
		gridlines.sort(new WB_IsoGridLine.HexLineSort());
		i = 0;
		for (WB_IsoGridLine line : gridlines) {
			line.sort();
			line.optimize();
			if (i % 2 == 0)
				line.reverse();
			i++;
		}
	}
	
	private void collectInterHexSegmentsGrid(Map<Long, WB_IsoGridLine> linesMap) {
		WB_HexGridCell neighbor;
		int z, region, part, palette;
		long hash, nhash;
		for (WB_HexGridCell cell : cells.values()) {
			hash = getCellKey(cell.getQ(), cell.getR());
			for (int i = 0; i < 6; i++) {
				if (cell.getPart(i) >= 0) {

					neighbor = getNeighbor(cell.getQ(), cell.getR(), i);
					if (neighbor == null) {

					} else {
						nhash = getCellKey(cell.getQ() + interHexNeighborQ[i], cell.getR() + interHexNeighborR[i]);
						region = neighbor.getRegion(interHexNeighbor[i]);
						part = neighbor.getPart(interHexNeighbor[i]);
						palette = neighbor.getColorSourceIndex(interHexNeighbor[i]);
						if (cell.getPart(i) == part && (nhash < hash || region == -1)) {

							z = neighbor.getZ(interHexNeighbor[i]);

							if (z!=cell.getZ(i)&&!areSeparate(region, cell.getRegion(i), cell.getZ(i), z, palette, cell.getColorSourceIndex(i))) {
								addSegment(cell.getQ(), cell.getR(), interHexSegment[2 * i], interHexSegment[2 * i + 1],
										linesMap);
							}
						}

					}

				}

			}

		}
	}

	private void collectInterHexSegmentsInterior(Map<Long, WB_IsoGridLine> linesMap) {
		WB_HexGridCell neighbor;
		int z, region, part, palette;
		long hash, nhash;
		for (WB_HexGridCell cell : cells.values()) {
			hash = getCellKey(cell.getQ(), cell.getR());
			for (int i = 0; i < 6; i++) {
				if (cell.getPart(i) >= 0) {

					neighbor = getNeighbor(cell.getQ(), cell.getR(), i);
					if (neighbor == null) {

					} else {
						nhash = getCellKey(cell.getQ() + interHexNeighborQ[i], cell.getR() + interHexNeighborR[i]);
						region = neighbor.getRegion(interHexNeighbor[i]);
						part = neighbor.getPart(interHexNeighbor[i]);
						palette = neighbor.getColorSourceIndex(interHexNeighbor[i]);
						if (cell.getPart(i) == part && (nhash < hash || region == -1)) {

							z = neighbor.getZ(interHexNeighbor[i]);

							if (areSeparate(region, cell.getRegion(i), cell.getZ(i), z, palette, cell.getColorSourceIndex(i))) {
								addSegment(cell.getQ(), cell.getR(), interHexSegment[2 * i], interHexSegment[2 * i + 1],
										linesMap);
							}
						}

					}

				}

			}

		}
	}

	private void collectInterHexSegmentsOutline(Map<Long, WB_IsoGridLine> outlinesMap) {
		WB_HexGridCell neighbor;
		int region, part;
		long hash, nhash;
		for (WB_HexGridCell cell : cells.values()) {
			hash = getCellKey(cell.getQ(), cell.getR());
			for (int i = 0; i < 6; i++) {
				if (cell.getRegion(i) >= 0) {
					neighbor = getNeighbor(cell.getQ(), cell.getR(), i);
					if (neighbor == null) {
						addSegment(cell.getQ(), cell.getR(), interHexSegment[2 * i], interHexSegment[2 * i + 1],
								outlinesMap);
					} else {
						nhash = getCellKey(cell.getQ() + interHexNeighborQ[i], cell.getR() + interHexNeighborR[i]);
						region = neighbor.getRegion(interHexNeighbor[i]);
						if (nhash < hash || region == -1) {
							part = neighbor.getPart(interHexNeighbor[i]);
							if (cell.getPart(i) != part) {
								addSegment(cell.getQ(), cell.getR(), interHexSegment[2 * i], interHexSegment[2 * i + 1],
										outlinesMap);
							}
						}

					}
				}
			}

		}
	}
	
	private void collectInterTriangleSegmentsGrid(Map<Long, WB_IsoGridLine> linesMap) {
		int z, region, part, palette;
		for (WB_HexGridCell cell : cells.values()) {
			for (int i = 0; i < 6; i++) {

				region = cell.getRegion(interTriangleNeighbor[i]);
				z = cell.getZ(interTriangleNeighbor[i]);

				part = cell.getPart(interTriangleNeighbor[i]);
				palette = cell.getColorSourceIndex(interTriangleNeighbor[i]);
				if (cell.getPart(i) != part) {

				} else if (z!=cell.getZ(i)&&!areSeparate(region, cell.getRegion(i), cell.getZ(i), z, palette, cell.getColorSourceIndex(i))) {
					addSegment(cell.getQ(), cell.getR(), interTriangleSegment[2 * i], interTriangleSegment[2 * i + 1],
							linesMap);
				}

			}

		}
	}

	private void collectInterTriangleSegmentsInterior(Map<Long, WB_IsoGridLine> linesMap) {
		int z, region, part, palette;
		for (WB_HexGridCell cell : cells.values()) {
			for (int i = 0; i < 6; i++) {

				region = cell.getRegion(interTriangleNeighbor[i]);
				z = cell.getZ(interTriangleNeighbor[i]);

				part = cell.getPart(interTriangleNeighbor[i]);
				palette = cell.getColorSourceIndex(interTriangleNeighbor[i]);
				if (cell.getPart(i) != part) {

				} else if (areSeparate(region, cell.getRegion(i), cell.getZ(i), z, palette, cell.getColorSourceIndex(i))) {
					addSegment(cell.getQ(), cell.getR(), interTriangleSegment[2 * i], interTriangleSegment[2 * i + 1],
							linesMap);
				}

			}

		}
	}

	private void collectInterTriangleSegmentsOutline(Map<Long, WB_IsoGridLine> outlinesMap) {
		int part;
		for (WB_HexGridCell cell : cells.values()) {
			for (int i = 0; i < 6; i++) {
				part = cell.getPart(interTriangleNeighbor[i]);
				if (cell.getPart(i) != part) {
					addSegment(cell.getQ(), cell.getR(), interTriangleSegment[2 * i], interTriangleSegment[2 * i + 1],
							outlinesMap);
				}
			}

		}
	}

	private void addSegment(double q, double r, int i1, int i2, Map<Long, WB_IsoGridLine> linesMap) {
		WB_IsoGridSegment segment = new WB_IsoGridSegment((int) Math.round(6 * (q + vertexOffsets[2 * i1])),
				(int) Math.round(6 * (r + vertexOffsets[2 * i1 + 1])),
				(int) Math.round(6 * (q + vertexOffsets[2 * i2])),
				(int) Math.round(6 * (r + vertexOffsets[2 * i2 + 1])));
		long key = segment.getHash();
		WB_IsoGridLine line = linesMap.get(key);
		if (line == null) {
			line = new WB_IsoGridLine(segment.getType(), segment.getLineValue());
			linesMap.put(key, line);
		}
		line.add(segment);

	}

	boolean areSeparate(int region1, int region2, int z1, int z2, int palette1, int palette2) {
		return palette1 != palette2 || region1 != region2 || Math.abs(z1 - z2) > 1;
	}

	public void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy) {
		vertex(pg, ox + offsets[2 * triangleVertices[t][i]] * sx, oy + offsets[2 * triangleVertices[t][i] + 1] * sy);

	}

	public void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy, double u, double v) {
		vertex(pg, ox + offsets[2 * triangleVertices[t][i]] * sx, oy + offsets[2 * triangleVertices[t][i] + 1] * sy, u,
				v);

	}

	public void line(PGraphics pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx,
			double sy) {
		pg.line((float) (q1 / 6.0 * s60 * sx + ox), (float) ((r1 - q1 * c60) / 6.0 * sy + oy),
				(float) ((q2 / 6.0 * s60 * sx) + ox), (float) ((r2 - q2 * c60) / 6.0 * sy + oy));

	}

	public void point(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy) {
		pg.point((float) (q / 6.0 * s60 * sx + ox), (float) ((r - q * c60) / 6.0 * sy + oy));

	}

	public void point(PApplet pg, double q, double r, double ox, double oy, double sx, double sy) {
		pg.point((float) (q / 6.0 * s60 * sx + ox), (float) ((r - q * c60) / 6.0 * sy + oy));

	}

	public void circle(PApplet pg, double q, double r, double ox, double oy, double sx, double sy, double diameter) {
		pg.ellipse((float) (q / 6.0 * s60 * sx + ox), (float) ((r - q * c60) / 6.0 * sy + oy), (float) diameter,(float)diameter);

	}

	public void circle(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy, double diameter) {
		pg.ellipse((float) (q / 6.0 * s60 * sx + ox), (float) ((r - q * c60) / 6.0 * sy + oy), (float) diameter,(float)diameter);

	}

	public void line(PApplet pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx,
			double sy) {
		pg.line((float) (q1 / 6.0 * s60 * sx + ox), (float) ((r1 - q1 * c60) / 6.0 * sy + oy),
				(float) ((q2 / 6.0 * s60 * sx) + ox), (float) ((r2 - q2 * c60) / 6.0 * sy + oy));

	}

	public void clippedLine(PGraphics pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx,
			double sy, double xmin, double ymin, double xmax, double ymax) {
		double x0 = q1 / 6.0 * s60 * sx + ox;
		double y0 = (r1 - q1 * c60) / 6.0 * sy + oy;
		double x1 = (q2 / 6.0 * s60 * sx) + ox;
		double y1 = (r2 - q2 * c60) / 6.0 * sy + oy;

		int outcode0 = computeOutCode(x0, y0, xmin, ymin, xmax, ymax);
		int outcode1 = computeOutCode(x1, y1, xmin, ymin, xmax, ymax);
		boolean accept = false;

		while (true) {
			if ((outcode0 | outcode1) == 0) {
				accept = true;
				break;
			} else if ((outcode0 & outcode1) > 0) {
				break;
			} else {
				double x, y;
				x = 0.0;
				y = 0.0;

				int outcodeOut = outcode1 > outcode0 ? outcode1 : outcode0;

				if ((outcodeOut & TOP) > 0) {
					x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0);
					y = ymax;
				} else if ((outcodeOut & BOTTOM) > 0) {
					x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0);
					y = ymin;
				} else if ((outcodeOut & RIGHT) > 0) {
					y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0);
					x = xmax;
				} else if ((outcodeOut & LEFT) > 0) {
					y = y0 + (y1 - y0) * (xmin - x0) / (x1 - x0);
					x = xmin;
				}

				if (outcodeOut == outcode0) {
					x0 = x;
					y0 = y;
					outcode0 = computeOutCode(x0, y0, xmin, ymin, xmax, ymax);
				} else {
					x1 = x;
					y1 = y;
					outcode1 = computeOutCode(x1, y1, xmin, ymin, xmax, ymax);
				}
			}
		}
		if (accept) {
			pg.line((float) x0, (float) y0, (float) x1, (float) y1);

		}

	}

	public void clippedLine(PApplet home, double q1, double r1, double q2, double r2, double ox, double oy, double sx,
			double sy, double xmin, double ymin, double xmax, double ymax) {
		clippedLine(home.g, q1, r1, q2, r2, ox, oy, sx, sy, xmin, ymin, xmax, ymax);

	}

	int INSIDE = 0; // 0000
	int LEFT = 1; // 0001
	int RIGHT = 2; // 0010
	int BOTTOM = 4; // 0100
	int TOP = 8; // 1000

	int computeOutCode(double x, double y, double xmin, double ymin, double xmax, double ymax) {
		int code;
		code = INSIDE;
		if (x < xmin)
			code |= LEFT;
		else if (x > xmax)
			code |= RIGHT;
		if (y < ymin)
			code |= BOTTOM;
		else if (y > ymax)
			code |= TOP;
		return code;
	}

	public double[] getGridCoordinates(double q, double r, double ox, double oy, double sx, double sy) {
		return new double[] { q * s60 * sx + ox, (r - q * c60) * sy + oy };
	}

	public int[] getTriangleAtGridCoordinates(double x, double y, double ox, double oy, double sx, double sy) {
		int q = (int) Math.round((x - ox) / (s60 * sx));
		int r = (int) Math.round((c60 * sx * (x - ox) + s60 * sy * (y - oy)) / (s60 * sx * sy));
		double[] xyc = getGridCoordinates((int) Math.round(q), (int) Math.round(r), ox, oy, sx, sy);
		double angle = Math.atan2(y - xyc[1], x - xyc[0]) / Math.PI * 180.0 + 30.0;
		if (angle < 0)
			angle += 360;
		int t = (int) angle / 60;

		return new int[] { q, r, t };

	}

}
