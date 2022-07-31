package wblut.isogrid;

import java.util.ArrayList;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.hexgrid.WB_HexGridData36;


public class WB_IsoHexGrid36  extends WB_IsoHexGrid implements WB_HexGridData36 {
	

	public WB_IsoHexGrid36() {
		super();
		offsets = new double[50];
		for (int i = 0; i < 25; i++) {
			offsets[2 * i] = vertexOffsets[2 * i] * s60;
			offsets[2 * i + 1] = (vertexOffsets[2 * i + 1] - vertexOffsets[2 * i] * c60);
		}
	}

	

	 void addTriangle(int q, int r, int f, int s, int z, int orientation, int colorSourceIndex, int i, int j, int k) {
		long key = getCellHash(q, r);
		WB_IsoGridCell cell = cells.get(key);

		if (cell == null) {
			cell = new WB_IsoGridCell(q, r, 36);
			cells.put(key, cell);

		}

		if (z > cell.getZ(s)) {
			cell.orientation[s] = orientation;
			cell.part[s] =-1;
			cell.z[s] = z;
			cell.occupied[s]=true;
			cell.triangleIndex[s] = f;
			cell.triangleColorSourceIndex[s] = colorSourceIndex;
			cell.setI(s,i);
			cell.setJ(s,j);
			cell.setK(s,k);
			cell.triangleUV[s] = triangleUVs[f];
			cell.triangleUVDirections[s][0] = triangleUVDirections[f][0];
			cell.triangleUVDirections[s][1] = triangleUVDirections[f][1];
			cell.triangleUVDirectionSigns[s][0] = triangleUVDirectionSigns[f][0];
			cell.triangleUVDirectionSigns[s][1] = triangleUVDirectionSigns[f][1];
			cell.triangleUVOffsets[s][0] = cell.getIndices(s)[cell.triangleUVDirections[s][0]]*cell.triangleUVDirectionSigns[s][0]-(cell.triangleUVDirectionSigns[s][0]<0?1:0);
			cell.triangleUVOffsets[s][1] = cell.getIndices(s)[cell.triangleUVDirections[s][1]]*cell.triangleUVDirectionSigns[s][1]-(cell.triangleUVDirectionSigns[s][1]<0?1:0);

		}

	}

	public void addCube(int i, int j, int k, int... statePalette) {
		int state=statePalette[0];
		int palette=statePalette[1];
		
		int z = i + j + k;
		int q = i - k;
		int r = j - k;
		int layer = q+r;
		while (layer > 1) {
			layer -= 3;
		}
	
		while (layer < -1) {
			layer += 3;
		}

		int ns;
		switch (layer) {

		case 0:
			for (int s = 0; s < 36; s++) {
				if (triangleOrientations[state][s] >= 0)
					addTriangle(q, r, s, s, z, triangleOrientations[state][s], palette, i, j, k);
			}
			break;

		case +1:
			for (int s = 0; s < 36; s++) {
				ns = mapTrianglesFromPosOne[s];
				if (triangleOrientations[state][s] >= 0)
					addTriangle(q + mapQFromPosOne[s], r + mapRFromPosOne[s], s, ns, z, triangleOrientations[state][s],
							palette, i, j, k);
			}
			break;
		case -1:
			for (int s = 0; s < 36; s++) {
				ns = mapTrianglesFromNegOne[s];
				if (triangleOrientations[state][s] >= 0)
					addTriangle(q + mapQFromNegOne[s], r + mapRFromNegOne[s], s, ns, z, triangleOrientations[state][s],
							palette, i, j, k);
			}
			break;

		default:

		}
	}
public boolean isFull(int q, int r) {
		
		int layer = q+r;
		while (layer > 1) {
			layer -= 3;
		}
	
		while (layer < -1) {
			layer += 3;
		}

		int ns;
		switch (layer) {

		case 0:
			long key = getCellHash(q, r);
			WB_IsoGridCell cell = cells.get(key);
	       if(cell==null || !cell.isFull()) return false;
			
	       
			return true;
			
		case +1:
			
			for (int s = 0; s < 36; s++) {
				key = getCellHash(q + mapQFromPosOne[s], r + mapRFromPosOne[s]);
				cell = cells.get(key);
				if(cell==null) return false;
				ns = mapTrianglesFromPosOne[s];
				if(!cell.isOccupied(ns)) return false;
			}
		    return true;

		case -1:
			for (int s = 0; s < 36; s++) {
				key = getCellHash(q + mapQFromNegOne[s], r +mapRFromNegOne[s]);
				cell = cells.get(key);
				if(cell==null) return false;
				ns = mapTrianglesFromNegOne[s];
				if(!cell.isOccupied(ns)) return false;
			}
			return true;

		default:
			return false;
		}
		
	}



	public void collectLines(boolean optimize) {

		outlinesMap.clear();
		collectInterHexSegmentsOutline(outlinesMap);
		collectInterPieceSegmentsOutline(outlinesMap);
		collectIntraPieceSegmentsOutline(outlinesMap);

		outlines = new ArrayList<WB_IsoGridLine>();
		outlines.addAll(outlinesMap.values());
		outlines.sort(new WB_IsoGridLine.HexLineSort());
		int i = 0;
		for (WB_IsoGridLine line : outlines) {
			line.sort();
			if(optimize) { line.optimize();
			if (i % 2 == 0)
				line.reverse();}
			i++;
		}

		linesMap.clear();
		collectInterHexSegmentsInterior(linesMap);
		collectInterPieceSegmentsInterior(linesMap);
		collectIntraPieceSegmentsInterior(linesMap);

		lines = new ArrayList<WB_IsoGridLine>();
		lines.addAll(linesMap.values());
		lines.sort(new WB_IsoGridLine.HexLineSort());
		i = 0;
		for (WB_IsoGridLine line : lines) {
			line.sort();
			if(optimize) {line.optimize();
			if (i % 2 == 0)
				line.reverse();}
			i++;
		}
	}

	private void collectInterHexSegmentsInterior(Map<Long, WB_IsoGridLine> linesMap) {
		WB_IsoGridCell neighbor;
		int z, orientation, palette, part;
		long hash, nhash;
		for (WB_IsoGridCell cell : cells.values()) {
			hash = getCellHash(cell.getQ(), cell.getR());
			for (int i = 0; i < 36; i++) {
				if (cell.isOccupied(i) ) {
					if (interHexNeighbor[i] >= 0) {
						neighbor = getNeighbor(cell.getQ(), cell.getR(), i);
						if (neighbor == null) {
							// addSegment(cell.getQ(), cell.getR(), interHexSegment[2 * i],
							// interHexSegment[2 * i + 1],
							// linesMap);
						} else {
							nhash = getCellHash(cell.getQ() + interHexNeighborQ[i], cell.getR() + interHexNeighborR[i]);
							orientation = neighbor.getOrientation(interHexNeighbor[i]);
							part = neighbor.getPart(interHexNeighbor[i]);
							if (cell.getPart(i) == part && (nhash < hash || !neighbor.isOccupied(interHexNeighbor[i]))) {

								z = neighbor.getZ(interHexNeighbor[i]);
								palette = neighbor.getColor(interHexNeighbor[i]);
								if (areSeparate(orientation, cell.getOrientation(i), palette, cell.getColor(i),
										cell.getZ(i), z)) {
									addSegment(cell.getQ(), cell.getR(), interHexSegment[2 * i],
											interHexSegment[2 * i + 1], linesMap);
								}
							}

						}
					}
				}

			}

		}
	}

	private void collectInterHexSegmentsOutline(Map<Long, WB_IsoGridLine> outlinesMap) {
		WB_IsoGridCell neighbor;
		int part, orientation;
		long hash, nhash;
		for (WB_IsoGridCell cell : cells.values()) {
			hash = getCellHash(cell.getQ(), cell.getR());
			for (int i = 0; i < 36; i++) {
				if (cell.isOccupied(i)) {
					if (interHexNeighbor[i] >= 0) {
						neighbor = getNeighbor(cell.getQ(), cell.getR(), i);
						if (neighbor == null) {
							addSegment(cell.getQ(), cell.getR(), interHexSegment[2 * i], interHexSegment[2 * i + 1],
									outlinesMap);
						} else {
							nhash = getCellHash(cell.getQ() + interHexNeighborQ[i], cell.getR() + interHexNeighborR[i]);
							orientation = neighbor.getOrientation(interHexNeighbor[i]);
							if (nhash < hash || !neighbor.isOccupied(interHexNeighbor[i])) {
								part = neighbor.getPart(interHexNeighbor[i]);
								if (cell.getPart(i) != part) {
									addSegment(cell.getQ(), cell.getR(), interHexSegment[2 * i],
											interHexSegment[2 * i + 1], outlinesMap);
								}
							}
						}
					}
				}

			}

		}
	}



	private void collectInterPieceSegmentsInterior(Map<Long, WB_IsoGridLine> linesMap) {
		int z, orientation, palette, part;
		for (WB_IsoGridCell cell : cells.values()) {
			for (int i = 0; i < 36; i++) {
				if (interPieceNeighbor[i] > i) {
					orientation = cell.getOrientation(interPieceNeighbor[i]);
					z = cell.getZ(interPieceNeighbor[i]);
					palette = cell.getColor(interPieceNeighbor[i]);
					part = cell.getPart(interPieceNeighbor[i]);
					if (cell.getPart(i) != part) {

					} else if (areSeparate(orientation, cell.getOrientation(i), palette, cell.getColor(i),
							cell.getZ(i), z)) {
						addSegment(cell.getQ(), cell.getR(), interPieceSegment[2 * i],
								interPieceSegment[2 * i + 1], linesMap);
					}
				}

			}

		}
	}

	private void collectInterPieceSegmentsOutline(Map<Long, WB_IsoGridLine> outlinesMap) {
		int part;
		for (WB_IsoGridCell cell : cells.values()) {
			for (int i = 0; i < 36; i++) {
				if (interPieceNeighbor[i] > i) {
					part = cell.getPart(interPieceNeighbor[i]);
					if (cell.getPart(i) != part) {
						addSegment(cell.getQ(), cell.getR(), interPieceSegment[2 * i],
								interPieceSegment[2 * i + 1], outlinesMap);
					}
				}

			}

		}
	}

	

	private void collectIntraPieceSegmentsInterior(Map<Long, WB_IsoGridLine> linesMap) {
		int z, orientation, palette, part;
		for (WB_IsoGridCell cell : cells.values()) {
			for (int i = 0; i < 36; i++) {

				orientation = cell.getOrientation(intraPieceNeighbor[i]);
				z = cell.getZ(intraPieceNeighbor[i]);
				palette = cell.getColor(intraPieceNeighbor[i]);
				part = cell.getPart(intraPieceNeighbor[i]);
				if (cell.getPart(i) != part) {
					// outline
				} else if (areSeparate(orientation, cell.getOrientation(i), palette, cell.getColor(i), cell.getZ(i),
						z)) {
					addSegment(cell.getQ(), cell.getR(), intraPieceSegment[2 * i], intraPieceSegment[2 * i + 1],
							linesMap);
				}

			}

		}
	}

	private void collectIntraPieceSegmentsOutline(Map<Long, WB_IsoGridLine> outlinesMap) {
		int part;
		for (WB_IsoGridCell cell : cells.values()) {
			for (int i = 0; i < 36; i++) {
				part = cell.getPart(intraPieceNeighbor[i]);
				if (cell.getPart(i) != part) {
					addSegment(cell.getQ(), cell.getR(), intraPieceSegment[2 * i], intraPieceSegment[2 * i + 1],
							outlinesMap);
				}

			}

		}
	}

	private WB_IsoGridCell getNeighbor(int q, int r, int i) {
		return cells.get(getCellHash(q + interHexNeighborQ[i], r + interHexNeighborR[i]));
	}

	private void addSegment(double q, double r, int i1, int i2, Map<Long, WB_IsoGridLine> linesMap) {
		WB_IsoGridSegment segment = new WB_IsoGridSegment((int) Math.round(6 * (q + vertexOffsets[2 * i1])),
				(int) Math.round(6 * (r + vertexOffsets[2 * i1 + 1])),
				(int) Math.round(6 * (q + vertexOffsets[2 * i2])),
				(int) Math.round(6 * (r + vertexOffsets[2 * i2 + 1])));
		long key = segment.getHash();
		WB_IsoGridLine line = linesMap.get(key);
		if (line == null) {
			line = new WB_IsoGridLine(segment.getType(), segment.getLineValue());
			linesMap.put(key, line);
		}
		line.add(segment);

	}

	

	
	public double[] getGridCoordinates(double q, double r, double ox, double oy, double sx, double sy) {
		return new double[] { q * s60 * sx + ox, (r - q * c60) * sy + oy };
	}

	public int[] getTriangleAtGridCoordinates(double x, double y, double ox, double oy, double sx, double sy) {
		int q = (int) Math.round((x - ox) / (s60 * sx));
		int r = (int) Math.round((c60 * sx * (x - ox) + s60 * sy * (y - oy)) / (s60 * sx * sy));
		double[] xyc = getGridCoordinates((int) Math.round(q), (int) Math.round(r),ox,oy,sx,sy);
		double angle = Math.atan2(y - xyc[1], x - xyc[0]) / Math.PI * 180.0 + 30.0;
		if (angle < 0)
			angle += 360;
		int t = (int) angle / 60;

		return new int[] { q, r, t };
	}



	@Override
	void getHexCoordinates(int i, double ox, double oy, double sx, double sy, double[] into) {
		into[0]=ox + offsets[2 * i] * sx;
		into[1]= oy + offsets[2 * i + 1] * sy;
	}


	@Override
	void getTriangleCoordinates(int t, int i, double ox, double oy, double sx, double sy, double[] into) {
		into[0]= ox + offsets[2 * triangleVertices[t][i]] * sx;
		into[1]=oy + offsets[2 * triangleVertices[t][i] + 1] * sy;
	}
	
	
	

	
}
