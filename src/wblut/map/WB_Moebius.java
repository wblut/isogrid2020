package wblut.map;

import java.util.Objects;

import processing.core.PApplet;
import wblut.isogrid.WB_IsoSystem6;

public class WB_Moebius implements WB_Map {
	double ar,ai,br,bi,cr,ci,dr,di,  scale;
	
	
	public WB_Moebius(double ar, double ai, double br, double bi, double cr, double ci, double dr, double di, double scale) {
		super();
	
		this.ar = ar;
		this.ai = ai;
		this.br = br;
		this.bi = bi;
		this.cr = cr;
		this.ci = ci;
		this.dr = dr;
		this.di = di;
		this.scale=scale;
	}
	


	public double getAi() {
		return ai;
	}

	public double getAr() {
		return ar;
	}

	public double getBi() {
		return bi;
	}

	public double getBr() {
		return br;
	}

	public double getCi() {
		return ci;
	}

	public double getCr() {
		return cr;
	}

	public double getDi() {
		return di;
	}

	public double getDr() {
		return dr;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ai, ar, bi, br, ci, cr, di, dr, scale);
	}

	public double getScale() {
		return scale;
	}


	public void setScale(double scale) {
		this.scale = scale;
	}

	double nomr,nomi,denomr, denomi, denom, resultr, resulti;
	


	@Override
	public void map(double zr, double zi,  double[]into) {
		zr/=scale;
	
		zi/=scale;
		
		
		nomr=br+ar*zr-ai*zi;
		nomi=bi+ar*zi+ai*zr;
		
		
		denomr=dr+cr*zr-ci*zi;
		denomi=di+cr*zi+ci*zr;
		denom=(denomr*denomr+denomi*denomi);
		if(denom<1e-6) {
			denom=1e-6;
		}
		resultr=(nomr*denomr+nomi*denomi)/denom;
		resulti=(nomi*denomr-nomr*denomi)/denom;
		
		
		into[0]=scale*resultr;
		into[1]=scale*resulti;
	}

	public void setAll(double ar, double ai, double br, double bi, double cr, double ci, double dr, double di) {
		this.ar = ar;
		this.ai = ai;
		this.br = br;
		this.bi = bi;
		this.cr = cr;
		this.ci = ci;
		this.dr = dr;
		this.di = di;
	}

	public void setAi(double ac) {
		this.ai = ac;
	}

	public void setAr(double ar) {
		this.ar = ar;
	}

	public void setBi(double bc) {
		this.bi = bc;
	}

	public void setBr(double br) {
		this.br = br;
	}

	public void setCi(double cc) {
		this.ci = cc;
	}

	public void setCr(double cr) {
		this.cr = cr;
	}

	public void setDi(double dc) {
		this.di = dc;
	}

	public void setDr(double dr) {
		this.dr = dr;
	}

	@Override
	public String toString() {
		return "WB_Moebius [ar=" + ar + ", ai=" + ai + ", br=" + br + ", bi=" + bi + ", cr=" + cr + ", ci=" + ci
				+ ", dr=" + dr + ", di=" + di + "]";
	}
	
	
	public static void main(String... args) {

		WB_Moebius map=new WB_Moebius(0,0,1,0,1,0,0,0,1);
		
		  double[] test=new double[]{0,0};
		  map.map(0,0,test);
		  
		  
		  System.out.println(test[0]+" "+test[1]);
		
	}

}
