package wblut.isogrid.color;

import processing.core.PGraphics;
import wblut.isogrid.WB_IsoGridCell;

public interface WB_IsoColor {
	public int getColor(PGraphics pg, WB_IsoGridCell cell, int triangle);

}
