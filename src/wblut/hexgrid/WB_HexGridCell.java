package wblut.hexgrid;

import java.util.Comparator;

public class WB_HexGridCell {
	protected int numTriangles;
	protected int q, r;
	protected int[] triangleColor;
	public int[] triangleColorSourceIndex;
	public int[] z;
	public boolean[] occupied;
	public int[] region;
	public int[] part;
	public double[][] triangleUV;
	protected WB_HexGridCell(int q, int r, int numTriangles) {
		this.numTriangles = numTriangles;
		this.q = q;
		this.r = r;
		triangleColor = new int[numTriangles];
		triangleColorSourceIndex = new int[numTriangles];
		region = new int[numTriangles];
		part = new int[numTriangles];
		triangleUV = new double[numTriangles][6];	
		z = new int[numTriangles];
		occupied=new boolean[numTriangles];
		for (int f = 0; f < numTriangles; f++) {
			triangleColor[f] = -1;
			triangleColorSourceIndex[f] = -1;
			region[f] = -1;
			part[f] = -1;
			z[f] = Integer.MIN_VALUE;
			occupied[f]=false;
		}
	}

	protected WB_HexGridCell(WB_HexGridCell cell) {
		this.numTriangles = cell.numTriangles;
		this.q = cell.q;
		this.r = cell.r;
		triangleColor = new int[numTriangles];
		triangleColorSourceIndex = new int[numTriangles];
		region = new int[numTriangles];
		part = new int[numTriangles];
		z = new int[numTriangles];
		occupied=new boolean[numTriangles];
		triangleUV = new double[numTriangles][6];	
		for (int f = 0; f < numTriangles; f++) {
			triangleColor[f] = cell.triangleColor[f];
			triangleColorSourceIndex[f] = cell.triangleColorSourceIndex[f];
			region[f] = cell.region[f];
			part[f] = cell.part[f];
			z[f] = cell.z[f];
			occupied[f]=cell.occupied[f];
			for(int c=0;c<6;c++) {
				triangleUV[f][c]=cell.triangleUV[f][c];
			}
		}
	}
	
	protected void clear(int f) {
		triangleColor[f] = -1;
		triangleColorSourceIndex[f] = -1;
		region[f] = -1;
		part[f] = -1;
		z[f] = Integer.MIN_VALUE;
		occupied[f]=false;
		for(int c=0;c<6;c++) {
			triangleUV[f][c]=0;
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

	public int getColor(int f) {
		return triangleColor[f];
	}

	public int getColorSourceIndex(int f) {
		return triangleColorSourceIndex[f];
	}

	public int getPart(int f) {
		return part[f];
	}

	public int getRegion(int f) {
		return region[f];
	}

	public int getZ(int f) {
		return z[f];
	}
	
	public double getTriangleU(int f, int i) {

		return triangleUV[f][2 * i];
	}

	public double getTriangleV(int f, int i) {

		return triangleUV[f][2 * i + 1];
	}
	
	public double[] getTriangleUV(int f) {

		return triangleUV[f];
	}

	public boolean isEmpty() {
		for (int f = 0; f < numTriangles; f++) {
			if (occupied[f])
				return false;
		}
		return true;
	}
	
	public boolean isFull() {
		for (int f = 0; f < numTriangles; f++) {
			if (!occupied[f])
				return false;
		}
		return true;
	}
	
	public boolean isOccupied(int f) {

			return occupied[f];
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

	static protected class HexCellSort implements Comparator<WB_HexGridCell> {
		@Override
		public int compare(WB_HexGridCell arg0, WB_HexGridCell arg1) {
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

	public void rotateCCW(int k) {
		rotateCCW(triangleColor,k);
		rotateCCW(triangleColorSourceIndex,k);
		rotateCCW(z,k);
		rotateCCW(region,k);
		rotateCCW(part,k);
		rotateCCW(triangleUV,k);
		rotateCCW(occupied,k);
			
	}
	
	public void rotateCW(int k) {
		rotateCW(triangleColor,k);
		rotateCW(triangleColorSourceIndex,k);
		rotateCW(z,k);
		rotateCW(region,k);
		rotateCW(part,k);	
		rotateCW(triangleUV,k);
		rotateCW(occupied,k);
	}

	 void swapElement(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	 
	 void swapElement(double[][] array, int i, int j) {
			double[] temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}
	 void swapElement(boolean[] array, int i, int j) {
			boolean temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}

	void reverseArray(int[] array, int low, int high) {
		for (int i = low, j = high; i < j; i++, j--) {
			swapElement(array, i, j);
		}
	}
	
	 void reverseArray(double[][] array, int low, int high) {
			for (int i = low, j = high; i < j; i++, j--) {
				swapElement(array, i, j);
			}
		}
	 
	 void reverseArray(boolean[] array, int low, int high) {
			for (int i = low, j = high; i < j; i++, j--) {
				swapElement(array, i, j);
			}
		}

	void rotateCCW(int[] array, int k) {
		reverseArray(array, numTriangles - k, numTriangles - 1);
		reverseArray(array, 0, numTriangles- k - 1);
		reverseArray(array, 0, numTriangles- 1);
	}
	
	void rotateCW(int[] array, int k) {
		reverseArray(array, 0, k - 1);
		reverseArray(array,  k,numTriangles - 1);
		reverseArray(array, 0, numTriangles - 1);
	}
	
	void rotateCCW(double[][] array, int k) {
		reverseArray(array, numTriangles - k, numTriangles - 1);
		reverseArray(array, 0, numTriangles- k - 1);
		reverseArray(array, 0, numTriangles- 1);
	}
	
	void rotateCW(double[][] array, int k) {
		reverseArray(array, 0, k - 1);
		reverseArray(array,  k,numTriangles - 1);
		reverseArray(array, 0, numTriangles - 1);
	}
	
	void rotateCCW(boolean[] array, int k) {
		reverseArray(array, numTriangles - k, numTriangles - 1);
		reverseArray(array, 0, numTriangles- k - 1);
		reverseArray(array, 0, numTriangles- 1);
	}
	
	void rotateCW(boolean[] array, int k) {
		reverseArray(array, 0, k - 1);
		reverseArray(array,  k,numTriangles - 1);
		reverseArray(array, 0, numTriangles - 1);
	}

}
