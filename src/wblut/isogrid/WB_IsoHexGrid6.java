package wblut.isogrid;

import java.util.ArrayList;

import java.util.Map;

import processing.core.PGraphics;

public class WB_IsoHexGrid6 extends WB_IsoHexGrid implements WB_IsoHexGridData6 {

	public WB_IsoHexGrid6() {
		super();
		offsets = new double[14];
		for (int i = 0; i < 7; i++) {
			offsets[2 * i] = vertexOffsets[2 * i] * s60;
			offsets[2 * i + 1] = (vertexOffsets[2 * i + 1] - vertexOffsets[2 * i] * c60);
		}
	}


	public void addTriangle(int q, int r, int f, int s, int z, int orientation, int palette, int i,
			int j, int k) {
		long key = getCellHash(q, r);
		WB_IsoGridCell cell = cells.get(key);

		if (cell == null) {
			cell = new WB_IsoGridCell(q, r, 6);
			cells.put(key, cell);

		}

		if (z > cell.z[s]) {
			
			int cycle=(f==s)?0:(s == mapTrianglesFromPosOne[f])?((s % 2 == 1) ? 2 : 4):((s % 2 == 0) ? 2 : 4);
			
			
			cell.orientation[s] = orientation;
			cell.z[s] = z;
			cell.of[s] = f;
			cell.palette[s] = palette;
			cell.cubei[s] = i;
			cell.cubej[s] = j;
			cell.cubek[s] = k;
			cell.triangleUV[s] = cycle(cycle, triangleUVs[f]);
			cell.triangleUVDirections[s][0] = triangleUVDirections[f][0];
			cell.triangleUVDirections[s][1] = triangleUVDirections[f][1];
			cell.triangleUVOffsets[s][0] = cell.getCube(s)[triangleUVDirections[f][0]];
			cell.triangleUVOffsets[s][1] = cell.getCube(s)[triangleUVDirections[f][1]];
			cell.part[s] = -1;
		}

	}

	double[] cycle(int c, double[] source) {
		int n = source.length;
		double[] result = new double[n];
		for (int i = 0; i < n; i++) {
			result[i] = source[(i + c) % n];
		}
		return result;

	}

	public void addCube(int i, int j, int k, int...params) {
		int z = i + j + k;
		int z3 = z / 3;
		int ni = i - z3;
		int nj = j - z3;
		int nk = k - z3;
		int layer = ni + nj + nk;
		while (layer > 1) {
			ni--;
			nj--;
			nk--;
			layer = ni + nj + nk;
		}
		while (layer < -1) {
			ni++;
			nj++;
			nk++;
			layer = ni + nj + nk;
		}
		int q = i - k;
		int r = j - k;
		int ns;
		switch (layer) {

		case 0:
			for (int s = 0; s < 6; s++) {
				if (triangleOrientations[s] >= 0)
					addTriangle(q, r, s, s, z, triangleOrientations[s], params[0], i, j, k);
			}
			break;

		case +1:
			for (int s = 0; s < 6; s++) {
				ns = mapTrianglesFromPosOne[s];
				if (triangleOrientations[s] >= 0)
					addTriangle(q + mapQFromPosOne[s], r + mapRFromPosOne[s], s, ns, z,
							triangleOrientations[s],  params[0], i, j, k);
			}
			break;
		case -1:
			for (int s = 0; s < 6; s++) {
				ns = mapTrianglesFromNegOne[s];
				if (triangleOrientations[s] >= 0)
					addTriangle(q + mapQFromNegOne[s], r + mapRFromNegOne[s], s, ns,  z,
							triangleOrientations[s],  params[0], i, j, k);
			}
			break;

		default:

		}
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
	}


	private void collectInterHexSegmentsInterior(Map<Long, WB_IsoGridLine> linesMap) {
		WB_IsoGridCell neighbor;
		int z, orientation, colorIndex, part;
		long hash, nhash;
		for (WB_IsoGridCell cell : cells.values()) {
			hash = getCellHash(cell.getQ(), cell.getR());
			for (int i = 0; i < 6; i++) {
				if (cell.getOrientation(i) >= 0) {
					if (interHexNeighbor[i] >= 0) {
						neighbor = getNeighbor(cell.getQ(), cell.getR(), i);
						if (neighbor == null) {

						} else {
							nhash = getCellHash(cell.getQ() + interHexNeighborQ[i], cell.getR() + interHexNeighborR[i]);
							orientation = neighbor.getOrientation(interHexNeighbor[i]);
							part = neighbor.getPart(interHexNeighbor[i]);
							if (cell.getPart(i) == part && (nhash < hash || orientation == -1)) {

								z = neighbor.getZ(interHexNeighbor[i]);
								colorIndex = neighbor.getPalette(interHexNeighbor[i]);
								if (areSeparate(orientation, cell.getOrientation(i), colorIndex, cell.getPalette(i),
										cell.getZ(i), z)) {
									addSegment(cell.getQ(), cell.getR(), interHexSegment[2 * i],
											interHexSegment[2 * i + 1], linesMap);
								}
							}

						}
					}
				}

			}

		}
	}

	private void collectInterHexSegmentsOutline(Map<Long, WB_IsoGridLine> outlinesMap) {
		WB_IsoGridCell neighbor;
		int part, orientation;
		long hash, nhash;
		for (WB_IsoGridCell cell : cells.values()) {
			hash = getCellHash(cell.getQ(), cell.getR());
			for (int i = 0; i < 6; i++) {
				if (cell.getOrientation(i) >= 0) {
					neighbor = getNeighbor(cell.getQ(), cell.getR(), i);
					if (neighbor == null) {
						addSegment(cell.getQ(), cell.getR(), interHexSegment[2 * i], interHexSegment[2 * i + 1],
								outlinesMap);
					} else {
						nhash = getCellHash(cell.getQ() + interHexNeighborQ[i], cell.getR() + interHexNeighborR[i]);
						orientation = neighbor.getOrientation(interHexNeighbor[i]);
						if (nhash < hash || orientation == -1) {
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

	private void collectInterTriangleSegmentsInterior(Map<Long, WB_IsoGridLine> linesMap) {
		int z, orientation, palette, part;
		for (WB_IsoGridCell cell : cells.values()) {
			for (int i = 0; i < 6; i++) {

				orientation = cell.getOrientation(interTriangleNeighbor[i]);
				z = cell.getZ(interTriangleNeighbor[i]);
				palette = cell.getPalette(interTriangleNeighbor[i]);
				part = cell.getPart(interTriangleNeighbor[i]);
				if (cell.getPart(i) != part) {

				} else if (areSeparate(orientation, cell.getOrientation(i), palette, cell.getPalette(i), cell.getZ(i),
						z)) {
					addSegment(cell.getQ(), cell.getR(), interTriangleSegment[2 * i], interTriangleSegment[2 * i + 1],
							linesMap);
				}

			}

		}
	}

	private void collectInterTriangleSegmentsOutline(Map<Long, WB_IsoGridLine> outlinesMap) {
		int part;
		for (WB_IsoGridCell cell : cells.values()) {
			for (int i = 0; i < 6; i++) {

				part = cell.getPart(interTriangleNeighbor[i]);
				if (cell.getPart(i) != part) {
					addSegment(cell.getQ(), cell.getR(), interTriangleSegment[2 * i], interTriangleSegment[2 * i + 1],
							outlinesMap);
				}
			}

		}
	}

	private WB_IsoGridCell getNeighbor(int q, int r, int i) {
		return cells.get(getCellHash(q + interHexNeighborQ[i], r + interHexNeighborR[i]));
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


	void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy) {
		vertex(pg, ox + offsets[2 * triangleVertices[t][i]] * sx, oy + offsets[2 * triangleVertices[t][i] + 1] * sy);

	}

	void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy, double u, double v) {
		vertex(pg, ox + offsets[2 * triangleVertices[t][i]] * sx, oy + offsets[2 * triangleVertices[t][i] + 1] * sy, u,
				v);

	}


	void line(PGraphics pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx, double sy) {
		pg.line((float) (q1 / 6.0 * s60 * sx + ox), (float) ((r1 - q1 * c60) / 6.0 * sy + oy),
				(float) ((q2 / 6.0 * s60 * sx) + ox), (float) ((r2 - q2 * c60) / 6.0 * sy + oy));

	}

	void point(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy) {
		pg.point((float) (q / 6.0 * s60 * sx + ox), (float) ((r - q * c60) / 6.0 * sy + oy));

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
