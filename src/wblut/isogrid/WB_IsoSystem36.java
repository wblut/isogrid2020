package wblut.isogrid;

import java.util.List;

import org.apache.commons.rng.simple.RandomSource;

import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.cubegrid.WB_CubeGrid;
import wblut.hexgrid.WB_HexGrid;
import wblut.isogrid.color.WB_IsoColor;

public class WB_IsoSystem36 extends WB_IsoSystem {

	boolean DUAL;

	public WB_IsoSystem36(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			boolean full, PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, full, home);
		DUAL = false;
	}

	public WB_IsoSystem36(double L, int I, int J, int K, double centerX, double centerY, WB_IsoColor colors, int seed,
			boolean full, PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, full, home);
		DUAL = false;
	}

	public WB_IsoSystem36(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, true, home);
		DUAL = false;
	}

	public WB_IsoSystem36(double L, int I, int J, int K, double centerX, double centerY, WB_IsoColor colors, int seed,
			PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, true, home);
		DUAL = false;
	}

	public WB_IsoSystem36(WB_IsoSystem36 iso) {
		super(iso);
		DUAL = iso.DUAL;

	}

	public WB_IsoSystem36(WB_IsoSystem6 iso) {
		randomGen = RandomSource.create(RandomSource.MT);
		state = randomGen.saveState();
		this.home = iso.home;
		this.L = iso.L;
		this.I = iso.I + 3;
		this.J = iso.J + 3;
		this.K = iso.K + 3;
		IJK = I * J * K;
		colorSources = iso.colorSources;
		this.centerX = iso.centerX;
		this.centerY = iso.centerY;
		this.cubeGrid = new WB_CubeGrid(I, J, K);
		this.seed = iso.seed;
		setGrid();
		DEFER = false;
		for (int i = 0; i < iso.I; i++) {
			for (int j = 0; j < iso.J; j++) {
				for (int k = 0; k < iso.K; k++) {
					if (iso.cubeGrid.get(i, j, k)) {
						cubeGrid.set(i, j + 1, k + 1, true);
						cubeGrid.set(i + 1, j + 1, k + 1, true);
						cubeGrid.set(i + 2, j + 1, k + 1, true);
						cubeGrid.set(i + 3, j + 1, k + 1, true);
						cubeGrid.set(i, j + 2, k + 1, true);
						cubeGrid.set(i + 1, j + 2, k + 1, true);
						cubeGrid.set(i + 2, j + 2, k + 1, true);
						cubeGrid.set(i + 3, j + 2, k + 1, true);
						cubeGrid.set(i + 1, j + 3, k + 1, true);
						cubeGrid.set(i + 2, j + 3, k + 1, true);
						cubeGrid.set(i + 1, j, k + 1, true);
						cubeGrid.set(i + 2, j, k + 1, true);
						cubeGrid.set(i, j + 1, k + 2, true);
						cubeGrid.set(i + 1, j + 1, k + 2, true);
						cubeGrid.set(i + 2, j + 1, k + 2, true);
						cubeGrid.set(i + 3, j + 1, k + 2, true);
						cubeGrid.set(i, j + 2, k + 2, true);
						cubeGrid.set(i + 1, j + 2, k + 2, true);
						cubeGrid.set(i + 2, j + 2, k + 2, true);
						cubeGrid.set(i + 3, j + 2, k + 2, true);
						cubeGrid.set(i + 1, j + 3, k + 2, true);
						cubeGrid.set(i + 2, j + 3, k + 2, true);
						cubeGrid.set(i + 1, j, k + 2, true);
						cubeGrid.set(i + 2, j, k + 2, true);
						cubeGrid.set(i + 1, j + 1, k + 3, true);
						cubeGrid.set(i + 2, j + 1, k + 3, true);
						cubeGrid.set(i + 1, j + 2, k + 3, true);
						cubeGrid.set(i + 2, j + 2, k + 3, true);
						cubeGrid.set(i + 1, j + 1, k, true);
						cubeGrid.set(i + 2, j + 1, k, true);
						cubeGrid.set(i + 1, j + 2, k, true);
						cubeGrid.set(i + 2, j + 2, k, true);
					}

				}
			}
		}

		DUAL = true;
		map();

		DEFER = false;
		GLOBALDEFER = false;
		YFLIP = true;

	}

	public WB_IsoSystem36(WB_IsoSystem6 iso, boolean twoD) {
		randomGen = RandomSource.create(RandomSource.MT);
		state = randomGen.saveState();
		this.home = iso.home;

		if (!twoD) {
			this.L = iso.L;
			this.I = iso.I + 2;
			this.J = iso.J + 2;
			this.K = iso.K + 2;
			IJK = I * J * K;
			colorSources = iso.colorSources;
			this.centerX = iso.centerX;
			this.centerY = iso.centerY;
			this.cubeGrid = new WB_CubeGrid(I, J, K);
			this.seed = iso.seed;
			setGrid();

			for (int i = 0; i < iso.I; i++) {
				for (int j = 0; j < iso.J; j++) {
					for (int k = 0; k < iso.K; k++) {
						if (iso.cubeGrid.get(i, j, k)) {
							cubeGrid.set(i+1, j + 1, k , true);
							cubeGrid.set(i , j + 1, k + 1, true);
							cubeGrid.set(i + 1, j + 1, k + 1, true);
							cubeGrid.set(i + 2, j + 1, k+1 , true);
							cubeGrid.set(i+1, j + 1, k +2, true);
							cubeGrid.set(i+1, j , k +1, true);
							cubeGrid.set(i+1, j+2 , k +1, true);
						}

					}
				}
			}

		} else {
			this.L = iso.L;
			this.I = 2*iso.I;
			this.J = 2*iso.J;
			this.K = 2*iso.K;
			IJK = I * J * K;
			colorSources = iso.colorSources;
			this.centerX = iso.centerX;
			this.centerY = iso.centerY;
			this.cubeGrid = new WB_CubeGrid(I, J, K);
			this.seed = iso.seed;
			setGrid();

			for (int i = 0; i < iso.I; i++) {
				for (int j = 0; j < iso.J; j++) {
					for (int k = 0; k < iso.K; k++) {
						if (iso.cubeGrid.get(i, j, k)) {
							
							
							cubeGrid.set(2*i, 2*j ,2*k, true);
							cubeGrid.set(2*i, 2*j ,2*k, true);
							cubeGrid.set(2*i, 2*j ,2*k, true);
							cubeGrid.set(2*i, 2*j ,2*k, true);
							cubeGrid.set(2*i, 2*j ,2*k, true);
							cubeGrid.set(2*i, 2*j ,2*k, true);
							cubeGrid.set(i+2, j ,k+1, true);
							cubeGrid.set(i+1, j ,k+2, true);
							cubeGrid.set(i+1, j+1 ,k, true);
							cubeGrid.set(i, j +1,k+1, true);
							cubeGrid.set(i+1, j+1 ,k+1, true);
							cubeGrid.set(i+2, j +1,k+1, true);
							cubeGrid.set(i+1, j+1 ,k+2, true);
							
						}

					}
				}
			}
		}
		DUAL = true;
		map();

		DEFER = false;
		GLOBALDEFER = false;
		YFLIP = true;
	}

	public WB_IsoSystem36(boolean[][][] pattern, int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY, int[] colors, int seed, PApplet home) {

		super(pattern, scaleI, scaleJ, scaleK, L, centerX, centerY, colors, seed, home);
		DUAL = false;

	}

	public WB_IsoSystem36(boolean[][][] pattern, int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY, WB_IsoColor colors, int seed, PApplet home) {
		super(pattern, scaleI, scaleJ, scaleK, L, centerX, centerY, colors, seed, home);
		DUAL = false;
	}

	public WB_IsoSystem36(boolean[] pattern, int pI, int pJ, int pK, int scaleI, int scaleJ, int scaleK, double L,
			double centerX, double centerY, int[] colors, int seed, PApplet home) {
		super(pattern, pI, pJ, pK, scaleI, scaleJ, scaleK, L, centerX, centerY, colors, seed, home);
		DUAL = false;

	}

	public WB_IsoSystem36(boolean[] pattern, int pI, int pJ, int pK, int scaleI, int scaleJ, int scaleK, double L,
			double centerX, double centerY, WB_IsoColor colors, int seed, PApplet home) {
		super(pattern, pI, pJ, pK, scaleI, scaleJ, scaleK, L, centerX, centerY, colors, seed, home);
		DUAL = false;
	}

	public WB_IsoSystem36(WB_IsoSystem iso, int scaleI, int scaleJ, int scaleK) {
		super(iso, scaleI, scaleJ, scaleK);
	}

	public WB_IsoSystem fromPart(int part, int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY) {
		throw new java.lang.UnsupportedOperationException("Not supported.");

	}

	public WB_IsoSystem36 rotateICC() {
		WB_IsoSystem36 result = new WB_IsoSystem36(L, I, K, J, centerX, centerY, colorSources.get(0), seed, false,
				home);
		result.cubeGrid = cubeGrid.rotateICC();
		result.map();
		for (int i = 1; i < colorSources.size(); i++) {
			result.addColorSource(colorSources.get(i));
		}
		return result;
	}

	public WB_IsoSystem36 rotateICW() {
		WB_IsoSystem36 result = new WB_IsoSystem36(L, I, K, J, centerX, centerY, colorSources.get(0), seed, false,
				home);
		result.cubeGrid = cubeGrid.rotateICW();
		result.map();
		for (int i = 1; i < colorSources.size(); i++) {
			result.addColorSource(colorSources.get(i));
		}
		return result;
	}

	public WB_IsoSystem36 rotateJCW() {
		if (YFLIP) {
			return rotateJCCImpl();
		} else {

			return rotateJCWImpl();
		}
	}

	public WB_IsoSystem36 rotateJCC() {
		if (YFLIP) {
			return rotateJCWImpl();
		} else {
			return rotateJCCImpl();
		}
	}

	WB_IsoSystem36 rotateJCCImpl() {
		WB_IsoSystem36 result = new WB_IsoSystem36(L, K, J, I, centerX, centerY, colorSources.get(0), seed, false,
				home);
		result.cubeGrid = cubeGrid.rotateJCC();
		result.map();
		for (int i = 1; i < colorSources.size(); i++) {
			result.addColorSource(colorSources.get(i));
		}
		return result;
	}

	WB_IsoSystem36 rotateJCWImpl() {
		WB_IsoSystem36 result = new WB_IsoSystem36(L, K, J, I, centerX, centerY, colorSources.get(0), seed, false,
				home);
		result.cubeGrid = cubeGrid.rotateJCW();
		result.map();
		for (int i = 1; i < colorSources.size(); i++) {
			result.addColorSource(colorSources.get(i));
		}
		return result;
	}

	public WB_IsoSystem36 rotateKCC() {
		WB_IsoSystem36 result = new WB_IsoSystem36(L, J, I, K, centerX, centerY, colorSources.get(0), seed, false,
				home);
		result.cubeGrid = cubeGrid.rotateKCC();
		result.map();
		for (int i = 1; i < colorSources.size(); i++) {
			result.addColorSource(colorSources.get(i));
		}
		return result;
	}

	public WB_IsoSystem36 rotateKCW() {
		WB_IsoSystem36 result = new WB_IsoSystem36(L, J, I, K, centerX, centerY, colorSources.get(0), seed, false,
				home);
		result.cubeGrid = cubeGrid.rotateKCW();
		result.map();
		for (int i = 1; i < colorSources.size(); i++) {
			result.addColorSource(colorSources.get(i));
		}
		return result;
	}

	void setGrid() {
		hexGrid = new WB_IsoHexGrid36();
	}

	int getNumberOfTriangles() {
		return 36;
	}

	public void setDual(boolean b) {
		DUAL = b;

	}

	public int state(int i, int j, int k) {
		if (DUAL) {
			int state = 0;
			if (cubeGrid.get(i, j, k))
				state += 1;
			if (i + 1 < I && cubeGrid.get(i + 1, j, k))
				state += 2;
			if (j + 1 < J && cubeGrid.get(i, j + 1, k))
				state += 4;
			if (i + 1 < I && j + 1 < J && cubeGrid.get(i + 1, j + 1, k))
				state += 8;
			if (k + 1 < K && cubeGrid.get(i, j, k + 1))
				state += 16;
			if (i + 1 < I && k + 1 < K && cubeGrid.get(i + 1, j, k + 1))
				state += 32;
			if (j + 1 < J && k + 1 < K && cubeGrid.get(i, j + 1, k + 1))
				state += 64;
			if (i + 1 < I && j + 1 < J && k + 1 < K && cubeGrid.get(i + 1, j + 1, k + 1))
				state += 128;
			return state;
		} else {
			if (cubeGrid.get(i, j, k)) {
				return 255;
			} else {
				return 0;
			}
		}
	}

	public void mapVoxelsToHexGrid() {
		hexGrid.clear();
		int id = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					if (cubeGrid.get(id))
						hexGrid.addCube(i, j, k, state(i, j, k), cubeGrid.getColorSourceIndex(id));
					id++;
				}

			}

		}

	}

	public void mapVoxelsToHexGrid(int minZ, int maxZ) {
		hexGrid.clear();
		int id = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					if ((i + j + k >= minZ) && (i + j + k > maxZ) && cubeGrid.get(id))
						hexGrid.addCube(i, j, k, state(i, j, k), cubeGrid.getColorSourceIndex(id));
					id++;
				}

			}

		}

	}

	@Override
	public WB_HexGrid bakeTriangles(PGraphics pg) {

		throw new java.lang.UnsupportedOperationException("Not supported.");
	}

	@Override
	public WB_HexGrid bakeTriangles(PGraphics pg, int sourceI, int sourceJ, int sourceK, int offsetI, int offsetJ,
			int offsetK) {

		throw new java.lang.UnsupportedOperationException("Not supported.");
	}

	@Override
	public WB_IsoSystem slice(int sI, int sJ, int sK, int dI, int dJ, int dK, int scaleI, int scaleJ, int scaleK,
			double L, double centerX, double centerY) {
		throw new java.lang.UnsupportedOperationException("Not supported.");

	}

}
