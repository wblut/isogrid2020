package wblut.isogrid;

import processing.core.PApplet;

public class WB_IsoSystem36 extends WB_IsoSystem<WB_IsoHexGrid36> {

	boolean DUAL;

	public WB_IsoSystem36(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,WB_IsoHexGrid36 grid, PApplet home) {
		super(L,I,J,K,centerX,centerY,colors,seed,grid,home);
		DUAL=false;
	}

	public WB_IsoSystem36(WB_IsoSystem36 iso) {
		super(iso);
		DUAL=iso.DUAL;

	}
	
	@Override
	int getNumberOfTriangles() {
		return 36;
	}
	
	public void setDual(boolean b) {
		DUAL=b;
		
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
	

	public void mapVoxelsToHexGrid() {
		grid.clear();
		int id = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					if (cubes.get(id))
						grid.addCube(i, j, k, state(i, j, k), -1, cubes.getPalette(id));
					id++;
				}
				
			}
			
		}
		cubes.labelParts();
		grid.setParts(cubes);
		grid.collectRegions();
		grid.collectLines();
	}

	

}
