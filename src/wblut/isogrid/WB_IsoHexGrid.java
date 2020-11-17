package wblut.isogrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PGraphics;


public abstract class WB_IsoHexGrid {
	
	double[] offsets;
	Map<Long, WB_IsoGridCell> cells;
	Map<Long, WB_IsoGridLine> linesMap;
	Map<Long, WB_IsoGridLine> outlinesMap;
	List<WB_IsoGridLine> lines;
	List<WB_IsoGridLine> outlines;
	
	WB_IsoHexGrid() {
		cells = new HashMap<Long, WB_IsoGridCell>();
		linesMap = new HashMap<Long, WB_IsoGridLine>();
		lines = new ArrayList<WB_IsoGridLine>();
		outlinesMap = new HashMap<Long, WB_IsoGridLine>();
		outlines = new ArrayList<WB_IsoGridLine>();

	}
	

	final public int[] hexToCube(int q, int r) {
		int i = q;
		int j = r;
		int k = 0;
		while (i + j + k < -1) {
			i++;
			j++;
			k++;
		}
		while (i + j + k > +1) {
			i--;
			j--;
			k--;
		}
		return new int[] { i, j, k };
	}

	final public int[] cubeToHex(int i, int j, int k) {
		return new int[] { i - k, j - k };
	}

	final public void clear() {
		cells.clear();
		linesMap.clear();
		outlinesMap.clear();
	}
	
	public abstract void addTriangle(int q, int r, int f, int s,  int z, int orientation,int palette, int i, int j,
			int k) ;

	public abstract void addCube(int i, int j, int k, int... params) ;

	final public List<WB_IsoGridCell> getCells() {
		List<WB_IsoGridCell> cellsList = new ArrayList<WB_IsoGridCell>();
		cellsList.addAll(cells.values());
		cellsList.sort(new WB_IsoGridCell.HexCellSort());
		return cellsList;
	}

	public abstract void collectLines() ;

	final public void collectRegions() {
		int maxRegion = -1;
		for (WB_IsoGridCell cell : cells.values()) {
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.getOrientation(f) != -1) {
					cell.region[f] = 3 * cell.getPart(f) + cell.getOrientation(f);
					maxRegion = Math.max(cell.region[f], maxRegion);
				} else {
					cell.region[f] = -1;
				}
			}
		}
		
		if (maxRegion > -1) {
			int[][] regionRange = new int[maxRegion+1][6];
			for (int i = 0; i <= maxRegion; i++) {
				for (int j = 0; j < 3; j++) {
					regionRange[i][2 * j] = Integer.MAX_VALUE;
					regionRange[i][2 * j + 1] = -Integer.MAX_VALUE;
				}
			}
			for (WB_IsoGridCell cell : cells.values()) {
				for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
					if (cell.getOrientation(f) != -1) {
						regionRange[cell.getRegion(f)][0] = Math.min(regionRange[cell.getRegion(f)][0],
								cell.getCube(f)[0]);
						regionRange[cell.getRegion(f)][2] = Math.min(regionRange[cell.getRegion(f)][2],
								cell.getCube(f)[1]);
						regionRange[cell.getRegion(f)][4] = Math.min(regionRange[cell.getRegion(f)][4],
								cell.getCube(f)[2]);

						regionRange[cell.getRegion(f)][1] = Math.max(regionRange[cell.getRegion(f)][1],
								cell.getCube(f)[0]);
						regionRange[cell.getRegion(f)][3] = Math.max(regionRange[cell.getRegion(f)][3],
								cell.getCube(f)[1]);
						regionRange[cell.getRegion(f)][5] = Math.max(regionRange[cell.getRegion(f)][5],
								cell.getCube(f)[2]);

					}
				}
			}

			for (WB_IsoGridCell cell : cells.values()) {
				for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
					if (cell.getOrientation(f) != -1) {
						cell.drop[f] =(regionRange[cell.getRegion(f)][3]-regionRange[cell.getRegion(f)][2])==0||cell.getOrientation(f)==1?1.0:(cell.getCube(f)[1]- regionRange[cell.getRegion(f)][2] )/(double)( regionRange[cell.getRegion(f)][3]-regionRange[cell.getRegion(f)][2]);
					}
				}
			}

		}

	}

	final public List<WB_IsoGridLine> getLines() {
		return lines;
	}

	final public List<WB_IsoGridLine> getOutlines() {
		return outlines;
	}

	// long hash from 2 int
	final static long getCellHash(int q, int r) {
		long A = (q >= 0 ? 2 * (long) q : -2 * (long) q - 1);
		long B = (r >= 0 ? 2 * (long) r : -2 * (long) r - 1);
		long C = ((A >= B ? A * A + A + B : A + B * B) / 2);
		return q < 0 && r < 0 || q >= 0 && r >= 0 ? C : -C - 1;
	}


	final void setParts(WB_CubeGrid cubes) {
		for (WB_IsoGridCell cell : cells.values()) {
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.getOrientation(f) > -1)
					cell.part[f] = cubes.getPart(cell.cubei[f] ,cell.cubej[f],cell.cubek[f]);
			}
		}

	}

	final static boolean areSeparate(int orientation1, int orientation2, int palette1, int palette2, int z1, int z2) {
		return orientation1 != orientation2 || palette1 != palette2 || Math.abs(z1 - z2) > 1;
	}


	
	final void hexVertex(PGraphics pg,int i, double ox, double oy, double sx, double sy) {
		vertex(pg, ox + offsets[2 * i] * sx, oy + offsets[2 * i + 1] * sy);
		
	}
	
	abstract void triVertex(PGraphics pg,int t,int i, double ox, double oy, double sx, double sy) ;
	
	abstract void triVertex(PGraphics pg,int t,int i, double ox, double oy, double sx, double sy,double u, double v) ;

	final void vertex(PGraphics pg, final double px, double py) {
		pg.vertex((float) px, (float) py);
	}

	final void vertex(PGraphics pg, final double px, double py, double u, double v) {
		pg.vertex((float) px, (float) py, (float) u, (float) v);
	}
	
	abstract void line(PGraphics pg, double q1, double r1,double q2, double r2, double ox, double oy, double sx, double sy) ;
	
	abstract void point(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy);
	
	abstract void line(
			PApplet pg, double q1, double r1,double q2, double r2, double ox, double oy, double sx, double sy) ;
	
	abstract void point(PApplet pg, double q, double r, double ox, double oy, double sx, double sy);

	abstract double[] getGridCoordinates(double q, double r, double ox, double oy, double sx, double sy) ;

	abstract int[] getTriangleAtGridCoordinates(double x, double y, double ox, double oy, double sx, double sy);
	

}
