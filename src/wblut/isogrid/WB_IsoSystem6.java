package wblut.isogrid;

import java.util.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.hexgrid.WB_HexGrid;
import wblut.hexgrid.WB_HexGrid6;
import wblut.isogrid.color.WB_IsoColor;

public class WB_IsoSystem6 extends WB_IsoSystem {

	public WB_IsoSystem6(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			boolean full, PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, full, home);
	}
	
	public WB_IsoSystem6(double L, int I, int J, int K, double centerX, double centerY, WB_IsoColor colors, int seed,
			boolean full, PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, full, home);
	}

	public WB_IsoSystem6(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
			PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, true, home);
	}
	
	public WB_IsoSystem6(double L, int I, int J, int K, double centerX, double centerY,WB_IsoColor colors, int seed,
			PApplet home) {
		super(L, I, J, K, centerX, centerY, colors, seed, true, home);
	}

	public WB_IsoSystem6(WB_IsoSystem6 iso) {
		super(iso);
	}
	
	public WB_IsoSystem6(WB_IsoSystem6 iso, int colors) {
		super(iso, colors);
	}
	
	public WB_IsoSystem6 (WB_IsoSystem6 iso, int splitJ, int deltaJ) {
		super(iso, splitJ,deltaJ);
	}

	public WB_IsoSystem6(WB_IsoSystem iso, int scaleI, int scaleJ, int scaleK) {
		super(iso, scaleI, scaleJ, scaleK);
	}

	public WB_IsoSystem6(boolean[][][] pattern, int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY, int[] colors, int seed, PApplet home) {
		super(pattern, scaleI, scaleJ, scaleK, L, centerX, centerY, colors, seed, home);

	}
	
	public WB_IsoSystem6(boolean[][][] pattern, int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY, WB_IsoColor colors, int seed, PApplet home) {
		super(pattern, scaleI, scaleJ, scaleK, L, centerX, centerY, colors, seed, home);
}
	
	public WB_IsoSystem6(boolean[][][] pattern, int[][][] colorIndices, int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY, WB_IsoColor colors, int seed, PApplet home) {
		super(pattern, colorIndices, scaleI, scaleJ, scaleK, L, centerX, centerY, colors, seed, home);
}

	public WB_IsoSystem6(boolean[] pattern, int pI, int pJ, int pK,int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY, int[] colors, int seed, PApplet home) {
		super(pattern,pI,pJ,pK, scaleI, scaleJ, scaleK, L, centerX, centerY, colors, seed, home);

	}
	
	public WB_IsoSystem6(boolean[] pattern, int pI, int pJ, int pK, int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY, WB_IsoColor colors, int seed, PApplet home) {
		super(pattern, pI,pJ,pK,scaleI, scaleJ, scaleK, L, centerX, centerY, colors, seed, home);

	}
	
	
	/**
	 * From part.
	 *
	 * @param part the part
	 * @param scaleI the scale I
	 * @param scaleJ the scale J
	 * @param scaleK the scale K
	 * @param L the l
	 * @param centerX the center X
	 * @param centerY the center Y
	 * @return the w B iso system 6
	 */
	public WB_IsoSystem6 fromPart(int part, int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY) {
		int[] partLimits=cubeGrid.getPartLimits(part);
		if(partLimits==null) return null;
		boolean[][][] subGrid=new boolean[partLimits[1]-partLimits[0]+1][partLimits[3]-partLimits[2]+1][partLimits[5]-partLimits[4]+1];
		int[][][] subColors=new int[partLimits[1]-partLimits[0]+1][partLimits[3]-partLimits[2]+1][partLimits[5]-partLimits[4]+1];
		int index;
		for(int i=0;i<=partLimits[1]-partLimits[0];i++) {
			for(int j=0;j<=partLimits[3]-partLimits[2];j++) {
				for(int k=0;k<=partLimits[5]-partLimits[4];k++) {
					index=index(partLimits[0]+i,partLimits[2]+j,partLimits[4]+k);
					subGrid[i][j][k]=(cubeGrid.getPart(index)==part)?cubeGrid.get(index):false;
					subColors[i][j][k]=(cubeGrid.getPart(index)==part)?cubeGrid.getColorSourceIndex(index):-1;
					
				}
			}
		}
		WB_IsoSystem6 iso=new WB_IsoSystem6(subGrid, subColors,scaleI,scaleJ,scaleK,L,centerX,centerY,colorSources.get(0),seed,home);
		for(int i=1;i<colorSources.size();i++) {
			iso.addColorSource(colorSources.get(i));
		}
		
		return iso;
		
	}
	public WB_IsoSystem6 slice(int sI, int sJ, int sK, int dI, int dJ, int dK,  int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY) {
		
		boolean[][][] subGrid=new boolean[dI][dJ][dK];
		int[][][] subColors=new int[dI][dJ][dK];
		int index;
		for(int i=sI;i<sI+dI;i++) {
			for(int j=sJ;j<sJ+dJ;j++) {
				for(int k=sK;k<sK+dK;k++) {
					index=index(i,j,k);
					subGrid[i][j][k]=cubeGrid.get(index);
					subColors[i][j][k]=(cubeGrid.get(index))?cubeGrid.getColorSourceIndex(index):-1;
					
				}
			}
		}
		WB_IsoSystem6 iso=new WB_IsoSystem6(subGrid, subColors,scaleI,scaleJ,scaleK,L,centerX,centerY,colorSources.get(0),seed,home);
		for(int i=1;i<colorSources.size();i++) {
			iso.addColorSource(colorSources.get(i));
		}
		
		return iso;
		
	}
	void setGrid() {
		hexGrid = new WB_IsoHexGrid6();
	}

	int getNumberOfTriangles() {
		return 6;
	}

	public void mapVoxelsToHexGrid() {
		hexGrid.clear();
		int id = 0;
		List<int[]> voxelsAtQR;
		for (int q = -K + 1; q < Math.max(I, K); q++) {
			for (int r = -K + 1; r < Math.max(J, K); r++) {
				voxelsAtQR = this.cubesAtGridPosition(q, r);
				for (int[] voxel : voxelsAtQR) {
					id = index(voxel[0], voxel[1], voxel[2]);
					if (cubeGrid.get(id)) {
						hexGrid.addCube(voxel[0], voxel[1], voxel[2], cubeGrid.getColorSourceIndex(id));
					}
					if (hexGrid.isFull(q, r)) {

						break;
					}

				}
			}
		}
	}
	
	public void mapVoxelsToHexGrid(int minZ, int maxZ) {
		hexGrid.clear();
		int id = 0;
		List<int[]> voxelsAtQR;
		int z;
		for (int q = -K + 1; q < Math.max(I, K); q++) {
			for (int r = -K + 1; r < Math.max(J, K); r++) {
				voxelsAtQR = this.cubesAtGridPosition(q, r);
				for (int[] voxel : voxelsAtQR) {
					z=voxel[0]+ voxel[1]+ voxel[2];
					if(z>=minZ && z<maxZ) {
					id = index(voxel[0], voxel[1], voxel[2]);
					if (cubeGrid.get(id)) {
						hexGrid.addCube(voxel[0], voxel[1], voxel[2], cubeGrid.getColorSourceIndex(id));
					}
					if (hexGrid.isFull(q, r)) {

						break;
					}
					}

				}
			}
		}
	}
	
	

	public WB_IsoSystem6 rotateICC() {
		WB_IsoSystem6 result=new WB_IsoSystem6(L,I,K,J,centerX,centerY,colorSources.get(0),seed,false,home);
		result.cubeGrid=cubeGrid.rotateICC();
		result.map();
		for(int i=1;i<colorSources.size();i++) {
			result.addColorSource(colorSources.get(i));
		}
	return result;
	}
	

	public WB_IsoSystem6 rotateICW() {
		WB_IsoSystem6 result=new WB_IsoSystem6(L,I,K,J,centerX,centerY,colorSources.get(0),seed,false,home);
		result.cubeGrid=cubeGrid.rotateICW();
		result.map();
		for(int i=1;i<colorSources.size();i++) {
			result.addColorSource(colorSources.get(i));
		}
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
		WB_IsoSystem6 result=new WB_IsoSystem6(L,K,J,I,centerX,centerY,colorSources.get(0),seed,false,home);
		result.cubeGrid=cubeGrid.rotateJCC();
		result.map();
		for(int i=1;i<colorSources.size();i++) {
			result.addColorSource(colorSources.get(i));
		}
	return result;
	}
	

	WB_IsoSystem6 rotateJCWImpl() {
		WB_IsoSystem6 result=new WB_IsoSystem6(L,K,J,I,centerX,centerY,colorSources.get(0),seed,false,home);
		result.cubeGrid=cubeGrid.rotateJCW();
		result.map();
		for(int i=1;i<colorSources.size();i++) {
			result.addColorSource(colorSources.get(i));
		}
	return result;
	}
	
	public WB_IsoSystem6 rotateKCC() {
		WB_IsoSystem6 result=new WB_IsoSystem6(L,J,I,K,centerX,centerY,colorSources.get(0),seed,false,home);
		result.cubeGrid=cubeGrid.rotateKCC();
		result.map();
		for(int i=1;i<colorSources.size();i++) {
			result.addColorSource(colorSources.get(i));
		}
	return result;
	}
	

	public WB_IsoSystem6 rotateKCW() {
		WB_IsoSystem6 result=new WB_IsoSystem6(L,J,I,K,centerX,centerY,colorSources.get(0),seed,false,home);
		result.cubeGrid=cubeGrid.rotateKCW();
		result.map();
		for(int i=1;i<colorSources.size();i++) {
			result.addColorSource(colorSources.get(i));
		}
	return result;
	}

	@Override
	public WB_HexGrid bakeTriangles(PGraphics pg) {
		WB_HexGrid bakedGrid=new WB_HexGrid6();
		for (WB_IsoGridCell cell : hexGrid.cells.values()) {
			for (int t = 0; t < cell.getNumberOfTriangles(); t++) {
				if (cell.orientation[t] > -1) {
					
					int offsetU = cell.getTriangleUOffset(t);
					int offsetV = cell.getTriangleVOffset(t);
					double scaleU,scaleV;
					switch (cell.getTriangleUDirection(t)) {
					case 0:
						scaleU = 1.0 / Math.max(1.0, I);

						break;

					case 1:
						scaleU = 1.0 / Math.max(1.0, J);

						break;

					case 2:
						scaleU = 1.0 / Math.max(1.0,  K);
						break;

					default:
						scaleU = 1.0;

					}

					switch (cell.getTriangleVDirection(t)) {
					case 0:
						scaleV = 1.0 / Math.max(1.0,  I);

						break;

					case 1:
						scaleV = 1.0 / Math.max(1.0,J);

						break;

					case 2:
						scaleV = 1.0 / Math.max(1.0,  K);

						break;

					default:
						scaleV = 1.0;
					}
					double[] UV=new double[] {
							scaleU * (cell.getTriangleU(t, 0) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(t, 0) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(t, 0) + offsetV),
							scaleU * (cell.getTriangleU(t, 1) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(t, 1) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(t, 1) + offsetV),
							scaleU * (cell.getTriangleU(t, 2) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(t, 2) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(t, 2) + offsetV)};

					
					
					
					
					
					bakedGrid.setTriangle(cell.getQ(), cell.getR(), t, colorSources.get(cell.getColorSourceIndex(t)).getColor(pg, cell, t), cell.getColorSourceIndex(t),cell.getPart(t), cell.getRegion(t),cell.getZ(t),UV);
					
				}
			}
		}
		return bakedGrid;
	}
	

	public WB_HexGrid bakeTriangles(PGraphics pg, int sourceI, int sourceJ, int sourceK, int offsetI, int offsetJ, int offsetK) {
		WB_HexGrid bakedGrid=new WB_HexGrid6();
		for (WB_IsoGridCell cell : hexGrid.cells.values()) {
			for (int t = 0; t < cell.getNumberOfTriangles(); t++) {
				if (cell.orientation[t] > -1) {
					
					 

					int offsetU = (cell.getIndices(t)[cell.triangleUVDirections[t][0]]+((cell.triangleUVDirections[t][0]==0)?offsetI:(cell.triangleUVDirections[t][0]==1)?offsetJ:offsetK))*cell.triangleUVDirectionSigns[t][0]-(cell.triangleUVDirectionSigns[t][0]<0?1:0);
					int offsetV = (cell.getIndices(t)[cell.triangleUVDirections[t][1]]+((cell.triangleUVDirections[t][1]==0)?offsetI:(cell.triangleUVDirections[t][1]==1)?offsetJ:offsetK))*cell.triangleUVDirectionSigns[t][1]-(cell.triangleUVDirectionSigns[t][1]<0?1:0);
					double scaleU,scaleV;
					switch (cell.getTriangleUDirection(t)) {
					case 0:
						scaleU = 1.0 / Math.max(1.0, sourceI);

						break;

					case 1:
						scaleU = 1.0 / Math.max(1.0, sourceJ);

						break;

					case 2:
						scaleU = 1.0 / Math.max(1.0,  sourceK);
						break;

					default:
						scaleU = 1.0;

					}

					switch (cell.getTriangleVDirection(t)) {
					case 0:
						scaleV = 1.0 / Math.max(1.0,  sourceI);

						break;

					case 1:
						scaleV = 1.0 / Math.max(1.0,sourceJ);

						break;

					case 2:
						scaleV = 1.0 / Math.max(1.0,  sourceK);

						break;

					default:
						scaleV = 1.0;
					}
					double[] UV=new double[] {
							scaleU * (cell.getTriangleU(t, 0) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(t, 0) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(t, 0) + offsetV),
							scaleU * (cell.getTriangleU(t, 1) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(t, 1) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(t, 1) + offsetV),
							scaleU * (cell.getTriangleU(t, 2) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(t, 2) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(t, 2) + offsetV)};

					
					
					
					
					
					bakedGrid.setTriangle(cell.getQ(), cell.getR(), t, colorSources.get(cell.getColorSourceIndex(t)).getColor(pg, cell, t), cell.getColor(t),cell.getPart(t), cell.getRegion(t),cell.getZ(t),UV);
					
				}
			}
		}
		return bakedGrid;
	}

}
