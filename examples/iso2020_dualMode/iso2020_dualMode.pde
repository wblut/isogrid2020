import wblut.isogrid.*;
WB_IsoSystem36 iso;

void setup() {
  size(800, 800, P3D);
  smooth(8);
  //scale, I, J, K, center x, center y, colors, random seed, PApplet
  iso=new WB_IsoSystem36(64, 16, 16, 16, width/2, height/2, createColors(), (int)random(1000000), this);
  iso.invertAll();
  //isoHexGrid36 in dual mode: set = activate vertices instead of voxels
  iso.setDual(true);
  
  iso.set(0, 0, 0);
  iso.set(1, 0, 0);
  iso.set(0, 1, 0);
  iso.set(1, 1, 0);
  iso.set(0, 0, 1);
  iso.set(1, 0, 1);
  iso.set(0, 1, 1);
  iso.set(1, 1, 1);
  iso.set(0, 0, 2);
  iso.set(1, 0, 2);
  iso.set(0, 1, 2);
  iso.set(1, 1, 2);
  iso.set(2, 0, 0);
  iso.set(2, 1, 0);
  iso.set(2, 0, 1);
  iso.set(2, 1, 1);
  iso.set(0, 2, 0);
  iso.set(1, 2, 0);
  iso.set(0, 2, 1);
  iso.set(1, 2, 1);
}

void draw() {
  background(255);
  stroke(0);
  strokeWeight(1);
  iso.drawLines();
  strokeWeight(4);
  iso.drawOutlines();
  noStroke();
  iso.drawTriangles();
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

    {1, 0, 0, 120, 0, 120}, 
    {0, 1, 0, 255, 255,0}, 
    {0, 0, 1, 0, 200, 200} 
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
