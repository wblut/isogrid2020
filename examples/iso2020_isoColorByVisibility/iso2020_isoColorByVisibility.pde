import wblut.isogrid.*;
WB_IsoSystem6 iso;
WB_IsoColor isoColor;
void setup() {
  size(800, 800, P3D);
  smooth(8);

  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  iso=new WB_IsoSystem6(6, 64, 64, 64, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);
  iso.subdivide(0.50, 16, 32, 16);
  iso.sliceJBlocks(0.5, 8, 2, 8, 8, 8);
  iso.sliceKBlocks(0.5, 10, 2, 16, 16, 16);
  iso.wallBlocks(0.5, 24, 24, 24);
  iso.edgeAll();
  iso.openKBlocks(0.5, 8, 8);
  iso.openJBlocks(0.5, 16, 16);

  colorMode(HSB);
  //Color brightness determined by how hidden a side is from the environment
  isoColor=new WB_IsoColor(new HueByRegion(), 255, new BrightnessByVisibility());
  iso.smoothVisibility(0.25, 10);
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
  iso.drawTriangles(isoColor);
}

class HueByRegion implements WB_ColorChannel {
  float value(WB_IsoGridCell cell, int triangle) {
    return (cell.getRegion(triangle)*17)%256;
  }
}

class BrightnessByVisibility implements WB_ColorChannel {
  float value(WB_IsoGridCell cell, int triangle) {
    int orientation=cell.getOrientation(triangle);
    //visibility has 6 direction: negative I (0), positive I (1), negative J (2), positive J (3), negative K (4), positive K (5)
    //normally, orientation 0 corresponds to positive I, orientation 1 is positive J and orientation 2 is positive K

    double visibility=0;
    if (orientation==0) {
      visibility=cell.getVisibility(triangle, 1);
    } else if (orientation==1) {
      visibility=cell.getVisibility(triangle, 3);
    } else if (orientation==2) {
      visibility=cell.getVisibility(triangle, 5);
    }
    return constrain(sq(sq((float)visibility))*256.0, 0, 255);
  }
}

void mousePressed() {
  iso.smoothVisibility(0.25);
}
