package wblut.isogrid.deco;

import java.util.Arrays;
import java.util.Comparator;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public class WB_IsoDecoratorCell {
	int numTriangles;
	private int q, r;
	Map<Integer,Layer> layers;

	protected WB_IsoDecoratorCell(int q, int r, int numTriangles) {
		this.q = q;
		this.r = r;
		this.numTriangles = numTriangles;
		layers=new ConcurrentSkipListMap<Integer,Layer>();
	}
	
	public int getNumberOfTriangles() {
		return numTriangles;
	}
	
	public int getNumberOfLayers() {
		return layers.size();
	}

	public Set<Integer> getLayers() {
		return layers.keySet();
	}

	public int getQ() {
		return q;
	}

	public int getR() {
		return r;
	}
	
	public int getI(int l, int f) {
		return layers.get(l).i[f];
	}
	
	public int getJ(int l,int f) {
		return layers.get(l).j[f];
	}
	
	public int getK(int l,int f) {
		return layers.get(l).k[f];
	}
	
	public int[] getIndices(int l,int f) {
		if (layers.get(l).i[f] == -Integer.MAX_VALUE)
			return null;
		return new int[] { layers.get(l).i[f], layers.get(l).j[f], layers.get(l).k[f] };
	}

	
	public void setI(int l,int f,  int id) {
		layers.get(l).i[f]=id;
	}
	
	public void setJ(int l,int f, int id) {
		layers.get(l).j[f]=id;
	}
	
	public void setK(int l,int f, int id) {
		layers.get(l).k[f]=id;
	}
	
	public int getZ(int l,int f) {
		return layers.get(l).z[f];
	}
	
	public int getTriangle(int l,int f) {
		return layers.get(l).triangle[f];
	}
	
	public int getOrientation(int l,int f) {
		return layers.get(l).orientation[f];
	}

	public int getTexture(int l,int f) {
		return layers.get(l).texture[f];
	}
	
	
	
	public double getTriangleU(int l,int f, int i) {

		return layers.get(l).triangleUV[f][2 * i];
	}

	public double getTriangleV(int l,int f, int i) {

		return layers.get(l).triangleUV[f][2 * i + 1];
	}
	
	public int getTriangleUOffset(int l,int f) {

		return layers.get(l).triangleUVOffsets[f][0];
	}
	
	public int getTriangleVOffset(int l,int f) {

		return layers.get(l).triangleUVOffsets[f][1];
	}
	
	public int getTriangleUDirection(int l,int f) {

		return layers.get(l).triangleUVDirections[f][0];
	}

	public int getTriangleVDirection(int l,int f) {

		return layers.get(l).triangleUVDirections[f][1];
	}


	public boolean isEmpty(int l) {
		for (int f = 0; f < numTriangles; f++) {
			if (layers.get(l).orientation[f] > -1)
				return false;
		}
		return true;
	}
	
	public void addLayer(int s, int orientation, int z, int f, int texture,int scale, int di, int dj, int dk,int i, int j, int k, double[] triangleUV, int[] triangleUVDirections, int[] triangleUVDirectionSigns) {
		
		
		Layer layer=layers.get(z);
		if(layer==null) {
			layer=new Layer();
			layers.put(z, layer);
			
		}
		layer.orientation[s] = orientation;
		layer.z[s] = z;
		layer.triangle[s] = f;
		layer.texture[s] = texture;
		layer.i[s]=i+di;
		layer.j[s]=j+dj;
		layer.k[s]=k+dk;
		int[] d=new int[] {di,dj,dk};
	
		for(int t=0;t<6;t+=2) {
			layer.triangleUV[s][t]=(triangleUV[t]+d[triangleUVDirections[0]]*triangleUVDirectionSigns[0]-(triangleUVDirectionSigns[0]<0?1:0))/(double)scale;
			
	
			layer.triangleUV[s][t+1]=(triangleUV[t+1]+d[triangleUVDirections[1]]*triangleUVDirectionSigns[1]-(triangleUVDirectionSigns[1]<0?1:0))/(double)scale;
			
		}
		
	}

	protected long getHash() {
		long A = (q >= 0 ? 2 * (long) q : -2 * (long) q - 1);
		long B = (r >= 0 ? 2 * (long) r : -2 * (long) r - 1);
		long C = ((A >= B ? A * A + A + B : A + B * B) / 2);
		return q < 0 && r < 0 || q >= 0 && r >= 0 ? C : -C - 1;
	}

	static protected long getHash(int q, int r) {
		long A = (q >= 0 ? 2 * (long) q : -2 * (long) q - 1);
		long B = (r >= 0 ? 2 * (long) r : -2 * (long) r - 1);
		long C = ((A >= B ? A * A + A + B : A + B * B) / 2);
		return q < 0 && r < 0 || q >= 0 && r >= 0 ? C : -C - 1;
	}

	static protected class DecoratorCellSort implements Comparator<WB_IsoDecoratorCell> {
		@Override
		public int compare(WB_IsoDecoratorCell arg0, WB_IsoDecoratorCell arg1) {
			if (arg0.q < arg1.q) {
				return -1;
			} else if (arg0.q > arg1.q) {
				return 1;
			} else if (arg0.r < arg1.r) {
				return -1;
			} else if (arg0.r > arg1.r) {
				return 1;
			}
			return 0;
		}
	}
	
	class Layer {
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(z);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Layer other = (Layer) obj;
			if (!Arrays.equals(z, other.z))
				return false;
			return true;
		}

		private int[] i;
		private int[] j;
		private int[] k;
		
		protected int[] z;
		
		protected int[] triangle;
		protected int[] orientation;
		protected int[] texture;
		
		protected double[][] triangleUV;
		protected int[][] triangleUVOffsets;
		protected int[][] triangleUVDirections;
		Layer(){
			
			i = new int[numTriangles];
			j = new int[numTriangles];
			k = new int[numTriangles];
		
			z = new int[numTriangles];
			triangle = new int[numTriangles];	
			orientation = new int[numTriangles];
			texture = new int[numTriangles];
			
			triangleUV = new double[numTriangles][6];
			for (int f = 0; f < numTriangles; f++) {
				i[f] = -Integer.MAX_VALUE;
				j[f] = -Integer.MAX_VALUE;
				k[f] = -Integer.MAX_VALUE;
				z[f] = -Integer.MAX_VALUE;
				triangle[f] = -1;
				orientation[f] = -1;
				texture[f] = 0;

			}
			
			
		}
		
		public int[] getIndices(int f) {
			if (i[f] == -Integer.MAX_VALUE)
				return null;
			return new int[] {i[f], j[f], k[f] };
		}

		
	}

}

