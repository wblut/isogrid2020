package wblut.map;

public class WB_CompoundMap implements WB_Map {
	WB_Map map1;
	WB_Map map2;
	
	public WB_CompoundMap(WB_Map map1, WB_Map map2) {
		super();
		this.map1 = map1;
		this.map2 = map2;
	}

	@Override
	public void map(double x, double y,  double[] into) {
		map1.map(x,y,into);
		map2.map(into[0],into[1],into);
	}

}
