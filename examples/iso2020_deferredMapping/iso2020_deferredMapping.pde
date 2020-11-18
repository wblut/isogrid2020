import processing.svg.*;
import wblut.isogrid.*;

WB_IsoSystem6 iso;
int time;
void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  println("Start without deferral");
  time=millis();

  //mapping the grid at every step
  iso=new WB_IsoSystem6(2, 128, 128,128, width/2, height/2, new int[]{color(50), color(255), color(180)}, 15, false, this);
  iso.fill();
  iso.subdivide(0.50, 16, 32, 16);
  iso.subdivide(0.30, 8, 16, 8);
  iso.subdivide(0.15, 4, 8, 4);
  iso.sliceJAll(8, 4);
  iso.wallAll();
  iso.openIBlocks(0.5, 16, 16);
  iso.openJBlocks(0.15, 8, 8);
  println("Elapsed time: "+(millis()-time)+"ms");

  println("Start with deferral");
  time=millis();

  //mapping the grid at the end
  iso=new WB_IsoSystem6(2, 128,128,128, width/2, height/2, new int[]{color(50), color(255), color(180)}, 15, false, this);
  iso.setDeferred(true);
  iso.fill();
  iso.subdivide(0.50, 16, 32, 16);
  iso.subdivide(0.30, 8, 16, 8);
  iso.subdivide(0.15, 4, 8, 4);
  iso.sliceJAll(8, 4);
  iso.wallAll();
  iso.openIBlocks(0.5, 16, 16);
  iso.openJBlocks(0.15, 8, 8);
  iso.setDeferred(false);
  iso.refresh();
  println("Elapsed time: "+(millis()-time)+"ms");
}

void draw() {
  background(255);
  stroke(0);
  strokeWeight(2);
  iso.drawOutlines();
  strokeWeight(1);
  iso.drawLines();
  noStroke();
  iso.drawTriangles();
}
