package wblut.isogrid.color;

import java.util.Arrays;

import processing.core.PGraphics;
import wblut.isogrid.WB_IsoGridCell;

public class WB_IsoPalette implements WB_IsoColor {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(palette);
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
		WB_IsoPalette other = (WB_IsoPalette) obj;
		if (!Arrays.equals(palette, other.palette))
			return false;
		return true;
	}

	private int[] palette;

	public WB_IsoPalette(int color0, int color1, int color2) {
		palette = createDualPalette(new int[] { color0, color1, color2 });
	}
	
	public WB_IsoPalette(int[] colors) {
		if(colors.length!=3 && colors.length!=10) {
			throw new IllegalArgumentException("Number of colors should be 3 or 10.");
		}else if(colors.length==3) {
		palette = createDualPalette(new int[] { colors[0], colors[1], colors[2] });
		}else {
			palette = new int[10];
			System.arraycopy(colors, 0, this.palette, 0,10);
		}
	}

	private int getColor(int c) {
		if (c < 0 || c > 9) {
			throw new IllegalArgumentException("Index not in range [0,9].");
		}
		return palette[c];
	}
	
	public int getColor(PGraphics pg, WB_IsoGridCell cell, int triangle) {
		
		
		return getColor(cell.getOrientation(triangle));
	}

	private int[] createDualPalette(int[] palette) {
		if (palette.length % 3 != 0)
			throw new IllegalArgumentException("Palette length should be a mutiple of 3.");

		int numberOfPalettes = palette.length / 3;
		int[] colors = new int[10 * numberOfPalettes];
		double hsqrt2 =  Math.sqrt(2.0) * 0.5f;
		double hsqrt3 =  Math.sqrt(3.0) * 0.5f;

		double[][] normals = new double[10][3];

		normals[0] = new double[] { 1, 0, 0 };
		normals[1] = new double[] { 0, 1, 0 };
		normals[2] = new double[] { 0, 0, 1 };
		normals[3] = new double[] { hsqrt2, hsqrt2, 0 };
		normals[4] = new double[] { hsqrt2, 0, hsqrt2 };
		normals[5] = new double[] { 0, hsqrt2, hsqrt2 };
		normals[6] = new double[] { hsqrt3, hsqrt3, hsqrt3 };
		normals[7] = new double[] { -hsqrt3, hsqrt3, hsqrt3 };
		normals[8] = new double[] { hsqrt3, -hsqrt3, hsqrt3 };
		normals[9] = new double[] { hsqrt3, hsqrt3, -hsqrt3 };

		for (int p = 0; p < numberOfPalettes; p++) {

			double[][] light = new double[][] {

					{ 1, 0, 0, (palette[3 * p] >> 16) & 0xff, (palette[3 * p] >> 8) & 0xff, palette[3 * p] & 0xff },
					{ 0, 1, 0, (palette[3 * p + 1] >> 16) & 0xff, (palette[3 * p + 1] >> 8) & 0xff,
							palette[3 * p + 1] & 0xff },
					{ 0, 0, 1, (palette[3 * p + 2] >> 16) & 0xff, (palette[3 * p + 2] >> 8) & 0xff,
							palette[3 * p + 2] & 0xff } };

			for (int i = 0; i < 10; i++) {
				double red, green, blue, dot;
				red = green = blue = 0;
				for (int l = 0; l < 3; l++) {
					dot =  Math.max(0,
							normals[i][0] * light[l][0] + normals[i][1] * light[l][1] + normals[i][2] * light[l][2]);
					red += dot * light[l][3];
					green += dot * light[l][4];
					blue += dot * light[l][5];
				}

				double max =  Math.max(red, Math.max(green, blue));
				if (max > 400.0f) {
					red *= 400.0f / max;
					green *= 400.0f / max;
					blue *= 400.0f / max;
				}
				colors[10 * p + i] = 
						
						
						
						0xff000000|(((int) Math.max(Math.min(red, 255), 0) << 16)
						| (((int) Math.max(Math.min(green, 255), 0)) << 8) | (int) Math.max(Math.min(blue, 255), 0));
			}

		}

		return colors;
	}
}
