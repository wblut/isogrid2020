package wblut.cubegrid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;

public class WB_CubeGrid {

	int I, J, K, JK, IJK;
	boolean[] voxels;
	boolean[] buffer;
	boolean[] swap;

	int[] colorSourceIndex;
	int[] colorSourceIndexBuffer;
	int[] colorSOurceIndexSwap;
	boolean[] visited;
	int[] parts;
	int[][] partLimits;
	int[] sortedPartIndices;

	int[] accessibility;
	double[] exposure;

	public WB_CubeGrid(int I, int J, int K) {

		this.I = I;
		this.J = J;
		this.K = K;
		JK = J * K;
		IJK = I * JK;

		voxels = new boolean[IJK];
		buffer = new boolean[IJK];
		visited = new boolean[IJK];
		colorSourceIndex = new int[IJK];
		colorSourceIndexBuffer = new int[IJK];
		parts = new int[IJK];
		accessibility = new int[6 * IJK];
		exposure = new double[6 * IJK];
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
		colorSourceIndex = new int[IJK];
		System.arraycopy(cubes.colorSourceIndex, 0, colorSourceIndex, 0, IJK);
		colorSourceIndexBuffer = new int[IJK];
		parts = new int[IJK];
		System.arraycopy(cubes.parts, 0, parts, 0, IJK);
		exposure = new double[6 * IJK];
		System.arraycopy(cubes.exposure, 0, exposure, 0, 6 * IJK);
		accessibility = new int[6 * IJK];
		System.arraycopy(cubes.accessibility, 0, accessibility, 0, 6 * IJK);
	}

	public WB_CubeGrid rotateICC() {
		WB_CubeGrid result = new WB_CubeGrid(I, K, J);
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					result.set(i, k, j, get(i, j, K - 1 - k));
					result.setColorSourceIndex(i, k, j, getColorSourceIndex(i, j, K - 1 - k));
				}
			}
		}
		return result;
	}

	public WB_CubeGrid rotateICW() {
		WB_CubeGrid result = new WB_CubeGrid(I, K, J);
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					result.set(i, k, j, get(i, J - 1 - j, k));
					result.setColorSourceIndex(i, k, j, getColorSourceIndex(i, J - 1 - j, k));
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
					result.set(k, j, i, get(i, j, K - 1 - k));
					result.setColorSourceIndex(k, j, i, getColorSourceIndex(i, j, K - 1 - k));
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
					result.set(k, j, i, get(I - 1 - i, j, k));
					result.setColorSourceIndex(k, j, i, getColorSourceIndex(I - 1 - i, j, k));
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
					result.set(j, i, k, get(i, J - 1 - j, k));
					result.setColorSourceIndex(j, i, k, getColorSourceIndex(i, J - 1 - j, k));
				}
			}
		}
		return result;
	}

	public WB_CubeGrid rotateKCW() {
		WB_CubeGrid result = new WB_CubeGrid(J, I, K);
		for (int k = 0; k < K; k++) {
			for (int j = 0; j < J; j++) {
				for (int i = 0; i < I; i++) {
					result.set(j, i, k, get(I - 1 - i, j, k));
					result.setColorSourceIndex(j, i, k, getColorSourceIndex(I - 1 - i, j, k));
				}
			}
		}
		return result;
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

	public void clearBuffer() {
		int id = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					buffer[id] = false;
					id++;
				}
			}
		}

	}

	public void copyToBuffer() {
		int id = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					buffer[id] = voxels[id];
					id++;
				}
			}
		}

	}

	public void swap() {
		swap = buffer;
		buffer = voxels;
		voxels = swap;
	}

	public void paletteSwap() {
		colorSOurceIndexSwap = colorSourceIndexBuffer;
		colorSourceIndexBuffer = colorSourceIndex;
		colorSourceIndex = colorSOurceIndexSwap;
	}

	public int getI() {
		return I;
	}

	public int getJ() {
		return J;
	}

	public int getK() {
		return K;
	}

	public boolean[] getVoxels() {
		return voxels;

	}

	public void set(int id, boolean b) {
		voxels[id] = b;
	}

	public boolean get(int id) {
		return voxels[id];
	}

	public void set(int i, int j, int k, boolean b) {
		int index = index(i, j, k);
		if (index == -1)
			return;
		voxels[index] = b;
	}

	public void set27Neighborhood(int i, int j, int k, boolean b) {
		for (int di = -1; di <= 1; di++) {
			for (int dj = -1; dj <= 1; dj++) {
				for (int dk = -1; dk <= 1; dk++) {

					set(i + di, j + dj, k + dk, b);
				}
			}
		}

	}

	public void set6Neighborhood(int i, int j, int k, boolean b) {
		set(i, j, k, b);
		set(i + 1, j, k, b);
		set(i - 1, j, k, b);
		set(i, j + 1, k, b);
		set(i, j - 1, k, b);
		set(i, j, k + 1, b);
		set(i, j, k - 1, b);
	}

	public void setInwardNeighborhood(int i, int j, int k, boolean b) {
		int lli = i < I / 2 ? 0 : -1;
		int uli = i > I / 2 ? 0 : 1;
		int llj = j < J / 2 ? 0 : -1;
		int ulj = j > J / 2 ? 0 : 1;
		int llk = k < K / 2 ? 0 : -1;
		int ulk = k > K / 2 ? 0 : 1;

		for (int di = lli; di <= uli; di++) {
			for (int dj = llj; dj <= ulj; dj++) {
				for (int dk = llk; dk <= ulk; dk++) {

					set(i + di, j + dj, k + dk, b);
				}
			}
		}

	}

	public void setOutwardNeighborhood(int i, int j, int k, boolean b) {
		int lli = i > I / 2 ? 0 : -1;
		int uli = i < I / 2 ? 0 : 1;
		int llj = j > J / 2 ? 0 : -1;
		int ulj = j > J / 2 ? 0 : 1;
		int llk = k > K / 2 ? 0 : -1;
		int ulk = k < K / 2 ? 0 : 1;

		for (int di = lli; di <= uli; di++) {
			for (int dj = llj; dj <= ulj; dj++) {
				for (int dk = llk; dk <= ulk; dk++) {

					set(i + di, j + dj, k + dk, b);
				}
			}
		}

	}

	public boolean get(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1)
			return false;
		return voxels[index];
	}

	public void setBuffer(int id, boolean b) {
		buffer[id] = b;
	}

	public void setBuffer27Neighborhood(int i, int j, int k, boolean b) {
		for (int di = -1; di <= 1; di++) {
			for (int dj = -1; dj <= 1; dj++) {
				for (int dk = -1; dk <= 1; dk++) {

					setBuffer(i + di, j + dj, k + dk, b);
				}
			}
		}

	}

	public void setBuffer6Neighborhood(int i, int j, int k, boolean b) {
		setBuffer(i, j, k, b);
		setBuffer(i + 1, j, k, b);
		setBuffer(i - 1, j, k, b);
		setBuffer(i, j + 1, k, b);
		setBuffer(i, j - 1, k, b);
		setBuffer(i, j, k + 1, b);
		setBuffer(i, j, k - 1, b);
	}

	public void setBufferInwardNeighborhood(int i, int j, int k, boolean b) {
		int lli = i < I / 2 ? 0 : -1;
		int uli = i > I / 2 ? 0 : 1;
		int llj = j < J / 2 ? 0 : -1;
		int ulj = j > J / 2 ? 0 : 1;
		int llk = k < K / 2 ? 0 : -1;
		int ulk = k > K / 2 ? 0 : 1;

		for (int di = lli; di <= uli; di++) {
			for (int dj = llj; dj <= ulj; dj++) {
				for (int dk = llk; dk <= ulk; dk++) {

					setBuffer(i + di, j + dj, k + dk, b);
				}
			}
		}

	}

	public void setBufferOutwardNeighborhood(int i, int j, int k, boolean b) {
		int lli = i > I / 2 ? 0 : -1;
		int uli = i < I / 2 ? 0 : 1;
		int llj = j > J / 2 ? 0 : -1;
		int ulj = j > J / 2 ? 0 : 1;
		int llk = k > K / 2 ? 0 : -1;
		int ulk = k < K / 2 ? 0 : 1;

		for (int di = lli; di <= uli; di++) {
			for (int dj = llj; dj <= ulj; dj++) {
				for (int dk = llk; dk <= ulk; dk++) {

					setBuffer(i + di, j + dj, k + dk, b);
				}
			}
		}

	}

	public boolean getBuffer(int id) {
		return buffer[id];
	}

	public void setBuffer(int i, int j, int k, boolean b) {
		int index = index(i, j, k);
		if (index == -1)
			return;
		buffer[index] = b;
	}

	public boolean getBuffer(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1)
			return false;
		return buffer[index];
	}

	public void setColorSourceIndex(int id, int palette) {
		colorSourceIndex[id] = palette;
	}

	public int getColorSourceIndex(int id) {
		return colorSourceIndex[id];
	}

	public void setColorSourceIndex(int i, int j, int k, int palette) {
		int index = index(i, j, k);
		if (index == -1)
			return;
		colorSourceIndex[index] = palette;
	}

	public int getColorSourceIndex(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1)
			return 0;
		return colorSourceIndex[index];
	}

	public void setColorSourceIndexBuffer(int id, int palette) {
		colorSourceIndexBuffer[id] = palette;
	}

	public int getColorSourceIndexBuffer(int id) {
		return colorSourceIndexBuffer[id];
	}

	public void setColorSourceIndexBuffer(int i, int j, int k, int palette) {
		int index = index(i, j, k);
		if (index == -1)
			return;
		colorSourceIndexBuffer[index] = palette;
	}

	public int getColorSourceIndexBuffer(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1)
			return 0;
		return colorSourceIndexBuffer[index];
	}

	public void setPart(int id, int part) {
		parts[id] = part;
	}

	public int getPart(int id) {
		return parts[id];
	}

	public void setPart(int i, int j, int k, int part) {
		int index = index(i, j, k);
		if (index == -1)
			return;
		parts[index] = part;
	}

	public int getPart(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1)
			return -1;
		return parts[index];
	}

	public int[] getPartLimits(int id) {
		if (partLimits == null || id < 0 || id >= partLimits.length)
			return null;
		return partLimits[id];

	}

	public void setAccessibility(int id, int side, int value) {
		accessibility[6 * id + side] = value;
	}

	public int getAccessibility(int id, int side) {
		return accessibility[6 * id + side];
	}

	public void setAccessibility(int i, int j, int k, int side, int value) {
		int index = index(i, j, k);
		if (index == -1)
			return;
		accessibility[6 * index + side] = value;
	}

	public int getAccessibility(int i, int j, int k, int side) {
		int index = index(i, j, k);
		if (index == -1)
			return 0;
		return accessibility[6 * index + side];
	}

	public void setExposure(int id, int side, double value) {
		exposure[6 * (id) + side] = value;
	}

	public double getExposure(int id, int side) {
		return exposure[6 * (id) + side];
	}

	public void setExposure(int i, int j, int k, int side, double value) {
		int index = index(i, j, k);
		if (index == -1)
			return;
		exposure[6 * index + side] = value;
	}

	public double getExposure(int i, int j, int k, int side) {
		int index = index(i, j, k);
		if (index == -1)
			return 0;// new double[] { 0, 0, 0 };
		return exposure[6 * index + side];
	}

	public void rescaleExposure(double value) {
		double expMax = Double.NEGATIVE_INFINITY;
		double expMin = Double.POSITIVE_INFINITY;
		double r, g, b;
		for (int i = 0; i < 6 * IJK; i++) {
			r = exposure[i];// [0];
			g = exposure[i];// [1];
			b = exposure[i];// [2];
			expMax = Math.max(0.299 * r + 0.587 * g + 0.114 * b, expMax);
			expMin = Math.min(0.299 * r + 0.587 * g + 0.114 * b, expMin);
		}
		double exp;
		for (int i = 0; i < 6 * IJK; i++) {
			r = exposure[i];// [0];
			g = exposure[i];// [1];
			b = exposure[i];// [2];
			exp = 0.299 * r + 0.587 * g + 0.114 * b;

			exposure[i] = exp == 0 ? 0
					: ((expMax - expMin == 0) ? r / exp * value : (exp - expMin) / (expMax - expMin) * r / exp * value);
			// exposure[i][1] =exp==0?0:((expMax - expMin==0)?r/exp * value: (exp - expMin)
			// / (expMax - expMin) * g/exp * value);
			// exposure[i][2] =exp==0?0:((expMax - expMin==0)?r/exp * value: (exp - expMin)
			// / (expMax - expMin) * b/exp * value);
		}

	}

	public void rescaleExposure(double value, double bias, double gain, double f) {
		double expMax = Double.NEGATIVE_INFINITY;
		double expMin = Double.POSITIVE_INFINITY;
		double r, g, b;
		for (int i = 0; i < 6 * IJK; i++) {
			r = exposure[i];// [0];
			g = exposure[i];// [1];
			b = exposure[i];// [2];
			expMax = Math.max(0.299 * r + 0.587 * g + 0.114 * b, expMax);
			expMin = Math.min(0.299 * r + 0.587 * g + 0.114 * b, expMin);
		}
		double exp;
		for (int i = 0; i < 6 * IJK; i++) {
			r = exposure[i];// [0];
			g = exposure[i];// [1];
			b = exposure[i];// [2];
			exp = 0.299 * r + 0.587 * g + 0.114 * b;

			exposure[i] = exp == 0 ? 0
					: f * value
							* gain(bias(
									((expMax - expMin == 0) ? r / exp : (exp - expMin) / (expMax - expMin) * r / exp),
									bias), gain);
			// exposure[i][1] =exp==0?0:f*value*gain(bias(((expMax - expMin==0)?g/exp : (exp
			// - expMin) / (expMax - expMin) * g/exp ),bias),gain);;
			// exposure[i][2] =exp==0?0:f*value*gain(bias(((expMax - expMin==0)?b/exp : (exp
			// - expMin) / (expMax - expMin) * b/exp ),bias),gain);;
		}

	}

	public double bias(double x, double b) {
		return x / ((1.0 / b - 2.0) * (1.0 - x) + 1.0);
	}

	public double gain(double x, double g) {
		if (x <= 0.5) {
			return 0.5 * bias(2.0 * x, g);
		} else {
			return 0.5 + 0.5 * bias(2 * x - 1, 1 - g);
		}

	}

	public void and(int id, boolean b) {
		voxels[id] &= b;
	}

	public void and(int i, int j, int k, boolean b) {
		int index = index(i, j, k);
		if (index == -1)
			return;

		voxels[index] &= b;
	}

	public void or(int id, boolean b) {
		voxels[id] |= b;
	}

	public void or(int i, int j, int k, boolean b) {
		int index = index(i, j, k);
		if (index == -1)
			return;
		voxels[index] |= b;
	}

	public void xor(int id, boolean b) {
		voxels[id] ^= b;
	}

	public void xor(int i, int j, int k, boolean b) {
		int index = index(i, j, k);
		if (index == -1)
			return;
		voxels[index] ^= b;
	}

	public void not(int id) {
		voxels[id] = !voxels[id];
	}

	public void not(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1)
			return;
		voxels[index] = !voxels[index];
	}

	public void copy(int si, int sj, int sk, int di, int dj, int dk, int ti, int tj, int tk) {

		int tindex, sindex;
		for (int i = 0; i < di; i++) {
			for (int j = 0; j < dj; j++) {
				for (int k = 0; k < dk; k++) {
					tindex = index(ti + i, tj + j, tk + k);
					sindex = index(si + i, sj + j, sk + k);

					if (tindex > -1) {
						voxels[tindex] = (sindex > -1) ? voxels[sindex] : false;
					}
				}
			}
		}

	}

	public int labelParts() {
		List<int[]> partLimitList = new ArrayList<int[]>();
		HashMap<Integer, Integer> partSizes = new HashMap<Integer, Integer>();
		for (int id = 0; id < IJK; id++) {
			visited[id] = false;
		}

		int currentPart = 0;
		int[] indices = null;
		do {
			indices = findSeed();
			if (indices != null) {
				int[] limits = new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE,
						Integer.MAX_VALUE, Integer.MIN_VALUE };
				partLimitList.add(limits);
				int size = floodFill(indices[0], indices[1], indices[2], indices[3], currentPart, parts, limits);
				partSizes.put(currentPart, size);
				currentPart++;
			}

		} while (indices != null);
		partSizes = sortByValues(partSizes);
		sortedPartIndices = new int[currentPart];
		Set<Map.Entry<Integer, Integer>> set = partSizes.entrySet();
		Iterator<Map.Entry<Integer, Integer>> iterator = set.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			Map.Entry<Integer, Integer> me = iterator.next();
			sortedPartIndices[index++] = me.getKey();

		}
		partLimits = new int[currentPart][6];
		index = 0;
		for (int[] limits : partLimitList) {
			partLimits[index++] = limits;
		}

		return currentPart;
	}

	private int[] findSeed() {
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					int index = index(i, j, k);
					if (!visited[index] && voxels[index]) {
						return new int[] { i, j, k, colorSourceIndex[index] };
					}
				}
			}
		}
		return null;
	}

	private int floodFill(int i, int j, int k, int colors, int part, int[] parts, int[] limits) {
		int count = 0;
		Queue<int[]> queue = new LinkedList<int[]>();
		queue.add(new int[] { i, j, k, colors });
		while (!queue.isEmpty()) {
			int[] cell = queue.remove();

			if (floodFillDo(cell[0], cell[1], cell[2], cell[3], part, parts)) {
				limits[0] = Math.min(limits[0], cell[0]);
				limits[1] = Math.max(limits[1], cell[0]);
				limits[2] = Math.min(limits[2], cell[1]);
				limits[3] = Math.max(limits[3], cell[1]);
				limits[4] = Math.min(limits[4], cell[2]);
				limits[5] = Math.max(limits[5], cell[2]);
				count++;
				queue.add(new int[] { cell[0] - 1, cell[1], cell[2], cell[3] });
				queue.add(new int[] { cell[0] + 1, cell[1], cell[2], cell[3] });
				queue.add(new int[] { cell[0], cell[1] - 1, cell[2], cell[3] });
				queue.add(new int[] { cell[0], cell[1] + 1, cell[2], cell[3] });
				queue.add(new int[] { cell[0], cell[1], cell[2] - 1, cell[3] });
				queue.add(new int[] { cell[0], cell[1], cell[2] + 1, cell[3] });
			}
		}
		return count;
	}

	private boolean floodFillDo(int i, int j, int k, int colors, int part, int[] parts) {
		int index = index(i, j, k);

		if (index == -1)
			return false;
		if (visited[index])
			return false;
		if (!voxels[index])
			return false;
		if (this.colorSourceIndex[index] != colors)
			return false;
		parts[index] = part;
		visited[index] = true;
		return true;
	}

	private static HashMap<Integer, Integer> sortByValues(HashMap<Integer, Integer> map) {
		List<Map.Entry<Integer, Integer>> list = new LinkedList<Map.Entry<Integer, Integer>>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});

		HashMap<Integer, Integer> sortedHashMap = new LinkedHashMap<Integer, Integer>();
		for (Iterator<Map.Entry<Integer, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Integer> entry = it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	public int[] getSortedPartIndices() {
		if (partLimits == null)
			return null;
		return sortedPartIndices;

	}

	public void setExposureByVisibility(int cutoff, double xEye, double yEye, double zEye, double xCenter,
			double yCenter, double zCenter, final double dx, final double dy, final double dz) {
		double[] c = new double[] { xCenter - I * 0.5 * dx, yCenter - J * 0.5 * dy, zCenter - K * 0.5 * dz };
		int index = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {

					double xRay = c[0] + (i + 0.5) * dx - xEye;
					double yRay = c[1] + (j + 0.5) * dy - yEye;
					double zRay = c[2] + (k + 0.5) * dz - zEye;

					double dRay = Math.sqrt(xRay * xRay + yRay * yRay + zRay * zRay);

					xRay /= dRay;
					yRay /= dRay;
					zRay /= dRay;

					exposure[6 * index] = isVisible(cutoff, c[0] + (i - 0.01) * dx, c[1] + (j + 0.5) * dy,
							c[2] + (k + 0.5) * dz, xEye, yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)
									? Math.max(0, cutoff * xRay)
									: 0;
					exposure[6 * index + 1] = isVisible(cutoff, c[0] + (i + 1.01) * dx, c[1] + (j + 0.5) * dy,
							c[2] + (k + 0.5) * dz, xEye, yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)
									? Math.max(0, -cutoff * xRay)
									: 0;

					exposure[6 * index + 2] = isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j - 0.01) * dy,
							c[2] + (k + 0.5) * dz, xEye, yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)
									? Math.max(0, cutoff * yRay)
									: 0;
					exposure[6 * index + 3] = isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j + 1.01) * dy,
							c[2] + (k + 0.5) * dz, xEye, yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)
									? Math.max(0, -cutoff * yRay)
									: 0;

					exposure[6 * index + 4] = isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j + 0.5) * dy,
							c[2] + (k - 0.01) * dz, xEye, yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)
									? Math.max(0, cutoff * zRay)
									: 0;
					exposure[6 * index + 5] = isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j + 0.5) * dy,
							c[2] + (k + 1.01) * dz, xEye, yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)
									? Math.max(0, -cutoff * zRay)
									: 0;

					index++;
				}

			}
		}

	}

	public void addPointLight(double intensityR, double intensityG, double intensityB, double falloff, int cutoff,
			double xEye, double yEye, double zEye, double xCenter, double yCenter, double zCenter, final double dx,
			final double dy, final double dz) {
		double[] c = new double[] { xCenter - I * 0.5 * dx, yCenter - J * 0.5 * dy, zCenter - K * 0.5 * dz };
		int index = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {

					double xRay = c[0] + (i + 0.5) * dx - xEye;
					double yRay = c[1] + (j + 0.5) * dy - yEye;
					double zRay = c[2] + (k + 0.5) * dz - zEye;

					double dRay = Math.sqrt(xRay * xRay + yRay * yRay + zRay * zRay);

					xRay /= dRay;
					yRay /= dRay;
					zRay /= dRay;

					double fo = Math.min(1.0, falloff * falloff / (dRay * dRay));
					if (isVisible(cutoff, c[0] + (i - 0.01) * dx, c[1] + (j + 0.5) * dy, c[2] + (k + 0.5) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {
						exposure[6 * index] += Math.max(0, intensityR * xRay * fo);
						// exposure[6 * index][1] += Math.max(0, intensityG * xRay * fo);
						// exposure[6 * index][2] += Math.max(0, intensityB * xRay * fo);

					}

					if (isVisible(cutoff, c[0] + (i + 1.01) * dx, c[1] + (j + 0.5) * dy, c[2] + (k + 0.5) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {

						exposure[6 * index + 1] += Math.max(0, -intensityR * xRay * fo);
						// exposure[6 * index + 1][1] += Math.max(0, -intensityG * xRay * fo);
						// exposure[6 * index + 1][2] += Math.max(0, -intensityB * xRay * fo);
					}
					if (isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j - 0.01) * dy, c[2] + (k + 0.5) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {
						exposure[6 * index + 2] += Math.max(0, intensityR * yRay * fo);
						// exposure[6 * index + 2][1] += Math.max(0, intensityG * yRay * fo);
						// exposure[6 * index + 2][2] += Math.max(0, intensityB * yRay * fo);
					}
					if (isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j + 1.01) * dy, c[2] + (k + 0.5) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {
						exposure[6 * index + 3] += Math.max(0, -intensityR * yRay * fo);
						// exposure[6 * index + 3][1] += Math.max(0, -intensityG * yRay * fo);
						// exposure[6 * index + 3][2] += Math.max(0, -intensityB * yRay * fo);
					}
					if (isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j + 0.5) * dy, c[2] + (k - 0.01) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {
						exposure[6 * index + 4] += Math.max(0, intensityR * zRay * fo);
						// exposure[6 * index + 4][1] += Math.max(0, intensityG * zRay * fo);
						// exposure[6 * index + 4][2] += Math.max(0, intensityB * zRay * fo);
					}
					if (isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j + 0.5) * dy, c[2] + (k + 1.01) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {
						exposure[6 * index + 5] += Math.max(0, -intensityR * zRay * fo);
						// exposure[6 * index + 5][1] += Math.max(0, -intensityG * zRay * fo);
						// exposure[6 * index + 5][2] += Math.max(0, -intensityB * zRay * fo);
					}
					index++;
				}

			}
		}

	}

	public void addDirectionalLight(double intensityR, double intensityG, double intensityB, int cutoff, double xDir,
			double yDir, double zDir, double xCenter, double yCenter, double zCenter, final double dx, final double dy,
			final double dz) {
		double[] c = new double[] { xCenter - I * 0.5 * dx, yCenter - J * 0.5 * dy, zCenter - K * 0.5 * dz };
		int index = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {

					double xRay = xDir;
					double yRay = yDir;
					double zRay = zDir;

					double dRay = Math.sqrt(xRay * xRay + yRay * yRay + zRay * zRay);

					xRay /= dRay;
					yRay /= dRay;
					zRay /= dRay;

					double xEye = c[0] + (i + 0.5) * dx - 10 * I * dx * xDir;
					double yEye = c[1] + (j + 0.5) * dy - 10 * J * dy * yDir;
					double zEye = c[2] + (k + 0.5) * dz - 10 * K * dz * zDir;

					double fo = 1.0;
					if (isVisible(cutoff, c[0] + (i - 0.01) * dx, c[1] + (j + 0.5) * dy, c[2] + (k + 0.5) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {
						exposure[6 * index] += Math.max(0, intensityR * xRay * fo);
						// exposure[6 * index][1] += Math.max(0, intensityG * xRay * fo);
						// exposure[6 * index][2] += Math.max(0, intensityB * xRay * fo);

					}

					if (isVisible(cutoff, c[0] + (i + 1.01) * dx, c[1] + (j + 0.5) * dy, c[2] + (k + 0.5) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {

						exposure[6 * index + 1] += Math.max(0, -intensityR * xRay * fo);
						// exposure[6 * index + 1][1] += Math.max(0, -intensityG * xRay * fo);
						// exposure[6 * index + 1][2] += Math.max(0, -intensityB * xRay * fo);
					}
					if (isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j - 0.01) * dy, c[2] + (k + 0.5) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {
						exposure[6 * index + 2] += Math.max(0, intensityR * yRay * fo);
						// exposure[6 * index + 2][1] += Math.max(0, intensityG * yRay * fo);
						// exposure[6 * index + 2][2] += Math.max(0, intensityB * yRay * fo);
					}
					if (isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j + 1.01) * dy, c[2] + (k + 0.5) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {
						exposure[6 * index + 3] += Math.max(0, -intensityR * yRay * fo);
						// exposure[6 * index + 3][1] += Math.max(0, -intensityG * yRay * fo);
						// exposure[6 * index + 3][2] += Math.max(0, -intensityB * yRay * fo);
					}
					if (isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j + 0.5) * dy, c[2] + (k - 0.01) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {
						exposure[6 * index + 4] += Math.max(0, intensityR * zRay * fo);
						// exposure[6 * index + 4][1] += Math.max(0, intensityG * zRay * fo);
						// exposure[6 * index + 4][2] += Math.max(0, intensityB * zRay * fo);
					}
					if (isVisible(cutoff, c[0] + (i + 0.5) * dx, c[1] + (j + 0.5) * dy, c[2] + (k + 1.01) * dz, xEye,
							yEye, zEye, xCenter, yCenter, zCenter, dx, dy, dz)) {
						exposure[6 * index + 5] += Math.max(0, -intensityR * zRay * fo);
						// exposure[6 * index + 5][1] += Math.max(0, -intensityG * zRay * fo);
						// exposure[6 * index + 5][2] += Math.max(0, -intensityB * zRay * fo);
					}

					index++;
				}

			}
		}

	}

	public void setMaxAccessibilityAtBoundaries(int cutoff) {
		int index = 0;
		for (int i = 0; i < I; i++) {
			index = i * JK;
			for (int k = 0; k < K; k++) {
				for (int j = 0; j < J; j++) {
					accessibility[6 * (index + j * K) + 2] = cutoff;
					if (get(index + j * K)) {
						break;
					}
				}
				for (int j = J - 1; j >= 0; j--) {
					accessibility[6 * (index + j * K) + 3] = cutoff;
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
					accessibility[6 * (index + k) + 4] = cutoff;
					if (get(index + k)) {

						break;
					}
				}
				for (int k = K - 1; k >= 0; k--) {
					accessibility[6 * (index + k) + 5] = cutoff;
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
					accessibility[6 * (index + i * JK)] = cutoff;
					if (get(index + i * JK)) {

						break;
					}
				}
				for (int i = I - 1; i >= 0; i--) {
					accessibility[6 * (index + i * JK) + 1] = cutoff;
					if (get(index + i * JK)) {

						break;
					}
				}
				index++;
			}
		}
	}

	public void scanInteriorCubes(int cutoff,boolean interior) {

		scanInteriorCubes(cutoff, 1,interior);
	}

	public void scanInteriorCubes(int cutoff, float factor, boolean interior) {
		int index = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					if (getAccessibility(index, 0) < cutoff)
						setAccessibility(index, 0, (int) (factor * scanILeft(i, index, cutoff)));
					if (getAccessibility(index, 1) < cutoff)
						setAccessibility(index, 1, (int) (factor * scanIRight(i, index, cutoff)));
					if (getAccessibility(index, 2) < cutoff)
						setAccessibility(index, 2, (int) (factor * scanJLeft(j, index, cutoff)));
					if (getAccessibility(index, 3) < cutoff)
						setAccessibility(index, 3, (int) (factor * scanJRight(j, index, cutoff)));
					if (getAccessibility(index, 4) < cutoff)
						setAccessibility(index, 4, (int) (factor * scanKLeft(k, index, cutoff)));
					if (getAccessibility(index, 5) < cutoff)
						setAccessibility(index, 5, (int) (factor * scanKRight(k, index, cutoff)));

					index++;
				}
			}
		}
		if (interior) {
			index = 0;
			for (int i = 0; i < I; i++) {
				for (int j = 0; j < J; j++) {
					for (int k = 0; k < K; k++) {
						if (get(index)) {
							if ((getAccessibility(index, 0) > 0 && getAccessibility(index, 0) < 3)
								||(getAccessibility(index, 1) > 0 && getAccessibility(index, 1) < 3)
								||(getAccessibility(index, 2) > 0 && getAccessibility(index, 2) < 3)
								||(getAccessibility(index, 3) > 0 && getAccessibility(index, 3) < 3)
								||(getAccessibility(index, 4) > 0 && getAccessibility(index, 4) < 3)
								||(getAccessibility(index, 5) > 0 && getAccessibility(index, 5) < 3)) {
								
								setAccessibility(index, 0, (int) (factor * cutoff));
							
								setAccessibility(index, 1, (int) (factor * cutoff));
							
								setAccessibility(index, 2, (int) (factor * cutoff));
							
								setAccessibility(index, 3, (int) (factor * cutoff));
						
								setAccessibility(index, 4, (int) (factor * cutoff));
							
								setAccessibility(index, 5, (int) (factor * cutoff));
							}
						}
						index++;
					}
				}
			}
		}
	}

	int scanILeft(int i, int index, int cutoff) {
		int count = 0;
		for (int ci = i - 1; ci >= 0; ci--) {
			if (get(index + (ci - i) * JK)) {
				break;
			}
			count++;
			if (count >= cutoff)
				break;
		}
		return count;
	}

	int scanIRight(int i, int index, int cutoff) {
		int count = 0;
		for (int ci = i + 1; ci < I; ci++) {
			if (get(index + (ci - i) * JK)) {
				break;
			}
			count++;
			if (count >= cutoff)
				break;
		}
		return count;
	}

	int scanJLeft(int j, int index, int cutoff) {
		int count = 0;

		for (int cj = j - 1; cj >= 0; cj--) {
			if (get(index + (cj - j) * K)) {
				break;
			}
			count++;
			if (count >= cutoff)
				break;
		}
		return count;
	}

	int scanJRight(int j, int index, int cutoff) {
		int count = 0;
		for (int cj = j + 1; cj < J; cj++) {
			if (get(index + (cj - j) * K)) {
				break;
			}
			count++;
			if (count >= cutoff)
				break;
		}
		return count;
	}

	int scanKLeft(int k, int index, int cutoff) {
		int count = 0;
		for (int ck = k - 1; ck >= 0; ck--) {
			if (get(index + ck - k)) {
				break;
			}
			count++;
			if (count >= cutoff)
				break;
		}
		return count;
	}

	int scanKRight(int k, int index, int cutoff) {
		int count = 0;
		for (int ck = k + 1; ck < K; ck++) {
			if (get(index + ck - k)) {
				break;
			}
			count++;
			if (count >= cutoff)
				break;
		}
		return count;
	}

	public void calculateExposure(double di, double dj, double dk) {
		double[] dv = new double[] { di, di, dj, dj, dk, dk };
		int index = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					for (int side = 0; side < 6; side++) {
						exposure[6 * index + side] = dv[side] * accessibility[6 * index + side];

					}
					index++;
				}
			}
		}
	}

	public void calculateExposure(double di, double dj, double dk, double diback, double djback, double dkback) {
		double[] dv = new double[] { diback, di, djback, dj, dkback, dk };
		int index = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					for (int side = 0; side < 6; side++) {
						exposure[6 * index + side] = dv[side] * accessibility[6 * index + side];

					}
					index++;
				}
			}
		}
	}

	public void diffuseExposure(double bf, int cutoff) {
		double[] exposureBuffer = new double[6 * IJK];
		int index = 0;
		double[] bounceFactor = new double[6];
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					if (voxels[index]) {
						for (int side = 0; side < 6; side++) {
							// for (int c = 0; c < 3; c++) {
							exposureBuffer[6 * (index) + side] = exposure[6 * (index) + side];
							// }
							bounceFactor[side] = Math.max(0.0,
									1.0 - accessibility[6 * (index) + side] / (double) cutoff) * bf;
						}

						for (int side = 0; side < 6; side++) {
							for (int oside = 0; oside < 6; oside++) {
								if (side / 2 != oside / 2) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + side] += bounceFactor[side]
											* exposure[6 * (index) + oside];
									// }
								}
							}
						}

						if (j > 0) {
							// for (int c = 0; c < 3; c++) {
							exposureBuffer[6 * (index)] += bounceFactor[0] * exposure[6 * (index - K) + 2];
							exposureBuffer[6 * (index) + 1] += bounceFactor[1] * exposure[6 * (index - K) + 2];

							// }
						}
						if (j < J - 1) {
							// for (int c = 0; c < 3; c++) {

							exposureBuffer[6 * (index)] += bounceFactor[0] * exposure[6 * (index + K) + 3];
							exposureBuffer[6 * (index) + 1] += bounceFactor[1] * exposure[6 * (index + K) + 3];

							// }
						}
						if (k > 0) {
							// for (int c = 0; c < 3; c++) {

							exposureBuffer[6 * (index)] += bounceFactor[0] * exposure[6 * (index - 1) + 4];
							exposureBuffer[6 * (index) + 1] += bounceFactor[1] * exposure[6 * (index - 1) + 4];

							// }
						}
						if (k < K - 1) {
							// for (int c = 0; c < 3; c++) {

							exposureBuffer[6 * (index)] += bounceFactor[0] * exposure[6 * (index + 1) + 5];
							exposureBuffer[6 * (index) + 1] += bounceFactor[1] * exposure[6 * (index + 1) + 5];

							// }
						}
						// exposureBuffer[6 * (index)] = Math.min(exposureBuffer[6 * (index)], cutoff);
						// exposureBuffer[6 * (index) + 1] = Math.min(exposureBuffer[6 * (index) + 1],
						// cutoff);

						if (i > 0) {
							// for (int c = 0; c < 3; c++) {

							exposureBuffer[6 * (index) + 2] += bounceFactor[2] * exposure[6 * (index - JK)];
							exposureBuffer[6 * (index) + 3] += bounceFactor[3] * exposure[6 * (index - JK)];

							// }
						}
						if (i < I - 1) {
							// for (int c = 0; c < 3; c++) {

							exposureBuffer[6 * (index) + 2] += bounceFactor[2] * exposure[6 * (index + JK) + 1];
							exposureBuffer[6 * (index) + 3] += bounceFactor[3] * exposure[6 * (index + JK) + 1];

							// }
						}
						if (k > 0) {
							// for (int c = 0; c < 3; c++) {

							exposureBuffer[6 * (index) + 2] += bounceFactor[2] * exposure[6 * (index - 1) + 4];
							exposureBuffer[6 * (index) + 3] += bounceFactor[3] * exposure[6 * (index - 1) + 4];

							// }
						}
						if (k < K - 1) {
							// for (int c = 0; c < 3; c++) {

							exposureBuffer[6 * (index) + 2] += bounceFactor[2] * exposure[6 * (index + 1) + 5];
							exposureBuffer[6 * (index) + 3] += bounceFactor[3] * exposure[6 * (index + 1) + 5];

							// }
						}
						// exposureBuffer[6 * (index) + 2] = Math.min(exposureBuffer[6 * (index) + 2],
						// cutoff);
						// exposureBuffer[6 * (index) + 3] = Math.min(exposureBuffer[6 * (index) + 3],
						// cutoff);

						if (i > 0) {
							// for (int c = 0; c < 3; c++) {

							exposureBuffer[6 * (index) + 4] += bounceFactor[4] * exposure[6 * (index - JK)];
							exposureBuffer[6 * (index) + 5] += bounceFactor[5] * exposure[6 * (index - JK)];

							// }
						}
						if (i < I - 1) {
							// for (int c = 0; c < 3; c++) {

							exposureBuffer[6 * (index) + 4] += bounceFactor[4] * exposure[6 * (index + JK) + 1];
							exposureBuffer[6 * (index) + 5] += bounceFactor[5] * exposure[6 * (index + JK) + 1];

							// }
						}
						if (j > 0) {
							// for (int c = 0; c < 3; c++) {

							exposureBuffer[6 * (index) + 4] += bounceFactor[4] * exposure[6 * (index - K) + 2];
							exposureBuffer[6 * (index) + 5] += bounceFactor[5] * exposure[6 * (index - K) + 2];

							// }
						}
						if (j < J - 1) {
							// for (int c = 0; c < 3; c++) {

							exposureBuffer[6 * (index) + 4] += bounceFactor[4] * exposure[6 * (index + K) + 3];
							exposureBuffer[6 * (index) + 5] += bounceFactor[5] * exposure[6 * (index + K) + 3];

							// }
						}
						// exposureBuffer[6 * (index) + 4] = Math.min(exposureBuffer[6 * (index) + 4],
						// cutoff);
						// exposureBuffer[6 * (index) + 5] = Math.min(exposureBuffer[6 * (index) + 5],
						// cutoff);

					}
					index++;
				}
			}
		}
		double[] tmp = exposure;
		exposure = exposureBuffer;
		exposureBuffer = tmp;
	}

	public void smoothExposure(double smoothFactor) {

		double[] exposureBuffer = new double[6 * IJK];

		int index = 0;
		double w;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					for (int side = 0; side < 6; side++) {
						// for (int c = 0; c < 3; c++) {
						exposureBuffer[6 * (index) + side] = exposure[6 * (index) + side];
						// }
					}
					if (voxels[index]) {
						w = 1.0;
						if (i == 0 || !voxels[index - JK]) {// left side open
							if (j > 0 && voxels[index - K] && (i == 0 || !voxels[index - K - JK])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index)] += smoothFactor * exposure[6 * (index - K)];
								// }
								w += smoothFactor;
							}
							if (j < J - 1 && voxels[index + K] && (i == 0 || !voxels[index + K - JK])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index)] += smoothFactor * exposure[6 * (index + K)];
								// }
								w += smoothFactor;
							}
							if (k > 0 && voxels[index - 1] && (i == 0 || !voxels[index - 1 - JK])) {

								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index)] += smoothFactor * exposure[6 * (index - 1)];
								// }
								w += smoothFactor;
							}
							if (k < K - 1 && voxels[index + 1] && (i == 0 || !voxels[index + 1 - JK])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index)] += smoothFactor * exposure[6 * (index + 1)];
								// }
								w += smoothFactor;

							}
						}

						// for (int c = 0; c < 3; c++) {
						exposureBuffer[6 * index] /= w;
						// }

						w = 1.0;
						if (i == I - 1 || !voxels[index + JK]) {// right side open
							if (j > 0 && voxels[index - K] && (i == I - 1 || !voxels[index - K + JK])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 1] += smoothFactor * exposure[6 * (index - K) + 1];
								// }
								w += smoothFactor;
							}
							if (j < J - 1 && voxels[index + K] && (i == I - 1 || !voxels[index + K + JK])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 1] += smoothFactor * exposure[6 * (index + K) + 1];
								// }
								w += smoothFactor;
							}
							if (k > 0 && voxels[index - 1] && (i == I - 1 || !voxels[index - 1 + JK])) {

								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 1] += smoothFactor * exposure[6 * (index - 1) + 1];
								// }
								w += smoothFactor;
							}
							if (k < K - 1 && voxels[index + 1] && (i == I - 1 || !voxels[index + 1 + JK])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 1] += smoothFactor * exposure[6 * (index + 1) + 1];
								// }
								w += smoothFactor;

							}

						}

						// for (int c = 0; c < 3; c++) {
						exposureBuffer[6 * index + 1] /= w;
						// }

						w = 1.0;
						if (j == 0 || !voxels[index - K]) {// bottom side open

							if (i > 0 && voxels[index - JK] && (j == 0 || !voxels[index - JK - K])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 2] += smoothFactor * exposure[6 * (index - JK) + 2];
								// }
								w += smoothFactor;

							}
							if (i < I - 1 && voxels[index + JK] && (j == 0 || !voxels[index + JK - K])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 2] += smoothFactor * exposure[6 * (index + JK) + 2];
								// }
								w += smoothFactor;
							}
							if (k > 0 && voxels[index - 1] && (j == 0 || !voxels[index - 1 - K])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 2] += smoothFactor * exposure[6 * (index - 1) + 2];
								// }
								w += smoothFactor;
							}
							if (k < K - 1 && voxels[index + 1] && (j == 0 || !voxels[index + 1 - K])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 2] += smoothFactor * exposure[6 * (index + 1) + 2];
								// }
								w += smoothFactor;
							}

						}
						// for (int c = 0; c < 3; c++) {
						exposureBuffer[6 * (index) + 2] /= w;
						// }

						w = 1.0;
						if (j == J - 1 || !voxels[index + K]) {// top side open

							if (i > 0 && voxels[index - JK] && (j == J - 1 || !voxels[index - JK + K])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 3] += smoothFactor * exposure[6 * (index - JK) + 3];
								// }
								w += smoothFactor;

							}
							if (i < I - 1 && voxels[index + JK] && (j == J - 1 || !voxels[index + JK + K])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 3] += smoothFactor * exposure[6 * (index + JK) + 3];
								// }
								w += smoothFactor;
							}
							if (k > 0 && voxels[index - 1] && (j == J - 1 || !voxels[index - 1 + K])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 3] += smoothFactor * exposure[6 * (index - 1) + 3];
								// }
								w += smoothFactor;
							}
							if (k < K - 1 && voxels[index + 1] && (j == J - 1 || !voxels[index + 1 + K])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 3] += smoothFactor * exposure[6 * (index + 1) + 3];
								// }
								w += smoothFactor;
							}

						}
						// for (int c = 0; c < 3; c++) {
						exposureBuffer[6 * (index) + 3] /= w;
						// }

						w = 1.0;
						if (k == 0 || !voxels[index - 1]) {// back open

							if (i > 1 && voxels[index - JK] && (k == 0 || !voxels[index - JK - 1])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 4] += smoothFactor * exposure[6 * (index - JK) + 4];
								// }
								w += smoothFactor;
							}
							if (i < I - 1 && voxels[index + JK] && (k == 0 || !voxels[index + JK - 1])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 4] += smoothFactor * exposure[6 * (index + JK) + 4];
								// }
								w += smoothFactor;
							}
							if (j > 0 && voxels[index - K] && (k == 0 || !voxels[index - K - 1])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 4] += smoothFactor * exposure[6 * (index - K) + 4];
								// }
								w += smoothFactor;
							}
							if (j < J - 1 && voxels[index + K] && (k == 0 || !voxels[index + K - 1])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 4] += smoothFactor * exposure[6 * (index + K) + 4];
								// }
								w += smoothFactor;
							}
						}
						// for (int c = 0; c < 3; c++) {
						exposureBuffer[6 * (index) + 4] /= w;
						// }

						w = 1.0;
						if (k == K - 1 || !voxels[index + 1]) {// front open

							if (i > 1 && voxels[index - JK] && (k == K - 1 || !voxels[index - JK + 1])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 5] += smoothFactor * exposure[6 * (index - JK) + 5];
								// }
								w += smoothFactor;
							}
							if (i < I - 1 && voxels[index + JK] && (k == K - 1 || !voxels[index + JK + 1])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 5] += smoothFactor * exposure[6 * (index + JK) + 5];
								// }
								w += smoothFactor;
							}
							if (j > 0 && voxels[index - K] && (k == K - 1 || !voxels[index - K + 1])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 5] += smoothFactor * exposure[6 * (index - K) + 5];
								// }
								w += smoothFactor;
							}
							if (j < J - 1 && voxels[index + K] && (k == K - 1 || !voxels[index + K + 1])) {
								// for (int c = 0; c < 3; c++) {
								exposureBuffer[6 * (index) + 5] += smoothFactor * exposure[6 * (index + K) + 5];
								// }
								w += smoothFactor;
							}
						}
						// for (int c = 0; c < 3; c++) {
						exposureBuffer[6 * (index) + 5] /= w;
						// }

					}

					index++;
				}
			}
		}
		double[] tmp = exposure;
		exposure = exposureBuffer;
		exposureBuffer = tmp;
	}

	public void smoothExposure(double smoothFactor, int iter) {

		double[] exposureBuffer = new double[6 * IJK];
		double[] tmp;

		for (int r = 0; r < iter; r++) {

			int index = 0;
			double w;
			for (int i = 0; i < I; i++) {
				for (int j = 0; j < J; j++) {
					for (int k = 0; k < K; k++) {
						for (int side = 0; side < 6; side++) {
							// for (int c = 0; c < 3; c++) {
							exposureBuffer[6 * (index) + side] = exposure[6 * (index) + side];
							// }
						}
						if (voxels[index]) {
							w = 1.0;
							if (i == 0 || !voxels[index - JK]) {// left side open
								if (j > 0 && voxels[index - K] && (i == 0 || !voxels[index - K - JK])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index)] += smoothFactor * exposure[6 * (index - K)];
									// }
									w += smoothFactor;
								}
								if (j < J - 1 && voxels[index + K] && (i == 0 || !voxels[index + K - JK])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index)] += smoothFactor * exposure[6 * (index + K)];
									// }
									w += smoothFactor;
								}
								if (k > 0 && voxels[index - 1] && (i == 0 || !voxels[index - 1 - JK])) {

									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index)] += smoothFactor * exposure[6 * (index - 1)];
									// }
									w += smoothFactor;
								}
								if (k < K - 1 && voxels[index + 1] && (i == 0 || !voxels[index + 1 - JK])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index)] += smoothFactor * exposure[6 * (index + 1)];
									// }
									w += smoothFactor;

								}
							}

							// for (int c = 0; c < 3; c++) {
							exposureBuffer[6 * index] /= w;
							// }

							w = 1.0;
							if (i == I - 1 || !voxels[index + JK]) {// right side open
								if (j > 0 && voxels[index - K] && (i == I - 1 || !voxels[index - K + JK])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 1] += smoothFactor * exposure[6 * (index - K) + 1];
									// }
									w += smoothFactor;
								}
								if (j < J - 1 && voxels[index + K] && (i == I - 1 || !voxels[index + K + JK])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 1] += smoothFactor * exposure[6 * (index + K) + 1];
									// }
									w += smoothFactor;
								}
								if (k > 0 && voxels[index - 1] && (i == I - 1 || !voxels[index - 1 + JK])) {

									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 1] += smoothFactor * exposure[6 * (index - 1) + 1];
									// }
									w += smoothFactor;
								}
								if (k < K - 1 && voxels[index + 1] && (i == I - 1 || !voxels[index + 1 + JK])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 1] += smoothFactor * exposure[6 * (index + 1) + 1];
									// }
									w += smoothFactor;

								}

							}

							// for (int c = 0; c < 3; c++) {
							exposureBuffer[6 * index + 1] /= w;
							// }

							w = 1.0;
							if (j == 0 || !voxels[index - K]) {// bottom side open

								if (i > 0 && voxels[index - JK] && (j == 0 || !voxels[index - JK - K])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 2] += smoothFactor * exposure[6 * (index - JK) + 2];
									// }
									w += smoothFactor;

								}
								if (i < I - 1 && voxels[index + JK] && (j == 0 || !voxels[index + JK - K])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 2] += smoothFactor * exposure[6 * (index + JK) + 2];
									// }
									w += smoothFactor;
								}
								if (k > 0 && voxels[index - 1] && (j == 0 || !voxels[index - 1 - K])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 2] += smoothFactor * exposure[6 * (index - 1) + 2];
									// }
									w += smoothFactor;
								}
								if (k < K - 1 && voxels[index + 1] && (j == 0 || !voxels[index + 1 - K])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 2] += smoothFactor * exposure[6 * (index + 1) + 2];
									// }
									w += smoothFactor;
								}

							}
							// for (int c = 0; c < 3; c++) {
							exposureBuffer[6 * (index) + 2] /= w;
							// }

							w = 1.0;
							if (j == J - 1 || !voxels[index + K]) {// top side open

								if (i > 0 && voxels[index - JK] && (j == J - 1 || !voxels[index - JK + K])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 3] += smoothFactor * exposure[6 * (index - JK) + 3];
									// }
									w += smoothFactor;

								}
								if (i < I - 1 && voxels[index + JK] && (j == J - 1 || !voxels[index + JK + K])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 3] += smoothFactor * exposure[6 * (index + JK) + 3];
									// }
									w += smoothFactor;
								}
								if (k > 0 && voxels[index - 1] && (j == J - 1 || !voxels[index - 1 + K])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 3] += smoothFactor * exposure[6 * (index - 1) + 3];
									// }
									w += smoothFactor;
								}
								if (k < K - 1 && voxels[index + 1] && (j == J - 1 || !voxels[index + 1 + K])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 3] += smoothFactor * exposure[6 * (index + 1) + 3];
									// }
									w += smoothFactor;
								}

							}
							// for (int c = 0; c < 3; c++) {
							exposureBuffer[6 * (index) + 3] /= w;
							// }

							w = 1.0;
							if (k == 0 || !voxels[index - 1]) {// back open

								if (i > 1 && voxels[index - JK] && (k == 0 || !voxels[index - JK - 1])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 4] += smoothFactor * exposure[6 * (index - JK) + 4];
								}
								w += smoothFactor;
								// }
								if (i < I - 1 && voxels[index + JK] && (k == 0 || !voxels[index + JK - 1])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 4] += smoothFactor * exposure[6 * (index + JK) + 4];
									// }
									w += smoothFactor;
								}
								if (j > 0 && voxels[index - K] && (k == 0 || !voxels[index - K - 1])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 4] += smoothFactor * exposure[6 * (index - K) + 4];
									// }
									w += smoothFactor;
								}
								if (j < J - 1 && voxels[index + K] && (k == 0 || !voxels[index + K - 1])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 4] += smoothFactor * exposure[6 * (index + K) + 4];
									// }
									w += smoothFactor;
								}
							}
							// for (int c = 0; c < 3; c++) {
							exposureBuffer[6 * (index) + 4] /= w;
							// }

							w = 1.0;
							if (k == K - 1 || !voxels[index + 1]) {// front open

								if (i > 1 && voxels[index - JK] && (k == K - 1 || !voxels[index - JK + 1])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 5] += smoothFactor * exposure[6 * (index - JK) + 5];
									// }
									w += smoothFactor;
								}
								if (i < I - 1 && voxels[index + JK] && (k == K - 1 || !voxels[index + JK + 1])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 5] += smoothFactor * exposure[6 * (index + JK) + 5];
									// }
									w += smoothFactor;
								}
								if (j > 0 && voxels[index - K] && (k == K - 1 || !voxels[index - K + 1])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 5] += smoothFactor * exposure[6 * (index - K) + 5];
									// }
									w += smoothFactor;
								}
								if (j < J - 1 && voxels[index + K] && (k == K - 1 || !voxels[index + K + 1])) {
									// for (int c = 0; c < 3; c++) {
									exposureBuffer[6 * (index) + 5] += smoothFactor * exposure[6 * (index + K) + 5];
									// }
									w += smoothFactor;
								}
							}
							// for (int c = 0; c < 3; c++) {
							exposureBuffer[6 * (index) + 5] /= w;
							// }

						}

						index++;
					}
				}
			}
			tmp = exposure;
			exposure = exposureBuffer;
			exposureBuffer = tmp;
		}
	}

	public void reset() {
		int id = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					// for (int c = 0; c < 3; c++) {

					exposure[6 * (id)] = 0;
					exposure[6 * (id) + 1] = 0;
					exposure[6 * (id) + 2] = 0;
					exposure[6 * (id) + 3] = 0;
					exposure[6 * (id) + 4] = 0;
					exposure[6 * (id) + 5] = 0;
					// }
					parts[id] = -1;
					id++;
				}
			}
		}

	}

	private int index(final int i, final int j, final int k, int li, int ui, int lj, int uj, int lk, int uk) {
		if (i > li - 1 && j > lj - 1 && k > lk - 1 && i < ui && j < uj && k < uk) {
			return k + j * K + i * JK;
		} else {
			return -1;
		}
	}

	public int numOrthoNeighbors(int i, int j, int k) {
		int n = 0;
		if (get(i - 1, j, k))
			n++;
		if (get(i + 1, j, k))
			n++;
		if (get(i, j - 1, k))
			n++;
		if (get(i, j + 1, k))
			n++;
		if (get(1, j, k - 1))
			n++;
		if (get(i, j, k + 1))
			n++;
		return n;
	}

	public int numCornerNeighbors(int i, int j, int k) {
		int n = 0;
		if (get(i - 1, j - 1, k - 1))
			n++;
		if (get(i + 1, j - 1, k - 1))
			n++;
		if (get(i - 1, j + 1, k - 1))
			n++;
		if (get(i + 1, j + 1, k - 1))
			n++;
		if (get(i - 1, j - 1, k + 1))
			n++;
		if (get(i + 1, j - 1, k + 1))
			n++;
		if (get(1 - 1, j + 1, k + 1))
			n++;
		if (get(i + 1, j + 1, k + 1))
			n++;
		return n;
	}

	public int numEdgeNeighbors(int i, int j, int k) {
		int n = 0;
		if (get(i - 1, j - 1, k))
			n++;
		if (get(i + 1, j - 1, k))
			n++;
		if (get(i - 1, j + 1, k))
			n++;
		if (get(i + 1, j + 1, k))
			n++;

		if (get(i - 1, j, k - 1))
			n++;
		if (get(i + 1, j, k - 1))
			n++;
		if (get(i - 1, j, k + 1))
			n++;
		if (get(i + 1, j, k + 1))
			n++;

		if (get(i, j - 1, k - 1))
			n++;
		if (get(i, j + 1, k - 1))
			n++;
		if (get(i, j - 1, k + 1))
			n++;
		if (get(i, j + 1, k + 1))
			n++;

		return n;
	}

	public boolean isIsolated(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1 || !get(index))
			return false;
		if (get(i - 1, j, k))
			return false;
		if (get(i + 1, j, k))
			return false;
		if (get(i, j - 1, k))
			return false;
		if (get(i, j + 1, k))
			return false;
		if (get(i, j, k - 1))
			return false;
		if (get(i, j, k + 1))
			return false;

		return true;

	}

	public boolean isStub(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1 || !get(index))
			return false;
		int n = 0;
		if (!get(i, j - 1, k) && !get(i, j + 1, k) && !get(i - 1, j, k) && !get(i + 1, j, k) && !get(i - 1, j - 1, k)
				&& !get(i - 1, j + 1, k) && !get(i - 1, j + 1, k) && !get(i + 1, j + 1, k)) {
			if (get(i, j, k - 1))
				n++;
			if (get(i, j, k + 1))
				n++;
		}
		if (!get(i, j - 1, k) && !get(i, j + 1, k) && !get(i, j, k - 1) && !get(i, j, k + 1) && !get(i, j - 1, k - 1)
				&& !get(i, j + 1, k - 1) && !get(i, j + 1, k + 1) && !get(i, j - 1, k + 1)) {
			if (get(i - 1, j, k))
				n++;
			if (get(i + 1, j, k))
				n++;
		}

		if (!get(i - 1, j, k) && !get(i + 1, j, k) && !get(i, j, k - 1) && !get(i, j, k + 1) && !get(i - 1, j, k - 1)
				&& !get(i + 1, j, k - 1) && !get(i + 1, j, k + 1) && !get(i - 1, j, k + 1)) {
			if (get(i, j - 1, k))
				n++;
			if (get(i, j + 1, k))
				n++;
		}

		return n == 1;

	}

	public boolean isMissingConnector(int i, int j, int k) {
		int index = index(i, j, k);
		if (get(index))
			return false;
		int n = 0;
		if (isStub(i - 1, j, k))
			n++;
		if (isStub(i + 1, j, k))
			n++;
		if (isStub(i, j - 1, k))
			n++;
		if (isStub(i, j + 1, k))
			n++;
		if (isStub(i, j, k - 1))
			n++;
		if (isStub(i, j, k + 1))
			n++;

		return n > 2;

	}

	public boolean isThinPlate(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1 || !get(index))
			return false;
		int ni = 0, nj = 0, nk = 0;
		if (!get(i - 1, j, k))
			ni++;
		if (!get(i + 1, j, k))
			ni++;
		if (!get(i, j - 1, k))
			nj++;
		if (!get(i, j + 1, k))
			nj++;
		if (!get(i, j, k - 1))
			nk++;
		if (!get(i, j, k + 1))
			nk++;

		return (ni == 2 || nj == 2 || nk == 2);

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

	public int index(int i, int j, int k) {
		if (i < 0 || i >= I || j < 0 || j >= J || k < 0 || k >= K)
			return -1;
		return k + K * j + JK * i;
	}

	private int getVertexIndex(int i, int j, int k, int[][][] vertexIndices, List<double[]> vertices, double[] c,
			final double dx, final double dy, final double dz) {

		if (vertexIndices[i][j][k] == -1) {
			vertexIndices[i][j][k] = vertices.size() + 1;
			vertices.add(new double[] { c[0] + i * dx, c[1] + j * dy, c[2] + k * dz });
		}
		return vertexIndices[i][j][k];

	}

	public void exportAsObj(String path, double cx, double cy, double cz, final double dx, final double dy,
			final double dz, int li, int ui, int lj, int uj, int lk, int uk) {
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

	/**
	 *
	 *
	 * @param grid
	 * @param home
	 * @return
	 */
	public PShape getPShape(final PApplet home, double cx, double cy, double cz, final double dx, final double dy,
			final double dz, int li, int ui, int lj, int uj, int lk, int uk) {
		home.pushMatrix();
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.QUADS);
		double[] c = new double[] { cx - I * 0.5 * dx, cy - J * 0.5 * dy, cz - K * 0.5 * dz };
		home.translate((float) c[0], (float) c[1], (float) c[2]);
		drawXFaces(retained, dx, dy, dz, li, ui, lj, uj, lk, uk);
		drawYFaces(retained, dx, dy, dz, li, ui, lj, uj, lk, uk);
		drawZFaces(retained, dx, dy, dz, li, ui, lj, uj, lk, uk);
		retained.endShape();
		home.popMatrix();
		return retained;
	}

	/**
	 *
	 *
	 * @param grid
	 * @param home
	 * @return
	 */
	public PShape getWireframePShape(final PApplet home, double cx, double cy, double cz, final double dx,
			final double dy, final double dz, int li, int ui, int lj, int uj, int lk, int uk) {
		home.pushMatrix();
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.LINES);
		double[] c = new double[] { cx - I * 0.5 * dx, cy - J * 0.5 * dy, cz - K * 0.5 * dz };
		home.translate((float) c[0], (float) c[1], (float) c[2]);
		drawXFaces(retained, dx, dy, dz, li, ui, lj, uj, lk, uk);
		drawYFaces(retained, dx, dy, dz, li, ui, lj, uj, lk, uk);
		drawZFaces(retained, dx, dy, dz, li, ui, lj, uj, lk, uk);
		retained.endShape();
		home.popMatrix();
		return retained;
	}

	/**
	 *
	 *
	 * @param grid
	 * @param retained
	 */
	void drawXEdges(final PShape retained, final double dx, final double dy, final double dz, int li, int ui, int lj,
			int uj, int lk, int uk) {
		int val00, valm0, valmm, val0m, sum;
		double x, y, z;
		for (int i = li; i < ui; i++) {
			x = i * dx;
			for (int j = lj; j <= uj; j++) {
				y = j * dy;
				for (int k = lk; k <= uk; k++) {
					z = k * dz;
					val00 = get(i, j, k) ? 1 : 0;
					valm0 = get(i, j - 1, k) ? 1 : 0;
					valmm = get(i, j - 1, k - 1) ? 1 : 0;
					val0m = get(i, j, k - 1) ? 1 : 0;
					sum = val00 + valm0 + valmm + val0m;
					if (sum == 1 || sum == 3) {
						line(x, y, z, x + dx, y, z, retained);
					}
					if (sum == 2) {
						if (val00 + valmm == 2 || val0m + valm0 == 2) {
							line(x, y, z, x + dx, y, z, retained);
						}
					}
				}
			}
		}
	}

	/**
	 *
	 *
	 * @param grid
	 * @param retained
	 */
	void drawXFaces(final PShape retained, final double dx, final double dy, final double dz, int li, int ui, int lj,
			int uj, int lk, int uk) {
		int val0, valm, sum;
		double x, y, z;
		for (int i = li; i <= ui; i++) {
			x = i * dx;
			for (int j = lj; j < uj; j++) {
				y = j * dy;
				for (int k = lk; k < uk; k++) {
					z = k * dz;
					val0 = get(i, j, k) ? 1 : 0;
					valm = get(i - 1, j, k) ? 1 : 0;
					sum = val0 + valm;
					if (sum == 1) {
						retained.vertex((float) x, (float) y, (float) z);
						retained.vertex((float) x, (float) (y + dy), (float) z);
						retained.vertex((float) x, (float) (y + dy), (float) (z + dz));
						retained.vertex((float) x, (float) y, (float) (z + dz));
					}
				}
			}
		}
	}

	/**
	 *
	 *
	 * @param grid
	 * @param retained
	 */
	void drawYEdges(final PShape retained, final double dx, final double dy, final double dz, int li, int ui, int lj,
			int uj, int lk, int uk) {
		int val00, valm0, valmm, val0m, sum;
		double x, y, z;
		for (int j = lj; j < uj; j++) {
			y = j * dy;
			for (int i = li; i <= ui; i++) {
				x = i * dx;
				for (int k = lk; k <= uk; k++) {
					z = k * dz;
					val00 = get(i, j, k) ? 1 : 0;
					valm0 = get(i - 1, j, k) ? 1 : 0;
					valmm = get(i - 1, j, k - 1) ? 1 : 0;
					val0m = get(i, j, k - 1) ? 1 : 0;
					sum = val00 + valm0 + valmm + val0m;
					if (sum == 1 || sum == 3) {
						line(x, y, z, x, y + dy, z, retained);
					}
					if (sum == 2) {
						if (val00 + valmm == 2 || val0m + valm0 == 2) {
							line(x, y, z, x, y + dy, z, retained);
						}
					}
				}
			}
		}
	}

	/**
	 *
	 *
	 * @param grid
	 * @param retained
	 */
	void drawYFaces(final PShape retained, final double dx, final double dy, final double dz, int li, int ui, int lj,
			int uj, int lk, int uk) {
		int val0, valm, sum;
		double x, y, z;
		for (int i = li; i < ui; i++) {
			x = i * dx;
			for (int j = lj; j <= uj; j++) {
				y = j * dy;
				for (int k = lk; k < uk; k++) {
					z = k * dz;
					val0 = get(i, j, k) ? 1 : 0;
					valm = get(i, j - 1, k) ? 1 : 0;
					sum = val0 + valm;
					if (sum == 1) {
						retained.vertex((float) x, (float) y, (float) z);
						retained.vertex((float) (x + dx), (float) y, (float) z);
						retained.vertex((float) (x + dx), (float) y, (float) (z + dz));
						retained.vertex((float) x, (float) y, (float) (z + dz));
					}
				}
			}
		}
	}

	/**
	 *
	 *
	 * @param grid
	 * @param retained
	 */
	void drawZEdges(final PShape retained, final double dx, final double dy, final double dz, int li, int ui, int lj,
			int uj, int lk, int uk) {
		int val00, valm0, valmm, val0m, sum;
		double x, y, z;
		for (int k = lk; k < uk; k++) {
			z = k * dz;
			for (int j = lj; j <= uj; j++) {
				y = j * dy;
				for (int i = li; i <= ui; i++) {
					x = i * dx;
					val00 = get(i, j, k) ? 1 : 0;
					valm0 = get(i - 1, j, k) ? 1 : 0;
					valmm = get(i - 1, j - 1, k) ? 1 : 0;
					val0m = get(i, j - 1, k) ? 1 : 0;
					sum = val00 + valm0 + valmm + val0m;
					if (sum == 1 || sum == 3) {
						line(x, y, z, x, y, z + dz, retained);
					}
					if (sum == 2) {
						if (val00 + valmm == 2 || val0m + valm0 == 2) {
							line(x, y, z, x, y, z + dz, retained);
						}
					}
				}
			}
		}
	}

	/**
	 *
	 *
	 * @param grid
	 * @param retained
	 */
	void drawZFaces(final PShape retained, final double dx, final double dy, final double dz, int li, int ui, int lj,
			int uj, int lk, int uk) {
		int val0, valm, sum;
		double x, y, z;
		for (int i = li; i < ui; i++) {
			x = i * dx;
			for (int j = lj; j < uj; j++) {
				y = j * dy;
				for (int k = lk; k <= uk; k++) {
					z = k * dz;
					val0 = get(i, j, k) ? 1 : 0;
					valm = get(i, j, k - 1) ? 1 : 0;
					sum = val0 + valm;
					if (sum == 1) {
						retained.vertex((float) x, (float) y, (float) z);
						retained.vertex((float) (x + dx), (float) y, (float) z);
						retained.vertex((float) (x + dx), (float) (y + dy), (float) z);
						retained.vertex((float) x, (float) (y + dy), (float) z);
					}
				}
			}
		}
	}

	/**
	 *
	 *
	 * @param x1
	 * @param y1
	 * @param z1
	 * @param x2
	 * @param y2
	 * @param z2
	 * @param retained
	 */
	void line(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2,
			final PShape retained) {
		retained.vertex((float) x1, (float) y1, (float) z1);
		retained.vertex((float) x2, (float) y2, (float) z2);
	}

	double EPSILON = 1e-12;

	public boolean isVisible(int cutoff, double xTarget, double yTarget, double zTarget, double xEye, double yEye,
			double zEye, double xCenter, double yCenter, double zCenter, final double dx, final double dy,
			final double dz) {
		double lx = xCenter - I * 0.5 * dx;
		double ly = yCenter - J * 0.5 * dy;
		double lz = zCenter - K * 0.5 * dz;
		double ux = xCenter + I * 0.5 * dx;
		double uy = yCenter + J * 0.5 * dy;
		double uz = zCenter + K * 0.5 * dz;

		double xRay = xTarget - xEye;
		double yRay = yTarget - yEye;
		double zRay = zTarget - zEye;

		double dRay = Math.sqrt(xRay * xRay + yRay * yRay + zRay * zRay);
		if (Math.abs(dRay) < EPSILON)
			return true;
		xRay /= dRay;
		yRay /= dRay;
		zRay /= dRay;

		double tMin, tMax;

		double xRayInv = 1.0 / xRay;
		if (xRayInv >= 0.0) {
			tMin = (lx - xEye) * xRayInv;
			tMax = (ux - xEye) * xRayInv;
		} else {
			tMin = (ux - xEye) * xRayInv;
			tMax = (lx - xEye) * xRayInv;

		}

		double yRayInv = 1.0 / yRay;
		double tYMin, tYMax;
		if (yRayInv >= 0.0) {
			tYMin = (ly - yEye) * yRayInv;
			tYMax = (uy - yEye) * yRayInv;
		} else {
			tYMin = (uy - yEye) * yRayInv;
			tYMax = (ly - yEye) * yRayInv;

		}

		if (tMin > tYMax || tYMin > tMax)
			return true;
		if (tYMin > tMin)
			tMin = tYMin;
		if (tYMax < tMax)
			tMax = tYMax;

		double zRayInv = 1.0 / zRay;
		double tZMin, tZMax;
		if (zRayInv >= 0.0) {
			tZMin = (lz - zEye) * zRayInv;
			tZMax = (uz - zEye) * zRayInv;
		} else {
			tZMin = (uz - zEye) * zRayInv;
			tZMax = (lz - zEye) * zRayInv;

		}

		if (tMin > tZMax || tZMin > tMax)
			return true;
		if (tZMin > tMin)
			tMin = tZMin;
		if (tZMax < tMax)
			tMax = tZMax;

		if (tMax < 0.0)
			return true;

		tMin = Math.max(tMin, 0.0);

		double xRayStart = xEye + xRay * tMin;
		double yRayStart = yEye + yRay * tMin;
		double zRayStart = zEye + zRay * tMin;

		tMax = Math.min(tMax, dRay);

		double xRayEnd = xEye + xRay * tMax;
		double yRayEnd = yEye + yRay * tMax;
		double zRayEnd = zEye + zRay * tMax;

		int currentXIndex = (int) Math.max(1, Math.ceil((xRayStart - lx) / dx));
		int endXIndex = (int) Math.max(1, Math.ceil((xRayEnd - lx) / dx));
		int stepX;
		double tDeltaX;
		double tMaxX;
		if (xRay > 0.0) {
			stepX = 1;
			tDeltaX = dx * xRayInv;
			tMaxX = tMin + (lx + currentXIndex * dx - xRayStart) * xRayInv;

		} else if (xRay < 0.0) {
			stepX = -1;
			tDeltaX = -dx * xRayInv;
			int prevXIndex = currentXIndex - 1;
			tMaxX = tMin + (lx + prevXIndex * dx - xRayStart) * xRayInv;
		} else {
			stepX = 0;
			tDeltaX = tMax;
			tMaxX = tMax;

		}

		if (stepX == 1) {
			endXIndex = Math.max(currentXIndex, endXIndex);

		} else if (stepX == -1) {
			endXIndex = Math.min(currentXIndex, endXIndex);

		}

		int currentYIndex = (int) Math.max(1, Math.ceil((yRayStart - ly) / dy));
		int endYIndex = (int) Math.max(1, Math.ceil((yRayEnd - ly) / dy));
		int stepY;
		double tDeltaY;
		double tMaxY;
		if (yRay > 0.0) {
			stepY = 1;
			tDeltaY = dy * yRayInv;
			tMaxY = tMin + (ly + currentYIndex * dy - yRayStart) * yRayInv;

		} else if (yRay < 0.0) {
			stepY = -1;
			tDeltaY = -dy * yRayInv;
			int prevYIndex = currentYIndex - 1;
			tMaxY = tMin + (ly + prevYIndex * dy - yRayStart) * yRayInv;
		} else {
			stepY = 0;
			tDeltaY = tMax;
			tMaxY = tMax;

		}

		if (stepY == 1) {
			endYIndex = Math.max(currentYIndex, endYIndex);

		} else if (stepY == -1) {
			endYIndex = Math.min(currentYIndex, endYIndex);

		}

		int currentZIndex = (int) Math.max(1, Math.ceil((zRayStart - lz) / dz));
		int endZIndex = (int) Math.max(1, Math.ceil((zRayEnd - lz) / dz));

		int stepZ;
		double tDeltaZ;
		double tMaxZ;
		if (zRay > 0.0) {
			stepZ = 1;
			tDeltaZ = dz * zRayInv;
			tMaxZ = tMin + (lz + currentZIndex * dz - zRayStart) * zRayInv;

		} else if (zRay < 0.0) {
			stepZ = -1;
			tDeltaZ = -dz * zRayInv;
			int prevZIndex = currentZIndex - 1;
			tMaxZ = tMin + (lz + prevZIndex * dz - zRayStart) * zRayInv;
		} else {
			stepZ = 0;
			tDeltaZ = tMax;
			tMaxZ = tMax;

		}

		if (stepZ == 1) {
			endZIndex = Math.max(currentZIndex, endZIndex);

		} else if (stepZ == -1) {
			endZIndex = Math.min(currentZIndex, endZIndex);

		}
		int steps = 0;

		// System.out.println(xRay +" "+ yRay+" "+zRay+" "+ stepX+" "+stepY+" "+ stepZ+"
		// "+currentXIndex+ " "+ currentYIndex+ " "+ currentZIndex+" "+endXIndex+"
		// "+endYIndex+" "+endZIndex);

		while ((currentXIndex != endXIndex || currentYIndex != endYIndex || currentZIndex != endZIndex)
				&& steps < cutoff) {
			if (tMaxX < tMaxY && tMaxX < tMaxZ) {
				// X-axis traversal.
				currentXIndex += stepX;
				tMaxX += tDeltaX;
			} else if (tMaxY < tMaxZ) {
				// Y-axis traversal.
				currentYIndex += stepY;
				tMaxY += tDeltaY;
			} else {
				// Z-axis traversal.
				currentZIndex += stepZ;
				tMaxZ += tDeltaZ;
			}
			if (get(currentXIndex, currentYIndex, currentZIndex))
				return false;
			steps++;
		}
		if (steps == cutoff)
			return false;
		return true;
	}

}
