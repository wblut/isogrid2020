package wblut.isogrid;



import processing.core.PApplet;

public class WB_IsoSystem6 extends WB_IsoSystem<WB_IsoHexGrid6> {


	public WB_IsoSystem6(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,WB_IsoHexGrid6 grid, PApplet home) {
		super(L,I,J,K,centerX,centerY,colors,seed,grid,home);
	}

	public WB_IsoSystem6(WB_IsoSystem6 iso) {
		super(iso);
	}
	
	
	public void mapVoxelsToHexGrid() {
		grid.clear();
		int id=0;
		for (int i = 0; i < I; i++) {
			for (int j =0; j < J; j++) {
				for (int k =0; k < K; k++) {
					if (cubes.get(id))
						grid.addCube(i, j, k, cubes.getPalette(id));
					id++;
				}
			}
		}
		cubes.labelParts();		
		grid.setParts(cubes);		
		grid.collectRegions();		
		grid.collectLines();
	}

	@Override
	int getNumberOfTriangles() {
		return 6;
	}


	
	
}
