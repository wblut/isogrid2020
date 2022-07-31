package wblut.map;

public class WB_DoNothingMap implements WB_Map {


	@Override
	public void map(double x, double y,  double[] into) {
		into[0]=x;
		into[1]=y;
	}

}
