package wblut.isogrid.color;

import wblut.isogrid.WB_IsoGridCell;

public interface WB_ColorChannel {
	public float value(WB_IsoGridCell cell, int triangle);
	
	
	public class Constant implements WB_ColorChannel{
		float val;
		
		public Constant(double val) {
		this.val=(float)val;
			
		}

		@Override
		public float value(WB_IsoGridCell cell, int triangle) {
			return val;
		}
		
	}
}
