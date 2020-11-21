import wblut.isogrid.*;
WB_IsoSystem36 iso;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  iso=new WB_IsoSystem36(64, 16, 16, 16, width/2, height/2, new color[]{color(120,0,120),color(220,220,0),color(0,180,180)}, (int)random(1000000), this);
  iso.invertAll();
  //isoHexGrid36 in dual mode: set = activate vertices instead of voxels
  iso.setDual(true);
  
  iso.set(0, 0, 0);
  iso.set(1, 0, 0);
  iso.set(0, 1, 0);
  iso.set(1, 1, 0);
  iso.set(0, 0, 1);
  iso.set(1, 0, 1);
  iso.set(0, 1, 1);
  iso.set(1, 1, 1);
  iso.set(0, 0, 2);
  iso.set(1, 0, 2);
  iso.set(0, 1, 2);
  iso.set(1, 1, 2);
  iso.set(2, 0, 0);
  iso.set(2, 1, 0);
  iso.set(2, 0, 1);
  iso.set(2, 1, 1);
  iso.set(0, 2, 0);
  iso.set(1, 2, 0);
  iso.set(0, 2, 1);
  iso.set(1, 2, 1);
}

void draw() {
  background(255);
  stroke(0);
  strokeWeight(1);
  iso.drawLines();
  strokeWeight(4);
  iso.drawOutlines();
  noStroke();
  iso.drawTriangles();
}
