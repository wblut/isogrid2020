package wblut.isogrid;

import java.util.List;

import processing.core.PApplet;

public class WB_IsoSystem6 extends WB_IsoSystem {

	public WB_IsoSystem6(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			boolean full, PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, full, home);
		if (colors.length % 3 != 0) {
			throw new IllegalArgumentException("Palette length should be a mutiple of 3 or 10.");
		}
		numPalettes = colors.length / 3;
	}

	public WB_IsoSystem6(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, true, home);
		if (colors.length % 3 != 0) {
			throw new IllegalArgumentException("Palette length should be a mutiple of 3 or 10.");
		}
		numPalettes = colors.length / 3;
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
	
	public static void main(String... args) {
		 boolean[][][] pattern=WB_PixelFont.getText3xN("test 4", 5, 2, 1, false, false, true);

		  //pattern, I upsampling, J upsampling, K upsampling, cell size, center x, center y, colors, random seed, PApplet
		  WB_IsoSystem6 iso=new WB_IsoSystem6(3.6, 21, 5, 8, 0,0, new int[]{0,0,0},0, false, new PApplet());

		  iso.setPattern(pattern, 0, 0, 0, 1, 1, 1, false, false, false);
		  iso=new WB_IsoSystem6(iso,4,4,4);
		  iso.refresh();
		
	
	}
	
	

}
