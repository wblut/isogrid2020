package wblut.hexgrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import wblut.isogrid.WB_IsoGridLine;
import wblut.isogrid.WB_IsoGridSegment;
import wblut.map.WB_DoNothingMap;
import wblut.map.WB_Map;


public abstract class WB_HexGrid {
	static final WB_Map DONOTHING = new WB_DoNothingMap();
	double[] offsets;
	Map<Long, WB_HexGridCell> cells;
	int numTriangles;

	Map<Long, WB_IsoGridLine> linesMap;
	Map<Long, WB_IsoGridLine> outlinesMap;
	Map<Long, WB_IsoGridLine> gridlinesMap;
	List<WB_IsoGridLine> lines;
	List<WB_IsoGridLine> outlines;
	List<WB_IsoGridLine> gridlines;
	double[] coord = new double[] { 0, 0 };
	double[] coord2 = new double[] { 0, 0 };
	boolean USEMAP;
	WB_Map map;
	
	

	WB_HexGrid() {
		cells = new HashMap<Long, WB_HexGridCell>();
		linesMap = new HashMap<Long, WB_IsoGridLine>();
		lines = new ArrayList<WB_IsoGridLine>();
		outlinesMap = new HashMap<Long, WB_IsoGridLine>();
		outlines = new ArrayList<WB_IsoGridLine>();
		gridlinesMap = new HashMap<Long, WB_IsoGridLine>();
		gridlines = new ArrayList<WB_IsoGridLine>();
		USEMAP = false;
		map = DONOTHING;
	}

	final public void clear() {
		cells.clear();
		linesMap.clear();
		outlinesMap.clear();
		gridlinesMap.clear();
	}
	
	public void setUseMap(boolean b) {
		USEMAP = b;
	}
	
	public void setMap(WB_Map map) {
		this.map = map;
	}

	public abstract void setTriangle(int q, int r, int t, int colors, int palette, int part, int region, int z);

	public abstract void setTriangle(int q, int r, int t, int colors, int palette, int part, int region, int z,
			double[] UV);

	public abstract void clearTriangle(int q, int r, int t);

	public abstract void collectLines();

	final public List<WB_HexGridCell> getCells() {
		List<WB_HexGridCell> cellsList = new ArrayList<WB_HexGridCell>();
		cellsList.addAll(cells.values());
		cellsList.sort(new WB_HexGridCell.HexCellSort());
		return cellsList;
	}

	final public WB_HexGridCell get(long key) {
		return cells.get(key);
	}

	// long hash from 2 int
	final static long getCellKey(int q, int r) {
		long A = (q >= 0 ? 2 * (long) q : -2 * (long) q - 1);
		long B = (r >= 0 ? 2 * (long) r : -2 * (long) r - 1);
		long C = ((A >= B ? A * A + A + B : A + B * B) / 2);
		return q < 0 && r < 0 || q >= 0 && r >= 0 ? C : -C - 1;
	}

	public void shift(int Q, int R) {
		Map<Long, WB_HexGridCell> shiftedCells = new HashMap<Long, WB_HexGridCell>();
		for (WB_HexGridCell cell : cells.values()) {
			cell.q = cell.q + Q;
			cell.r = cell.r + R;
			shiftedCells.put(getCellKey(cell.q, cell.r), new WB_HexGridCell(cell));
		}
		cells = shiftedCells;
	}

	public void add(WB_HexGrid grid) {
		for (WB_HexGridCell cell : grid.cells.values()) {
			WB_HexGridCell otherCell = cells.get(getCellKey(cell.q, cell.r));
			if (otherCell == null) {
				cells.put(getCellKey(cell.q, cell.r), new WB_HexGridCell(cell));
			} else {
				for (int i = 0; i < 6; i++) {
					if (!otherCell.isOccupied(i)) {
						otherCell.part[i] = cell.part[i];
						otherCell.triangleColor[i] = cell.triangleColor[i];
						otherCell.triangleColorSourceIndex[i] = cell.triangleColorSourceIndex[i];
						otherCell.z[i] = cell.z[i];
						otherCell.region[i] = cell.region[i];
						otherCell.occupied[i] = cell.occupied[i];
						otherCell.triangleUV[i] = cell.triangleUV[i];
					}
				}

			}
		}
		for (WB_IsoGridLine line : grid.lines) {
			lines.add(new WB_IsoGridLine(line));
		}
		for (WB_IsoGridLine line : grid.outlines) {
			outlines.add(new WB_IsoGridLine(line));
		}
		for (WB_IsoGridLine line : grid.gridlines) {
			gridlines.add(new WB_IsoGridLine(line));
		}

	}

	public boolean outOfBounds(int offsetq, int offsetr, int minQ, int maxQ, int minR, int maxR, int minS, int maxS) {
		int s;
		for (WB_HexGridCell cell : cells.values()) {
			s = ((cell.q + offsetq) - 2 * (cell.r + offsetr));

			if (cell.q + offsetq < minQ || cell.q + offsetq > maxQ || cell.r + offsetr < minR || cell.r + offsetr > maxR
					|| s < minS || s > maxS)
				return true;

		}
		return false;

	}

	public boolean outOfRadius(int offsetq, int offsetr, double radius, double sx, double sy) {
		double[] coord;
		for (WB_HexGridCell cell : cells.values()) {
			coord = getGridCoordinates(cell.q + offsetq, cell.r + offsetr, 0.0, 0.0, sx, sy);
			if (coord[0] * coord[0] + coord[1] * coord[1] > radius * radius)
				return true;
		}
		return false;

	}

	public boolean outOfRadius(int offsetq, int offsetr, double innerRadius, double outerRadius, double sx, double sy) {
		double[] coord;
		double d2;
		for (WB_HexGridCell cell : cells.values()) {
			coord = getGridCoordinates(cell.q + offsetq, cell.r + offsetr, 0.0, 0.0, sx, sy);
			d2 = coord[0] * coord[0] + coord[1] * coord[1];
			if (d2 > outerRadius * outerRadius || d2 < innerRadius * innerRadius)
				return true;
		}
		return false;

	}

	public boolean outOfImage(int offsetq, int offsetr, PImage img, double ox, double oy, double sx, double sy) {
		double[] coord;
		double isx = 1.0 / sx;
		double isy = 1.0 / sy;
		if (Double.isNaN(isx) || Double.isNaN(isy))
			return true;
		for (WB_HexGridCell cell : cells.values()) {
			coord = getGridCoordinates(cell.q + offsetq, cell.r + offsetr, ox, oy, 1.0 / sx, 1.0 / sy);
			int color = img.get((int) Math.round(coord[0]), (int) Math.round(coord[1]));
			if (((color >> 16) & 0xFF) <= 127)
				return true;
		}
		return false;

	}

	public int getColor(int offsetq, int offsetr, PImage img, double ox, double oy, double sx, double sy) {
		double[] coord;
		double isx = 1.0 / sx;
		double isy = 1.0 / sy;
		if (Double.isNaN(isx) || Double.isNaN(isy))
			return -1;

		coord = getGridCoordinates(offsetq, offsetr, ox, oy, 1.0 / sx, 1.0 / sy);
		int color = img.get((int) Math.round(coord[0]), (int) Math.round(coord[1]));
		return color;

	}

	public boolean overlap(WB_HexGrid grid) {
		if (grid.cells.size() > cells.size())
			return grid.overlap(this);
		for (WB_HexGridCell cell : grid.cells.values()) {

			if (cells.containsKey(getCellKey(cell.q, cell.r)))
				return true;
		}
		return false;
	}

	public boolean overlapTriangle(WB_HexGrid grid) {
		if (grid.cells.size() > cells.size())
			return grid.overlap(this);
		WB_HexGridCell otherCell;
		for (WB_HexGridCell cell : grid.cells.values()) {
			otherCell = cells.get(getCellKey(cell.q, cell.r));
			if (otherCell != null) {
				for (int i = 0; i < 6; i++) {
					if (cell.isOccupied(i) && otherCell.isOccupied(i))
						return true;

				}

			}

		}
		return false;

	}

	public boolean overlap(WB_HexGrid grid, int offsetq, int offsetr) {
		if (grid.cells.size() > cells.size())
			return grid.overlap(this, -offsetq, -offsetr);
		for (WB_HexGridCell cell : grid.cells.values()) {

			if (cells.containsKey(getCellKey(cell.q + offsetq, cell.r + offsetr)))
				return true;
		}
		return false;
	}

	public boolean overlapTriangle(WB_HexGrid grid, int offsetq, int offsetr) {
		if (grid.cells.size() > cells.size())
			return grid.overlap(this, -offsetq, -offsetr);
		WB_HexGridCell otherCell;
		for (WB_HexGridCell cell : grid.cells.values()) {
			otherCell = cells.get(getCellKey(cell.q + offsetq, cell.r + offsetr));
			if (otherCell != null) {
				for (int i = 0; i < 6; i++) {
					if (cell.isOccupied(i) && otherCell.isOccupied(i))
						return true;

				}

			}
		}
		return false;
	}

	public void pad() {
		WB_HexGridCell neighbor;
		long key;
		List<WB_HexGridCell> cellCol = new ArrayList<WB_HexGridCell>();
		cellCol.addAll(cells.values());
		for (WB_HexGridCell cell : cellCol) {
			for (int t = 0; t < 6; t++) {
				if (cell.occupied[t]) {
					key = getCellKey(cell.q + WB_HexGridData6.interHexNeighborQ[t],
							cell.r + WB_HexGridData6.interHexNeighborR[t]);
					neighbor = cells.get(key);
					if (neighbor == null) {
						cells.put(key, new WB_HexGridCell(cell.q + WB_HexGridData6.interHexNeighborQ[t],
								cell.r + WB_HexGridData6.interHexNeighborR[t], 6));

					}

				}

			}

		}

	}

	public void center() {
		int minQ = Integer.MAX_VALUE;
		int minR = Integer.MAX_VALUE;
		int maxQ = Integer.MIN_VALUE;
		int maxR = Integer.MIN_VALUE;
		for (WB_HexGridCell cell : cells.values()) {
			minQ = Math.min(minQ, cell.q);
			minR = Math.min(minR, cell.r);
			maxQ = Math.max(maxQ, cell.q);
			maxR = Math.max(maxR, cell.r);
		}
		int shiftQ = (maxQ + minQ) / 2;
		int shiftR = (maxR + minR) / 2;
		int res = (shiftQ + shiftR) % 3;
		shiftR -= res;

		shift(-shiftQ, -shiftR);

	}

	void point(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy) {
		coord = getGridCoordinates(q, r, ox, oy, sx, sy);
		point(pg, coord[0], coord[1]);
	
	}

	void point(PApplet pg, double q, double r, double ox, double oy, double sx, double sy) {
		point(pg.g, q, r, ox, oy, sx, sy);
	}

	void circle(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy, double diameter) {
		coord = getGridCoordinates(q, r, ox, oy, sx, sy);
		circle(pg, coord[0], coord[1], diameter);
	
	}

	void circle(PApplet pg, double q, double r, double ox, double oy, double sx, double sy, double diameter) {
		circle(pg.g, q, r, ox, oy, sx, sy, diameter);
	
	}

	public void rotateCCW(int k) {
		int tmpq, tmpr;
		for (WB_HexGridCell cell : cells.values()) {
			for (int i = 0; i < k; i++) {
				tmpq = cell.q;
				tmpr = cell.r;
				cell.q = tmpq-tmpr;
				cell.r = tmpq;
			}
			cell.rotateCCW(k);

		}

	}
	
	void line(PGraphics pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx, double sy) {
		coord = getGridCoordinates(q1, r1, ox, oy, sx, sy);
		coord2 = getGridCoordinates(q2, r2, ox, oy, sx, sy);
		line(pg, coord[0], coord[1], coord2[0], coord2[1]);
	
	}

	void line(PApplet home, double q1, double r1, double q2, double r2, double ox, double oy, double sx, double sy) {
		
		coord = getGridCoordinates(q1, r1, ox, oy, sx, sy);
		coord2 = getGridCoordinates(q2, r2, ox, oy, sx, sy);
		line(home, coord[0], coord[1], coord2[0], coord2[1]);
	
	}

	void clippedLine(PApplet pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx, double sy,
			double xmin, double ymin, double xmax, double ymax) {
		clippedLine(pg.g, q1, r1, q2, r2, ox, oy, sx, sy, xmin, ymin, xmax, ymax);
	
	}

	void clippedLine(PGraphics pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx,
			double sy, double xmin, double ymin, double xmax, double ymax) {
	
		coord = getGridCoordinates(q1, r1, ox, oy, sx, sy);
		coord2 = getGridCoordinates(q2, r2, ox, oy, sx, sy);
		if(USEMAP) {
			map.map(coord[0], coord[1],coord);
			
			map.map(coord2[0], coord2[1],coord2);
		}
	
		double x0 = coord[0];
		double y0 = coord[1];
		double x1 = coord2[0];
		double y1 = coord2[1];
	
		int outcode0 = computeOutCode(x0, y0, xmin, ymin, xmax, ymax);
		int outcode1 = computeOutCode(x1, y1, xmin, ymin, xmax, ymax);
		boolean accept = false;
	
		while (true) {
			if ((outcode0 | outcode1) == 0) {
				// bitwise OR is 0: both points inside window; trivially accept and exit loop
				accept = true;
				break;
			} else if ((outcode0 & outcode1) > 0) {
				// bitwise AND is not 0: both points share an outside zone (LEFT, RIGHT, TOP,
				// or BOTTOM), so both must be outside window; exit loop (accept is false)
				break;
			} else {
				// failed both tests, so calculate the line segment to clip
				// from an outside point to an intersection with clip edge
				double x, y;
				x = 0.0;
				y = 0.0;
	
				// At least one endpoint is outside the clip rectangle; pick it.
				int outcodeOut = outcode1 > outcode0 ? outcode1 : outcode0;
	
				// Now find the intersection point;
				// use formulas:
				// slope = (y1 - y0) / (x1 - x0)
				// x = x0 + (1 / slope) * (ym - y0), where ym is ymin or ymax
				// y = y0 + slope * (xm - x0), where xm is xmin or xmax
				// No need to worry about divide-by-zero because, in each case, the
				// outcode bit being tested guarantees the denominator is non-zero
				if ((outcodeOut & TOP) > 0) { // point is above the clip window
					x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0);
					y = ymax;
				} else if ((outcodeOut & BOTTOM) > 0) { // point is below the clip window
					x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0);
					y = ymin;
				} else if ((outcodeOut & RIGHT) > 0) { // point is to the right of clip window
					y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0);
					x = xmax;
				} else if ((outcodeOut & LEFT) > 0) { // point is to the left of clip window
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
		if (accept) {
			pg.line((float) x0, (float) y0, (float) x1, (float) y1);
	
		}
	
	}
	
	int INSIDE = 0; // 0000
	int LEFT = 1; // 0001
	int RIGHT = 2; // 0010
	int BOTTOM = 4; // 0100
	int TOP = 8; // 1000

	// Compute the bit code for a point (x, y) using the clip
	// bounded diagonally by (xmin, ymin), and (xmax, ymax)
	// ASSUME THAT xmax, xmin, ymax and ymin are global constants.
	int computeOutCode(double x, double y, double xmin, double ymin, double xmax, double ymax) {
		int code;
		code = INSIDE; // initialised as being inside of [[clip window]]
		if (x < xmin) // to the left of clip window
			code |= LEFT;
		else if (x > xmax) // to the right of clip window
			code |= RIGHT;
		if (y < ymin) // below the clip window
			code |= BOTTOM;
		else if (y > ymax) // above the clip window
			code |= TOP;
		return code;
	}


	public void rotateCW(int k) {
		int tmpq, tmpr;
		for (WB_HexGridCell cell : cells.values()) {
			for (int i = 0; i < k; i++) {
				tmpq = cell.q;
				tmpr = cell.r;
				
				cell.q = tmpr;
				cell.r = tmpr - tmpq;
			}
			cell.rotateCW(k);

		}

	}

	abstract public double[] getGridCoordinates(double q, double r, double ox, double oy, double sx, double sy) ;

	abstract void getHexCoordinates(int i,double ox, double oy, double sx, double sy, double[] into) ;

	abstract void getTriangleCoordinates(int t,int i,double ox, double oy, double sx, double sy, double[] into) ;

	abstract int[] getTriangleAtGridCoordinates(double x, double y, double ox, double oy, double sx, double sy);

	private void triVertices(PGraphics pg, int t, double[] center, double sx, double sy) {
		triVertex(pg, t, 0, center[0], center[1], sx, sy);
		triVertex(pg, t, 1, center[0], center[1], sx, sy);
		triVertex(pg, t, 2, center[0], center[1], sx, sy);

	}

	private void triVertices(PGraphics pg, int t, double[] center, double sx, double sy, double[] UV) {
		triVertex(pg, t, 0, center[0], center[1], sx, sy, UV[0], UV[1]);
		triVertex(pg, t, 1, center[0], center[1], sx, sy, UV[2], UV[3]);
		triVertex(pg, t, 2, center[0], center[1], sx, sy, UV[4], UV[5]);

	}
	
	private void triVertices(PGraphics pg, int t, double[] center, double sx, double sy, double[] UV, double du, double dv) {
		triVertex(pg, t, 0, center[0], center[1], sx, sy, UV[0]+du, UV[1]+dv);
		triVertex(pg, t, 1, center[0], center[1], sx, sy, UV[2]+du, UV[3]+dv);
		triVertex(pg, t, 2, center[0], center[1], sx, sy, UV[4]+du, UV[5]+dv);

	}

	void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy) {
		getTriangleCoordinates(t, i, ox, oy, sx, sy, coord);
		vertex(pg, coord[0], coord[1]);
	
	}

	void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy, double u, double v) {
		getTriangleCoordinates(t, i, ox, oy, sx, sy, coord);
		vertex(pg, coord[0], coord[1], u, v);
	
	}

	void hexVertex(PGraphics pg, int i, double ox, double oy, double sx, double sy) {
		getHexCoordinates(i, ox, oy, sx, sy, coord);
		vertex(pg, coord[0], coord[1]);
	
	}

	final void vertex(PGraphics pg, final double px, double py) {
		if (!USEMAP) {
			pg.vertex((float) px, (float) py);
		} else {
			map.map(px, py, coord);
			pg.vertex((float) coord[0], (float) coord[1]);
		}
	}

	final void vertex(PGraphics pg, final double px, double py, double u, double v) {
		if (!USEMAP) {
			pg.vertex((float) px, (float) py, (float) u, (float) v);
		} else {
			map.map(px, py, coord);
			pg.vertex((float) coord[0], (float) coord[1], (float) u, (float) v);
		}
	
	}

	void point(PGraphics pg, double x, double y) {
		if (!USEMAP) {
			pg.point((float) x, (float) y);
		} else {
			map.map(x, y, coord);
			pg.point((float) coord[0], (float) coord[1]);
		}
		
	
	}


	void circle(PGraphics pg, double x, double y, double diameter) {
		if (!USEMAP) {
			pg.ellipse((float) x, (float) y, (float) diameter, (float) diameter);
			}else {
				map.map(x, y, coord);

				pg.ellipse((float) coord[0], (float) coord[1], (float) diameter, (float) diameter);
			}
		
		
		
	}
	
	void line(PApplet home, double x1, double y1, double x2, double y2) {
		if (!USEMAP) {
			home.line((float) x1, (float) y1, (float) x2, (float) y2);
		}else {
			
			map.map(x1, y1, coord);
			map.map(x2, y2, coord2);
			
			home.line((float) coord[0], (float) coord[1],(float) coord2[0], (float) coord2[1]);
		}
	}


	void line(PGraphics pg, double x1, double y1, double x2, double y2) {
		if (!USEMAP) {
		pg.line((float) x1, (float) y1, (float) x2, (float) y2);
		}else {
			
			map.map(x1, y1, coord);
			map.map(x2, y2, coord2);
			
			pg.line((float) coord[0], (float) coord[1],(float) coord2[0], (float) coord2[1]);
		}
	}

	final public void drawClippedGridLines(PApplet home, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		drawClippedGridLines(home.g, ox, oy, sx, sy, xmin, ymin, xmax, ymax);
	}

	final public void drawClippedGridLines(PGraphics pg, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		for (WB_IsoGridLine line : gridlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				clippedLine(pg, segment.getQ1()/6.0, segment.getR1()/6.0, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy,
						xmin, ymin, xmax, ymax);
			}
		}
	}

	final public void drawClippedLines(PApplet home, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		drawClippedLines(home.g, ox, oy, sx, sy, xmin, ymin, xmax, ymax);
	}

	final public void drawClippedLines(PGraphics pg, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		for (WB_IsoGridLine line : lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				clippedLine(pg, segment.getQ1()/6.0, segment.getR1()/6.0, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy,
						xmin, ymin, xmax, ymax);
			}
		}
	}

	final public void drawClippedOutlines(PApplet home, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		drawClippedOutlines(home.g, ox, oy, sx, sy, xmin, ymin, xmax, ymax);
	}

	final public void drawClippedOutlines(PGraphics pg, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		for (WB_IsoGridLine line : outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				clippedLine(pg, segment.getQ1()/6.0, segment.getR1()/6.0, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy,
						xmin, ymin, xmax, ymax);
			}
		}
	}

	final public void drawGridLinePoints(PApplet home, double ox, double oy, double sx, double sy, double diameter) {
		drawGridLinePoints(home.g, ox, oy, sx, sy, diameter);
	}

	final public void drawGridLinePoints(PGraphics pg, double ox, double oy, double sx, double sy, double diameter) {
		for (WB_IsoGridLine line : gridlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				circle(pg, segment.getQ1()/6.0, segment.getR1()/6.0, ox, oy, sx, sy, diameter);
				circle(pg, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy, diameter);
			}
		}
	}

	final public void drawGridLines(PApplet home, double ox, double oy, double sx, double sy) {
	
		for (WB_IsoGridLine line : gridlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(home, segment.getQ1()/6.0, segment.getR1()/6.0, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy);
			}
		}
	}

	final public void drawGridLines(PGraphics pg, double ox, double oy, double sx, double sy) {
		for (WB_IsoGridLine line : gridlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(pg, segment.getQ1()/6.0, segment.getR1()/6.0, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy);
			}
		}
	}

	final public void drawHex(PApplet home, int q, int r, double ox, double oy, double sx, double sy) {
		drawHex(home.g, q, r, ox, oy, sx, sy);
	}

	final public void drawHex(PGraphics pg, int q, int r, double ox, double oy, double sx, double sy) {
		double[] center = getGridCoordinates(q, r, ox, oy, sx, sy);
		pg.beginShape();
		for (int i = 0; i < 6; i++) {
			hexVertex(pg, i, center[0], center[1], sx, sy);
		}
		pg.endShape(PConstants.CLOSE);
	}

	final public void drawHexGrid(PApplet home, double ox, double oy, double sx, double sy) {
		drawHexGrid(home.g, ox, oy, sx, sy);
	}

	final public void drawHexGrid(PGraphics pg, double ox, double oy, double sx, double sy) {
		for (WB_HexGridCell cell : cells.values()) {
			drawHex(pg, cell.getQ(), cell.getR(), ox, oy, sx, sy);
		}
	}

	final public void drawLinePoints(PApplet home, double ox, double oy, double sx, double sy, double diameter) {
		drawLinePoints(home.g, ox, oy, sx, sy, diameter);
	}

	final public void drawLinePoints(PGraphics pg, double ox, double oy, double sx, double sy, double diameter) {
		for (WB_IsoGridLine line : lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				circle(pg, segment.getQ1()/6.0, segment.getR1()/6.0, ox, oy, sx, sy, diameter);
				circle(pg, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy, diameter);
			}
		}
	}

	final public void drawLines(PApplet home, double ox, double oy, double sx, double sy) {
		for (WB_IsoGridLine line : lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(home, segment.getQ1()/6.0, segment.getR1()/6.0, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy);
			}
		}
	}

	final public void drawLines(PGraphics pg, double ox, double oy, double sx, double sy) {
		for (WB_IsoGridLine line : lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(pg, segment.getQ1()/6.0, segment.getR1()/6.0, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy);
			}
		}
	}

	final public void drawOutlinePoints(PApplet home, double ox, double oy, double sx, double sy, double diameter) {
		drawOutlinePoints(home.g, ox, oy, sx, sy, diameter);
	}

	final public void drawOutlinePoints(PGraphics pg, double ox, double oy, double sx, double sy, double diameter) {
		for (WB_IsoGridLine line : outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				circle(pg, segment.getQ1()/6.0, segment.getR1()/6.0, ox, oy, sx, sy, diameter);
				circle(pg, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy, diameter);
			}
		}
	}

	final public void drawOutlines(PApplet home, double ox, double oy, double sx, double sy) {
		for (WB_IsoGridLine line : outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(home, segment.getQ1()/6.0, segment.getR1()/6.0, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy);
			}
		}
	}

	final public void drawOutlines(PGraphics pg, double ox, double oy, double sx, double sy) {
		for (WB_IsoGridLine line : outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(pg, segment.getQ1()/6.0, segment.getR1()/6.0, segment.getQ2()/6.0, segment.getR2()/6.0, ox, oy, sx, sy);
			}
		}
	}

	final public void drawTriangle(PGraphics pg, int q, int r, int t, double ox, double oy, double sx, double sy) {
		double[] center = getGridCoordinates(q, r, ox, oy, sx, sy);
		pg.beginShape();
		triVertices(pg, t, center, sx, sy);
		pg.endShape(PConstants.CLOSE);
	}

	public void drawTriangles(PApplet home, double ox, double oy, double sx, double sy) {
		drawTriangles(home.g, ox, oy, sx, sy);
	}

	public void drawTriangles(PApplet home, double ox, double oy, double sx, double sy, PImage[] textures) {
		drawTriangles(home.g, ox, oy, sx, sy, textures);
	}

	public void drawTriangles(PApplet home, double ox, double oy, double sx, double sy, PImage[] textures, double du, double dv) {
		drawTriangles(home.g, ox, oy, sx, sy, textures,du, dv);
	}

	public void drawTriangles(PApplet home, int palette, double ox, double oy, double sx, double sy) {
		drawTriangles(home.g, palette, ox, oy, sx, sy);
	}

	public void drawTriangles(PGraphics pg, double ox, double oy, double sx, double sy) {
		pg.pushStyle();
		pg.noStroke();
		for (WB_HexGridCell cell : cells.values()) {
			for (int t = 0; t < cell.getNumberOfTriangles(); t++) {
				if (cell.isOccupied(t)) {
					pg.fill(cell.getColor(t));
					drawTriangle(pg, cell.getQ(), cell.getR(), t, ox, oy, sx, sy);
				}
			}
		}
		pg.popStyle();
	
	}

	public void drawTriangles(PGraphics pg, double ox, double oy, double sx, double sy, PImage[] textures) {
		pg.pushStyle();
		pg.noStroke();
		for (WB_HexGridCell cell : cells.values()) {
			for (int t = 0; t < cell.getNumberOfTriangles(); t++) {
				if (cell.isOccupied(t)) {
					pg.beginShape();
					pg.texture(textures[(cell.region[t] % 3 + 3 * cell.triangleColorSourceIndex[t] + textures.length)
							% textures.length]);
					pg.tint(cell.getColor(t));
					triVertices(pg, t, getGridCoordinates(cell.getQ(), cell.getR(), ox, oy, sx, sy), sx, sy,
							cell.getTriangleUV(t));
					pg.endShape(PConstants.CLOSE);
				}
			}
		}
		pg.popStyle();
	
	}

	public void drawTriangles(PGraphics pg, double ox, double oy, double sx, double sy, PImage[] textures, double du, double dv) {
		pg.pushStyle();
		pg.noStroke();
		for (WB_HexGridCell cell : cells.values()) {
			for (int t = 0; t < cell.getNumberOfTriangles(); t++) {
				if (cell.isOccupied(t)) {
					pg.beginShape();
					pg.texture(textures[(cell.region[t] % 3 + 3 * cell.triangleColorSourceIndex[t] + textures.length)
							% textures.length]);
					pg.tint(cell.getColor(t));
					triVertices(pg, t, getGridCoordinates(cell.getQ(), cell.getR(), ox, oy, sx, sy), sx, sy,
							cell.getTriangleUV(t),du,dv);
					pg.endShape(PConstants.CLOSE);
				}
			}
		}
		pg.popStyle();
	
	}

	public void drawTriangles(PGraphics pg, int palette, double ox, double oy, double sx, double sy) {
		pg.pushStyle();
		pg.noStroke();
		for (WB_HexGridCell cell : cells.values()) {
			for (int t = 0; t < cell.getNumberOfTriangles(); t++) {
				if (cell.isOccupied(t) && cell.getColorSourceIndex(t) == palette) {
					pg.fill(cell.getColor(t));
					drawTriangle(pg, cell.getQ(), cell.getR(), t, ox, oy, sx, sy);
				}
			}
		}
		pg.popStyle();
	
	}



}
