package wblut.isogrid.deco;

import java.util.Arrays;

import processing.core.PImage;

public class WB_IsoDecorationTexture {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(textures);
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
		WB_IsoDecorationTexture other = (WB_IsoDecorationTexture) obj;
		if (!Arrays.equals(textures, other.textures))
			return false;
		return true;
	}

	private PImage[] textures;

	public WB_IsoDecorationTexture(PImage texture0, PImage texture1, PImage texture2) {
		textures = new PImage[] {texture0,texture1,texture2};
	}
	
	WB_IsoDecorationTexture(PImage[] textures) {
		if(textures.length!=3) {
			throw new IllegalArgumentException("Number of textures should be 3.");
		}	
		textures = new PImage[3];
			System.arraycopy(textures, 0, this.textures, 0,3);
		
	}

	public PImage getTexture(int t) {
		if (t < 0 || t > 2) {
			throw new IllegalArgumentException("Index not in range [0,2].");
		}
		return textures[t];
	}
}
