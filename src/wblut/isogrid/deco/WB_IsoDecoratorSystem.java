package wblut.isogrid.deco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PImage;
import wblut.cubegrid.WB_CubeGrid;
import wblut.isogrid.WB_IsoHexGrid;

public class WB_IsoDecoratorSystem {
	WB_CubeGrid cubes;
	List<WB_IsoDecorationTexture> textures;
	int DI, DJ, DK, DJK;
	WB_IsoDecoratorGrid decoGrid;

	Map<Integer, Decoration> decorations;

	public WB_IsoDecoratorSystem(WB_CubeGrid cubes, WB_IsoHexGrid hexGrid) {
		this.cubes = cubes;
		DI = cubes.getI();
		DJ = cubes.getJ();
		DK = cubes.getK();
		DJK = DJ * DK;
		textures = new ArrayList<WB_IsoDecorationTexture>();
		decorations = new HashMap<Integer, Decoration>();
		decoGrid = new WB_IsoDecoratorGrid6(hexGrid);
	}

	public void addDecoration(int i, int j, int k, int scale, PImage texture0, PImage texture1, PImage texture2) {
		int index = index(i, j, k);

		if (index == -1)
			return;

		for (int di = i; di < i + scale; di++) {
			for (int dj = j; dj < j + scale; dj++) {
				for (int dk = k; dk < k + scale; dk++) {
					if (cubes.get(di, dj, dk))
						return;
				}
			}
		}

		WB_IsoDecorationTexture texture = new WB_IsoDecorationTexture(texture0, texture1, texture2);
		int tid = textures.indexOf(texture);
		if (tid == -1) {
			textures.add(texture);
			tid = textures.size() - 1;
		}
		decorations.put(index, new Decoration(tid, scale, i, j, k));

	}

	public void clearDecoration(int i, int j, int k) {
		int index = index(i, j, k);
		if (index == -1)
			return;
		decorations.remove(index);

	}

	public int getDecorationId(int i, int j, int k) {

		return decorations.get(index(i, j, k)).getTextureId();
	}

	public int getScale(int i, int j, int k) {

		return decorations.get(index(i, j, k)).getScale();
	}

	public void mapDecorationsToGrid() {
		List<Decoration> sorted = new ArrayList<Decoration>();
		sorted.addAll(decorations.values());

		Collections.sort(sorted, new Decoration.DecorationSort());
		decoGrid.clear();
		for (Decoration deco : sorted) {

			decoGrid.addCube(deco.i, deco.j, deco.k, deco.textureId, deco.scale);

		}

	}

	int index(int i, int j, int k) {
		if (i < 0 || i >= DI || j < 0 || j >= DJ || k < 0 || k >= DK)
			return -1;
		return k + DK * j + DJK * i;
	}

	public WB_IsoDecoratorGrid getDecoGrid() {
		return decoGrid;
	}

	public WB_IsoDecorationTexture getTexture(int i) {
		return textures.get(i);
	}

	static class Decoration {

		Decoration(int textureId, int scale, int i, int j, int k) {
			this.textureId = textureId;
			this.scale = scale;
			this.i = i;
			this.j = j;
			this.k = k;
			this.z = i + j + k + 3 * scale;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;

			result = prime * result + scale;
			result = prime * result + textureId;
			result = prime * result + i;
			result = prime * result + j;
			result = prime * result + k;
			
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
			Decoration other = (Decoration) obj;

			if (scale != other.scale)
				return false;
			if (textureId != other.textureId)
				return false;
			if (i != other.i)
				return false;

			if (j != other.j)
				return false;

			if (k != other.k)
				return false;
			return true;
		}

		public int getTextureId() {
			return textureId;
		}

		public void setTextureId(int textureId) {
			this.textureId = textureId;
		}

		public int getScale() {
			return scale;
		}

		public void setScale(int scale) {
			this.scale = scale;
		}

		public int getZ() {
			return z;
		}

		public void setZ(int z) {
			this.z = z;
		}

		private int textureId;
		private int scale;
		private int i, j, k, z;

		static protected class DecorationSort implements Comparator<Decoration> {
			@Override
			public int compare(Decoration arg0, Decoration arg1) {
				if (arg0.z < arg1.z) {
					return -1;
				} else if (arg0.z > arg1.z) {
					return 1;
				}
				return 0;
			}
		}

	}

}
