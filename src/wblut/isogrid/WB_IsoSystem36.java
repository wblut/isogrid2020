package wblut.isogrid;

import java.util.List;

import org.apache.commons.rng.simple.RandomSource;

import processing.core.PApplet;

public class WB_IsoSystem36 extends WB_IsoSystem<WB_IsoHexGrid36> {

	boolean DUAL;

	public WB_IsoSystem36(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			boolean full, PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, full, home);
		if(colors.length%10==0) {
			//10-color palette
		}else if(colors.length%3==0) {
		 this.colors=createDualPalette(colors);
		}
		else {
			throw new IllegalArgumentException("Palette length should be a mutiple of 3 or 10.");
		}
		numPalettes=colors.length/10;
		DUAL = false;
	}

	public WB_IsoSystem36(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, true, home);
		if(colors.length%10==0) {
			//10-color palette
		}else if(colors.length%3==0) {
		 this.colors=createDualPalette(colors);
		}
		else {
			throw new IllegalArgumentException("Palette length should be a mutiple of 3 or 10.");
		}
		numPalettes=colors.length/10;
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
		colors = createDualPalette(iso.colors);
		numPalettes=colors.length/10;
		this.centerX = iso.centerX;
		this.centerY = iso.centerY;
		this.cubes = new WB_CubeGrid(I, J, K);
		this.seed = iso.seed;
		setGrid();

		for (int i = 0; i < iso.I; i++) {
			for (int j = 0; j < iso.J; j++) {
				for (int k = 0; k < iso.K; k++) {
					if (iso.cubes.get(i, j, k)) {
						cubes.set(i, j + 1, k + 1, true);
						cubes.set(i + 1, j + 1, k + 1, true);
						cubes.set(i + 2, j + 1, k + 1, true);
						cubes.set(i + 3, j + 1, k + 1, true);
						cubes.set(i, j + 2, k + 1, true);
						cubes.set(i + 1, j + 2, k + 1, true);
						cubes.set(i + 2, j + 2, k + 1, true);
						cubes.set(i + 3, j + 2, k + 1, true);
						cubes.set(i + 1, j + 3, k + 1, true);
						cubes.set(i + 2, j + 3, k + 1, true);
						cubes.set(i + 1, j, k + 1, true);
						cubes.set(i + 2, j, k + 1, true);
						cubes.set(i, j + 1, k + 2, true);
						cubes.set(i + 1, j + 1, k + 2, true);
						cubes.set(i + 2, j + 1, k + 2, true);
						cubes.set(i + 3, j + 1, k + 2, true);
						cubes.set(i, j + 2, k + 2, true);
						cubes.set(i + 1, j + 2, k + 2, true);
						cubes.set(i + 2, j + 2, k + 2, true);
						cubes.set(i + 3, j + 2, k + 2, true);
						cubes.set(i + 1, j + 3, k + 2, true);
						cubes.set(i + 2, j + 3, k + 2, true);
						cubes.set(i + 1, j, k + 2, true);
						cubes.set(i + 2, j, k + 2, true);
						cubes.set(i + 1, j + 1, k + 3, true);
						cubes.set(i + 2, j + 1, k + 3, true);
						cubes.set(i + 1, j + 2, k + 3, true);
						cubes.set(i + 2, j + 2, k + 3, true);
						cubes.set(i + 1, j + 1, k, true);
						cubes.set(i + 2, j + 1, k, true);
						cubes.set(i + 1, j + 2, k, true);
						cubes.set(i + 2, j + 2, k, true);
					}

				}
			}
		}

		DUAL = true;
		mapVoxelsToHexGrid();

		DEFER = false;
		GLOBALDEFER = false;
		YFLIP = true;

	}

	void setGrid() {
		grid = new WB_IsoHexGrid36();
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
			if (cubes.get(i, j, k))
				state += 1;
			if (i + 1 < I && cubes.get(i + 1, j, k))
				state += 2;
			if (j + 1 < J && cubes.get(i, j + 1, k))
				state += 4;
			if (i + 1 < I && j + 1 < J && cubes.get(i + 1, j + 1, k))
				state += 8;
			if (k + 1 < K && cubes.get(i, j, k + 1))
				state += 16;
			if (i + 1 < I && k + 1 < K && cubes.get(i + 1, j, k + 1))
				state += 32;
			if (j + 1 < J && k + 1 < K && cubes.get(i, j + 1, k + 1))
				state += 64;
			if (i + 1 < I && j + 1 < J && k + 1 < K && cubes.get(i + 1, j + 1, k + 1))
				state += 128;
			return state;
		} else {
			if (cubes.get(i, j, k)) {
				return 255;
			} else {
				return 0;
			}
		}
	}
	/*
	 * public void mapVoxelsToHexGrid() { grid.clear(); int id = 0; for (int i = 0;
	 * i < I; i++) { for (int j = 0; j < J; j++) { for (int k = 0; k < K; k++) { if
	 * (cubes.get(id)) grid.addCube(i, j, k, state(i, j, k), cubes.getPalette(id));
	 * id++; }
	 * 
	 * }
	 * 
	 * } cubes.labelParts(); grid.setParts(cubes); grid.collectRegions();
	 * grid.collectLines(); }
	 * 
	 */

	public void mapVoxelsToHexGrid() {
		grid.clear();
		int id = 0;
		if (false) {
			List<int[]> voxelsAtQR;
			for (int q = -K + 1; q < Math.max(I, K); q++) {
				for (int r = -K + 1; r < Math.max(J, K); r++) {
					voxelsAtQR = this.cubesAtGridPosition(q, r);
					for (int[] voxel : voxelsAtQR) {
						id = index(voxel[0], voxel[1], voxel[2]);

						if (cubes.get(id)) {
							grid.addCube(voxel[0], voxel[1], voxel[2], state(voxel[0], voxel[1], voxel[2]),
									cubes.getPalette(id));
						}

						if (grid.isFull(q, r)) {
							break;
						}

					}
				}
			}
		} else {
			for (int i = 0; i < I; i++) {
				for (int j = 0; j < J; j++) {
					for (int k = 0; k < K; k++) {
						if (cubes.get(id))
							grid.addCube(i, j, k, state(i, j, k), cubes.getPalette(id));
						id++;
					}

				}

			}

		}

	}

}
