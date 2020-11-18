package wblut.isogrid;

import java.util.Comparator;

public class WB_IsoGridCell {
	private int q, r;
	int numTriangles;
	protected int[] z;
	protected int[] of;
	protected int[] orientation;
	protected int[] palette;
	
	protected int[] cubei;
	protected int[] cubej;
	protected int[] cubek;
	protected double[][] triangleUV;
	protected int[][] triangleUVOffsets;
	protected int[][] triangleUVDirections;
	protected int[] region;
	protected int[] part;
	protected double[] drop;


	protected WB_IsoGridCell(int q, int r, int numTriangles) {
		this.numTriangles = numTriangles;
		this.q = q;
		this.r = r;
		orientation = new int[numTriangles];
		palette = new int[numTriangles];
		z = new int[numTriangles];
		of = new int[numTriangles];
		cubei = new int[numTriangles];
		cubej = new int[numTriangles];
		cubek = new int[numTriangles];
		region = new int[numTriangles];
		drop = new double[numTriangles];
		triangleUV = new double[numTriangles][6];
		triangleUVOffsets = new int[numTriangles][2];
		triangleUVDirections = new int[numTriangles][2];
		part = new int[numTriangles];
		for (int f = 0; f < numTriangles; f++) {
			orientation[f] = -1;
			palette[f] = 0;
			z[f] = -Integer.MAX_VALUE;
			of[f] = -Integer.MAX_VALUE;
			cubei[f] = -Integer.MAX_VALUE;
			cubej[f] = -Integer.MAX_VALUE;
			cubek[f] = -Integer.MAX_VALUE;
			region[f] = -1;
			drop[f] = -1;
			part[f] = -1;
		}
	}
	
	public int getNumberOfTriangles() {
		return numTriangles;
	}

	public int getOrientation(int f) {
		return orientation[f];
	}

	public int getPalette(int f) {
		return palette[f];
	}

	public int getZ(int f) {
		return z[f];
	}

	public int getF(int f) {
		return of[f];
	}

	public int[] getCube(int f) {
		if (cubei[f] == -Integer.MAX_VALUE)
			return null;
		return new int[] { cubei[f], cubej[f], cubek[f] };
	}

	public int getRegion(int f) {
		return region[f];
	}

	public double getDrop(int f) {
		return drop[f];
	}

	public int getPart(int f) {
		return part[f];
	}

	public int getQ() {
		return q;
	}

	public int getR() {
		return r;
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
