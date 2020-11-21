import wblut.isogrid.*;
WB_IsoSystem6 iso;
WB_IsoColor isoColor;
void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  iso=new WB_IsoSystem6(8,32, 32,32, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);
  iso.subdivide(0.50, 8,16, 8);
  iso.sliceJAll(4, 1);
  iso.sliceKBlocks(0.5, 5, 2, 8, 8,8);
  iso.wallBlocks(0.5, 4, 4, 4);
  iso.openKAll();  
  iso.openJAll();
  iso.openKAll();
  colorMode(HSB);
  //Brightness of color determined by triangle type, similar to orientation, but there are two (or 12 for a WB_IsoSystem36) triangles per side each with their own id
  isoColor=new WB_IsoColor(new HueByTriangle(), 255, 255);
}

void draw() {
  background(255);
  stroke(0);
  strokeWeight(2);
  iso.drawOutlines();
  strokeWeight(1);
  iso.drawLines();
  noStroke();
  iso.drawTriangles(isoColor);
}

class HueByTriangle implements WB_ColorChannel {
   float value(WB_IsoGridCell cell, int triangle){
     return (cell.getTriangle(triangle)*42)%256;
    } 
}
