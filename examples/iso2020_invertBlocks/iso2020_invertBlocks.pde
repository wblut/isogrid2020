import wblut.isogrid.*;
WB_IsoSystem6 iso;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed,  PApplet
  iso=new WB_IsoSystem6(4, 39, 93, 64, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);

  
  iso.perforateJBlocks(0.50, 8, 8, 2, 2,16,16,8);
  iso.sliceJAll(8, 2);
  //chance, I block, J block, K block
  iso.invertBlocks(0.5,16,8,16);
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
  //iso.drawTriangles();
}
