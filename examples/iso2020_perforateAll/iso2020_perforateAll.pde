import wblut.isogrid.*;
WB_IsoSystem6 iso;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed,  PApplet
  iso=new WB_IsoSystem6(4, 64, 64, 64, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);

  //step size J, step size K, hole size J, hole size K
  iso.perforateIAll( 8, 16, 1, 4);
  //step size I, step size K, hole size I, hole size K
  iso.perforateJAll( 32, 32, 4, 4);
  //step size I, step size J, hole size I, hole size J
  iso.perforateKAll( 16, 16, 6, 1);
}

void draw() {
  background(255);
  stroke(0);
  strokeWeight(2);
  iso.drawOutlines();
  strokeWeight(1);
  iso.drawLines();
  noStroke();
  //iso.drawTriangles();
}
