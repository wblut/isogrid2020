package wblut.isogrid;

import java.util.List;

import org.apache.commons.rng.simple.RandomSource;

import processing.core.PApplet;

public class WB_IsoSystem36 extends WB_IsoSystem {

	boolean DUAL;

	public WB_IsoSystem36(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			boolean full, PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, full, home);
		DUAL = false;
	}
	
	public WB_IsoSystem36(double L, int I, int J, int K, double centerX, double centerY, List<WB_IsoPalette> palettes, int seed,
			boolean full, PApplet home) {
		super(L, I, J, K, centerX, centerY, palettes, seed, full, home);
		DUAL = false;
	}

	public WB_IsoSystem36(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, true, home);
		DUAL = false;
	}
	
	public WB_IsoSystem36(double L, int I, int J, int K, double centerX, double centerY, List<WB_IsoPalette> palettes, int seed,
			PApplet home) {
		super(L, I, J, K, centerX, centerY, palettes, seed, true, home);
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
		palettes = iso.palettes;
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
		PARTS=true;
		VISIBILITY=true;
		DUAL = true;
		map();

		DEFER = false;
		GLOBALDEFER = false;
		YFLIP = true;

	}
	
	public WB_IsoSystem36( boolean[][][] pattern, int scaleI, int scaleJ, int scaleK, double L, double centerX, double centerY, int[] colors, int seed,
			PApplet home) {
		
	super(pattern, scaleI, scaleJ, scaleK, L, centerX,centerY, colors, seed,home);
		DUAL=false;
	
	}
	
	public WB_IsoSystem36(WB_IsoSystem iso, int scaleI, int scaleJ, int scaleK) {
		super(iso, scaleI, scaleJ, scaleK);
	}
	
	public WB_IsoSystem36 rotateICC() {
		WB_IsoSystem36 result=new WB_IsoSystem36(L,I,K,J,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateICC();
		result.map();
		
	return result;
	}
	

	public WB_IsoSystem36 rotateICW() {
		WB_IsoSystem36 result=new WB_IsoSystem36(L,I,K,J,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateICW();
		result.map();
		
	return result;
	}
	
	public WB_IsoSystem36 rotateJCW() {
		if(YFLIP) {
			return rotateJCCImpl();
		}else {
			
			return rotateJCWImpl();
		}
	}
	public WB_IsoSystem36 rotateJCC() {
		if(YFLIP) {
			return rotateJCWImpl();
		}else {
			return rotateJCCImpl();
		}
	}


	WB_IsoSystem36 rotateJCCImpl() {
		WB_IsoSystem36 result=new WB_IsoSystem36(L,K,J,I,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateJCC();
		result.map();
		
	return result;
	}
	

	WB_IsoSystem36 rotateJCWImpl() {
		WB_IsoSystem36 result=new WB_IsoSystem36(L,K,J,I,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateJCW();
		result.map();
		
	return result;
	}
	
	public WB_IsoSystem36 rotateKCC() {
		WB_IsoSystem36 result=new WB_IsoSystem36(L,J,I,K,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateKCC();
		result.map();
		
	return result;
	}
	

	public WB_IsoSystem36 rotateKCW() {
		WB_IsoSystem36 result=new WB_IsoSystem36(L,J,I,K,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateKCW();
		result.map();
		
	return result;
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
