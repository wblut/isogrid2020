import wblut.isogrid.*;
WB_IsoSystem6 iso;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed,  PApplet
  iso=new WB_IsoSystem6(4, 59,103, 57, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);
  
  //chance to slice block, layers on, layers off, block I, block J, block K
  iso.sliceIBlocks(0.75, 8, 2, 16, 32, 4);
  iso.sliceJBlocks(0.75, 8, 2, 16, 32, 4);
  iso.sliceKBlocks(0.75, 8, 2, 16, 32, 4);
}

void draw() {
  background(255);
  iso.centerGrid();
  stroke(0);
  strokeWeight(2);
  iso.drawOutlines();
  strokeWeight(1);
  iso.drawLines();
  noStroke();
  iso.drawTriangles();
}
