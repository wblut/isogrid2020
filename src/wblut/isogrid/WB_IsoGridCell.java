package wblut.isogrid;

import java.util.Comparator;

import wblut.hexgrid.WB_HexGridCell;

public class WB_IsoGridCell extends WB_HexGridCell {

	private int[] i;
	private int[] j;
	private int[] k;
   
	protected int minZ;
	protected int[] triangleIndex;
	protected int[] orientation;
	
	protected int[][] triangleUVOffsets;
	protected int[][] triangleUVDirections;
	protected int[][] triangleUVDirectionSigns;
	protected double[][] exposure;
	protected double[] drop;

	
	protected WB_IsoGridCell(int q, int r, int numTriangles) {
		super(q,r,numTriangles);
		i = new int[numTriangles];
		j = new int[numTriangles];
		k = new int[numTriangles];
		triangleIndex = new int[numTriangles];	
		orientation = new int[numTriangles];
		triangleUVOffsets = new int[numTriangles][2];
		triangleUVDirections = new int[numTriangles][2];
		triangleUVDirectionSigns = new int[numTriangles][2];
		drop = new double[numTriangles];
		exposure = new double[numTriangles][6];
		minZ = Integer.MAX_VALUE;
		for (int f = 0; f < numTriangles; f++) {
			i[f] = Integer.MIN_VALUE;
			j[f] = Integer.MIN_VALUE;
			k[f] = Integer.MIN_VALUE;
			triangleIndex[f] = -1;
			orientation[f] = -1;
			drop[f] = -1;
			exposure[f] = new double[] {0,0,0,0,0,0};
	
		}
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
	
	
	
	public int getTriangleIndex(int f) {
		return triangleIndex[f];
	}
	
	public int getOrientation(int f) {
		return orientation[f];
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
	
	public int getTriangleUDirectionSign(int f) {

		return triangleUVDirectionSigns[f][0];
	}

	public int getTriangleVDirectionSign(int f) {

		return triangleUVDirectionSigns[f][1];
	}


	public double getDrop(int f) {
		return drop[f];
	}
	
	public double getExposure(int f,int dir) {
		return exposure[f][dir];
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
