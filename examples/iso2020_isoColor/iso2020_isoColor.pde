import wblut.isogrid.*;
WB_IsoSystem6 iso;
WB_IsoColor isoColor;
void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  iso=new WB_IsoSystem6(4, 64, 64, 64, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);
  iso.subdivide(0.50, 16, 32, 16);
  iso.sliceJAll(8, 4);
  iso.wallBlocks(0.5, 8, 8, 8);
  iso.openKAll();  
  iso.openJAll();
  iso.openKAll();
 colorMode(HSB);
  isoColor=new WB_IsoColor(new Hue(),new Saturation(), new Brightness());
  
}

void draw() {
  background(255);
  stroke(0);
  strokeWeight(2);
  iso.drawOutlines();
  strokeWeight(1);
  iso.drawLines();
  noStroke();
  iso.drawTriangles(isoColor);
}



class Hue implements WB_ColorChannel{
  float value(WB_IsoGridCell cell, int triangle){
   return 256.0*noise(cell.getI(triangle)/32.0,cell.getJ(triangle)/32.0,cell.getK(triangle)/32.0);
  }  
}

class Saturation implements WB_ColorChannel{
  float value(WB_IsoGridCell cell, int triangle){
   return (cell.getPart(triangle)*55)%256;
  } 
}



class Brightness implements WB_ColorChannel{
  float value(WB_IsoGridCell cell, int triangle){
   return cell.getZ(triangle)*255.0/192;
  }
  
}
