import wblut.isogrid.*;
WB_IsoSystem36 iso;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  iso=new WB_IsoSystem36(64,16,16,16, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);

}

void draw() {
  background(255);
  stroke(0);
  strokeWeight(1);
  //radius
  iso.drawTriangleGrid(580);
  strokeWeight(4);
  //radius, type (0,1,2)
  iso.drawHexGrid(580,0);
}
