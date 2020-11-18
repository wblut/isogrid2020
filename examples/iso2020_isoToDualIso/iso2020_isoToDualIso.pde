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
  //a dual iso requires 10 colors instead of 3
  dualIso=new WB_IsoSystem36(iso, createColors());
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

color[] createColors() {

  color[] colors=new color[10];
  float hsqrt2=sqrt(2.0)*0.5;
  float hsqrt3=sqrt(3.0)*0.5;

  float[][] normals=new float[10][3];

  normals[0]= new float[]{1, 0, 0};
  normals[1]= new float[]{0, 1, 0};
  normals[2]= new float[]{0, 0, 1};
  normals[3]= new float[]{hsqrt2, hsqrt2, 0};
  normals[4]= new float[]{hsqrt2, 0, hsqrt2};
  normals[5]= new float[]{0, hsqrt2, hsqrt2};
  normals[6]= new float[]{hsqrt3, hsqrt3, hsqrt3};
  normals[7]= new float[]{-hsqrt3, hsqrt3, hsqrt3};
  normals[8]= new float[]{hsqrt3, -hsqrt3, hsqrt3};
  normals[9]= new float[]{hsqrt3, hsqrt3, -hsqrt3};

  float[][] light=new float[][]{

    {1, 0, 0, 50, 50, 50}, 
    {0, 1, 0, 255, 255,255}, 
    {0, 0, 1, 180, 180, 180} 
  };

  for (int i=0; i<10; i++) {
    float red, green, blue, dot;
    red=green=blue=0;
    for (int l=0; l<3; l++) {
      dot=((i>6)?1.0:1.0)*max(0, normals[i][0]*light[l][0]+normals[i][1]*light[l][1]+normals[i][2]*light[l][2]);
      red+=dot*light[l][3];
      green+=dot*light[l][4];
      blue+=dot*light[l][5];
    }

    float max=max(red, green, blue);
    if (max>400.0) {
      red*=400.0/max; 
      green*=400.0/max; 
      blue*=400.0/max;
    }
    colors[i]=color(constrain(red, 0, 255), constrain(green, 0, 255), constrain(blue, 0, 255));
  }
  return colors;
}

void mousePressed(){
 drawDual=!drawDual; 

}
