package wblut.isogrid.deco;

import processing.core.PGraphics;
import wblut.isogrid.WB_IsoGridCell;
import wblut.isogrid.WB_IsoHexGrid;
import wblut.isogrid.WB_IsoHexGridData6;

public class WB_IsoDecoratorGrid6 extends WB_IsoDecoratorGrid implements WB_IsoHexGridData6 {

	public WB_IsoDecoratorGrid6(WB_IsoHexGrid hexGrid) {
		super(hexGrid);
		offsets = new double[14];
		for (int i = 0; i < 7; i++) {
			offsets[2 * i] = vertexOffsets[2 * i] * s60;
			offsets[2 * i + 1] = (vertexOffsets[2 * i + 1] - vertexOffsets[2 * i] * c60);
		}
	}

	public void addTriangle(int q, int r, int f, int s, int z, int orientation, int texture, int scale, int di, int dj,
			int dk, int i, int j, int k) {

		if ((orientation == 0 && di == scale - 1) || (orientation == 1 && dj == scale - 1)
				|| (orientation == 2 && dk == scale - 1)) {

			long key = getCellHash(q, r);
			WB_IsoDecoratorCell cell = cells.get(key);
			WB_IsoGridCell hexCell = hexGrid.get(key);
			if (cell == null) {
				cell = new WB_IsoDecoratorCell(q, r, 6);
				cells.put(key, cell);
			}

			int hexGridZ = (hexCell == null) ? -Integer.MAX_VALUE : hexCell.getZ(s);

			if (z > hexGridZ) {

				int cycle = (f == s) ? 0
						: (s == mapTrianglesFromPosOne[f]) ? ((s % 2 == 1) ? 2 : 4) : ((s % 2 == 0) ? 2 : 4);
				cell.addLayer(s, orientation, z, f, texture, scale, di, dj, dk, i, j, k, cycle(cycle, triangleUVs[f]),
						triangleUVDirections[f], triangleUVDirectionSigns[f]);

			}
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

	int[] cycle(int c, int[] source) {
		int n = source.length;
		int[] result = new int[n];
		for (int i = 0; i < n; i++) {
			result[i] = source[(i + c) % n];
		}
		return result;

	}

	public void addCube(int i, int j, int k, int... params) {

		int texture = params[0];
		int scale = params[1];
		for (int di = 0; di < scale; di++) {
			for (int dj = 0; dj < scale; dj++) {
				for (int dk = 0; dk < scale; dk++) {
					if (di == scale - 1 || dj == scale - 1 || dk == scale - 1) {
						int z = i + j + k + di + dj + dk;
						int q = i - k + di - dk;
						int r = j - k + dj - dk;
						int layer = q + r;
						while (layer > 1) {
							layer -= 3;
						}

						while (layer < -1) {
							layer += 3;
						}

						int ns;
						switch (layer) {

						case 0:
							for (int s = 0; s < 6; s++) {
								if (triangleOrientations[s] >= 0)
									addTriangle(q, r, s, s, z, triangleOrientations[s], texture, scale, di, dj, dk, i,
											j, k);
							}
							break;

						case +1:
							for (int s = 0; s < 6; s++) {
								ns = mapTrianglesFromPosOne[s];
								if (triangleOrientations[s] >= 0)
									addTriangle(q + mapQFromPosOne[s], r + mapRFromPosOne[s], s, ns, z,
											triangleOrientations[s], texture, scale, di, dj, dk, i, j, k);
							}
							break;
						case -1:
							for (int s = 0; s < 6; s++) {
								ns = mapTrianglesFromNegOne[s];
								if (triangleOrientations[s] >= 0)
									addTriangle(q + mapQFromNegOne[s], r + mapRFromNegOne[s], s, ns, z,
											triangleOrientations[s], texture, scale, di, dj, dk, i, j, k);
							}
							break;

						default:

						}
					}
				}
			}
		}

	}

	void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy, double u, double v) {
		vertex(pg, ox + offsets[2 * triangleVertices[t][i]] * sx, oy + offsets[2 * triangleVertices[t][i] + 1] * sy, u,
				v);

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
