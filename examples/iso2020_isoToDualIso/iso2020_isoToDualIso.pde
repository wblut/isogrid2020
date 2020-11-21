import processing.svg.*;
import wblut.isogrid.*;

WB_IsoSystem6 iso;
WB_IsoSystem36 dualIso;
boolean drawDual;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  iso=new WB_IsoSystem6(4, 64, 64, 64, width/2, height/2, new int[]{color(50), color(255), color(180)}, (int)random(1000000), this);
  iso.subdivide(0.50, 16, 32, 16);
  iso.subdivide(0.30, 8, 16, 8);
  iso.subdivide(0.15, 4, 8, 4);
  iso.sliceJAll(8, 4);
  iso.wallAll();
  iso.openIBlocks(0.5, 16, 16);
  iso.openJBlocks(0.15, 8, 8);
  dualIso=new WB_IsoSystem36(iso);
  drawDual=true;
}

void draw() {
  background(255);
  if (drawDual) {
    stroke(0);
    strokeWeight(2);
    dualIso.drawOutlines();
    strokeWeight(1);
    dualIso.drawLines();
    noStroke();
    dualIso.drawTriangles();
  } else {
    stroke(0);
    strokeWeight(2);
    iso.drawOutlines();
    strokeWeight(1);
    iso.drawLines();
    noStroke();
    iso.drawTriangles();
  }
}

void mousePressed(){
 drawDual=!drawDual; 
}
