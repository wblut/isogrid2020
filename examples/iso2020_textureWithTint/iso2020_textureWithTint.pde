import wblut.isogrid.*;
WB_IsoSystem6 iso;
WB_IsoColor isoColor;
PImage[] textures;
void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  iso=new WB_IsoSystem6(4, 64, 64, 64, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);
  PImage testTexture=loadImage("test.png");
  textures=new PImage[]{testTexture,testTexture,testTexture};
  iso.invertAll();
  //chance, off, on, block I, block J, block K
  iso.sliceIAll(16,2);
  iso.layerJBlocks(0.5,8,2,8,8,16);
  iso.layerKBlocks(0.25,16,4,16,32,8);
  iso.sliceJAll(8,1);
  isoColor=new WB_IsoColor(new HueByRegion(),255, new BrightnessByDepth(), new AlphaNoise());
  colorMode(HSB);
  textureMode(NORMAL);
  textureWrap(REPEAT);
}

void draw() {
  background(255);
  stroke(0);
  strokeWeight(2);
  iso.drawOutlines();
  strokeWeight(1);
  iso.drawLines();
  noStroke();
  iso.drawTriangles(textures,isoColor);
}


class HueByRegion implements WB_ColorChannel{
  float value(WB_IsoGridCell cell, int triangle){
   return (cell.getRegion(triangle)*17)%256;
  }  
}

class BrightnessByDepth implements WB_ColorChannel{
  float value(WB_IsoGridCell cell, int triangle){
   return cell.getZ(triangle)*255./(3*63);
  }  
}

class AlphaNoise implements WB_ColorChannel{
  float value(WB_IsoGridCell cell, int triangle){
   return 80+200.0*noise(cell.getI(triangle)/32.0,cell.getJ(triangle)/32.0,cell.getK(triangle)/32.0);
  }  
}
