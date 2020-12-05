package wblut.isogrid;

import java.util.List;

import processing.core.PApplet;

public class WB_IsoSystem6 extends WB_IsoSystem {

	public WB_IsoSystem6(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			boolean full, PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, full, home);
	}
	
	public WB_IsoSystem6(double L, int I, int J, int K, double centerX, double centerY, List<WB_IsoPalette> palettes, int seed,
			boolean full, PApplet home) {
		super(L, I, J, K, centerX, centerY, palettes, seed, full, home);
	}

	public WB_IsoSystem6(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, true, home);
	}
	
	public WB_IsoSystem6(double L, int I, int J, int K, double centerX, double centerY, List<WB_IsoPalette> palettes, int seed,
			PApplet home) {
		super(L, I, J, K, centerX, centerY, palettes, seed, true, home);
	}

	public WB_IsoSystem6(WB_IsoSystem6 iso) {
		super(iso);
	}

	public WB_IsoSystem6(WB_IsoSystem iso, int scaleI, int scaleJ, int scaleK) {
		super(iso, scaleI, scaleJ, scaleK);
	}

	public WB_IsoSystem6(boolean[][][] pattern, int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY, int[] colors, int seed, PApplet home) {
		super(pattern, scaleI, scaleJ, scaleK, L, centerX, centerY, colors, seed, home);

	}
	
	public WB_IsoSystem6(boolean[][][] pattern, int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY, List<WB_IsoPalette> palettes, int seed, PApplet home) {
		super(pattern, scaleI, scaleJ, scaleK, L, centerX, centerY, palettes, seed, home);

	}

	void setGrid() {
		grid = new WB_IsoHexGrid6();
	}

	int getNumberOfTriangles() {
		return 6;
	}

	public void mapVoxelsToHexGrid() {
		grid.clear();
		int id = 0;
		List<int[]> voxelsAtQR;
		for (int q = -K + 1; q < Math.max(I, K); q++) {
			for (int r = -K + 1; r < Math.max(J, K); r++) {
				voxelsAtQR = this.cubesAtGridPosition(q, r);
				for (int[] voxel : voxelsAtQR) {
					id = index(voxel[0], voxel[1], voxel[2]);
					if (cubes.get(id)) {
						grid.addCube(voxel[0], voxel[1], voxel[2], cubes.getPalette(id));
					}
					if (grid.isFull(q, r)) {

						break;
					}

				}
			}

		}

	}
	

	
	

	public WB_IsoSystem6 rotateICC() {
		WB_IsoSystem6 result=new WB_IsoSystem6(L,I,K,J,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateICC();
		result.map();
		
	return result;
	}
	

	public WB_IsoSystem6 rotateICW() {
		WB_IsoSystem6 result=new WB_IsoSystem6(L,I,K,J,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateICW();
		result.map();
		
	return result;
	}

	
	public WB_IsoSystem6 rotateJCW() {
		if(YFLIP) {
			return rotateJCCImpl();
		}else {
			
			return rotateJCWImpl();
		}
	}
	public WB_IsoSystem6 rotateJCC() {
		if(YFLIP) {
			return rotateJCWImpl();
		}else {
			return rotateJCCImpl();
		}
	}


	WB_IsoSystem6 rotateJCCImpl() {
		WB_IsoSystem6 result=new WB_IsoSystem6(L,K,J,I,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateJCC();
		result.map();
		
	return result;
	}
	

	WB_IsoSystem6 rotateJCWImpl() {
		WB_IsoSystem6 result=new WB_IsoSystem6(L,K,J,I,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateJCW();
		result.map();
		
	return result;
	}
	
	public WB_IsoSystem6 rotateKCC() {
		WB_IsoSystem6 result=new WB_IsoSystem6(L,J,I,K,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateKCC();
		result.map();
		
	return result;
	}
	

	public WB_IsoSystem6 rotateKCW() {
		WB_IsoSystem6 result=new WB_IsoSystem6(L,J,I,K,centerX,centerY,palettes,seed,false,home);
		result.cubes=cubes.rotateKCW();
		result.map();
		
	return result;
	}

}
