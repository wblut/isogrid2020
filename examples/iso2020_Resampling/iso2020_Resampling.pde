import wblut.isogrid.*;
WB_IsoSystem6 iso;
WB_IsoColor isoColor;
void setup() {
  size(800, 800, P3D);
  smooth(8);
  //text, pixel font height, pixel thickness ("Z direction"), pixel width of space (in addition to 1 pixel spacing between characters),  alternative version of font, flip X, flip Y
  boolean[][][] pattern=WB_PixelFont.getText3xN("test", 7, 2, 1, false, false, true);
  println("Pattern size: ("+pattern.length+", "+pattern[0].length+", "+pattern[0][0].length+")");
  
  int patternI=pattern.length;
  int patternJ=pattern[0].length;
  int patternK=pattern[0][0].length;
  iso=new WB_IsoSystem6(3.6, 4*patternI, 8*patternJ+2, 4*patternK, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), false, this);
  //pattern as boolean[][][], lower corner coordinate i, j ,k, I scale, J scale, K scale, flip I, flip J, flip K
  iso.setPattern(pattern, 0, 0, 0, 4,4,4, false, false, false);
 
 pattern=WB_PixelFont.getText3xN("test", 7, 2, 1, true, false, true);
 iso.setPattern(pattern, 0, 4*patternJ+2, 0, 4,4,4, false, false, false);
 
 //resample isosystem, >0=upsample by factor, <0=downsample by factor (on-voxels dominate when downscaled), I scale, J scale, K scale (0 is not allowed, 1 and -1 do nothing)
 iso=new WB_IsoSystem6(iso,2,-2,4);
 
 colorMode(HSB);
  isoColor=new WB_IsoColor(new HueByRegion(), 255, 255);
}

void draw() {
  background(25);
  iso.centerGrid();
  stroke(0);
  strokeWeight(2);
  iso.drawOutlines();
  strokeWeight(1);
  iso.drawLines();
  noStroke();
  iso.drawTriangles(isoColor);
}

class HueByRegion implements WB_ColorChannel {
  float value(WB_IsoGridCell cell, int triangle) {
    return (cell.getRegion(triangle)*17)%256;
  }
}
