package wblut.isogrid;

import processing.core.PGraphics;

public class WB_IsoColor {
 private WB_ColorChannel channel1;
 private WB_ColorChannel channel2;
 private WB_ColorChannel channel3;
 private WB_ColorChannel alpha;
 private boolean monochrome;

public WB_IsoColor(WB_ColorChannel channel1,WB_ColorChannel channel2, WB_ColorChannel channel3) {
	this.channel1=channel1;
	this.channel2=channel2;
	this.channel3=channel3;
	this.alpha=new WB_ColorChannel.Constant(255);
	monochrome=false;
}

public WB_IsoColor(WB_ColorChannel channel) {
	this.channel1=channel;
	this.channel2=channel;
	this.channel3=channel;
	this.alpha=new WB_ColorChannel.Constant(255);
	monochrome=true;
}


public WB_IsoColor(double channel1,WB_ColorChannel channel2, WB_ColorChannel channel3) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=channel2;
	this.channel3=channel3;
	this.alpha=new WB_ColorChannel.Constant(255);
	monochrome=false;
}

public WB_IsoColor(double channel) {
	this.channel1=new WB_ColorChannel.Constant(channel);
	this.channel2=new WB_ColorChannel.Constant(channel);
	this.channel3=new WB_ColorChannel.Constant(channel);
	this.alpha=new WB_ColorChannel.Constant(255);
	monochrome=true;
}


public WB_IsoColor(WB_ColorChannel channel1,double channel2,WB_ColorChannel channel3) {
	this.channel1=channel1;
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=channel3;
	this.alpha=new WB_ColorChannel.Constant(255);
	monochrome=false;
}


public WB_IsoColor(double channel1,double channel2, WB_ColorChannel channel3) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=channel3;
	this.alpha=new WB_ColorChannel.Constant(255);
	monochrome=false;
}

public WB_IsoColor(WB_ColorChannel channel1,WB_ColorChannel channel2, double channel3) {
	this.channel1=channel1;
	this.channel2=channel2;
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=new WB_ColorChannel.Constant(255);
	monochrome=false;
}

public WB_IsoColor(double channel1,WB_ColorChannel channel2, double channel3) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=channel2;
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=new WB_ColorChannel.Constant(255);
	monochrome=false;
}

public WB_IsoColor(WB_ColorChannel channel1 ,double channel2, double channel3) {
	this.channel1=channel1;
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=new WB_ColorChannel.Constant(255);
	monochrome=false;
}

public WB_IsoColor(double channel1,double channel2, double channel3) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=new WB_ColorChannel.Constant(255);
	monochrome=false;
}

public WB_IsoColor(WB_ColorChannel channel1,WB_ColorChannel channel2, WB_ColorChannel channel3, double alpha) {
	this.channel1=channel1;
	this.channel2=channel2;
	this.channel3=channel3;
	this.alpha=new WB_ColorChannel.Constant(alpha);
	monochrome=false;
}


public WB_IsoColor(double channel1,WB_ColorChannel channel2, WB_ColorChannel channel3, double alpha) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=channel2;
	this.channel3=channel3;
	this.alpha=new WB_ColorChannel.Constant(alpha);
	monochrome=false;
}


public WB_IsoColor(WB_ColorChannel channel1,double channel2,WB_ColorChannel channel3, double alpha) {
	this.channel1=channel1;
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=channel3;
	this.alpha=new WB_ColorChannel.Constant(alpha);
	monochrome=false;
}


public WB_IsoColor(double channel1,double channel2, WB_ColorChannel channel3, double alpha) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=channel3;
	this.alpha=new WB_ColorChannel.Constant(alpha);
	monochrome=false;
}

public WB_IsoColor(WB_ColorChannel channel1,WB_ColorChannel channel2, double channel3, double alpha) {
	this.channel1=channel1;
	this.channel2=channel2;
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=new WB_ColorChannel.Constant(alpha);
	monochrome=false;
}

public WB_IsoColor(double channel1,WB_ColorChannel channel2, double channel3, double alpha) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=channel2;
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=new WB_ColorChannel.Constant(alpha);
	monochrome=false;
}

public WB_IsoColor(WB_ColorChannel channel1 ,double channel2, double channel3, double alpha) {
	this.channel1=channel1;
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=new WB_ColorChannel.Constant(alpha);
	monochrome=false;
}

public WB_IsoColor(double channel1,double channel2, double channel3, double alpha) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=new WB_ColorChannel.Constant(alpha);
	monochrome=false;
}

public WB_IsoColor(WB_ColorChannel channel1,WB_ColorChannel channel2, WB_ColorChannel channel3, WB_ColorChannel alpha) {
	this.channel1=channel1;
	this.channel2=channel2;
	this.channel3=channel3;
	this.alpha=alpha;
	monochrome=false;
}


public WB_IsoColor(double channel1,WB_ColorChannel channel2, WB_ColorChannel channel3, WB_ColorChannel alpha) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=channel2;
	this.channel3=channel3;
	this.alpha=alpha;
	monochrome=false;
}


public WB_IsoColor(WB_ColorChannel channel1,double channel2,WB_ColorChannel channel3, WB_ColorChannel alpha) {
	this.channel1=channel1;
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=channel3;
	this.alpha=alpha;
	monochrome=false;
}


public WB_IsoColor(double channel1,double channel2, WB_ColorChannel channel3, WB_ColorChannel alpha) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=channel3;
	this.alpha=alpha;
	monochrome=false;
}

public WB_IsoColor(WB_ColorChannel channel1,WB_ColorChannel channel2, double channel3, WB_ColorChannel alpha) {
	this.channel1=channel1;
	this.channel2=channel2;
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=alpha;
	monochrome=false;
}

public WB_IsoColor(double channel1,WB_ColorChannel channel2, double channel3, WB_ColorChannel alpha) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=channel2;
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=alpha;
	monochrome=false;
}

public WB_IsoColor(WB_ColorChannel channel1 ,double channel2, double channel3, WB_ColorChannel alpha) {
	this.channel1=channel1;
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=alpha;
	monochrome=false;
}

public WB_IsoColor(double channel1,double channel2, double channel3, WB_ColorChannel alpha) {
	this.channel1=new WB_ColorChannel.Constant(channel1);
	this.channel2=new WB_ColorChannel.Constant(channel2);
	this.channel3=new WB_ColorChannel.Constant(channel3);
	this.alpha=alpha;
	monochrome=false;
}



public WB_IsoColor(WB_ColorChannel channel, double alpha) {
	this.channel1=channel;
	this.channel2=channel;
	this.channel3=channel;
	this.alpha=new WB_ColorChannel.Constant(alpha);
	monochrome=true;
}

public WB_IsoColor(double channel, double alpha) {
	this.channel1=new WB_ColorChannel.Constant(channel);
	this.channel2=new WB_ColorChannel.Constant(channel);
	this.channel3=new WB_ColorChannel.Constant(channel);
	this.alpha=new WB_ColorChannel.Constant(alpha);
	monochrome=true;
}

public WB_IsoColor(WB_ColorChannel channel, WB_ColorChannel alpha) {
	this.channel1=channel;
	this.channel2=channel;
	this.channel3=channel;
	this.alpha=alpha;
	monochrome=true;
}

public WB_IsoColor(double channel, WB_ColorChannel alpha) {
	this.channel1=new WB_ColorChannel.Constant(channel);
	this.channel2=new WB_ColorChannel.Constant(channel);
	this.channel3=new WB_ColorChannel.Constant(channel);
	this.alpha=alpha;
	monochrome=true;
}

public int color(PGraphics pg, WB_IsoGridCell cell, int triangle) {
	if(monochrome) return pg.color(channel1.value(cell, triangle),alpha.value(cell, triangle));
	return pg.color(channel1.value(cell, triangle),channel2.value(cell, triangle),channel3.value(cell, triangle),alpha.value(cell, triangle));
}


}
