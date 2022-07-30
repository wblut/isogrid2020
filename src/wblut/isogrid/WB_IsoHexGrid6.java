package wblut.isogrid;

import java.util.ArrayList;

import java.util.Map;

import processing.core.PApplet;
import processing.core.PGraphics;

public class WB_IsoHexGrid6 extends WB_IsoHexGrid implements WB_IsoHexGridData6 {

	public WB_IsoHexGrid6() {
		super();
		offsets = new double[14];
		for (int i = 0; i < 7; i++) {
			offsets[2 * i] = vertexOffsets[2 * i] * s60;
			offsets[2 * i + 1] = (vertexOffsets[2 * i + 1] - vertexOffsets[2 * i] * c60);
		}
	}


	void addTriangle(int q, int r, int f, int s, int z, int orientation, int colorSourceIndex, int i,
			int j, int k) {
		long key = getCellHash(q, r);
		WB_IsoGridCell cell = cells.get(key);

		if (cell == null) {
			cell = new WB_IsoGridCell(q, r, 6);
			cells.put(key, cell);

		}

		if (z > cell.z[s]) {
			
			int cycle=(f==s)?0:(s == mapTrianglesFromPosOne[f])?((s % 2 == 1) ? 2 : 4):((s % 2 == 0) ? 2 : 4);
			
			
			cell.orientation[s] = orientation;
			cell.z[s] = z;
			cell.triangleIndex[s] = f;
			cell.triangleColorSourceIndex[s] = colorSourceIndex;
			cell.setI(s,i);
			cell.setJ(s,j);
			cell.setK(s,k);
			cell.triangleUV[s] = cycle(cycle, triangleUVs[f]);
			cell.triangleUVDirections[s][0] = triangleUVDirections[f][0];
			cell.triangleUVDirections[s][1] = triangleUVDirections[f][1];
			cell.triangleUVDirectionSigns[s][0] = triangleUVDirectionSigns[f][0];
			cell.triangleUVDirectionSigns[s][1] = triangleUVDirectionSigns[f][1];
			cell.triangleUVOffsets[s][0] = cell.getIndices(s)[cell.triangleUVDirections[s][0]]*cell.triangleUVDirectionSigns[s][0]-(cell.triangleUVDirectionSigns[s][0]<0?1:0);
			cell.triangleUVOffsets[s][1] = cell.getIndices(s)[cell.triangleUVDirections[s][1]]*cell.triangleUVDirectionSigns[s][1]-(cell.triangleUVDirectionSigns[s][1]<0?1:0);
			cell.part[s] = -1;
			cell.occupied[s]=true;
		}
		

	}

	double[] cycle(int c, double[] source) {
		int n = source.length;
		double[] result = new double[n];
		for (int i = 0; i < n; i++) {
			result[i] = source[(i + c) % n];
		}
		return result;

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
	       if(cell==null||!cell.isFull()) return false;
			
	
			return true;
			
		case +1:
			
			for (int s = 0; s < 6; s++) {
				key = getCellHash(q + mapQFromPosOne[s], r + mapRFromPosOne[s]);
				cell = cells.get(key);
				if(cell==null) return false;
				ns = mapTrianglesFromPosOne[s];
				if(!cell.isOccupied(ns)) return false;
			}
		    return true;

		case -1:
			for (int s = 0; s < 6; s++) {
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
	

	public void addCube(int i, int j, int k, int...params) {
int palette=params[0];
		
		
		int z = i + j + k;
		int q = i - k;
		int r = j - k;
		int layer = q+r;
		layer=layer-3*((layer+1)/3);
		while (layer > 1) {
			layer -= 3;
		}
	
		while (layer < -1) {
			layer += 3;
		}

		int ns;
		switch (layer) {

		case 0:
			for (int s = 0; s < 6; s++) {
				if (triangleOrientations[s] >= 0)
					addTriangle(q, r, s, s, z, triangleOrientations[s], palette, i, j, k);
			}
			break;

		case +1:
			for (int s = 0; s < 6; s++) {
				ns = mapTrianglesFromPosOne[s];
				if (triangleOrientations[s] >= 0)
					addTriangle(q + mapQFromPosOne[s], r + mapRFromPosOne[s], s, ns, z,
							triangleOrientations[s],  palette, i, j, k);
			}
			break;
		case -1:
			for (int s = 0; s < 6; s++) {
				ns = mapTrianglesFromNegOne[s];
				if (triangleOrientations[s] >= 0)
					addTriangle(q + mapQFromNegOne[s], r + mapRFromNegOne[s], s, ns,  z,
							triangleOrientations[s],  palette, i, j, k);
			}
			break;

		default:

		}
		
	}


	public void collectLines() {

		outlinesMap.clear();
		collectInterHexSegmentsOutline(outlinesMap);
		collectInterTriangleSegmentsOutline(outlinesMap);

		outlines = new ArrayList<WB_IsoGridLine>();
		outlines.addAll(outlinesMap.values());
		outlines.sort(new WB_IsoGridLine.HexLineSort());
		int i = 0;
		for (WB_IsoGridLine line : outlines) {
			line.sort();
			line.optimize();
			if (i % 2 == 0)
				line.reverse();
			i++;
		}

		linesMap.clear();
		collectInterHexSegmentsInterior(linesMap);
		collectInterTriangleSegmentsInterior(linesMap);

		lines = new ArrayList<WB_IsoGridLine>();
		lines.addAll(linesMap.values());
		lines.sort(new WB_IsoGridLine.HexLineSort());
		i = 0;
		for (WB_IsoGridLine line : lines) {
			line.sort();
			line.optimize();
			if (i % 2 == 0)
				line.reverse();
			i++;
		}
		
		gridlinesMap.clear();
		collectInterHexSegmentsGrid(gridlinesMap);
		collectInterTriangleSegmentsGrid(gridlinesMap);

		gridlines = new ArrayList<WB_IsoGridLine>();
		gridlines.addAll(gridlinesMap.values());
		gridlines.sort(new WB_IsoGridLine.HexLineSort());
		i = 0;
		for (WB_IsoGridLine line : gridlines) {
			line.sort();
			line.optimize();
			if (i % 2 == 0)
				line.reverse();
			i++;
		}
		
	}


	private void collectInterHexSegmentsInterior(Map<Long, WB_IsoGridLine> linesMap) {
		WB_IsoGridCell neighbor;
		int z, orientation, colorIndex, part;
		long hash, nhash;
		for (WB_IsoGridCell cell : cells.values()) {
			hash = getCellHash(cell.getQ(), cell.getR());
			for (int i = 0; i < 6; i++) {
				if (cell.getOrientation(i) >= 0) {
					if (interHexNeighbor[i] >= 0) {
						neighbor = getNeighbor(cell.getQ(), cell.getR(), i);
						if (neighbor == null) {

						} else {
							nhash = getCellHash(cell.getQ() + interHexNeighborQ[i], cell.getR() + interHexNeighborR[i]);
							orientation = neighbor.getOrientation(interHexNeighbor[i]);
							part = neighbor.getPart(interHexNeighbor[i]);
							if (cell.getPart(i) == part && (nhash < hash ||  !neighbor.isOccupied(interHexNeighbor[i]))) {

								z = neighbor.getZ(interHexNeighbor[i]);
								colorIndex = neighbor.getColor(interHexNeighbor[i]);
								if (areSeparate(orientation, cell.getOrientation(i), colorIndex, cell.getColor(i),
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
	
	private void collectInterHexSegmentsGrid(Map<Long, WB_IsoGridLine> linesMap) {
		WB_IsoGridCell neighbor;
		int z, orientation, colorIndex, part;
		long hash, nhash;
		for (WB_IsoGridCell cell : cells.values()) {
			hash = getCellHash(cell.getQ(), cell.getR());
			for (int i = 0; i < 6; i++) {
				if (cell.getOrientation(i) >= 0) {
					if (interHexNeighbor[i] >= 0) {
						neighbor = getNeighbor(cell.getQ(), cell.getR(), i);
						if (neighbor == null) {

						} else {
							nhash = getCellHash(cell.getQ() + interHexNeighborQ[i], cell.getR() + interHexNeighborR[i]);
							orientation = neighbor.getOrientation(interHexNeighbor[i]);
							part = neighbor.getPart(interHexNeighbor[i]);
							if (cell.getPart(i) == part && (nhash < hash ||  !neighbor.isOccupied(interHexNeighbor[i]))) {

								z = neighbor.getZ(interHexNeighbor[i]);
								colorIndex = neighbor.getColor(interHexNeighbor[i]);
								if (z!=cell.getZ(i)&&!areSeparate(orientation, cell.getOrientation(i), colorIndex, cell.getColor(i),
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
			for (int i = 0; i < 6; i++) {
				if (cell.getOrientation(i) >= 0) {
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
								addSegment(cell.getQ(), cell.getR(), interHexSegment[2 * i], interHexSegment[2 * i + 1],
										outlinesMap);
							}
						}

					}
				}
			}

		}
	}

	private void collectInterTriangleSegmentsGrid(Map<Long, WB_IsoGridLine> linesMap) {
		int z, orientation, palette, part;
		for (WB_IsoGridCell cell : cells.values()) {
			for (int i = 0; i < 6; i++) {

				orientation = cell.getOrientation(interTriangleNeighbor[i]);
				z = cell.getZ(interTriangleNeighbor[i]);
				palette = cell.getColor(interTriangleNeighbor[i]);
				part = cell.getPart(interTriangleNeighbor[i]);
				if (cell.getPart(i) != part) {

				} else if (z!=cell.getZ(i)&&!areSeparate(orientation, cell.getOrientation(i), palette, cell.getColor(i), cell.getZ(i),
						z)) {
					addSegment(cell.getQ(), cell.getR(), interTriangleSegment[2 * i], interTriangleSegment[2 * i + 1],
							linesMap);
				}

			}

		}
	}
	
	private void collectInterTriangleSegmentsInterior(Map<Long, WB_IsoGridLine> linesMap) {
		int z, orientation, palette, part;
		for (WB_IsoGridCell cell : cells.values()) {
			for (int i = 0; i < 6; i++) {

				orientation = cell.getOrientation(interTriangleNeighbor[i]);
				z = cell.getZ(interTriangleNeighbor[i]);
				palette = cell.getColor(interTriangleNeighbor[i]);
				part = cell.getPart(interTriangleNeighbor[i]);
				if (cell.getPart(i) != part) {

				} else if (areSeparate(orientation, cell.getOrientation(i), palette, cell.getColor(i), cell.getZ(i),
						z)) {
					addSegment(cell.getQ(), cell.getR(), interTriangleSegment[2 * i], interTriangleSegment[2 * i + 1],
							linesMap);
				}

			}

		}
	}

	private void collectInterTriangleSegmentsOutline(Map<Long, WB_IsoGridLine> outlinesMap) {
		int part;
		for (WB_IsoGridCell cell : cells.values()) {
			for (int i = 0; i < 6; i++) {

				part = cell.getPart(interTriangleNeighbor[i]);
				if (cell.getPart(i) != part) {
					addSegment(cell.getQ(), cell.getR(), interTriangleSegment[2 * i], interTriangleSegment[2 * i + 1],
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


	void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy) {
		vertex(pg, ox + offsets[2 * triangleVertices[t][i]] * sx, oy + offsets[2 * triangleVertices[t][i] + 1] * sy);

	}

	void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy, double u, double v) {
		vertex(pg, ox + offsets[2 * triangleVertices[t][i]] * sx, oy + offsets[2 * triangleVertices[t][i] + 1] * sy, u,
				v);

	}


	void line(PGraphics pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx, double sy) {
		pg.line((float) (q1 / 6.0 * s60 * sx + ox), (float) ((r1 - q1 * c60) / 6.0 * sy + oy),
				(float) ((q2 / 6.0 * s60 * sx) + ox), (float) ((r2 - q2 * c60) / 6.0 * sy + oy));

	}

	void point(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy) {
		pg.point((float) (q / 6.0 * s60 * sx + ox), (float) ((r - q * c60) / 6.0 * sy + oy));

	}
	
	void point(PApplet pg, double q, double r, double ox, double oy, double sx, double sy) {
		pg.point((float) (q / 6.0 * s60 * sx + ox), (float) ((r - q * c60) / 6.0 * sy + oy));
	
	}
	
	void circle(PApplet pg, double q, double r, double ox, double oy, double sx, double sy,double diameter) {
		pg.ellipse((float) (q / 6.0 * s60 * sx + ox),
				(float) ((r - q * c60) / 6.0 *sy + oy),(float)diameter,(float)diameter);
		
	}


	void circle(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy,double diameter) {
		pg.ellipse((float) (q / 6.0 * s60 * sx + ox),
				(float) ((r - q * c60) / 6.0 *sy + oy),(float)diameter,(float)diameter);
		
	}
	void line(PApplet pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx, double sy) {
		pg.line((float) (q1 / 6.0 * s60 * sx + ox), (float) ((r1 - q1 * c60) / 6.0 * sy + oy),
				(float) ((q2 / 6.0 * s60 * sx) + ox), (float) ((r2 - q2 * c60) / 6.0 * sy + oy));

	}
	
	void clippedLine(PApplet pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx, double sy, double xmin, double ymin, double xmax, double ymax) {
			double x0=q1 / 6.0 * s60 * sx + ox;
			double y0=(r1 - q1 * c60) / 6.0 * sy + oy;
			double x1=(q2 / 6.0 * s60 * sx) + ox;
			double y1=(r2 - q2 * c60) / 6.0 * sy + oy;
			
			int outcode0 = computeOutCode(x0, y0, xmin, ymin, xmax, ymax);
			int outcode1 = computeOutCode(x1, y1, xmin, ymin, xmax, ymax);
			boolean accept = false;

			while (true) {
				if ((outcode0 | outcode1)==0) {
					// bitwise OR is 0: both points inside window; trivially accept and exit loop
					accept = true;
					break;
				} else if ((outcode0 & outcode1)>0) {
					// bitwise AND is not 0: both points share an outside zone (LEFT, RIGHT, TOP,
					// or BOTTOM), so both must be outside window; exit loop (accept is false)
					break;
				} else {
					// failed both tests, so calculate the line segment to clip
					// from an outside point to an intersection with clip edge
					double x, y;
					x=0.0;
					y=0.0;

					// At least one endpoint is outside the clip rectangle; pick it.
					int outcodeOut = outcode1 > outcode0 ? outcode1 : outcode0;

					// Now find the intersection point;
					// use formulas:
					//   slope = (y1 - y0) / (x1 - x0)
					//   x = x0 + (1 / slope) * (ym - y0), where ym is ymin or ymax
					//   y = y0 + slope * (xm - x0), where xm is xmin or xmax
					// No need to worry about divide-by-zero because, in each case, the
					// outcode bit being tested guarantees the denominator is non-zero
					if ((outcodeOut & TOP)>0) {           // point is above the clip window
						x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0);
						y = ymax;
					} else if ((outcodeOut & BOTTOM)>0) { // point is below the clip window
						x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0);
						y = ymin;
					} else if ((outcodeOut & RIGHT)>0) {  // point is to the right of clip window
						y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0);
						x = xmax;
					} else if ((outcodeOut & LEFT)>0) {   // point is to the left of clip window
						y = y0 + (y1 - y0) * (xmin - x0) / (x1 - x0);
						x = xmin;
					}

					// Now we move outside point to intersection point to clip
					// and get ready for next pass.
					if (outcodeOut == outcode0) {
						x0 = x;
						y0 = y;
						outcode0 = computeOutCode(x0, y0, xmin, ymin, xmax, ymax);
					} else {
						x1 = x;
						y1 = y;
						outcode1 = computeOutCode(x1, y1, xmin, ymin, xmax, ymax);
					}
				}
			}
			if(accept) {
				pg.line((float) x0, (float) y0,
						(float) x1, (float) y1);
				
			}
			
	
	}
	
	int INSIDE = 0; // 0000
	int LEFT = 1;   // 0001
	int RIGHT = 2;  // 0010
	int BOTTOM = 4; // 0100
	int TOP = 8;    // 1000

	// Compute the bit code for a point (x, y) using the clip
	// bounded diagonally by (xmin, ymin), and (xmax, ymax)
	// ASSUME THAT xmax, xmin, ymax and ymin are global constants.
	int computeOutCode(double x, double y, double xmin, double ymin, double xmax, double ymax)
	{
		int code;
		code = INSIDE;          // initialised as being inside of [[clip window]]
		if (x < xmin)           // to the left of clip window
			code |= LEFT;
		else if (x > xmax)      // to the right of clip window
			code |= RIGHT;
		if (y < ymin)           // below the clip window
			code |= BOTTOM;
		else if (y > ymax)      // above the clip window
			code |= TOP;
		return code;
	}
	
	
	public double[] getGridCoordinates(double q, double r, double ox, double oy, double sx, double sy) {
		return new double[] { q * s60 * sx + ox, (r - q * c60) * sy + oy };
	}

	public int[] getTriangleAtGridCoordinates(double x, double y, double ox, double oy, double sx, double sy) {
		int q = (int) Math.round((x - ox) / (s60 * sx));
		int r = (int) Math.round((c60 * sx * (x - ox) + s60 * sy * (y - oy)) / (s60 * sx * sy));
		double[] xyc = getGridCoordinates((int) Math.round(q), (int) Math.round(r), ox, oy, sx, sy);
		double angle = Math.atan2(y - xyc[1], x - xyc[0]) / Math.PI * 180.0 + 30.0;
		if (angle < 0)
			angle += 360;
		int t = (int) angle / 60;

		return new int[] { q, r, t };

	}


}
