import wblut.isogrid.*;
WB_IsoSystem6 iso;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed,  PApplet
  iso=new WB_IsoSystem6(4, 64, 64, 64, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);


  iso.invertAll();
  
  //step j, step k, size j, size k
  iso.barIAll(8,16,2,2);
  //step i, step k, size i, size k
  iso.barJAll(16,16,4,1);
  //step i, step j, size i, size j
  iso.barKAll(32,8,2,8);
  
  
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
