import wblut.isogrid.*;
WB_IsoSystem6 iso;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed,  PApplet
  iso=new WB_IsoSystem6(4, 64, 60, 58, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);
  int pal=iso.createPalette(new int[]{color(0), color(255), color(160)});
  iso.setDeferred(true);
  for (int i=0; i<64; i+=8) {
    for (int j=0; j<60; j+=2) {
      for (int k=0; k<58; k+=16) {
        if (random(100)<15.0) iso.setPalette(i, j, k,8,2,16, pal);
      }
    }
  }
  
  for (int i=0; i<64; i+=2) {
    for (int j=0; j<60; j+=16) {
      for (int k=0; k<58; k+=2) {
        if (random(100)<15.0) iso.setPalette(i, j, k,2,16,2,  new int[]{color(255, 0,0), color(0, 255, 0), color(0, 0, 255)});
      }
    }
  }
  iso.setDeferred(false);
  iso.invertAll();
  iso.layerIBlocks(0.5,16,2,16,16,16);
  iso.layerJBlocks(0.5,8,2,8,8,16);
  iso.layerKBlocks(0.5,16,4,16,32,8);
  iso.refresh();
  

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
