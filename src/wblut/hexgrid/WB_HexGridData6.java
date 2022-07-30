package wblut.hexgrid;

public interface WB_HexGridData6 {
	
	static public final int centerIndex = 6;

	static final double[] vertexOffsets = new double[] { 1, 0, 1, 1, 0, 1, -1, 0, -1, -1, 0, -1, 0, 0 };
	static final int[][] triangleVertices = new int[][] { { centerIndex, 0, 1 }, { centerIndex, 1, 2 },
			{ centerIndex, 2, 3 }, { centerIndex, 3, 4 }, { centerIndex, 4, 5 }, { centerIndex, 5, 0 } };

	static final double c60 = Math.cos(Math.PI / 3.0);
	static final double s60 = Math.sin(Math.PI / 3.0);
	static final int[] interHexNeighbor = new int[] { 3, 4, 5, 0, 1, 2 };
	static final int[] interHexSegment = new int[] { 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 0 };
	static final int[] interHexNeighborQ = new int[] { +2, +1, -1, -2, -1, 1 };
	static final int[] interHexNeighborR = new int[] { +1, +2, 1, -1, -2, -1 };
	static final int[] interTriangleNeighbor = new int[] { 1, 2, 3, 4, 5, 0 };
	static final int[] interTriangleSegment = new int[] { centerIndex, 1, centerIndex, 2, centerIndex, 3, centerIndex,
			4, centerIndex, 5, centerIndex, 0 };

	
}
