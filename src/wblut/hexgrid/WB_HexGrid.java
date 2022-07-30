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

public abstract class WB_HexGrid {

	double[] offsets;
	Map<Long, WB_HexGridCell> cells;
	int numTriangles;

	Map<Long, WB_IsoGridLine> linesMap;
	Map<Long, WB_IsoGridLine> outlinesMap;
	Map<Long, WB_IsoGridLine> gridlinesMap;
	List<WB_IsoGridLine> lines;
	List<WB_IsoGridLine> outlines;
	List<WB_IsoGridLine> gridlines;

	WB_HexGrid() {
		cells = new HashMap<Long, WB_HexGridCell>();
		linesMap = new HashMap<Long, WB_IsoGridLine>();
		lines = new ArrayList<WB_IsoGridLine>();
		outlinesMap = new HashMap<Long, WB_IsoGridLine>();
		outlines = new ArrayList<WB_IsoGridLine>();
		gridlinesMap = new HashMap<Long, WB_IsoGridLine>();
		gridlines = new ArrayList<WB_IsoGridLine>();
	}

	final public void clear() {
		cells.clear();
		linesMap.clear();
		outlinesMap.clear();
		gridlinesMap.clear();
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

	final void hexVertex(PGraphics pg, int i, double ox, double oy, double sx, double sy) {
		vertex(pg, ox + offsets[2 * i] * sx, oy + offsets[2 * i + 1] * sy);

	}

	final void vertex(PGraphics pg, final double px, double py) {
		pg.vertex((float) px, (float) py);
	}

	final void vertex(PGraphics pg, final double px, double py, double u, double v) {
		pg.vertex((float) px, (float) py, (float) u, (float) v);
	}

	abstract public void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy);

	abstract public void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy, double u,
			double v);

	abstract public void line(PGraphics pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx,
			double sy);

	abstract public void point(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy);

	abstract public void point(PApplet pg, double q, double r, double ox, double oy, double sx, double sy);

	abstract public void circle(PApplet pg, double q, double r, double ox, double oy, double sx, double sy,
			double diameter);

	abstract public void circle(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy,
			double diameter);

	abstract public void line(PApplet pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx,
			double sy);

	abstract public void clippedLine(PGraphics pg, double q1, double r1, double q2, double r2, double ox, double oy,
			double sx, double sy, double xmin, double ymin, double xmax, double ymax);

	abstract public void clippedLine(PApplet pg, double q1, double r1, double q2, double r2, double ox, double oy,
			double sx, double sy, double xmin, double ymin, double xmax, double ymax);

	abstract public double[] getGridCoordinates(double q, double r, double ox, double oy, double sx, double sy);

	abstract public int[] getTriangleAtGridCoordinates(double x, double y, double ox, double oy, double sx, double sy);

	private void triVertices(PGraphics pg, int t, double[] center, double sx, double sy) {
		triVertex(pg, t, 0, center[0], center[1], sx, sy);
		triVertex(pg, t, 1, center[0], center[1], sx, sy);
		triVertex(pg, t, 2, center[0], center[1], sx, sy);

	}

	final public void drawTriangle(PGraphics pg, int q, int r, int t, double ox, double oy, double sx, double sy) {
		double[] center = getGridCoordinates(q, r, ox, oy, sx, sy);
		pg.beginShape();
		triVertices(pg, t, center, sx, sy);
		pg.endShape(PConstants.CLOSE);
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

	public void drawTriangles(PApplet home, double ox, double oy, double sx, double sy) {
		drawTriangles(home.g, ox, oy, sx, sy);
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

	public void drawTriangles(PApplet home, int palette, double ox, double oy, double sx, double sy) {
		drawTriangles(home.g, palette, ox, oy, sx, sy);
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


	public void drawTriangles(PApplet home, double ox, double oy, double sx, double sy, PImage[] textures) {
		drawTriangles(home.g, ox, oy, sx, sy, textures);
	}
	
	public void drawTriangles(PApplet home, double ox, double oy, double sx, double sy, PImage[] textures, double du, double dv) {
		drawTriangles(home.g, ox, oy, sx, sy, textures,du, dv);
	}

	final public void drawLines(PGraphics home, double ox, double oy, double sx, double sy) {
		for (WB_IsoGridLine line : lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(home, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), ox, oy, sx, sy);
			}
		}
	}

	final public void drawLines(PApplet home, double ox, double oy, double sx, double sy) {
		for (WB_IsoGridLine line : lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(home, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), ox, oy, sx, sy);
			}
		}
	}

	final public void drawLinePoints(PGraphics pg, double ox, double oy, double sx, double sy, double diameter) {
		for (WB_IsoGridLine line : lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				circle(pg, segment.getQ1(), segment.getR1(), ox, oy, sx, sy, diameter);
				circle(pg, segment.getQ2(), segment.getR2(), ox, oy, sx, sy, diameter);
			}
		}
	}

	final public void drawLinePoints(PApplet home, double ox, double oy, double sx, double sy, double diameter) {
		drawLinePoints(home.g, ox, oy, sx, sy, diameter);
	}

	final public void drawClippedLines(PGraphics pg, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		for (WB_IsoGridLine line : lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				clippedLine(pg, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), ox, oy, sx, sy,
						xmin, ymin, xmax, ymax);
			}
		}
	}

	final public void drawClippedLines(PApplet home, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		drawClippedLines(home.g, ox, oy, sx, sy, xmin, ymin, xmax, ymax);
	}

	final public void drawOutlines(PGraphics home, double ox, double oy, double sx, double sy) {
		for (WB_IsoGridLine line : outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(home, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), ox, oy, sx, sy);
			}
		}
	}

	final public void drawOutlines(PApplet home, double ox, double oy, double sx, double sy) {
		for (WB_IsoGridLine line : outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(home, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), ox, oy, sx, sy);
			}
		}
	}

	final public void drawOutlinePoints(PGraphics pg, double ox, double oy, double sx, double sy, double diameter) {
		for (WB_IsoGridLine line : outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				circle(pg, segment.getQ1(), segment.getR1(), ox, oy, sx, sy, diameter);
				circle(pg, segment.getQ2(), segment.getR2(), ox, oy, sx, sy, diameter);
			}
		}
	}

	final public void drawOutlinePoints(PApplet home, double ox, double oy, double sx, double sy, double diameter) {
		drawOutlinePoints(home.g, ox, oy, sx, sy, diameter);
	}

	final public void drawClippedOutlines(PGraphics pg, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		for (WB_IsoGridLine line : outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				clippedLine(pg, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), ox, oy, sx, sy,
						xmin, ymin, xmax, ymax);
			}
		}
	}

	final public void drawClippedOutlines(PApplet home, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		drawClippedOutlines(home.g, ox, oy, sx, sy, xmin, ymin, xmax, ymax);
	}

	final public void drawGridLines(PGraphics home, double ox, double oy, double sx, double sy) {
		for (WB_IsoGridLine line : gridlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(home, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), ox, oy, sx, sy);
			}
		}
	}

	final public void drawGridLines(PApplet home, double ox, double oy, double sx, double sy) {

		for (WB_IsoGridLine line : gridlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(home, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), ox, oy, sx, sy);
			}
		}
	}

	final public void drawGridLinePoints(PGraphics pg, double ox, double oy, double sx, double sy, double diameter) {
		for (WB_IsoGridLine line : gridlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				circle(pg, segment.getQ1(), segment.getR1(), ox, oy, sx, sy, diameter);
				circle(pg, segment.getQ2(), segment.getR2(), ox, oy, sx, sy, diameter);
			}
		}
	}

	final public void drawGridLinePoints(PApplet home, double ox, double oy, double sx, double sy, double diameter) {
		drawGridLinePoints(home.g, ox, oy, sx, sy, diameter);
	}

	final public void drawClippedGridLines(PGraphics pg, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		for (WB_IsoGridLine line : gridlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				clippedLine(pg, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), ox, oy, sx, sy,
						xmin, ymin, xmax, ymax);
			}
		}
	}

	final public void drawClippedGridLines(PApplet home, double ox, double oy, double sx, double sy, double xmin,
			double ymin, double xmax, double ymax) {
		drawClippedGridLines(home.g, ox, oy, sx, sy, xmin, ymin, xmax, ymax);
	}

	final public void drawHex(PGraphics pg, int q, int r, double ox, double oy, double sx, double sy) {
		double[] center = getGridCoordinates(q, r, ox, oy, sx, sy);
		pg.beginShape();
		for (int i = 0; i < 6; i++) {
			hexVertex(pg, i, center[0], center[1], sx, sy);
		}
		pg.endShape(PConstants.CLOSE);
	}

	final public void drawHex(PApplet home, int q, int r, double ox, double oy, double sx, double sy) {
		drawHex(home.g, q, r, ox, oy, sx, sy);
	}

	final public void drawHexGrid(PGraphics pg, double ox, double oy, double sx, double sy) {
		for (WB_HexGridCell cell : cells.values()) {
			drawHex(pg, cell.getQ(), cell.getR(), ox, oy, sx, sy);
		}
	}

	final public void drawHexGrid(PApplet home, double ox, double oy, double sx, double sy) {
		drawHexGrid(home.g, ox, oy, sx, sy);
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

}
