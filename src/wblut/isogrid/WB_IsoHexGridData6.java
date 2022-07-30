package wblut.isogrid;

import wblut.hexgrid.WB_HexGridData6;

public interface WB_IsoHexGridData6 extends WB_HexGridData6{

	static final int[] mapTrianglesFromPosOne = new int[] { 4, 3, 0, 5, 2, 1 };
	static final int[] mapQFromPosOne = new int[] { 1, 1, -1, -1, 0, 0 };
	static final int[] mapRFromPosOne = new int[] { 1, 1, 0, 0, -1, -1 };
	static final int[] mapTrianglesFromNegOne = new int[] { 2, 5, 4, 1, 0, 3 };
	static final int[] mapQFromNegOne = new int[] { 1, 0, 0, -1, -1, 1 };
	static final int[] mapRFromNegOne = new int[] { 0, 1, 1, -1, -1, 0 };

	static int[] triangleOrientations = new int[] { 0, 1, 1, 2, 2, 0 };
	static int[] triangleSharedOrientations = new int[] { 5,2,1,4,3,0 };

	static final double[][] triangleUVs = new double[][] {

			{ 0,0, 1, 1, 1,0 }, { 1, 1, 1, 0, 0, 0 }, { 1, 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0,1 },
			{ 1, 0, 0, 1, 1, 1 }, { 0, 0, 0, 1, 1, 1 } };

	static final int[][] triangleUVDirections = new int[][] {

			{ 2, 1 }, { 0, 2 }, { 0, 2 }, { 0, 1 }, { 0, 1 }, { 2, 1 } };
	static final int[][] triangleUVDirectionSigns = new int[][] {

			{ -1, -1 }, { 1, 1 }, { 1, 1 }, { 1, -1 }, { 1, -1 }, { -1, -1 } };

}
