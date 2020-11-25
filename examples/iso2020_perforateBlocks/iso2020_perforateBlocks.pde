import wblut.isogrid.*;
WB_IsoSystem6 iso;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed,  PApplet
  iso=new WB_IsoSystem6(4, 103, 64, 57, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);

  //chance, step size J, step size K, hole size J, hole size K, block I, block J, block K
  iso.perforateIBlocks(0.50, 8, 8, 2, 2,16,16,8);
  //chance, step size I, step size K, hole size I, hole size K, block I, block J, block K
  iso.perforateJBlocks(0.50, 8, 8, 2, 2,16,16,8);
  //chance, step size I, step size J, hole size I, hole size J, block I, block J, block K
  iso.perforateKBlocks(0.50, 8, 8, 2, 2,16,16,8);
  iso.wallAll();
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
