import processing.svg.*;
import wblut.isogrid.*;

WB_IsoSystem6 iso;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  iso=new WB_IsoSystem6(4, 64, 64, 64, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);
  iso.subdivide(0.50, 16, 32, 16);
  iso.subdivide(0.30, 8, 16, 8);
  iso.subdivide(0.15, 4, 8, 4);
  iso.sliceJAll(8, 4);
  iso.wallAll();

  iso.openIBlocks(0.5, 16, 16);
  iso.openJBlocks(0.15, 8, 8);
}

void draw() {
  background(255);
  stroke(0);

  beginRecord(SVG, sketchPath("/SVG/lines.svg")); 
  background(255);
  iso.drawLinesSVG();
  endRecord();

  beginRecord(SVG, sketchPath("/SVG/outlines.svg")); 
  background(255);
  iso.drawOutlinesSVG();
  endRecord(); 

  beginRecord(SVG, sketchPath("/SVG/combined.svg")); 
  background(255);
  iso.drawLinesSVG();
  iso.drawOutlinesSVG();
  endRecord();

  strokeWeight(2);
  iso.drawOutlines();
  strokeWeight(1);
  iso.drawLines();
  noLoop();
}
