import wblut.isogrid.*;
WB_IsoSystem6 iso;
WB_IsoColor isoColor;
void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  iso=new WB_IsoSystem6(10, 32, 32,32, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);
  iso.subdivide(0.50, 8, 16, 7);
  iso.sliceJAll(4, 1);
  iso.wallBlocks(0.5, 4, 4, 4);
  iso.openKAll();  
  iso.openJAll();
  iso.openKAll();
  colorMode(HSB);
  //Color determined by grid coordinates q and r, not very useful as this is determined per hexagon of the flat grid and has little relation to the 3D stucture
  isoColor=new WB_IsoColor(160,new QGradient(), new RGradient());
  
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



class QGradient implements WB_ColorChannel{
  float value(WB_IsoGridCell cell, int triangle){
   return 255-abs(cell.getQ())*8;
  } 
}

class RGradient implements WB_ColorChannel{
  float value(WB_IsoGridCell cell, int triangle){
   return 255-abs(cell.getR())*8;
  } 
}
