package wblut.isogrid;

import java.util.Comparator;

public class WB_IsoGridCell {
	int numTriangles;
	private int q, r;
	private int[] i;
	private int[] j;
	private int[] k;
	
	protected int[] z;
	protected int[] triangle;
	protected int[] orientation;
	protected int[] palette;
	
	protected double[][] triangleUV;
	protected int[][] triangleUVOffsets;
	protected int[][] triangleUVDirections;
	
	protected int[] region;
	protected int[] part;
	protected double[][] visibility;
	protected double[] drop;


	protected WB_IsoGridCell(int q, int r, int numTriangles) {
		this.numTriangles = numTriangles;
		this.q = q;
		this.r = r;
		i = new int[numTriangles];
		j = new int[numTriangles];
		k = new int[numTriangles];
	
		z = new int[numTriangles];
		triangle = new int[numTriangles];	
		orientation = new int[numTriangles];
		palette = new int[numTriangles];
		
		triangleUV = new double[numTriangles][6];
		triangleUVOffsets = new int[numTriangles][2];
		triangleUVDirections = new int[numTriangles][2];
		
		region = new int[numTriangles];
		part = new int[numTriangles];
		drop = new double[numTriangles];
		visibility = new double[numTriangles][6];
		
		for (int f = 0; f < numTriangles; f++) {
			i[f] = -Integer.MAX_VALUE;
			j[f] = -Integer.MAX_VALUE;
			k[f] = -Integer.MAX_VALUE;
		
			z[f] = -Integer.MAX_VALUE;
			triangle[f] = -1;
			orientation[f] = -1;
			palette[f] = 0;
			
			region[f] = -1;
			drop[f] = -1;
			part[f] = -1;
			visibility[f] = new double[] {0,0,0,0,0,0};
		}
	}
	
	public int getNumberOfTriangles() {
		return numTriangles;
	}

	public int getQ() {
		return q;
	}

	public int getR() {
		return r;
	}
	
	public int getI(int f) {
		return i[f];
	}
	
	public int getJ(int f) {
		return j[f];
	}
	
	public int getK(int f) {
		return k[f];
	}
	
	public int[] getIndices(int f) {
		if (i[f] == -Integer.MAX_VALUE)
			return null;
		return new int[] { i[f], j[f], k[f] };
	}

	
	public void setI(int f,  int id) {
		i[f]=id;
	}
	
	public void setJ(int f, int id) {
		j[f]=id;
	}
	
	public void setK(int f, int id) {
		k[f]=id;
	}
	
	public int getZ(int f) {
		return z[f];
	}
	
	public int getTriangle(int f) {
		return triangle[f];
	}
	
	public int getOrientation(int f) {
		return orientation[f];
	}

	public int getPalette(int f) {
		return palette[f];
	}
	
	
	public double getTriangleU(int f, int i) {

		return triangleUV[f][2 * i];
	}

	public double getTriangleV(int f, int i) {

		return triangleUV[f][2 * i + 1];
	}

	public int getTriangleUOffset(int f) {

		return triangleUVOffsets[f][0];
	}

	public int getTriangleVOffset(int f) {

		return triangleUVOffsets[f][1];
	}
	
	public int getTriangleUDirection(int f) {

		return triangleUVDirections[f][0];
	}

	public int getTriangleVDirection(int f) {

		return triangleUVDirections[f][1];
	}

	
	public int getPart(int f) {
		return part[f];
	}

	public int getRegion(int f) {
		return region[f];
	}

	public double getDrop(int f) {
		return drop[f];
	}
	
	public double getVisibility(int f,int dir) {
		return visibility[f][dir];
	}

	public boolean isEmpty() {
		for (int f = 0; f < numTriangles; f++) {
			if (orientation[f] > -1)
				return false;
		}
		return true;
	}

	protected long getHash() {
		long A = (q >= 0 ? 2 * (long) q : -2 * (long) q - 1);
		long B = (r >= 0 ? 2 * (long) r : -2 * (long) r - 1);
		long C = ((A >= B ? A * A + A + B : A + B * B) / 2);
		return q < 0 && r < 0 || q >= 0 && r >= 0 ? C : -C - 1;
	}

	static protected long getHash(int q, int r) {
		long A = (q >= 0 ? 2 * (long) q : -2 * (long) q - 1);
		long B = (r >= 0 ? 2 * (long) r : -2 * (long) r - 1);
		long C = ((A >= B ? A * A + A + B : A + B * B) / 2);
		return q < 0 && r < 0 || q >= 0 && r >= 0 ? C : -C - 1;
	}

	static protected class HexCellSort implements Comparator<WB_IsoGridCell> {
		@Override
		public int compare(WB_IsoGridCell arg0, WB_IsoGridCell arg1) {
			if (arg0.q < arg1.q) {
				return -1;
			} else if (arg0.q > arg1.q) {
				return 1;
			} else if (arg0.r < arg1.r) {
				return -1;
			} else if (arg0.r > arg1.r) {
				return 1;
			}
			return 0;
		}
	}
	
	

}
