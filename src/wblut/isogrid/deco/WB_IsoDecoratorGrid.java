package wblut.isogrid.deco;

import java.util.Collection;
import java.util.HashMap;

import java.util.Map;

import processing.core.PGraphics;
import wblut.isogrid.WB_IsoHexGrid;


public abstract class WB_IsoDecoratorGrid {
	
	double[] offsets;
	Map<Long, WB_IsoDecoratorCell> cells;
	WB_IsoHexGrid hexGrid;
	
	WB_IsoDecoratorGrid(WB_IsoHexGrid hexGrid) {
		cells = new HashMap<Long, WB_IsoDecoratorCell>();
		this.hexGrid=hexGrid;
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
	}
	
	public abstract void addTriangle(int q, int r, int f, int s,  int z, int orientation,int texture, int scale, int di, int dj, int dk, int i, int j,
			int k) ;
	

	public abstract void addCube(int i, int j, int k, int... params) ;

	final public Collection<WB_IsoDecoratorCell> getCells() {
	
		return cells.values();
	}

	
	// long hash from 2 int
	final static long getCellHash(int q, int r) {
		long A = (q >= 0 ? 2 * (long) q : -2 * (long) q - 1);
		long B = (r >= 0 ? 2 * (long) r : -2 * (long) r - 1);
		long C = ((A >= B ? A * A + A + B : A + B * B) / 2);
		return q < 0 && r < 0 || q >= 0 && r >= 0 ? C : -C - 1;
	}


	
	
	final void hexVertex(PGraphics pg,int i, double ox, double oy, double sx, double sy) {
		vertex(pg, ox + offsets[2 * i] * sx, oy + offsets[2 * i + 1] * sy);
		
	}
	
	
	abstract void triVertex(PGraphics pg,int t,int i, double ox, double oy, double sx, double sy,double u, double v) ;

	final void vertex(PGraphics pg, final double px, double py) {
		pg.vertex((float) px, (float) py);
	}

	final void vertex(PGraphics pg, final double px, double py, double u, double v) {
		pg.vertex((float) px, (float) py, (float) u, (float) v);
	}
	

	abstract double[] getGridCoordinates(double q, double r, double ox, double oy, double sx, double sy) ;

	abstract int[] getTriangleAtGridCoordinates(double x, double y, double ox, double oy, double sx, double sy);
	

}
