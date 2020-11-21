import wblut.isogrid.*;
WB_IsoSystem6 iso;
WB_IsoColor isoColor;
void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  iso=new WB_IsoSystem6(8, 32, 32,32, width/2, height/2, new int[]{color(255, 0, 255), color(255, 255, 0), color(0, 255, 255)}, (int)random(1000000), this);
  iso.subdivide(0.50, 16, 16, 8);
  iso.sliceJAll(4, 1);
  iso.wallBlocks(0.5, 4, 4, 4);
  iso.openKAll();  
  iso.openJAll();
  iso.openKAll();
  
  //Color determined by cube coordinates i,j and k
  isoColor=new WB_IsoColor(new IGradient(),new JGradient(), new KGradient());
  
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

class IGradient implements WB_ColorChannel{
  float value(WB_IsoGridCell cell, int triangle){
   return cell.getI(triangle)*255.0/31;
  }  
}

class JGradient implements WB_ColorChannel{
  float value(WB_IsoGridCell cell, int triangle){
   return cell.getJ(triangle)*255.0/31;
  } 
}


class KGradient implements WB_ColorChannel{
  float value(WB_IsoGridCell cell, int triangle){
   return cell.getK(triangle)*255.0/31;
  }
  
}
