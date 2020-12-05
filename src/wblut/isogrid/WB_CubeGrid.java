package wblut.isogrid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class WB_CubeGrid {

	int I, J, K, JK, IJK;
	boolean[] voxels;
	boolean[] buffer;
	boolean[] swap;

	int[] palettes;
	int[] paletteBuffer;
	int[] paletteSwap;
	boolean[] visited;
	int[] parts;
	double[][] visibility;
	double[][] visibilityBuffer;

	private WB_CubeGrid() {

	}

	public WB_CubeGrid(int I, int J, int K) {

		this.I = I;
		this.J = J;
		this.K = K;
		JK = J * K;
		IJK = I * JK;

		voxels = new boolean[IJK];
		buffer = new boolean[IJK];
		visited = new boolean[IJK];
		palettes = new int[IJK];
		paletteBuffer = new int[IJK];
		parts = new int[IJK];
		visibility = new double[IJK][6];
		visibilityBuffer = null;
	}

	public WB_CubeGrid(WB_CubeGrid cubes) {

		this.I = cubes.I;
		this.J = cubes.J;
		this.K = cubes.K;
		JK = J * K;
		IJK = I * JK;

		voxels = new boolean[IJK];
		System.arraycopy(cubes.voxels, 0, voxels, 0, IJK);
		buffer = new boolean[IJK];
		visited = new boolean[IJK];
		palettes = new int[IJK];
		System.arraycopy(cubes.palettes, 0, palettes, 0, IJK);
		paletteBuffer = new int[IJK];
		parts = new int[IJK];

		System.arraycopy(cubes.parts, 0, parts, 0, IJK);
		visibility = new double[IJK][6];
		System.arraycopy(cubes.visibility, 0, visibility, 0, IJK);

	}

	public WB_CubeGrid rotateICC() {
		WB_CubeGrid result = new WB_CubeGrid(I, K, J);
		for (int i = 0; i < I; i++) {
		for (int j = 0; j < J; j++) {
			for (int k = 0; k < K; k++) {
					result.set(i,k, j, get(i, j, K-1-k));
				}
			}
		}
		return result;
	}
	
	public WB_CubeGrid rotateICW() {
		WB_CubeGrid result = new WB_CubeGrid(I,K, J);
		for (int i = 0; i < I; i++) {
		for (int j = 0; j < J; j++) {
			for (int k = 0; k < K; k++) {
					result.set(i,k, j, get(i, J-1-j, k));
				}
			}
		}
		return result;
	}
	
	public WB_CubeGrid rotateJCC() {
		WB_CubeGrid result = new WB_CubeGrid(K, J, I);
		for (int j = 0; j < J; j++) {
			for (int k = 0; k < K; k++) {
				for (int i = 0; i < I; i++) {
					result.set(k, j, i, get(i, j, K-1-k));
				}
			}
		}
		return result;
	}
	
	public WB_CubeGrid rotateJCW() {
		WB_CubeGrid result = new WB_CubeGrid(K, J, I);
		for (int j = 0; j < J; j++) {
			for (int k = 0; k < K; k++) {
				for (int i = 0; i < I; i++) {
					result.set(k, j, i, get(I-1-i, j, k));
				}
			}
		}
		return result;
	}
	
	public WB_CubeGrid rotateKCC() {
		WB_CubeGrid result = new WB_CubeGrid(J, I, K);
		for (int k = 0; k < K; k++) {
		for (int j = 0; j < J; j++) {
				for (int i = 0; i < I; i++) {
					result.set(j,i,k, get(i, J-1-j, k));
				}
			}
		}
		return result;
	}
	
	public WB_CubeGrid rotateKCW() {
		WB_CubeGrid result = new WB_CubeGrid(J,I,K);
		for (int k = 0; k < K; k++) {
		for (int j = 0; j < J; j++) {
				for (int i = 0; i < I; i++) {
					result.set(j, i, k, get(I-1-i, j, k));
				}
			}
		}
		return result;
	}
	

	public void swap() {
		swap = buffer;
		buffer = voxels;
		voxels = swap;
	}

	public void paletteSwap() {
		paletteSwap = paletteBuffer;
		paletteBuffer = palettes;
		palettes = paletteSwap;
	}

	public void fill() {
		int id = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					voxels[id] = true;
					id++;
				}
			}
		}

	}

	public void clear() {
		int id = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					voxels[id] = false;
					id++;
				}
			}
		}

	}

	public boolean get(int id) {
		return voxels[id];
	}

	public boolean get(int i, int j, int k) {
		return voxels[index(i, j, k)];
	}

	public boolean getBuffer(int id) {
		return buffer[id];
	}

	public boolean getBuffer(int i, int j, int k) {
		return buffer[index(i, j, k)];
	}

	public void set(int id, boolean b) {
		voxels[id] = b;
	}

	public void set(int i, int j, int k, boolean b) {
		voxels[index(i, j, k)] = b;
	}

	public void setBuffer(int id, boolean b) {
		buffer[id] = b;
	}

	public void setBuffer(int i, int j, int k, boolean b) {
		buffer[index(i, j, k)] = b;
	}

	public void and(int id, boolean b) {
		voxels[id] &= b;
	}

	public void and(int i, int j, int k, boolean b) {
		voxels[index(i, j, k)] &= b;
	}

	public void or(int id, boolean b) {
		voxels[id] |= b;
	}

	public void or(int i, int j, int k, boolean b) {
		voxels[index(i, j, k)] |= b;
	}

	public void xor(int id, boolean b) {
		voxels[id] ^= b;
	}

	public void xor(int i, int j, int k, boolean b) {
		voxels[index(i, j, k)] ^= b;
	}

	public void not(int id) {
		voxels[id] = !voxels[id];
	}

	public void not(int i, int j, int k) {
		int id = index(i, j, k);
		voxels[id] = !voxels[id];
	}

	public int getPalette(int id) {
		return palettes[id];
	}

	public int getPalette(int i, int j, int k) {
		return palettes[index(i, j, k)];
	}

	public int getPaletteBuffer(int id) {
		return paletteBuffer[id];
	}

	public int getPaletteBuffer(int i, int j, int k) {
		return paletteBuffer[index(i, j, k)];
	}

	public void setPalette(int id, int palette) {
		palettes[id] = palette;
	}

	public void setPalette(int i, int j, int k, int palette) {
		palettes[index(i, j, k)] = palette;
	}

	public void setPaletteBuffer(int id, int palette) {
		paletteBuffer[id] = palette;
	}

	public void setPaletteBuffer(int i, int j, int k, int palette) {
		paletteBuffer[index(i, j, k)] = palette;
	}

	public int getPart(int id) {
		return parts[id];
	}

	public int getPart(int i, int j, int k) {
		return parts[index(i, j, k)];
	}

	public void setPart(int id, int part) {
		parts[id] = part;
	}

	public void setPart(int i, int j, int k, int part) {
		parts[index(i, j, k)] = part;
	}

	public int labelParts() {

		int currentPart = 0;
		int[] indices = null;
		do {
			indices = findSeed();
			if (indices != null) {
				floodFill(indices[0], indices[1], indices[2], currentPart, parts);
			}

			currentPart++;
		} while (indices != null);
		return currentPart;
	}

	public void setVisibility() {
		setMaxVisibilityAtBoundaries();
		scanInteriorCubes();

		bleedVisibility(0.5);

	}

	void setMaxVisibilityAtBoundaries() {

		int index = 0;

		for (int i = 0; i < I; i++) {
			index = i * JK;
			for (int k = 0; k < K; k++) {
				for (int j = 0; j < J; j++) {
					visibility[index + j * K][2] = J;
					if (get(index + j * K)) {

						break;
					}
				}
				for (int j = J - 1; j >= 0; j--) {
					visibility[index + j * K][3] = J;
					if (get(index + j * K)) {

						break;
					}
				}
				index++;
			}
		}

		for (int i = 0; i < I; i++) {
			index = i * JK;
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					visibility[index + k][4] = K;
					if (get(index + k)) {

						break;
					}
				}
				for (int k = K - 1; k >= 0; k--) {
					visibility[index + k][5] = K;
					if (get(index + k)) {

						break;
					}
				}
				index += K;
			}
		}

		for (int j = 0; j < J; j++) {
			index = j * K;
			for (int k = 0; k < K; k++) {
				for (int i = 0; i < I; i++) {
					visibility[index + i * JK][0] = I;
					if (get(index + i * JK)) {

						break;
					}
				}
				for (int i = I - 1; i >= 0; i--) {
					visibility[index + i * JK][1] = I;
					if (get(index + i * JK)) {

						break;
					}
				}
				index++;
			}
		}
	}

	void scanInteriorCubes() {
		int index = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					if (getVisibility(index, 0) < I)
						setVisibility(index, 0, scanILeft(i, index));
					if (getVisibility(index, 1) < I)
						setVisibility(index, 1, scanIRight(i, index));
					if (getVisibility(index, 2) < J)
						setVisibility(index, 2, scanJLeft(j, index));
					if (getVisibility(index, 3) < J)
						setVisibility(index, 3, scanJRight(j, index));
					if (getVisibility(index, 4) < K)
						setVisibility(index, 4, scanKLeft(k, index));
					if (getVisibility(index, 5) < K)
						setVisibility(index, 5, scanKRight(k, index));

					setVisibility(index, 0, getVisibility(index, 0) / I);
					setVisibility(index, 1, getVisibility(index, 1) / I);
					setVisibility(index, 2, getVisibility(index, 2) / J);
					setVisibility(index, 3, getVisibility(index, 3) / J);
					setVisibility(index, 4, getVisibility(index, 4) / K);
					setVisibility(index, 5, getVisibility(index, 5) / K);

					index++;
				}
			}
		}

	}

	void bleedVisibility(double bleedFactor) {
		if (visibilityBuffer == null) {
			visibilityBuffer = new double[visibility.length][6];
		}
		int index = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					for (int side = 0; side < 6; side++) {
						visibilityBuffer[index][side] = visibility[index][side];

					}
					for (int side = 0; side < 6; side++) {
						for (int oside = 0; oside < 6; oside++) {
							if (side / 2 != oside / 2) {
								visibilityBuffer[index][side] += bleedFactor * visibility[index][oside];
							}
						}

					}

					if (j > 0) {
						visibilityBuffer[index][0] += bleedFactor * visibility[index - K][2];
						visibilityBuffer[index][1] += bleedFactor * visibility[index - K][2];

					}
					if (j < J - 1) {
						visibilityBuffer[index][0] += bleedFactor * visibility[index + K][3];
						visibilityBuffer[index][1] += bleedFactor * visibility[index + K][3];

					}
					if (k > 0) {
						visibilityBuffer[index][0] += bleedFactor * visibility[index - 1][4];
						visibilityBuffer[index][1] += bleedFactor * visibility[index - 1][4];

					}
					if (k < K - 1) {
						visibilityBuffer[index][0] += bleedFactor * visibility[index + 1][5];
						visibilityBuffer[index][1] += bleedFactor * visibility[index + 1][5];

					}
					visibilityBuffer[index][0] = Math.min(visibilityBuffer[index][0], 1.0);
					visibilityBuffer[index][1] = Math.min(visibilityBuffer[index][1], 1.0);

					if (i > 0) {
						visibilityBuffer[index][2] += bleedFactor * visibility[index - JK][0];
						visibilityBuffer[index][3] += bleedFactor * visibility[index - JK][0];

					}
					if (i < I - 1) {
						visibilityBuffer[index][2] += bleedFactor * visibility[index + JK][1];
						visibilityBuffer[index][3] += bleedFactor * visibility[index + JK][1];

					}
					if (k > 0) {
						visibilityBuffer[index][2] += bleedFactor * visibility[index - 1][4];
						visibilityBuffer[index][3] += bleedFactor * visibility[index - 1][4];

					}
					if (k < K - 1) {
						visibilityBuffer[index][2] += bleedFactor * visibility[index + 1][5];
						visibilityBuffer[index][3] += bleedFactor * visibility[index + 1][5];

					}
					visibilityBuffer[index][2] = Math.min(visibilityBuffer[index][2], 1.0);
					visibilityBuffer[index][3] = Math.min(visibilityBuffer[index][3], 1.0);

					if (i > 0) {
						visibilityBuffer[index][4] += bleedFactor * visibility[index - JK][0];
						visibilityBuffer[index][5] += bleedFactor * visibility[index - JK][0];

					}
					if (i < I - 1) {
						visibilityBuffer[index][4] += bleedFactor * visibility[index + JK][1];
						visibilityBuffer[index][5] += bleedFactor * visibility[index + JK][1];

					}
					if (j > 0) {
						visibilityBuffer[index][4] += bleedFactor * visibility[index - K][2];
						visibilityBuffer[index][5] += bleedFactor * visibility[index - K][2];

					}
					if (j < J - 1) {
						visibilityBuffer[index][4] += bleedFactor * visibility[index + K][3];
						visibilityBuffer[index][5] += bleedFactor * visibility[index + K][3];

					}
					visibilityBuffer[index][4] = Math.min(visibilityBuffer[index][4], 1.0);
					visibilityBuffer[index][5] = Math.min(visibilityBuffer[index][5], 1.0);

					index++;
				}
			}
		}
		double[][] tmp = visibility;
		visibility = visibilityBuffer;
		visibilityBuffer = tmp;
	}

	int scanILeft(int i, int index) {
		int count = 0;
		for (int ci = i - 1; ci >= 0; ci--) {
			if (get(index + (ci - i) * JK)) {
				break;
			}
			count++;
		}
		return count;
	}

	int scanIRight(int i, int index) {
		int count = 0;
		for (int ci = i + 1; ci < I; ci++) {
			if (get(index + (ci - i) * JK)) {
				break;
			}
			count++;
		}
		return count;
	}

	int scanJLeft(int j, int index) {
		int count = 0;

		for (int cj = j - 1; cj >= 0; cj--) {
			if (get(index + (cj - j) * K)) {
				break;
			}
			count++;
		}
		return count;
	}

	int scanJRight(int j, int index) {
		int count = 0;
		for (int cj = j + 1; cj < J; cj++) {
			if (get(index + (cj - j) * K)) {
				break;
			}
			count++;
		}
		return count;
	}

	int scanKLeft(int k, int index) {
		int count = 0;
		for (int ck = k - 1; ck >= 0; ck--) {
			if (get(index + ck - k)) {
				break;
			}
			count++;
		}
		return count;
	}

	int scanKRight(int k, int index) {
		int count = 0;
		for (int ck = k + 1; ck < K; ck++) {
			if (get(index + ck - k)) {
				break;
			}
			count++;
		}
		return count;
	}

	void smoothVisibility(double smoothFactor) {
		if (visibilityBuffer == null) {
			visibilityBuffer = new double[visibility.length][6];
		}
		int index = 0;
		double w;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					for (int side = 0; side < 6; side++) {
						visibilityBuffer[index][side] = visibility[index][side];
					}
					w = 1.0;
					if (j > 0) {
						visibilityBuffer[index][0] += smoothFactor * visibility[index - K][0];
						visibilityBuffer[index][1] += smoothFactor * visibility[index - K][1];
						w += smoothFactor;
					}
					if (j < J - 1) {
						visibilityBuffer[index][0] += smoothFactor * visibility[index + K][0];
						visibilityBuffer[index][1] += smoothFactor * visibility[index + K][1];
						w += smoothFactor;
					}
					if (k > 0) {
						visibilityBuffer[index][0] += smoothFactor * visibility[index - 1][0];
						visibilityBuffer[index][1] += smoothFactor * visibility[index - 1][1];
						w += smoothFactor;
					}
					if (k < K - 1) {
						visibilityBuffer[index][0] += smoothFactor * visibility[index + 1][0];
						visibilityBuffer[index][1] += smoothFactor * visibility[index + 1][1];
						w += smoothFactor;
					}
					visibilityBuffer[index][0] /= w;
					visibilityBuffer[index][1] /= w;

					w = 1.0;
					if (i > 0) {
						visibilityBuffer[index][2] += smoothFactor * visibility[index - JK][2];
						visibilityBuffer[index][3] += smoothFactor * visibility[index - JK][3];
						w += smoothFactor;
					}
					if (i < I - 1) {
						visibilityBuffer[index][2] += smoothFactor * visibility[index + JK][2];
						visibilityBuffer[index][3] += smoothFactor * visibility[index + JK][3];
						w += smoothFactor;
					}
					if (k > 0) {
						visibilityBuffer[index][2] += smoothFactor * visibility[index - 1][2];
						visibilityBuffer[index][3] += smoothFactor * visibility[index - 1][3];
						w += smoothFactor;
					}
					if (k < K - 1) {
						visibilityBuffer[index][2] += smoothFactor * visibility[index + 1][2];
						visibilityBuffer[index][3] += smoothFactor * visibility[index + 1][3];
						w += smoothFactor;
					}
					visibilityBuffer[index][2] /= w;
					visibilityBuffer[index][3] /= w;

					w = 1.0;
					if (i > 1) {
						visibilityBuffer[index][4] += smoothFactor * visibility[index - JK][4];
						visibilityBuffer[index][5] += smoothFactor * visibility[index - JK][5];
						w += smoothFactor;
					}
					if (i < I - 1) {
						visibilityBuffer[index][4] += smoothFactor * visibility[index + JK][4];
						visibilityBuffer[index][5] += smoothFactor * visibility[index + JK][5];
						w += smoothFactor;
					}
					if (j > 01) {
						visibilityBuffer[index][4] += smoothFactor * visibility[index - K][4];
						visibilityBuffer[index][5] += smoothFactor * visibility[index - K][5];
						w += smoothFactor;
					}
					if (j < J - 1) {
						visibilityBuffer[index][4] += smoothFactor * visibility[index + K][4];
						visibilityBuffer[index][5] += smoothFactor * visibility[index + K][5];
						w += smoothFactor;
					}
					visibilityBuffer[index][4] /= w;
					visibilityBuffer[index][5] /= w;

					index++;
				}
			}
		}
		double[][] tmp = visibility;
		visibility = visibilityBuffer;
		visibilityBuffer = tmp;
	}

	public double getVisibility(int i, int j, int k, int side) {
		return visibility[index(i, j, k)][side];
	}

	public double getVisibility(int id, int side) {
		return visibility[id][side];
	}

	public void setVisibility(int i, int j, int k, int side, double value) {
		visibility[index(i, j, k)][side] = value;
	}

	public void setVisibility(int id, int side, double value) {
		visibility[id][side] = value;
	}

	void reset() {
		int id = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					parts[id] = -1;
					visited[id] = false;
					visibility[id][0] = 0;
					visibility[id][1] = 0;
					visibility[id][2] = 0;
					id++;
				}
			}
		}

	}

	private int[] findSeed() {

		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					int index = index(i, j, k);
					if (!visited[index] && voxels[index]) {
						return new int[] { i, j, k };
					}
				}
			}
		}
		return null;
	}

	private int floodFill(int i, int j, int k, int part, int[] parts) {
		int count = 0;
		Queue<int[]> queue = new LinkedList<int[]>();
		queue.add(new int[] { i, j, k });
		while (!queue.isEmpty()) {
			int[] cell = queue.remove();

			if (floodFillDo(cell[0], cell[1], cell[2], part, parts)) {
				count++;
				queue.add(new int[] { cell[0] - 1, cell[1], cell[2] });
				queue.add(new int[] { cell[0] + 1, cell[1], cell[2] });
				queue.add(new int[] { cell[0], cell[1] - 1, cell[2] });
				queue.add(new int[] { cell[0], cell[1] + 1, cell[2] });
				queue.add(new int[] { cell[0], cell[1], cell[2] - 1 });
				queue.add(new int[] { cell[0], cell[1], cell[2] + 1 });
			}
		}
		return count;
	}

	private boolean floodFillDo(int i, int j, int k, int part, int[] parts) {
		int index = index(i, j, k);
		if (index == -1)
			return false;
		if (visited[index])
			return false;
		if (!voxels[index])
			return false;
		parts[index] = part;
		visited[index] = true;
		return true;
	}

	public int index(int i, int j, int k) {
		if (i < 0 || i >= I || j < 0 || j >= J || k < 0 || k >= K)
			return -1;
		return k + K * j + JK * i;
	}

	public void export(String path, double cx, double cy, double cz, final double dx, final double dy, final double dz,
			int li, int ui, int lj, int uj, int lk, int uk) {
		int[][][] vertexIndices;
		List<double[]> vertices;
		List<int[]> faces;

		vertexIndices = new int[I + 1][J + 1][K + 1];
		for (int i = 0; i <= I; i++) {
			for (int j = 0; j <= J; j++) {
				for (int k = 0; k <= K; k++) {
					vertexIndices[i][j][k] = -1;
				}
			}
		}
		vertices = new ArrayList<double[]>();
		faces = new ArrayList<int[]>();
		int val0, valm, sum;
		double[] c = new double[] { cx - I * 0.5 * dx, cy - J * 0.5 * dy, cz - K * 0.5 * dz };
		int index;

		for (int i = li; i <= ui; i++) {
			for (int j = lj; j < uj; j++) {
				for (int k = lk; k < uk; k++) {
					index = index(i, j, k, li, ui, lj, uj, lk, uk);
					val0 = index == -1 ? 0 : voxels[index] ? 1 : 0;
					index = index(i - 1, j, k, li, ui, lj, uj, lk, uk);
					valm = index == -1 ? 0 : voxels[index] ? 1 : 0;
					sum = val0 + valm;
					if (sum == 1) {
						faces.add(new int[] { getVertexIndex(i, j, k, vertexIndices, vertices, c, dx, dy, dz),
								getVertexIndex(i, j + 1, k, vertexIndices, vertices, c, dx, dy, dz),
								getVertexIndex(i, j + 1, k + 1, vertexIndices, vertices, c, dx, dy, dz),
								getVertexIndex(i, j, k + 1, vertexIndices, vertices, c, dx, dy, dz)

						});
					}
				}
			}
		}
		for (int i = li; i < ui; i++) {
			for (int j = lj; j <= uj; j++) {
				for (int k = lk; k < uk; k++) {
					index = index(i, j, k, li, ui, lj, uj, lk, uk);
					val0 = index == -1 ? 0 : voxels[index] ? 1 : 0;
					index = index(i, j - 1, k, li, ui, lj, uj, lk, uk);
					valm = index == -1 ? 0 : voxels[index] ? 1 : 0;
					sum = val0 + valm;
					if (sum == 1) {
						faces.add(new int[] { getVertexIndex(i, j, k, vertexIndices, vertices, c, dx, dy, dz),
								getVertexIndex(i + 1, j, k, vertexIndices, vertices, c, dx, dy, dz),
								getVertexIndex(i + 1, j, k + 1, vertexIndices, vertices, c, dx, dy, dz),
								getVertexIndex(i, j, k + 1, vertexIndices, vertices, c, dx, dy, dz) });
					}
				}
			}
		}
		for (int i = li; i < ui; i++) {
			for (int j = lj; j < uj; j++) {
				for (int k = lk; k <= uk; k++) {
					index = index(i, j, k, li, ui, lj, uj, lk, uk);
					val0 = index == -1 ? 0 : voxels[index] ? 1 : 0;
					index = index(i, j, k - 1, li, ui, lj, uj, lk, uk);
					valm = index == -1 ? 0 : voxels[index] ? 1 : 0;
					sum = val0 + valm;
					if (sum == 1) {
						faces.add(new int[] { getVertexIndex(i, j, k, vertexIndices, vertices, c, dx, dy, dz),
								getVertexIndex(i + 1, j, k, vertexIndices, vertices, c, dx, dy, dz),
								getVertexIndex(i + 1, j + 1, k, vertexIndices, vertices, c, dx, dy, dz),
								getVertexIndex(i, j + 1, k, vertexIndices, vertices, c, dx, dy, dz) });
					}
				}
			}
		}

		File f = new File(path);
		File dir = new File(f.getParent());
		dir.mkdirs();
		try (BufferedWriter objwriter = new BufferedWriter(new FileWriter(path))) {
			objwriter.write("# generated by WB_CubeGridExporter");
			objwriter.newLine();
			for (double[] vertex : vertices) {
				objwriter.write("v " + vertex[0] + " " + vertex[1] + " " + vertex[2]);
				objwriter.newLine();
			}
			for (int[] face : faces) {
				objwriter.write("f " + face[0] + " " + face[1] + " " + face[2]);
				objwriter.newLine();
				objwriter.write("f " + face[2] + " " + face[3] + " " + face[0]);
				objwriter.newLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int index(final int i, final int j, final int k, int li, int ui, int lj, int uj, int lk, int uk) {
		if (i > li - 1 && j > lj - 1 && k > lk - 1 && i < ui && j < uj && k < uk) {
			return k + j * K + i * JK;
		} else {
			return -1;
		}
	}

	private int getVertexIndex(int i, int j, int k, int[][][] vertexIndices, List<double[]> vertices, double[] c,
			final double dx, final double dy, final double dz) {

		if (vertexIndices[i][j][k] == -1) {
			vertexIndices[i][j][k] = vertices.size() + 1;
			vertices.add(new double[] { c[0] + i * dx, c[1] + j * dy, c[2] + k * dz });
		}
		return vertexIndices[i][j][k];

	}

	// 6 neighbors
	public boolean isBulk(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1 || !get(index))
			return false;
		for (int di = -1; di <= 1; di++) {
			for (int dj = -1; dj <= 1; dj++) {
				for (int dk = -1; dk <= 1; dk++) {
					index = index(i + di, j + dj, k + dk);
					if (index == -1 || !get(index))
						return false;
				}
			}
		}

		return true;
	}

	public boolean isWall(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1 || !get(index))
			return false;
		return !isBulk(i, j, k);
	}

	public boolean isEdge(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1 || !get(index))
			return false;
		if (isBulk(i, j, k))
			return false;
		int q = 0;
		int r = 0;
		int s = 0;

		if (isWall(i + 1, j, k))
			q++;
		if (isWall(i - 1, j, k))
			q++;
		if (isWall(i, j + 1, k))
			r++;
		if (isWall(i, j - 1, k))
			r++;
		if (isWall(i, j, k + 1))
			s++;
		if (isWall(i, j, k - 1))
			s++;
		int cases = (q == 0 ? 1 : 0) + (r == 0 ? 1 : 0) + (s == 0 ? 1 : 0);
		return cases != 1;

	}

}
