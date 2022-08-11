package wblut.isogrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.rng.RandomProviderState;
import org.apache.commons.rng.RestorableUniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import wblut.cubegrid.Maze;
import wblut.cubegrid.WB_CubeGrid;
import wblut.hexgrid.WB_HexGrid;
import wblut.isogrid.color.WB_IsoColor;
import wblut.isogrid.color.WB_IsoPalette;
import wblut.map.WB_DoNothingMap;
import wblut.map.WB_Map;

public abstract class WB_IsoSystem {
	static final WB_Map DONOTHING = new WB_DoNothingMap();
	double centerX, centerY;

	List<WB_IsoColor> colorSources;
	WB_CubeGrid cubeGrid;
	boolean DEFER;
	boolean GLOBALDEFER;
	WB_IsoHexGrid hexGrid;
	PApplet home;
	int I, J, K, JK, IJK;
	double L;
	WB_Map map;
	int minI, minJ, minK, maxI, maxJ, maxK;
	int numParts;
	int numRegions;
	RestorableUniformRandomProvider randomGen;
	int seed;
	RandomProviderState state;
	boolean USEMAP;
	boolean YFLIP;

	WB_IsoSystem() {

	}

	WB_IsoSystem(boolean[] pattern, int pI, int pJ, int pK, int scaleI, int scaleJ, int scaleK, double L,
			double centerX, double centerY, int[] colors, int seed, PApplet home) {
		randomGen = RandomSource.create(RandomSource.MT);
		state = randomGen.saveState();
		this.home = home;
		this.L = L;
		I = scaleI * pI;
		if (I < 0)
			throw new IllegalArgumentException("Pattern should be at least 1x1x1 and scale can't be zero or negative.");
		J = scaleJ * pJ;
		if (J < 0)
			throw new IllegalArgumentException(
					"Pattern should be at least 1x1x1 and scale can't be zero or negative..");
		K = scaleK * pK;
		if (K < 0)
			throw new IllegalArgumentException(
					"Pattern should be at least 1x1x1 and scale can't be zero or negative..");
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;
		JK = this.J * this.K;
		IJK = this.I * JK;
		addColorSource(colors);
		this.centerX = centerX;
		this.centerY = centerY;
		this.cubeGrid = new WB_CubeGrid(this.I, this.J, this.K);
		this.seed = seed;
		setGrid();
		DEFER = true;
		int index = 0;
		for (int i = 0; i < I; i += scaleI) {
			for (int j = 0; j < J; j += scaleJ) {
				for (int k = 0; k < K; k += scaleK) {
					if (pattern[index++])
						set(i, j, k, scaleI, scaleJ, scaleK);
				}
			}
		}

		DEFER = false;
		map();
		GLOBALDEFER = false;
		YFLIP = true;
		USEMAP = false;
		map = DONOTHING;

	}

	WB_IsoSystem(boolean[] pattern, int pI, int pJ, int pK, int scaleI, int scaleJ, int scaleK, double L,
			double centerX, double centerY, WB_IsoColor colors, int seed, PApplet home) {
		randomGen = RandomSource.create(RandomSource.MT);
		state = randomGen.saveState();
		this.home = home;
		this.L = L;
		I = scaleI * pI;
		if (I < 0)
			throw new IllegalArgumentException("Pattern should be at least 1x1x1 and scale can't be zero or negative.");
		J = scaleJ * pJ;
		if (J < 0)
			throw new IllegalArgumentException(
					"Pattern should be at least 1x1x1 and scale can't be zero or negative..");
		K = scaleK * pK;
		if (K < 0)
			throw new IllegalArgumentException(
					"Pattern should be at least 1x1x1 and scale can't be zero or negative..");
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;
		JK = this.J * this.K;
		IJK = this.I * JK;
		this.colorSources = new ArrayList<WB_IsoColor>();

		this.colorSources.add(colors);
		this.centerX = centerX;
		this.centerY = centerY;
		this.cubeGrid = new WB_CubeGrid(this.I, this.J, this.K);
		this.seed = seed;
		setGrid();
		DEFER = true;
		int index = 0;
		for (int i = 0; i < I; i += scaleI) {
			for (int j = 0; j < J; j += scaleJ) {
				for (int k = 0; k < K; k += scaleK) {
					if (pattern[index++])
						set(i, j, k, scaleI, scaleJ, scaleK);
				}
			}
		}

		DEFER = false;

		map();
		GLOBALDEFER = false;
		YFLIP = true;
		USEMAP = false;
		map = DONOTHING;
	}

	WB_IsoSystem(boolean[][][] pattern, int scaleI, int scaleJ, int scaleK, double L, double centerX, double centerY,
			int[] colors, int seed, PApplet home) {
		randomGen = RandomSource.create(RandomSource.MT);
		state = randomGen.saveState();
		this.home = home;
		this.L = L;
		I = scaleI * pattern.length;
		if (I < 0)
			throw new IllegalArgumentException("Pattern should be at least 1x1x1 and scale can't be zero or negative.");
		J = scaleJ * pattern[0].length;
		if (J < 0)
			throw new IllegalArgumentException(
					"Pattern should be at least 1x1x1 and scale can't be zero or negative..");
		K = scaleK * pattern[0][0].length;
		if (K < 0)
			throw new IllegalArgumentException(
					"Pattern should be at least 1x1x1 and scale can't be zero or negative..");
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;
		JK = this.J * this.K;
		IJK = this.I * JK;
		addColorSource(colors);
		this.centerX = centerX;
		this.centerY = centerY;
		this.cubeGrid = new WB_CubeGrid(this.I, this.J, this.K);
		this.seed = seed;
		setGrid();
		DEFER = true;
		for (int i = 0, pi = 0; i < I; i += scaleI, pi++) {
			for (int j = 0, pj = 0; j < J; j += scaleJ, pj++) {
				for (int k = 0, pk = 0; k < K; k += scaleK, pk++) {
					if (pattern[pi][pj][pk])
						set(i, j, k, scaleI, scaleJ, scaleK);
				}
			}
		}

		DEFER = false;
		map();
		GLOBALDEFER = false;
		YFLIP = true;
		USEMAP = false;
		map = DONOTHING;
	}

	WB_IsoSystem(boolean[][][] pattern, int scaleI, int scaleJ, int scaleK, double L, double centerX, double centerY,
			WB_IsoColor colors, int seed, PApplet home) {
		randomGen = RandomSource.create(RandomSource.MT);
		state = randomGen.saveState();
		this.home = home;
		this.L = L;
		I = scaleI * pattern.length;
		if (I < 0)
			throw new IllegalArgumentException("Pattern should be at least 1x1x1 and scale can't be zero or negative.");
		J = scaleJ * pattern[0].length;
		if (J < 0)
			throw new IllegalArgumentException(
					"Pattern should be at least 1x1x1 and scale can't be zero or negative..");
		K = scaleK * pattern[0][0].length;
		if (K < 0)
			throw new IllegalArgumentException(
					"Pattern should be at least 1x1x1 and scale can't be zero or negative..");
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;
		JK = this.J * this.K;
		IJK = this.I * JK;
		this.colorSources = new ArrayList<WB_IsoColor>();

		this.colorSources.add(colors);

		this.centerX = centerX;
		this.centerY = centerY;
		this.cubeGrid = new WB_CubeGrid(this.I, this.J, this.K);
		this.seed = seed;
		setGrid();
		DEFER = true;
		for (int i = 0, pi = 0; i < I; i += scaleI, pi++) {
			for (int j = 0, pj = 0; j < J; j += scaleJ, pj++) {
				for (int k = 0, pk = 0; k < K; k += scaleK, pk++) {
					if (pattern[pi][pj][pk])
						set(i, j, k, scaleI, scaleJ, scaleK);
				}
			}
		}

		DEFER = false;

		map();
		GLOBALDEFER = false;
		YFLIP = true;
		USEMAP = false;
		map = DONOTHING;
	}

	WB_IsoSystem(boolean[][][] pattern, int[][][] colorIndices, int scaleI, int scaleJ, int scaleK, double L,
			double centerX, double centerY, WB_IsoColor colors, int seed, PApplet home) {
		randomGen = RandomSource.create(RandomSource.MT);
		state = randomGen.saveState();
		this.home = home;
		this.L = L;
		I = scaleI * pattern.length;
		if (I < 0)
			throw new IllegalArgumentException("Pattern should be at least 1x1x1 and scale can't be zero or negative.");
		J = scaleJ * pattern[0].length;
		if (J < 0)
			throw new IllegalArgumentException(
					"Pattern should be at least 1x1x1 and scale can't be zero or negative..");
		K = scaleK * pattern[0][0].length;
		if (K < 0)
			throw new IllegalArgumentException(
					"Pattern should be at least 1x1x1 and scale can't be zero or negative..");
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;
		JK = this.J * this.K;
		IJK = this.I * JK;
		this.colorSources = new ArrayList<WB_IsoColor>();

		this.colorSources.add(colors);

		this.centerX = centerX;
		this.centerY = centerY;
		this.cubeGrid = new WB_CubeGrid(this.I, this.J, this.K);
		this.seed = seed;
		setGrid();
		DEFER = true;
		for (int i = 0, pi = 0; i < I; i += scaleI, pi++) {
			for (int j = 0, pj = 0; j < J; j += scaleJ, pj++) {
				for (int k = 0, pk = 0; k < K; k += scaleK, pk++) {
					if (pattern[pi][pj][pk])
						set(i, j, k, scaleI, scaleJ, scaleK);
					setColors(i, j, k, scaleI, scaleJ, scaleK, colorIndices[pi][pj][pk]);
				}
			}
		}

		DEFER = false;

		map();
		GLOBALDEFER = false;
		YFLIP = true;
		USEMAP = false;
	}

	WB_IsoSystem(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed, boolean full,
			PApplet home) {
		randomGen = RandomSource.create(RandomSource.MT, seed);

		state = randomGen.saveState();
		this.home = home;
		this.L = L;
		this.I = Math.max(1, I);
		this.J = Math.max(1, J);
		this.K = Math.max(1, K);
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;
		JK = this.J * this.K;
		IJK = this.I * JK;
		addColorSource(colors);
		this.centerX = centerX;
		this.centerY = centerY;
		this.cubeGrid = new WB_CubeGrid(this.I, this.J, this.K);
		this.seed = seed;
		setGrid();
		if (full) {
			set(0, 0, 0, I, J, K);
			map();
		}
		DEFER = false;
		GLOBALDEFER = false;
		YFLIP = true;
		USEMAP = false;
		map = DONOTHING;

	}

	WB_IsoSystem(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed, PApplet home) {
		this(L, I, J, K, centerX, centerY, colors, seed, true, home);

	}

	WB_IsoSystem(double L, int I, int J, int K, double centerX, double centerY, WB_IsoColor colors, int seed,
			boolean full, PApplet home) {
		randomGen = RandomSource.create(RandomSource.MT, seed);
		state = randomGen.saveState();
		this.home = home;
		this.L = L;
		this.I = Math.max(1, I);
		this.J = Math.max(1, J);
		this.K = Math.max(1, K);
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;
		JK = this.J * this.K;
		IJK = this.I * JK;
		addColorSource(colors);
		this.centerX = centerX;
		this.centerY = centerY;
		this.cubeGrid = new WB_CubeGrid(this.I, this.J, this.K);
		this.seed = seed;
		setGrid();

		if (full) {
			set(0, 0, 0, I, J, K);
			map();
		}
		DEFER = false;
		GLOBALDEFER = false;
		YFLIP = true;
		USEMAP = false;
		map = DONOTHING;

	}

	WB_IsoSystem(double L, int I, int J, int K, double centerX, double centerY, WB_IsoColor colors, int seed,
			PApplet home) {
		this(L, I, J, K, centerX, centerY, colors, seed, true, home);

	}

	WB_IsoSystem(WB_IsoSystem iso) {

		randomGen = RandomSource.create(RandomSource.MT, iso.seed);
		state = randomGen.saveState();
		this.home = iso.home;
		this.L = iso.L;
		I = iso.I;
		J = iso.J;
		K = iso.K;
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;
		JK = this.J * this.K;
		IJK = this.I * JK;
		this.colorSources = new ArrayList<WB_IsoColor>(iso.colorSources.size());
		for (WB_IsoColor colorSource : iso.colorSources) {
			this.colorSources.add(colorSource);
		}
		this.centerX = iso.centerX;
		this.centerY = iso.centerY;
		this.cubeGrid = new WB_CubeGrid(I, J, K);

		this.seed = iso.seed;
		setGrid();
		int index = 0;

		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (iso.cubeGrid.get(index)) {
						cubeGrid.set(index, true);
						cubeGrid.setColorSourceIndex(index, iso.cubeGrid.getColorSourceIndex(index));
					}
					index++;
				}
			}
		}
		DEFER = false;
		map();

		GLOBALDEFER = false;
		YFLIP = iso.YFLIP;
		USEMAP = iso.USEMAP;
		map = iso.map;
	}

	WB_IsoSystem(WB_IsoSystem iso, int colorIndex) {

		randomGen = RandomSource.create(RandomSource.MT, iso.seed);
		state = randomGen.saveState();
		this.home = iso.home;
		this.L = iso.L;
		I = iso.I;
		J = iso.J;
		K = iso.K;
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;
		JK = this.J * this.K;
		IJK = this.I * JK;
		this.colorSources = new ArrayList<WB_IsoColor>(iso.colorSources.size());
		for (WB_IsoColor colorSource : iso.colorSources) {
			this.colorSources.add(colorSource);
		}
		this.centerX = iso.centerX;
		this.centerY = iso.centerY;
		this.cubeGrid = new WB_CubeGrid(I, J, K);

		this.seed = iso.seed;
		setGrid();
		int index = 0;

		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (iso.cubeGrid.getColorSourceIndex(index) == colorIndex) {
						if (iso.cubeGrid.get(index)) {
							cubeGrid.set(index, true);
							cubeGrid.setColorSourceIndex(index, iso.cubeGrid.getColorSourceIndex(index));
						}

					}
					index++;
				}
			}
		}
		DEFER = false;
		map();

		GLOBALDEFER = false;
		YFLIP = iso.YFLIP;
		USEMAP = iso.USEMAP;
		map = iso.map;

	}

	WB_IsoSystem(WB_IsoSystem iso, int splitJ, int deltaJ) {

		randomGen = RandomSource.create(RandomSource.MT, iso.seed);
		state = randomGen.saveState();
		this.home = iso.home;
		this.L = iso.L;
		I = iso.I;
		J = iso.J + deltaJ;
		K = iso.K;
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;
		JK = J * K;
		IJK = I * JK;
		this.colorSources = new ArrayList<WB_IsoColor>(iso.colorSources.size());
		for (WB_IsoColor colorSource : iso.colorSources) {
			this.colorSources.add(colorSource);
		}
		this.centerX = iso.centerX;
		this.centerY = iso.centerY;
		this.cubeGrid = new WB_CubeGrid(this.I, this.J, this.K);

		this.seed = iso.seed;
		setGrid();
		int index = 0;
		int dj;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < iso.J; j++) {
				if (j > splitJ) {
					dj = j + deltaJ;

				} else {
					dj = j;

				}

				for (int k = 0; k < K; k++) {

					if (iso.cubeGrid.get(index)) {

						cubeGrid.set(i, dj, k, true);
						cubeGrid.setColorSourceIndex(i, dj, k, iso.cubeGrid.getColorSourceIndex(index));
					}
					index++;

				}
			}
		}
		DEFER = false;
		map();

		GLOBALDEFER = false;
		YFLIP = iso.YFLIP;
		USEMAP = iso.USEMAP;
		map = iso.map;

	}

	WB_IsoSystem(WB_IsoSystem iso, int scaleI, int scaleJ, int scaleK) {
		if (scaleI == 0 || scaleJ == 0 || scaleK == 0)
			throw new IllegalArgumentException("Scale cannot be 0.");
		if (Math.abs(scaleI) == 1 && Math.abs(scaleJ) == 1 && Math.abs(scaleK) == 1)
			throw new IllegalArgumentException("At least one scale should ber different from -1 or 1.");
		randomGen = RandomSource.create(RandomSource.MT, iso.seed);
		state = randomGen.saveState();
		this.home = iso.home;

		this.L = iso.L;
		if (scaleI > 0) {
			I = iso.I * scaleI;
		} else {
			I = iso.I / Math.abs(scaleI);
		}
		if (scaleJ > 0) {
			J = iso.J * scaleJ;
		} else {
			J = iso.J / Math.abs(scaleJ);
		}
		if (scaleK > 0) {
			K = iso.K * scaleK;
		} else {
			K = iso.K / Math.abs(scaleK);
		}
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;
		JK = this.J * this.K;
		IJK = this.I * JK;
		this.colorSources = new ArrayList<WB_IsoColor>(iso.colorSources.size());
		for (WB_IsoColor colorSource : iso.colorSources) {
			this.colorSources.add(colorSource);
		}
		this.centerX = iso.centerX;
		this.centerY = iso.centerY;
		this.cubeGrid = new WB_CubeGrid(I, J, K);

		this.seed = iso.seed;
		setGrid();
		int index = 0;
		int lookup = 0;
		int lookupI = -1, lookupJ = -1, lookupK = -1;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (scaleI > 0) {
						lookupI = i / scaleI;
					}
					if (scaleJ > 0) {
						lookupJ = j / scaleJ;
					}
					if (scaleK > 0) {
						lookupK = k / scaleK;
					}
					lookup = getSampledValue(iso, lookupI, lookupJ, lookupK, i, j, k, Math.abs(scaleI),
							Math.abs(scaleJ), Math.abs(scaleK));
					if (iso.cubeGrid.get(lookup)) {

						cubeGrid.set(index, true);
						cubeGrid.setColorSourceIndex(index, iso.cubeGrid.getColorSourceIndex(lookup));
					}
					index++;
				}
			}
		}
		DEFER = false;
		// map();

		GLOBALDEFER = false;
		YFLIP = true;
		USEMAP = iso.USEMAP;
		map = iso.map;

	}

	public int addColorSource(int[] colors) {
		if (colors.length == 0 || colors.length % 3 != 0)
			throw new IllegalArgumentException("Number of colors should be at least 3 or a higher multiple of 3.");
		if (this.colorSources == null)
			this.colorSources = new ArrayList<WB_IsoColor>();
		int n = colors.length / 3;
		for (int i = 0; i < n; i++) {
			this.colorSources.add(new WB_IsoPalette(colors[i * 3], colors[i * 3 + 1], colors[i * 3 + 2]));
		}

		return this.colorSources.size();
	}

	public int addColorSource(WB_IsoColor colors) {
		if (this.colorSources == null)
			this.colorSources = new ArrayList<WB_IsoColor>();
		int pal = this.colorSources.indexOf(colors);
		if (pal == -1) {
			this.colorSources.add(colors);
			pal = this.colorSources.size() - 1;
		}

		return pal;
	}

	public void addMissingConnectors() {
		fillBuffer();
		DEFER = true;
		boolean val;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					val = cubeGrid.get(i, j, k);
					if (cubeGrid.isMissingConnector(i, j, k)) {
						val = true;
					}

					cubeGrid.setBuffer(i, j, k, val);

				}
			}
		}

		cubeGrid.swap();

		DEFER = false;
		map();
	}

	public void and(int i, int j, int k) {
		cubeGrid.and(i, j, k, true);
		map();
	}

	public void and(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < Math.min(blocki, maxI - minI); di++) {
			for (int dj = 0; dj < Math.min(blockj, maxJ - minJ); dj++) {
				for (int dk = 0; dk < Math.min(blockk, maxK - minK); dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1) {
						cubeGrid.and(index, true);
					}
				}
			}
		}
		map();
	}

	public void and(WB_IsoSystem iso) {
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (!iso.get(i, j, k)) {
						clear(i, j, k);
					}
				}
			}
		}
		DEFER = false;
		map();

	}

	public void and(WB_IsoSystem iso, int offsetI, int offsetJ, int offsetK) {
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (!iso.get(i, j, k)) {
						clear(i + offsetI, j + offsetJ, k + offsetK);
					}
				}
			}
		}
		DEFER = false;
		map();

	}

	public final WB_HexGrid bakeTriangles(PApplet home) {
		return bakeTriangles(home.g);
	}

	public final WB_HexGrid bakeTriangles(PApplet home, int sourceI, int sourceJ, int sourceK, int offsetI, int offsetJ,
			int offsetK) {
		return bakeTriangles(home.g, sourceI, sourceJ, sourceK, offsetI, offsetJ, offsetK);
	}

	abstract public WB_HexGrid bakeTriangles(PGraphics pg);

	abstract public WB_HexGrid bakeTriangles(PGraphics pg, int sourceI, int sourceJ, int sourceK, int offsetI,
			int offsetJ, int offsetK);

	public void barIAll(int stepj, int stepk, int rj, int rk) {
		DEFER = true;
		for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				set(0, j - Math.max(1, rj), k - Math.max(1, rk), I, Math.max(2 * rj, 1), Math.max(2 * rk, 1));
			}
		}
		DEFER = false;
		map();
	}

	public void barIBlocks(float chance, int stepj, int stepk, int rj, int rk, int di, int dj, int dk) {
		boolean[][] column = new boolean[J][K];
		for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				for (int cj = -rj; cj <= rj; cj++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K)
							column[j + cj][k + ck] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int cj = 0; cj < dj; cj++) {
							for (int ck = 0; ck < dk; ck++) {
								if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K && column[j + cj][k + ck]) {
									set(i, j + cj, k + ck, di, 1, 1);
								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void barIBlocks(float chance, int stepj, int stepk, int rj, int rk, int di, int dj, int dk, int colors) {
		boolean[][] column = new boolean[J][K];
		for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				for (int cj = -rj; cj <= rj; cj++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K)
							column[j + cj][k + ck] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int cj = 0; cj < dj; cj++) {
							for (int ck = 0; ck < dk; ck++) {
								if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K && column[j + cj][k + ck]) {
									set(i, j + cj, k + ck, di, 1, 1);
									setColors(i, j + cj, k + ck, di, 1, 1, colors);
								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void barJAll(int stepi, int stepk, int ri, int rk) {
		DEFER = true;
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				set(i - Math.max(1, ri), 0, k - Math.max(1, rk), Math.max(2 * ri, 1), J, Math.max(2 * rk, 1));
			}
		}
		DEFER = false;
		map();
	}

	public void barJBlocks(float chance, int stepi, int stepk, int ri, int rk, int di, int dj, int dk) {
		boolean[][] column = new boolean[I][K];
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K)
							column[i + ci][k + ck] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							for (int ck = 0; ck < dk; ck++) {
								if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K && column[i + ci][k + ck]) {
									set(i + ci, j, k + ck, 1, dj, 1);
								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void barJBlocks(float chance, int stepi, int stepk, int ri, int rk, int di, int dj, int dk, int colors) {
		boolean[][] column = new boolean[I][K];
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K)
							column[i + ci][k + ck] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							for (int ck = 0; ck < dk; ck++) {
								if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K && column[i + ci][k + ck]) {
									set(i + ci, j, k + ck, 1, dj, 1);
									setColors(i + ci, j, k + ck, 1, dj, 1, colors);
								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void barKAll(int stepi, int stepj, int ri, int rj) {
		DEFER = true;
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
				set(i - Math.max(1, ri), j - Math.max(1, rj), 0, Math.max(2 * ri, 1), Math.max(2 * rj, 1), K);
			}
		}
		DEFER = false;
		map();
	}

	public void barKBlocks(float chance, int stepi, int stepj, int ri, int rj, int di, int dj, int dk) {
		boolean[][] column = new boolean[I][J];
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int cj = -rj; cj <= rj; cj++) {
						if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J)
							column[i + ci][j + cj] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							for (int cj = 0; cj < dj; cj++) {
								if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J && column[i + ci][j + cj]) {
									set(i + ci, j + cj, k, 1, 1, dk);
								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void barKBlocks(float chance, int stepi, int stepj, int ri, int rj, int di, int dj, int dk, int colors) {
		boolean[][] column = new boolean[I][J];
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int cj = -rj; cj <= rj; cj++) {
						if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J)
							column[i + ci][j + cj] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							for (int cj = 0; cj < dj; cj++) {
								if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J && column[i + ci][j + cj]) {
									set(i + ci, j + cj, k, 1, 1, dk);
									setColors(i + ci, j + cj, k, 1, 1, dk, colors);
								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void blocks(int n) {
		hexGrid.clear();
		cubeGrid.clear();
		DEFER = true;
		for (int r = 0; r < n; r++) {
			int sj = (int) random(J);
			int ej = (int) random(sj, J);
			int si = (int) random(I);
			int ei = (int) random(si, I);
			int sk = (int) random(K);
			int ek = (int) random(sk, K);
			or(si, sj, sk, ei - si, ej - sj, ek - sk);
		}
		DEFER = false;
		map();
	}

	public void blocks(int n, int is, int js, int ks, int ie, int je, int ke) {
		DEFER = true;
		for (int r = 0; r < n; r++) {
			int sj = (int) random(js, je);
			int ej = (int) random(sj, je);
			int si = (int) random(is, ie);
			int ei = (int) random(si, ie);
			int sk = (int) random(ks, ke);
			int ek = (int) random(sk, ke);
			or(si, sj, sk, ei - si, ej - sj, ek - sk);
		}
		DEFER = false;
		map();
	}

	public void calculateExposure(double di, double dj, double dk, int cutoff, double diffuse, double emission,
			double smooth, int iter) {
		cubeGrid.setMaxAccessibilityAtBoundaries(cutoff);
		cubeGrid.scanInteriorCubes(cutoff, false);
		cubeGrid.calculateExposure(di, dj, dk);
		cubeGrid.diffuseExposure(diffuse, cutoff);
		cubeGrid.smoothExposure(smooth, iter);
		hexGrid.setExposure(cubeGrid);
	}

	final public void centerGrid() {
		double[] center = hexGrid.getGridCoordinates(I / 2.0 - K / 2.0, J / 2.0 - K / 2.0, centerX, centerY, L,
				(YFLIP ? -1.0 : 1.0) * L);
		
		if(!USEMAP) {
			home.translate((float) (centerX - center[0]), (float) (centerY - (float) center[1]));
		}else {
			map.map(center[0], center[1], center);
			home.translate((float) (centerX - center[0]), (float) (centerY - (float) center[1]));
		}
		
		

	}

	final public void centerGrid(PGraphics pg) {
		double[] center = hexGrid.getGridCoordinates(I / 2.0 - K / 2.0, J / 2.0 - K / 2.0, centerX, centerY, L,
				(YFLIP ? -1.0 : 1.0) * L);
		pg.translate((float) (centerX - center[0]), (float) (centerY - (float) center[1]));

	}

	public void clear(int i, int j, int k) {
		cubeGrid.set(i, j, k, false);
		map();
	}

	public void clear(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < Math.min(blocki, maxI - minI); di++) {
			for (int dj = 0; dj < Math.min(blockj, maxJ - minJ); dj++) {
				for (int dk = 0; dk < Math.min(blockk, maxK - minK); dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1) {
						cubeGrid.set(index, false);
					}
				}
			}
		}
		map();
	}

	public void clearBulkBuffer(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < Math.min(blocki, maxI - minI); di++) {
			for (int dj = 0; dj < Math.min(blockj, maxJ - minJ); dj++) {
				for (int dk = 0; dk < Math.min(blockk, maxK - minK); dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1 || cubeGrid.isEdge(i + di, j + dj, k + dk)) {
						cubeGrid.setBuffer(index, false);
					}
				}
			}
		}
		map();
	}

	void copyOneBlockToBuffer(int si, int sj, int sk, int di, int dj, int dk) {
		int index;
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					index = index(i, j, k);
					if (index > -1) {
						cubeGrid.setBuffer(index, cubeGrid.get(index));
					}
				}
			}
		}
	}

	public void cross(int n, int i, int j, int k) {
		hexGrid.clear();
		cubeGrid.clear();
		DEFER = true;
		for (int r = 0; r < n; r++) {
			int sj = (int) random(J);
			int ej = (int) random(sj, J);
			int si = I / 2 - (int) random(i / 2);
			int ei = (int) random(si, I / 2 + i / 2);
			int sk = K / 2 - (int) random(k / 2);
			int ek = (int) random(sk, K / 2 + k / 2);
			xor(si, sj, sk, ei - si, ej - sj, ek - sk);
		}
		for (int r = 0; r < n; r++) {
			int sj = J / 2 - (int) random(j / 2);
			int ej = (int) random(sj, J / 2 + j / 2);
			int si = (int) random(I);
			int ei = (int) random(si, I);
			int sk = K / 2 - (int) random(k / 2);
			int ek = (int) random(sk, K / 2 + k / 2);
			xor(si, sj, sk, ei - si, ej - sj, ek - sk);
		}
		for (int r = 0; r < n; r++) {
			int sj = J / 2 - (int) random(j / 2);
			int ej = (int) random(sj, J / 2 + j / 2);
			int si = I / 2 - (int) random(i / 2);
			int ei = (int) random(si, I / 2 + i / 2);
			int sk = (int) random(K);
			int ek = (int) random(sk, K);
			xor(si, sj, sk, ei - si, ej - sj, ek - sk);
		}

		DEFER = false;
		map();
	}

	public List<int[]> cubesAtGridPosition(int q, int r) {
		List<int[]> cubeList = new ArrayList<int[]>();
		int stepLimit = Math.min(K, Math.min(I - q, J - r));
		for (int step = 0, i = q, j = r; step < stepLimit; step++, i++, j++) {
			if (i >= 0 && j >= 0)
				cubeList.add(new int[] { i, j, step });
		}
		Collections.reverse(cubeList);
		return cubeList;
	}

	public void edgeAll() {
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (cubeGrid.isEdge(i, j, k)) {
						cubeGrid.setBuffer(i, j, k, true);
					} else {

						cubeGrid.setBuffer(i, j, k, false);
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void edgeBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						edgeOneBlock(i, j, k, di, dj, dk);
					} else {
						copyOneBlockToBuffer(i, j, k, di, dj, dk);
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	void edgeOneBlock(int si, int sj, int sk, int di, int dj, int dk) {
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					if (index(i, j, k) > -1) {
						if (cubeGrid.isEdge(i, j, k)) {
							cubeGrid.setBuffer(i, j, k, true);
						} else {

							cubeGrid.setBuffer(i, j, k, false);
						}
					}
				}
			}
		}
	}

	public void edgePart(int part) {
		DEFER = true;
		int index = 0;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (cubeGrid.getPart(index) == part) {
						if (cubeGrid.isEdge(i, j, k)) {
							cubeGrid.setBuffer(index, true);
						} else {

							cubeGrid.setBuffer(index, false);
						}
					} else {
						cubeGrid.setBuffer(index, cubeGrid.get(index));
					}
					index++;
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void fattenAll() {

		fillBuffer();
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (cubeGrid.get(i, j, k)) {
						for (int di = -1; di <= 1; di++) {
							for (int dj = -1; dj <= 1; dj++) {
								for (int dk = -1; dk <= 1; dk++) {
									cubeGrid.setBuffer(i + di, j + dj, k + dk, true);
								}
							}
						}
					}
				}
			}
		}
		cubeGrid.swap();

		DEFER = false;
		map();

	}

	public void fattenBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						fattenOneBlock(i, j, k, di, dj, dk);
					} else {
						copyOneBlockToBuffer(i, j, k, di, dj, dk);
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	void fattenOneBlock(int si, int sj, int sk, int di, int dj, int dk) {
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					if (cubeGrid.get(i, j, k)) {
						for (int ddi = -1; ddi <= 1; ddi++) {
							for (int ddj = -1; ddj <= 1; ddj++) {
								for (int ddk = -1; ddk <= 1; ddk++) {
									cubeGrid.setBuffer(i + ddi, j + ddj, k + ddk, true);
								}
							}
						}
					}
				}
			}
		}

	}

	public void fill() {
		set(0, 0, 0, I, J, K);
		map();
	}

	void fillBuffer() {
		int id = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					cubeGrid.setBuffer(id, cubeGrid.get(id));
					id++;
				}

			}
		}
	}

	public abstract WB_IsoSystem fromPart(int part, int scaleI, int scaleJ, int scaleK, double L, double centerX,
			double centerY);

	public boolean get(int index) {
		return cubeGrid.get(index);
	}

	public boolean get(int i, int j, int k) {
		return cubeGrid.get(i, j, k);
	}

	final public double[] getCenter() {
		return hexGrid.getGridCoordinates(I / 2.0 - K / 2.0, J / 2.0 - K / 2.0, centerX, centerY, L,
				(YFLIP ? -1.0 : 1.0) * L);

	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public List<WB_IsoColor> getColors() {
		return colorSources;

	}

	public int getColors(int index) {
		return cubeGrid.getColorSourceIndex(index);
	}

	public int getColors(int i, int j, int k) {
		return cubeGrid.getColorSourceIndex(i, j, k);
	}

	public WB_CubeGrid getCubeGrid() {
		return cubeGrid;

	}

	public WB_IsoHexGrid getGrid() {
		return hexGrid;

	}


	public int getI() {
		return I;
	}

	public int getJ() {
		return J;
	}

	public int getK() {
		return K;
	}

	public double getL() {
		return L;
	}

	public int getNumberOfColorss() {
		return colorSources.size();
	}

	public int getNumberOfParts() {
		return numParts;
	}

	public int getNumberOfRegions() {
		return numRegions;
	}

	abstract int getNumberOfTriangles();

	private int getSampledValue(WB_IsoSystem source, int lookupI, int lookupJ, int lookupK, int i, int j, int k,
			int scaleI, int scaleJ, int scaleK) {
		if (lookupI >= 0 && lookupJ >= 0 && lookupK >= 0) {
			return source.index(lookupI, lookupJ, lookupK);
		}
		int si, ei;
		if (lookupI >= 0) {
			si = ei = lookupI;
		} else {
			si = i * scaleI;
			ei = i * scaleI + scaleI - 1;
		}
		int sj, ej;
		if (lookupJ >= 0) {
			sj = ej = lookupJ;
		} else {
			sj = j * scaleJ;
			ej = j * scaleJ + scaleJ - 1;
		}
		int sk, ek;
		if (lookupK >= 0) {
			sk = ek = lookupK;
		} else {
			sk = k * scaleK;
			ek = k * scaleK + scaleK - 1;
		}

		for (int ti = si; ti <= ei; ti++) {
			for (int tj = sj; tj <= ej; tj++) {
				for (int tk = sk; tk <= ek; tk++) {
					if (source.cubeGrid.get(ti, tj, tk))
						return source.index(ti, tj, tk);
				}
			}
		}
		return 0;
	}

	public int getSeed() {
		return seed;

	}

	final public int[] getTriangleAtGridCoordinates(double x, double y) {
		return hexGrid.getTriangleAtGridCoordinates(x, y, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
	}

	public void growAll() {
		cubeGrid.clearBuffer();
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (cubeGrid.get(i, j, k)) {
						cubeGrid.setBuffer27Neighborhood(i, j, k, true);
					}

				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void growBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						growOneBlock(i, j, k, di, dj, dk);
					} else {
						copyOneBlockToBuffer(i, j, k, di, dj, dk);
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void growEdgeAll() {
		cubeGrid.clearBuffer();
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (cubeGrid.isEdge(i, j, k)) {
						cubeGrid.setBuffer27Neighborhood(i, j, k, true);
					}

				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void growEdgeBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						growEdgeOneBlock(i, j, k, di, dj, dk);
					} else {
						copyOneBlockToBuffer(i, j, k, di, dj, dk);
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	void growEdgeOneBlock(int si, int sj, int sk, int di, int dj, int dk) {
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					if (index(i, j, k) > -1) {
						if (cubeGrid.isEdge(i, j, k)) {
							cubeGrid.setBuffer27Neighborhood(i, j, k, true);
						} else {

							cubeGrid.setBuffer(i, j, k, false);
						}
					}
				}
			}
		}
	}

	public void growInwardAll() {
		DEFER = true;
		cubeGrid.clearBuffer();
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (cubeGrid.get(i, j, k)) {
						cubeGrid.setBufferInwardNeighborhood(i, j, k, true);
					}

				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void growInwardBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						growInwardOneBlock(i, j, k, di, dj, dk);
					} else {
						copyOneBlockToBuffer(i, j, k, di, dj, dk);
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	void growInwardOneBlock(int si, int sj, int sk, int di, int dj, int dk) {
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					if (index(i, j, k) > -1) {
						if (cubeGrid.get(i, j, k)) {
							cubeGrid.setBufferInwardNeighborhood(i, j, k, true);
						} else {

							cubeGrid.setBuffer(i, j, k, false);
						}
					}
				}
			}
		}
	}

	void growOneBlock(int si, int sj, int sk, int di, int dj, int dk) {
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					if (index(i, j, k) > -1) {
						if (cubeGrid.get(i, j, k)) {
							cubeGrid.setBuffer27Neighborhood(i, j, k, true);
						} else {

							cubeGrid.setBuffer(i, j, k, false);
						}
					}
				}
			}
		}
	}

	public void growOutwardAll() {
		cubeGrid.clearBuffer();
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (cubeGrid.get(i, j, k)) {
						cubeGrid.setBufferOutwardNeighborhood(i, j, k, true);
					}

				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void growOutwardBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						growOutwardOneBlock(i, j, k, di, dj, dk);
					} else {
						copyOneBlockToBuffer(i, j, k, di, dj, dk);
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	void growOutwardOneBlock(int si, int sj, int sk, int di, int dj, int dk) {
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					if (index(i, j, k) > -1) {
						if (cubeGrid.get(i, j, k)) {
							cubeGrid.setBufferOutwardNeighborhood(i, j, k, true);
						} else {

							cubeGrid.setBuffer(i, j, k, false);
						}
					}
				}
			}
		}
	}

	int index(final int i, final int j, final int k) {

		if (i > -1 && j > -1 && k > -1 && i < I && j < J && k < K) {
			return k + j * K + i * JK;
		} else {
			return -1;
		}
	}

	public void invertAll() {
		not(0, 0, 0, I, J, K);
		map();
	}

	public void invertBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						not(i, j, k, di, dj, dk);
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	boolean isCenterx(int x, int v) {
		return (x % 2 == 0) ? (v % x) == x / 2 - 1 || (v % x) == x / 2 : (v % x) == x / 2;
	}

	public boolean isDeferred() {
		return DEFER || GLOBALDEFER;
	}

	boolean isDownx(int x, int v) {
		return (x % 2 == 0) ? (v % x) < x / 2 - 1 : (v % x) < x / 2;
	}

	boolean isEven(int v) {
		return (v % 2) == 0;
	}

	boolean isUpx(int x, int v) {
		return (v % x) > x / 2;
	}

	public void layerIAll(int on, int off) {
		DEFER = true;
		for (int i = minI + on; i < maxI; i += on + off) {
			set(i, 0, 0, off, J, K);
		}
		DEFER = false;
		map();
	}

	public void layerIBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[I];
		for (int i = minI; i < maxI; i += on + off) {
			for (int l = 0; l < on; l++) {
				if (i + l < I) {
					layer[i + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							if (i + ci < I && !layer[i + ci]) {
								set(i + ci, j, k, 1, dj, dk);
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void layerJAll(int on, int off) {
		DEFER = true;
		for (int j = minJ + on; j < maxJ; j += on + off) {
			set(0, j, 0, I, off, K);
		}
		DEFER = false;
		map();
	}

	public void layerJBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[J];
		for (int j = minJ; j < maxJ; j += on + off) {
			for (int l = 0; l < on; l++) {
				if (j + l < J) {
					layer[j + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int cj = 0; cj < dj; cj++) {
							if (j + cj < J && !layer[j + cj]) {
								set(i, j + cj, k, di, 1, dk);
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void layerKAll(int on, int off) {
		DEFER = true;
		for (int k = minK + on; k < maxK; k += on + off) {
			set(0, 0, k, I, J, off);
		}
		DEFER = false;
		map();
	}

	public void layerKBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[K];
		for (int k = minK; k < maxK; k += on + off) {
			for (int l = 0; l < on; l++) {
				if (k + l < K) {
					layer[k + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ck = 0; ck < dk; ck++) {
							if (k + ck < K && !layer[k + ck]) {
								set(i, j, k + ck, di, dj, 1);
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	final void map() {
		if (!isDeferred()) {
			refresh();
		}
	}

	public abstract void mapVoxelsToHexGrid();

	public abstract void mapVoxelsToHexGrid(int minZ, int maxZ);

	public void maze(int x) {
		if (x == 1) {
			fill();
			return;
		}
		if (x == 2) {
			maze2();
			return;
		}
		int MI = I / x;
		int MJ = J / x;
		int MK = K / x;
		Maze maze = new Maze(MI, MJ, MK, home);
		hexGrid.clear();
		cubeGrid.clear();
		DEFER = true;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					if (isCenterx(x, i) && isCenterx(x, j) && isCenterx(x, k)
							&& maze.getCell(i / x, j / x, k / x).links.size() > 0) {
						or(i, j, k);
					} else if (isDownx(x, i) && isCenterx(x, j) && isCenterx(x, k)) {
						if (maze.isLinked(i / x - 1, j / x, k / x, i / x, j / x, k / x))
							or(i, j, k);
					} else if (isUpx(x, i) && isCenterx(x, j) && isCenterx(x, k)) {
						if (maze.isLinked(i / x, j / x, k / x, i / x + 1, j / x, k / x))
							or(i, j, k);
					} else if (isDownx(x, j) && isCenterx(x, i) && isCenterx(x, k)) {
						if (maze.isLinked(i / x, j / x - 1, k / x, i / x, j / x, k / x))
							or(i, j, k);
					} else if (isUpx(x, j) && isCenterx(x, i) && isCenterx(x, k)) {
						if (maze.isLinked(i / x, j / x, k / x, i / x, j / x + 1, k / x))
							or(i, j, k);
					} else if (isDownx(x, k) && isCenterx(x, i) && isCenterx(x, j)) {
						if (maze.isLinked(i / x, j / x, k / x - 1, i / x, j / x, k / x))
							or(i, j, k);
					} else if (isUpx(x, k) && isCenterx(x, i) && isCenterx(x, j)) {
						if (maze.isLinked(i / x, j / x, k / x, i / x, j / x, k / x + 1))
							or(i, j, k);
					}

				}
			}
		}
		DEFER = false;
		map();
	}

	void maze2() {
		int MI = (I + 1) / 2;
		int MJ = (J + 1) / 2;
		int MK = (K + 1) / 2;
		Maze maze = new Maze(MI, MJ, MK, home);
		hexGrid.clear();
		cubeGrid.clear();
		DEFER = true;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					if (isEven(i) && isEven(j) && isEven(k)) {
						or(i, j, k);
					} else if (!isEven(i) && isEven(j) && isEven(k)) {
						if (maze.isLinked((i - 1) / 2, j / 2, k / 2, (i + 1) / 2, j / 2, k / 2))
							or(i, j, k);
					} else if (isEven(i) && !isEven(j) && isEven(k)) {
						if (maze.isLinked(i / 2, (j - 1) / 2, k / 2, i / 2, (j + 1) / 2, k / 2))
							or(i, j, k);
					} else if (isEven(i) && isEven(j) && !isEven(k)) {
						if (maze.isLinked(i / 2, j / 2, (k - 1) / 2, i / 2, j / 2, (k + 1) / 2))
							or(i, j, k);
					}

				}
			}
		}

		DEFER = false;
		map();
	}

	public void moveJAll(int dj) {
		DEFER = true;
		if (dj > 0) {
			for (int j = J - 1; j >= 0; j--) {
				for (int i = 0; i < I; i++) {
					for (int k = 0; k < K; k++) {
						if (j < dj || !get(i, j - dj, k)) {
							clear(i, j, k);
						} else {
							set(i, j, k);

						}
					}
				}
			}

		}
		DEFER = false;
		map();

	}

	public void not(int i, int j, int k) {
		cubeGrid.not(i, j, k);
		map();
	}

	public void not(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < Math.min(blocki, maxI - minI); di++) {
			for (int dj = 0; dj < Math.min(blockj, maxJ - minJ); dj++) {
				for (int dk = 0; dk < Math.min(blockk, maxK - minK); dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1) {
						cubeGrid.not(index);
					}
				}
			}
		}
		map();
	}

	public void notRandomWalk(int size, int step, int starti, int startj, int startk) {
		int si = starti;
		int sj = startj;
		int sk = startk;
		int[] dir;
		while (si >= minI && si - size < maxI && sj >= minJ && sj - size < maxJ && sk >= minK && sk - size < maxK) {
			dir = randomDirection();
			for (int s = 0; s < step; s++) {
				clear(si, sj, sk, size, size, size);
				si += size * dir[0];
				sj += size * dir[1];
				sk += size * dir[2];
			}
		}

		map();

	}

	public void notRandomWalk(int size, int step, int starti, int startj, int startk, int si, int sj, int sk, int ei,
			int ej, int ek) {
		int ci = starti;
		int cj = startj;
		int ck = startk;
		int[] dir;
		while (ci >= Math.max(minI, si) && ci - size < Math.min(maxI, ei) && cj >= Math.max(minJ, sj)
				&& cj - size < Math.min(maxJ, ej) && ck >= Math.max(minK, sk) && ck - size < Math.min(maxK, ek)) {
			dir = randomDirection();
			for (int s = 0; s < step; s++) {
				clear(ci, cj, ck, size, size, size);
				ci += size * dir[0];
				cj += size * dir[1];
				ck += size * dir[2];
			}
		}

		map();

	}

	public float occupation(int i, int j, int k, int di, int dj, int dk) {
		float occ = 0;
		for (int li = i; li < i + di; li++) {
			for (int lj = j; lj < j + dj; lj++) {
				for (int lk = k; lk < k + dk; lk++) {
					occ += (get(li, lj, lk) ? 1 : 0);
				}
			}
		}
		return occ / (di * dj * dk);

	}

	public void openIAll() {
		for (int j = minJ; j < maxJ; j++) {
			for (int k = minK; k < maxK; k++) {
				openLeftI(j, k);
				openRightI(j, k);
			}
		}
		map();
	}

	public void openIBlocks(double chance, int dj, int dk) {
		for (int j = minJ; j < maxJ; j += dj) {
			for (int k = minK; k < maxK; k += dk) {
				if (random(1.0) < chance) {
					for (int cj = 0; cj < dj; cj++) {
						for (int ck = 0; ck < dk; ck++) {
							if (j + cj < J && k + ck < K) {
								openLeftI(j + cj, k + ck);
								openRightI(j + cj, k + ck);
							}
						}
					}
				}
			}
		}
		map();
	}

	public void openJAll() {
		for (int i = minI; i < maxI; i++) {
			for (int k = minK; k < maxK; k++) {
				openLeftJ(i, k);
				openRightJ(i, k);
			}
		}
		map();
	}

	public void openJBlocks(double chance, int di, int dk) {
		for (int i = minI; i < maxI; i += di) {
			for (int k = minK; k < maxK; k += dk) {
				if (random(1.0) < chance) {
					for (int ci = 0; ci < di; ci++) {
						for (int ck = 0; ck < dk; ck++) {
							if (i + ci < I && k + ck < K) {
								openLeftJ(i + ci, k + ck);
								openRightJ(i + ci, k + ck);
							}
						}
					}
				}
			}
		}
		map();
	}

	public void openKAll() {
		for (int j = minJ; j < maxJ; j++) {
			for (int i = minI; i < maxI; i++) {
				openLeftK(i, j);
				openRightK(i, j);
			}
		}
		map();
	}

	public void openKBlocks(double chance, int di, int dj) {
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				if (random(1.0) < chance) {
					for (int ci = 0; ci < di; ci++) {
						for (int cj = 0; cj < dj; cj++) {
							if (j + cj < J && i + ci < I) {
								openLeftK(i + ci, j + cj);
								openRightK(i + ci, j + cj);
							}
						}
					}
				}
			}
		}
		map();
	}

	void openLeftI(int j, int k) {
		int i = 0;
		while (i < I && !cubeGrid.get(i, j, k)) {
			i++;
		}
		if (i == I)
			return;
		if (i == I - 1 || !cubeGrid.get(i + 1, j, k))
			cubeGrid.set(i, j, k, false);

	}

	void openLeftJ(int i, int k) {
		int j = 0;
		while (index(i, j, k) > -1 && !cubeGrid.get(i, j, k)) {
			j++;
		}
		if (j == J)
			return;
		if (j == J - 1 || !cubeGrid.get(i, j + 1, k))
			cubeGrid.set(i, j, k, false);

	}

	void openLeftK(int i, int j) {
		int k = 0;
		while (k < K && !cubeGrid.get(i, j, k)) {
			k++;
		}
		if (k == K)
			return;
		if (k == K - 1 || !cubeGrid.get(i, j, k + 1))
			cubeGrid.set(i, j, k, false);

	}

	void openRightI(int j, int k) {
		int i = I - 1;
		while (i >= 0 && !cubeGrid.get(i, j, k)) {
			i--;
		}
		if (i == -1)
			return;
		if (i == 0 || !cubeGrid.get(i - 1, j, k))
			cubeGrid.set(i, j, k, false);

	}

	void openRightJ(int i, int k) {
		int j = J - 1;
		while (j >= 0 && !cubeGrid.get(i, j, k)) {
			j--;
		}
		if (j == -1)
			return;
		if (j == 0 || !cubeGrid.get(i, j - 1, k))
			cubeGrid.set(i, j, k, false);

	}

	void openRightK(int i, int j) {
		int k = K - 1;
		while (k >= 0 && !cubeGrid.get(i, j, k)) {
			k--;
		}
		if (k == -1)
			return;
		if (k == 0 || !cubeGrid.get(i, j, k - 1))
			cubeGrid.set(i, j, k, false);

	}

	public void or(int i, int j, int k) {
		cubeGrid.or(i, j, k, true);
		map();
	}

	public void or(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < Math.min(blocki, maxI - minI); di++) {
			for (int dj = 0; dj < Math.min(blockj, maxJ - minJ); dj++) {
				for (int dk = 0; dk < Math.min(blockk, maxK - minK); dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1) {
						cubeGrid.or(index, true);
					}
				}
			}
		}
		map();
	}

	public void or(WB_IsoSystem iso) {
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (iso.get(i, j, k)) {
						set(i, j, k);
					}
				}
			}
		}
		DEFER = false;
		map();

	}

	public void or(WB_IsoSystem iso, int offsetI, int offsetJ, int offsetK) {
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (iso.get(i, j, k)) {
						set(i + offsetI, j + offsetJ, k + offsetK);
					}
				}
			}
		}
		DEFER = false;
		map();

	}

	public void perforateIAll(int stepj, int stepk, int rj, int rk) {
		DEFER = true;
		for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				clear(0, j - rj, k - rk, I, 2 * rj, 2 * rk);
			}
		}
		DEFER = false;
		map();
	}

	public void perforateIBlocks(float chance, int stepj, int stepk, int rj, int rk, int di, int dj, int dk) {
		boolean[][] column = new boolean[J][K];
		for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				for (int cj = -rj; cj <= rj; cj++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K)
							column[j + cj][k + ck] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int cj = 0; cj < dj; cj++) {
							for (int ck = 0; ck < dk; ck++) {
								if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K && column[j + cj][k + ck]) {
									clear(i, j + cj, k + ck, di, 1, 1);
								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void perforateJAll(int stepi, int stepk, int ri, int rk) {
		DEFER = true;
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				clear(i - ri, 0, k - rk, 2 * ri, J, 2 * rk);
			}
		}
		DEFER = false;
		map();
	}

	public void perforateJBlocks(float chance, int stepi, int stepk, int ri, int rk, int di, int dj, int dk) {
		boolean[][] column = new boolean[I][K];
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K)
							column[i + ci][k + ck] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							for (int ck = 0; ck < dk; ck++) {
								if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K && column[i + ci][k + ck]) {
									clear(i + ci, j, k + ck, 1, dj, 1);
								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void perforateKAll(int stepi, int stepj, int ri, int rj) {
		DEFER = true;
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
				clear(i - ri, j - rj, 0, 2 * ri, 2 * rj, K);
			}
		}
		DEFER = false;
		map();
	}

	public void perforateKBlocks(float chance, int stepi, int stepj, int ri, int rj, int di, int dj, int dk) {
		boolean[][] column = new boolean[I][J];
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int cj = -rj; cj <= rj; cj++) {
						if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J)
							column[i + ci][j + cj] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							for (int cj = 0; cj < dj; cj++) {
								if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J && column[i + ci][j + cj]) {
									clear(i + ci, j + cj, k, 1, 1, dk);
								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void pushI(int sj, int sk, int dj, int dk, int pushi) {
		DEFER = true;
		if (pushi > 0) {
			for (int k = sk; k < sk + dk; k++) {
				for (int j = sj; j < sj + dj; j++) {
					for (int i = 0; i < I; i++) {
						if (get(i + pushi, j, k)) {
							set(i, j, k);
						} else {
							clear(i, j, k);
						}
					}
				}

			}
		} else if (pushi < 0) {
			for (int k = sk; k < sk + dk; k++) {
				for (int j = sj; j < sj + dj; j++) {
					for (int i = I - 1; i >= 0; i--) {
						if (get(i + pushi, j, k)) {
							set(i, j, k);
						} else {
							clear(i, j, k);
						}
					}
				}

			}
		} else

			DEFER = false;
		map();
	}

	public void pushJ(int si, int sk, int di, int dk, int pushj) {
		DEFER = true;
		if (pushj > 0) {
			for (int i = si; i < si + di; i++) {
				for (int k = sk; k < sk + dk; k++) {
					for (int j = 0; j < J; j++) {
						if (get(i, j + pushj, k)) {
							set(i, j, k);
						} else {
							clear(i, j, k);
						}
					}
				}

			}
		} else if (pushj < 0) {
			for (int i = si; i < si + di; i++) {
				for (int k = sk; k < sk + dk; k++) {
					for (int j = J - 1; j >= 0; j--) {
						if (get(i, j + pushj, k)) {
							set(i, j, k);
						} else {
							clear(i, j, k);
						}
					}
				}

			}
		} else

			DEFER = false;
		map();
	}

	public void pushK(int si, int sj, int di, int dj, int pushk) {
		DEFER = true;
		if (pushk > 0) {
			for (int i = si; i < si + di; i++) {
				for (int j = sj; j < sj + dj; j++) {
					for (int k = 0; k < K; k++) {
						if (get(i, j, k + pushk)) {
							set(i, j, k);
						} else {
							clear(i, j, k);
						}
					}
				}

			}
		} else if (pushk < 0) {
			for (int i = si; i < si + di; i++) {
				for (int j = sj; j < sj + dj; j++) {
					for (int k = K - 1; k >= 0; k--) {
						if (get(i, j, k + pushk)) {
							set(i, j, k);
						} else {
							clear(i, j, k);
						}
					}
				}

			}
		} else

			DEFER = false;
		map();
	}

	final double random(double v) {
		return randomGen.nextDouble() * v;
	}

	final double random(double v, double w) {
		return v + randomGen.nextDouble() * (w - v);
	}

	int[] randomDirection() {
		switch ((int) home.random(6.0f)) {
		case 0:
			return new int[] { -1, 0, 0 };

		case 1:
			return new int[] { 1, 0, 0 };

		case 2:
			return new int[] { 0, -1, 0 };

		case 3:
			return new int[] { 0, 1, 0 };

		case 4:
			return new int[] { 0, 0, -1 };

		case 5:
			return new int[] { 0, 0, 1 };
		default:
			return new int[] { 0, 1, 0 };
		}
	}

	public void randomWalk(int size, int step, int starti, int startj, int startk) {
		int si = starti;
		int sj = startj;
		int sk = startk;
		int[] dir;
		while (si >= minI && si - size < maxI && sj >= minJ && sj - size < maxJ && sk >= minK && sk - size < maxK) {
			dir = randomDirection();
			for (int s = 0; s < step; s++) {
				set(si, sj, sk, size, size, size);
				si += size * dir[0];
				sj += size * dir[1];
				sk += size * dir[2];
			}
		}

		map();

	}

	public void randomWalk(int size, int step, int starti, int startj, int startk, int si, int sj, int sk, int ei,
			int ej, int ek) {
		int ci = starti;
		int cj = startj;
		int ck = startk;
		int[] dir;
		while (ci >= Math.max(minI, si) && ci - size < Math.min(maxI, ei) && cj >= Math.max(minJ, sj)
				&& cj - size < Math.min(maxJ, ej) && ck >= Math.max(minK, sk) && ck - size < Math.min(maxK, ek)) {
			dir = randomDirection();
			for (int s = 0; s < step; s++) {
				set(ci, cj, ck, size, size, size);
				ci += size * dir[0];
				cj += size * dir[1];
				ck += size * dir[2];
			}
		}

		map();

	}
	
	public void randomWalk(int size, int step, int starti, int startj, int startk, int steps) {
		int si = starti;
		int sj = startj;
		int sk = startk;
		int[] dir;
		for (int ss=0;ss<steps;ss++) {
			dir = randomDirection();
			for (int s = 0; s < step; s++) {
				set(si, sj, sk, size, size, size);
				si += size * dir[0];
				sj += size * dir[1];
				sk += size * dir[2];
			}
		}

		map();

	}

	
	public void refresh() {
		System.out.println("Mapping...");
		mapVoxelsToHexGrid();

		cubeGrid.reset();
		System.out.println("Labelling parts.");
		numParts = cubeGrid.labelParts();
		System.out.println("Setting parts.");
		hexGrid.setParts(cubeGrid);
		numRegions = 1;
		System.out.println("Setting regions.");
		numRegions = ((this.getNumberOfTriangles() == 6) ? 3 : 10) * numParts;
		hexGrid.collectRegions();
		System.out.println("Setting lines.");
		hexGrid.collectLines(!USEMAP);
		System.out.println("Mapping done.");

	}

	public void refresh(int minZ, int maxZ) {
		System.out.println("Mapping...");
		mapVoxelsToHexGrid(minZ, maxZ);
		cubeGrid.reset();
		System.out.println("Labelling parts.");
		numParts = cubeGrid.labelParts();
		System.out.println("Setting parts.");
		hexGrid.setParts(cubeGrid);
		numRegions = 1;
		System.out.println("Setting regions.");
		numRegions = ((this.getNumberOfTriangles() == 6) ? 3 : 10) * numParts;
		hexGrid.collectRegions();
		System.out.println("Setting lines.");
		hexGrid.collectLines(!USEMAP);
		System.out.println("Mapping done.");

	}

	public void repeat(float chance, int si, int sj, int sk, int di, int dj, int dk) {
		DEFER = true;
		boolean[][][] pattern = new boolean[di][dj][dk];
		for (int i = 0; i < di; i++) {
			for (int j = 0; j < dj; j++) {
				for (int k = 0; k < dk; k++) {
					pattern[i][j][k] = get(si + i, sj + j, sk + k);

				}
			}
		}

		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ii = 0; ii < di; ii++) {
							for (int jj = 0; jj < dj; jj++) {
								for (int kk = 0; kk < dk; kk++) {
									if (pattern[ii][jj][kk]) {
										set(i + ii, j + jj, k + kk);
									} else {
										clear(i + ii, j + jj, k + kk);
									}

								}
							}
						}
					}

				}
			}
		}
		DEFER = false;
		map();
	}

	public void repeat(int si, int sj, int sk, int di, int dj, int dk) {
		DEFER = true;
		boolean[][][] pattern = new boolean[di][dj][dk];
		for (int i = 0; i < di; i++) {
			for (int j = 0; j < dj; j++) {
				for (int k = 0; k < dk; k++) {
					pattern[i][j][k] = get(si + i, sj + j, sk + k);

				}
			}
		}

		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					for (int ii = 0; ii < di; ii++) {
						for (int jj = 0; jj < dj; jj++) {
							for (int kk = 0; kk < dk; kk++) {
								if (pattern[ii][jj][kk]) {
									set(i + ii, j + jj, k + kk);
								} else {
									clear(i + ii, j + jj, k + kk);
								}

							}
						}
					}

				}
			}
		}
		DEFER = false;
		map();
	}

	public void resetRange() {
		this.minI = 0;
		this.minJ = 0;
		this.minK = 0;
		this.maxI = I;
		this.maxJ = J;
		this.maxK = K;

	}

	final public void resetRNG() {
		randomGen.restoreState(state);
	}

	public abstract WB_IsoSystem rotateICC();

	public abstract WB_IsoSystem rotateICW();

	public abstract WB_IsoSystem rotateJCC();

	public abstract WB_IsoSystem rotateJCW();

	public abstract WB_IsoSystem rotateKCC();

	public abstract WB_IsoSystem rotateKCW();

	public void set(int i, int j, int k) {
		cubeGrid.set(i, j, k, true);
		map();
	}

	public void set(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < Math.min(blocki, maxI - minI); di++) {
			for (int dj = 0; dj < Math.min(blockj, maxJ - minJ); dj++) {
				for (int dk = 0; dk < Math.min(blockk, maxK - minK); dk++) {
					int index = index(minI + i + di, minJ + j + dj, minK + k + dk);
					if (index > -1) {
						cubeGrid.set(index, true);
					}
				}
			}
		}
		map();
	}

	public void set27Neighborhood(int i, int j, int k) {

		cubeGrid.set27Neighborhood(i, j, k, true);
		map();
	}

	public void set6Neighborhood(int i, int j, int k) {
		cubeGrid.set6Neighborhood(i, j, k, true);
		map();
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	public void setColors(int i, int j, int k, int colors) {
		cubeGrid.setColorSourceIndex(i, j, k, colors);
		map();
	}

	public void setColors(int i, int j, int k, int blocki, int blockj, int blockk, int colors) {
		for (int di = 0; di < Math.min(blocki, maxI - minI); di++) {
			for (int dj = 0; dj < Math.min(blockj, maxJ - minJ); dj++) {
				for (int dk = 0; dk < Math.min(blockk, maxK - minK); dk++) {
					int index = index(minI + i + di, minJ + j + dj, minK + k + dk);
					if (index > -1) {
						cubeGrid.setColorSourceIndex(index, colors);
					}
				}
			}
		}
		map();
	}

	public void setColors(int i, int j, int k, int blocki, int blockj, int blockk, int[] colors) {
		WB_IsoColor colorSource = new WB_IsoPalette(colors);
		int pal = this.colorSources.indexOf(colorSource);
		if (pal == -1) {
			this.colorSources.add(colorSource);
			pal = this.colorSources.size() - 1;
		}

		for (int di = 0; di < Math.min(blocki, maxI - minI); di++) {
			for (int dj = 0; dj < Math.min(blockj, maxJ - minJ); dj++) {
				for (int dk = 0; dk < Math.min(blockk, maxK - minK); dk++) {
					int index = index(minI + i + di, minJ + j + dj, minK + k + dk);
					if (index > -1) {
						cubeGrid.setColorSourceIndex(index, pal);
					}
				}
			}
		}
		map();
	}

	public void setColors(int i, int j, int k, int blocki, int blockj, int blockk, WB_IsoColor colors) {

		int pal = this.colorSources.indexOf(colors);
		if (pal == -1) {
			this.colorSources.add(colors);
			pal = this.colorSources.size() - 1;
		}

		for (int di = 0; di < Math.min(blocki, maxI - minI); di++) {
			for (int dj = 0; dj < Math.min(blockj, maxJ - minJ); dj++) {
				for (int dk = 0; dk < Math.min(blockk, maxK - minK); dk++) {
					int index = index(minI + i + di, minJ + j + dj, minK + k + dk);
					if (index > -1) {
						cubeGrid.setColorSourceIndex(index, pal);
					}
				}
			}
		}
		map();
	}

	public void setColors(int i, int j, int k, int[] colors) {
		WB_IsoColor colorSource = new WB_IsoPalette(colors);
		int pal = this.colorSources.indexOf(colorSource);
		if (pal == -1) {
			this.colorSources.add(colorSource);
			pal = this.colorSources.size() - 1;
		}
		cubeGrid.setColorSourceIndex(i, j, k, pal);
		map();
	}

	public void setColorSource(int i, int[] colors) {
		if (colors.length == 0 || colors.length % 3 != 0)
			throw new IllegalArgumentException("Number of colors should be at least 3 or a higher multiple of 3.");

		this.colorSources.set(i, new WB_IsoPalette(colors[i * 3], colors[i * 3 + 1], colors[i * 3 + 2]));

	}

	public void setColorSource(int i, WB_IsoColor colorSource) {
		this.colorSources.set(i, colorSource);

	}

	public void setDeferred(boolean b) {
		GLOBALDEFER = b;
	}

	abstract void setGrid();

	public void setL(double l) {
		L = l;
	}

	public void setMap(WB_Map map) {
		this.map = map;
	}

	public void setPattern(boolean[][][] pattern, int i, int j, int k, int scaleI, int scaleJ, int scaleK,
			boolean flipI, boolean flipJ, boolean flipK) {
		int I = pattern.length;
		if (I == 0)
			return;
		int J = pattern[0].length;
		if (J == 0)
			return;
		int K = pattern[0][0].length;
		if (K == 0)
			return;

		for (int ci = 0; ci < I; ci++) {
			for (int cj = 0; cj < J; cj++) {
				for (int ck = 0; ck < K; ck++) {
					if (pattern[ci][cj][ck])
						set(i + (flipI ? I - 1 - ci : ci) * scaleI, j + (flipJ ? J - 1 - cj : cj) * scaleJ,
								k + (flipK ? K - 1 - ck : ck) * scaleK, scaleI, scaleJ, scaleK);
				}
			}
		}
	}

	public void setPattern(WB_IsoSystem pattern, int i, int j, int k, int scaleI, int scaleJ, int scaleK, boolean flipI,
			boolean flipJ, boolean flipK) {
		int I = pattern.I;
		if (I == 0)
			return;
		int J = pattern.J;
		if (J == 0)
			return;
		int K = pattern.K;
		if (K == 0)
			return;
		int index = 0;
		for (int ci = 0; ci < I; ci++) {
			for (int cj = 0; cj < J; cj++) {
				for (int ck = 0; ck < K; ck++) {
					int pal = 0;
					if (pattern.get(index)) {

						pal = addColorSource(pattern.colorSources.get(pattern.getColors(index)));
						set(i + (flipI ? I - 1 - ci : ci) * scaleI, j + (flipJ ? J - 1 - cj : cj) * scaleJ,
								k + (flipK ? K - 1 - ck : ck) * scaleK, scaleI, scaleJ, scaleK);
						setColors(i + (flipI ? I - 1 - ci : ci) * scaleI, j + (flipJ ? J - 1 - cj : cj) * scaleJ,
								k + (flipK ? K - 1 - ck : ck) * scaleK, scaleI, scaleJ, scaleK, pal);
					}
					index++;
				}
			}
		}
	}

	public void setPatternBuffer(WB_IsoSystem pattern, int i, int j, int k, int scaleI, int scaleJ, int scaleK,
			boolean flipI, boolean flipJ, boolean flipK) {
		int I = pattern.I;
		if (I == 0)
			return;
		int J = pattern.J;
		if (J == 0)
			return;
		int K = pattern.K;
		if (K == 0)
			return;

		int index = 0;
		for (int ci = 0; ci < I; ci++) {
			for (int cj = 0; cj < J; cj++) {
				for (int ck = 0; ck < K; ck++) {
					int pal = 0;
					if (pattern.get(index)) {
						pal = addColorSource(pattern.colorSources.get(pattern.getColors(index)));
						set(i + (flipI ? I - 1 - ci : ci) * scaleI, j + (flipJ ? J - 1 - cj : cj) * scaleJ,
								k + (flipK ? K - 1 - ck : ck) * scaleK, scaleI, scaleJ, scaleK);
						setColors(i + (flipI ? I - 1 - ci : ci) * scaleI, j + (flipJ ? J - 1 - cj : cj) * scaleJ,
								k + (flipK ? K - 1 - ck : ck) * scaleK, scaleI, scaleJ, scaleK, pal);

						if (ck < K - 1 && !pattern.get(index + 1)) {
							clear(i + (flipI ? I - 1 - ci : ci) * scaleI, j + (flipJ ? J - 1 - cj : cj) * scaleJ,
									k + (flipK ? K - 1 - (ck + 1) : ck + 1) * scaleK, scaleI, scaleJ, scaleK);
							if (cj > 0 && !pattern.get(index - K + 1)) {
								clear(i + (flipI ? I - 1 - ci : ci) * scaleI,
										j + (flipJ ? J - 1 - (cj - 1) : cj - 1) * scaleJ,
										k + (flipK ? K - 1 - (ck + 1) : ck + 1) * scaleK, scaleI, scaleJ, scaleK);
								if (ci > 0 && !pattern.get(index - J * K - K + 1)) {
									clear(i + (flipI ? I - 1 - (ci - 1) : ci - 1) * scaleI,
											j + (flipJ ? J - 1 - (cj - 1) : cj - 1) * scaleJ,
											k + (flipK ? K - 1 - (ck + 1) : ck + 1) * scaleK, scaleI, scaleJ, scaleK);
								}
								if (ci < I - 1 && !pattern.get(index + J * K - K + 1)) {
									clear(i + (flipI ? I - 1 - (ci + 1) : ci + 1) * scaleI,
											j + (flipJ ? J - 1 - (cj - 1) : cj - 1) * scaleJ,
											k + (flipK ? K - 1 - (ck + 1) : ck + 1) * scaleK, scaleI, scaleJ, scaleK);
								}
							}
							if (cj > J - 1 && !pattern.get(index + K + 1)) {
								clear(i + (flipI ? I - 1 - ci : ci) * scaleI,
										j + (flipJ ? J - 1 - (cj + 1) : cj + 1) * scaleJ,
										k + (flipK ? K - 1 - (ck + 1) : ck + 1) * scaleK, scaleI, scaleJ, scaleK);

								if (ci > 0 && !pattern.get(index - J * K + K + 1)) {
									clear(i + (flipI ? I - 1 - (ci - 1) : ci - 1) * scaleI,
											j + (flipJ ? J - 1 - (cj + 1) : cj + 1) * scaleJ,
											k + (flipK ? K - 1 - (ck + 1) : ck + 1) * scaleK, scaleI, scaleJ, scaleK);
								}
								if (ci < I - 1 && !pattern.get(index + J * K + K + 1)) {
									clear(i + (flipI ? I - 1 - (ci + 1) : ci + 1) * scaleI,
											j + (flipJ ? J - 1 - (cj + 1) : cj + 1) * scaleJ,
											k + (flipK ? K - 1 - (ck + 1) : ck + 1) * scaleK, scaleI, scaleJ, scaleK);
								}
							}
							if (ci > 0 && !pattern.get(index - J * K + 1)) {
								clear(i + (flipI ? I - 1 - (ci - 1) : ci - 1) * scaleI,
										j + (flipJ ? J - 1 - cj : cj) * scaleJ,
										k + (flipK ? K - 1 - (ck + 1) : ck + 1) * scaleK, scaleI, scaleJ, scaleK);
							}
							if (ci < I - 1 && !pattern.get(index + J * K + 1)) {
								clear(i + (flipI ? I - 1 - (ci + 1) : ci + 1) * scaleI,
										j + (flipJ ? J - 1 - cj : cj) * scaleJ,
										k + (flipK ? K - 1 - (ck + 1) : ck + 1) * scaleK, scaleI, scaleJ, scaleK);
							}

						}

						if (ck > 0 && !pattern.get(index - 1)) {
							clear(i + (flipI ? I - 1 - ci : ci) * scaleI, j + (flipJ ? J - 1 - cj : cj) * scaleJ,
									k + (flipK ? K - 1 - (ck - 1) : ck - 1) * scaleK, scaleI, scaleJ, scaleK);

							if (cj > 0 && !pattern.get(index - K - 1)) {
								clear(i + (flipI ? I - 1 - ci : ci) * scaleI,
										j + (flipJ ? J - 1 - (cj - 1) : cj - 1) * scaleJ,
										k + (flipK ? K - 1 - (ck + 1) : ck + 1) * scaleK, scaleI, scaleJ, scaleK);
								if (ci > 0 && !pattern.get(index - J * K - K - 1)) {
									clear(i + (flipI ? I - 1 - (ci - 1) : ci - 1) * scaleI,
											j + (flipJ ? J - 1 - (cj - 1) : cj - 1) * scaleJ,
											k + (flipK ? K - 1 - (ck - 1) : ck - 1) * scaleK, scaleI, scaleJ, scaleK);
								}
								if (ci < I - 1 && !pattern.get(index + J * K - K - 1)) {
									clear(i + (flipI ? I - 1 - (ci + 1) : ci + 1) * scaleI,
											j + (flipJ ? J - 1 - (cj - 1) : cj - 1) * scaleJ,
											k + (flipK ? K - 1 - (ck - 1) : ck - 1) * scaleK, scaleI, scaleJ, scaleK);
								}
							}
							if (cj > J - 1 && !pattern.get(index + K - 1)) {
								clear(i + (flipI ? I - 1 - ci : ci) * scaleI,
										j + (flipJ ? J - 1 - (cj + 1) : cj + 1) * scaleJ,
										k + (flipK ? K - 1 - (ck - 1) : ck - 1) * scaleK, scaleI, scaleJ, scaleK);
								if (ci > 0 && !pattern.get(index - J * K + K - 1)) {
									clear(i + (flipI ? I - 1 - (ci - 1) : ci - 1) * scaleI,
											j + (flipJ ? J - 1 - (cj + 1) : cj + 1) * scaleJ,
											k + (flipK ? K - 1 - (ck - 1) : ck - 1) * scaleK, scaleI, scaleJ, scaleK);
								}
								if (ci < I - 1 && !pattern.get(index + J * K + K - 1)) {
									clear(i + (flipI ? I - 1 - (ci + 1) : ci + 1) * scaleI,
											j + (flipJ ? J - 1 - (cj + 1) : cj + 1) * scaleJ,
											k + (flipK ? K - 1 - (ck - 1) : ck - 1) * scaleK, scaleI, scaleJ, scaleK);
								}
							}
							if (ci > 0 && !pattern.get(index - J * K - 1)) {
								clear(i + (flipI ? I - 1 - (ci - 1) : ci - 1) * scaleI,
										j + (flipJ ? J - 1 - cj : cj) * scaleJ,
										k + (flipK ? K - 1 - (ck - 1) : ck - 1) * scaleK, scaleI, scaleJ, scaleK);
							}
							if (ci < I - 1 && !pattern.get(index + J * K - 1)) {
								clear(i + (flipI ? I - 1 - (ci + 1) : ci + 1) * scaleI,
										j + (flipJ ? J - 1 - cj : cj) * scaleJ,
										k + (flipK ? K - 1 - (ck - 1) : ck - 1) * scaleK, scaleI, scaleJ, scaleK);
							}

						}
						if (cj > 0 && !pattern.get(index - K)) {
							clear(i + (flipI ? I - 1 - ci : ci) * scaleI,
									j + (flipJ ? J - 1 - (cj - 1) : cj - 1) * scaleJ,
									k + (flipK ? K - 1 - ck : ck) * scaleK, scaleI, scaleJ, scaleK);
							if (ci > 0 && !pattern.get(index - J * K - K)) {
								clear(i + (flipI ? I - 1 - (ci - 1) : ci - 1) * scaleI,
										j + (flipJ ? J - 1 - (cj - 1) : cj - 1) * scaleJ,
										k + (flipK ? K - 1 - ck : ck) * scaleK, scaleI, scaleJ, scaleK);
							}
							if (ci < I - 1 && !pattern.get(index + J * K - K)) {
								clear(i + (flipI ? I - 1 - (ci + 1) : ci + 1) * scaleI,
										j + (flipJ ? J - 1 - (cj - 1) : cj - 1) * scaleJ,
										k + (flipK ? K - 1 - ck : ck) * scaleK, scaleI, scaleJ, scaleK);
							}

						}
						if (cj > J - 1 && !pattern.get(index + K)) {
							clear(i + (flipI ? I - 1 - ci : ci) * scaleI,
									j + (flipJ ? J - 1 - (cj + 1) : cj + 1) * scaleJ,
									k + (flipK ? K - 1 - ck : ck) * scaleK, scaleI, scaleJ, scaleK);
							if (ci > 0 && !pattern.get(index - J * K + K)) {
								clear(i + (flipI ? I - 1 - (ci - 1) : ci - 1) * scaleI,
										j + (flipJ ? J - 1 - (cj + 1) : cj + 1) * scaleJ,
										k + (flipK ? K - 1 - ck : ck) * scaleK, scaleI, scaleJ, scaleK);
							}
							if (ci < I - 1 && !pattern.get(index + J * K + K)) {
								clear(i + (flipI ? I - 1 - (ci + 1) : ci + 1) * scaleI,
										j + (flipJ ? J - 1 - (cj + 1) : cj + 1) * scaleJ,
										k + (flipK ? K - 1 - ck : ck) * scaleK, scaleI, scaleJ, scaleK);
							}

						}
						if (ci > 0 && !pattern.get(index - J * K)) {
							clear(i + (flipI ? I - 1 - (ci - 1) : ci - 1) * scaleI,
									j + (flipJ ? J - 1 - cj : cj) * scaleJ, k + (flipK ? K - 1 - ck : ck) * scaleK,
									scaleI, scaleJ, scaleK);
						}
						if (ci < I - 1 && !pattern.get(index + J * K)) {
							clear(i + (flipI ? I - 1 - (ci + 1) : ci + 1) * scaleI,
									j + (flipJ ? J - 1 - cj : cj) * scaleJ, k + (flipK ? K - 1 - ck : ck) * scaleK,
									scaleI, scaleJ, scaleK);
						}
					}

					index++;
				}
			}
		}
	}

	public void setRange(int minI, int minJ, int minK, int maxI, int maxJ, int maxK) {
		this.minI = minI;
		this.minJ = minJ;
		this.minK = minK;
		this.maxI = maxI;
		this.maxJ = maxJ;
		this.maxK = maxK;

	}

	final public void setRNGSeed(long seed) {
		randomGen = RandomSource.create(RandomSource.MT, seed);
		state = randomGen.saveState();
	}

	public void setSeed(int seed) {
		this.seed = seed;
		randomGen = RandomSource.create(RandomSource.MT, seed);

		state = randomGen.saveState();
	}

	public void setUseMap(boolean b) {
		USEMAP = b;
	}

	public void setYFlip(boolean b) {
		YFLIP = b;
	}

	public void simplify(float threshold, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (occupation(i, j, k, di, dj, dk) >= threshold) {

						set(i, j, k, di, dj, dk);

					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void simplify(float threshold, int di, int dj, int dk, int oi, int oj, int ok) {
		DEFER = true;
		for (int i = minI - oi; i < maxI; i += di) {
			for (int j = minJ - oj; j < maxJ; j += dj) {
				for (int k = minK - ok; k < maxK; k += dk) {
					if (occupation(i, j, k, di, dj, dk) >= threshold) {

						set(i, j, k, di, dj, dk);

					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public abstract WB_IsoSystem slice(int sI, int sJ, int sK, int dI, int dJ, int dK, int scaleI, int scaleJ,
			int scaleK, double L, double centerX, double centerY);

	public void sliceIAll(int on, int off) {
		DEFER = true;
		for (int i = minI + on; i < maxI; i += on + off) {
			clear(i, 0, 0, off, J, K);
		}
		DEFER = false;
		map();
	}

	public void sliceIAllBulk(int on, int off) {
		cubeGrid.copyToBuffer();
		DEFER = true;
		for (int i = minI + on; i < maxI; i += on + off) {
			clearBulkBuffer(i, 0, 0, off, J, K);
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void sliceIBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[I];
		for (int i = minI; i < maxI; i += on + off) {
			for (int l = 0; l < on; l++) {
				if (i + l < I) {
					layer[i + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							if (i + ci < I && !layer[i + ci]) {
								clear(i + ci, j, k, 1, dj, dk);
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void sliceIBlocksBulk(float chance, int on, int off, int di, int dj, int dk) {
		cubeGrid.copyToBuffer();
		boolean[] layer = new boolean[I];
		for (int i = minI; i < maxI; i += on + off) {
			for (int l = 0; l < on; l++) {
				if (i + l < I) {
					layer[i + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							if (i + ci < I && !layer[i + ci]) {
								clearBulkBuffer(i + ci, j, k, 1, dj, dk);
							}
						}
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void sliceJAll(int on, int off) {
		DEFER = true;
		for (int j = minJ + on; j < maxJ; j += on + off) {
			clear(0, j, 0, I, off, K);
		}
		DEFER = false;
		map();
	}

	public void sliceJAllBulk(int on, int off) {
		cubeGrid.copyToBuffer();
		DEFER = true;
		for (int j = minJ + on; j < maxJ; j += on + off) {
			clearBulkBuffer(0, j, 0, I, off, K);
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void sliceJBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[J];
		for (int j = minJ; j < maxJ; j += on + off) {
			for (int l = 0; l < on; l++) {
				if (j + l < J) {
					layer[j + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int cj = 0; cj < dj; cj++) {
							if (j + cj < J && !layer[j + cj]) {
								clear(i, j + cj, k, di, 1, dk);
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void sliceJBlocksBulk(float chance, int on, int off, int di, int dj, int dk) {
		cubeGrid.copyToBuffer();
		boolean[] layer = new boolean[J];
		for (int j = minJ; j < maxJ; j += on + off) {
			for (int l = 0; l < on; l++) {
				if (j + l < J) {
					layer[j + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int cj = 0; cj < dj; cj++) {
							if (j + cj < J && !layer[j + cj]) {
								clearBulkBuffer(i, j + cj, k, di, 1, dk);
							}
						}
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void sliceKAll(int on, int off) {
		DEFER = true;
		for (int k = minK + on; k < maxK; k += on + off) {
			clear(0, 0, k, I, J, off);
		}
		DEFER = false;
		map();
	}

	public void sliceKAllBulk(int on, int off) {
		cubeGrid.copyToBuffer();
		DEFER = true;
		for (int k = minK + on; k < maxK; k += on + off) {
			clearBulkBuffer(0, 0, k, I, J, off);
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void sliceKBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[K];
		for (int k = minK; k < maxK; k += on + off) {
			for (int l = 0; l < on; l++) {
				if (k + l < K) {
					layer[k + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ck = 0; ck < dk; ck++) {
							if (k + ck < K && !layer[k + ck]) {
								clear(i, j, k + ck, di, dj, 1);
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void sliceKBlocksBulk(float chance, int on, int off, int di, int dj, int dk) {
		cubeGrid.copyToBuffer();
		boolean[] layer = new boolean[K];
		for (int k = minK; k < maxK; k += on + off) {
			for (int l = 0; l < on; l++) {
				if (k + l < K) {
					layer[k + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ck = 0; ck < dk; ck++) {
							if (k + ck < K && !layer[k + ck]) {
								clearBulkBuffer(i, j, k + ck, di, dj, 1);
							}
						}
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void sub(WB_IsoSystem iso) {
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (iso.get(i, j, k)) {
						clear(i, j, k);
					}
				}
			}
		}
		DEFER = false;
		map();

	}

	public void sub(WB_IsoSystem iso, int offsetI, int offsetJ, int offsetK) {
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (iso.get(i, j, k)) {
						clear(i + offsetI, j + offsetJ, k + offsetK);
					}
				}
			}
		}
		DEFER = false;
		map();

	}

	public void subdivide(double chance, double retain, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						if (random(1.0) >= retain)
							clear(i + di / 2, j, k, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retain)
							clear(i, j, k, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retain)
							clear(i + di / 2, j, k + dk / 2, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retain)
							clear(i, j, k + dk / 2, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retain)
							clear(i + di / 2, j + dj / 2, k, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retain)
							clear(i, j + dj / 2, k, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retain)
							clear(i + di / 2, j + dj / 2, k + dk / 2, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retain)
							clear(i, j + dj / 2, k + dk / 2, di / 2, dj / 2, dk / 2);
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void subdivide(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						clear(i, j, k, di, dj, dk);
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void subdivideGradientJ(double chance0, double chanceJ, double retain0, double retainJ, int di, int dj,
			int dk) {
		DEFER = true;
		double chance, retain, retainh;
		for (int j = minJ; j < maxJ; j += dj) {
			chance = chance0 + j * (chanceJ - chance0) / J;
			retain = retain0 + j * (retainJ - retain0) / J;
			retainh = retain0 + (j + dj / 2) * (retainJ - retain0) / J;
			for (int i = minI; i < maxI; i += di) {

				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						if (random(1.0) >= retain)
							clear(i + di / 2, j, k, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retain)
							clear(i, j, k, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retain)
							clear(i + di / 2, j, k + dk / 2, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retain)
							clear(i, j, k + dk / 2, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retainh)
							clear(i + di / 2, j + dj / 2, k, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retainh)
							clear(i, j + dj / 2, k, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retainh)
							clear(i + di / 2, j + dj / 2, k + dk / 2, di / 2, dj / 2, dk / 2);
						if (random(1.0) >= retainh)
							clear(i, j + dj / 2, k + dk / 2, di / 2, dj / 2, dk / 2);
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void thinAll() {
		fillBuffer();
		DEFER = true;
		boolean val;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					val = cubeGrid.get(i, j, k);
					if (cubeGrid.isIsolated(i, j, k)) {
						val = false;
					}
					if (val && !cubeGrid.isStub(i, j, k) && !cubeGrid.isThinPlate(i, j, k)) {

						for (int di = -1; di <= 1; di++) {
							for (int dj = -1; dj <= 1; dj++) {
								for (int dk = -1; dk <= 1; dk++) {

									if (!cubeGrid.get(i - di, j - dj, k - dk) && cubeGrid.get(i + di, j + dj, k + dk)) {
										val = false;
									}

									if (!val)
										break;
								}
							}
						}

					}

					cubeGrid.setBuffer(i, j, k, val);

				}
			}
		}

		cubeGrid.swap();

		DEFER = false;
		map();
	}

	public void thinBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						thinOneBlock(i, j, k, di, dj, dk);
					} else {
						copyOneBlockToBuffer(i, j, k, di, dj, dk);
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	void thinOneBlock(int si, int sj, int sk, int di, int dj, int dk) {
		boolean val;
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					val = cubeGrid.get(i, j, k);
					if (cubeGrid.isIsolated(i, j, k)) {
						val = false;
					}
					if (val && !cubeGrid.isStub(i, j, k) && !cubeGrid.isThinPlate(i, j, k)) {

						for (int ddi = -1; ddi <= 1; ddi++) {
							for (int ddj = -1; ddj <= 1; ddj++) {
								for (int ddk = -1; ddk <= 1; ddk++) {

									if (!cubeGrid.get(i - ddi, j - ddj, k - ddk)
											&& cubeGrid.get(i + ddi, j + ddj, k + ddk)) {
										val = false;
									}

									if (!val)
										break;
								}
							}
						}

					}

					cubeGrid.setBuffer(i, j, k, val);
				}
			}
		}

	}

	public void tubeIAll(int stepj, int stepk, int rj, int rk) {
		DEFER = true;
		for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				set(0, j - rj, k - rk, I, 2 * rj, 2 * rk);
				clear(0, j - rj + 1, k - rk + 1, I, 2 * rj - 2, 2 * rk - 2);
			}
		}
		DEFER = false;
		map();
	}

	public void tubeIBlocks(float chance, int stepj, int stepk, int rj, int rk, int di, int dj, int dk) {
		int[][] column = new int[J][K];
		for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				for (int cj = -rj; cj <= rj; cj++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K)
							if (Math.abs(cj) == rj || Math.abs(ck) == rk) {
								column[j + cj][k + ck] = 1;
							} else {
								column[j + cj][k + ck] = 2;
							}
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int cj = 0; cj < dj; cj++) {
							for (int ck = 0; ck < dk; ck++) {
								if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K
										&& column[j + cj][k + ck] > 0) {
									if (column[j + cj][k + ck] == 1) {
										set(i, j + cj, k + ck, di, 1, 1);
									} else {
										clear(i, j + cj, k + ck, di, 1, 1);
									}
								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void tubeIBlocks(float chance, int stepj, int stepk, int rj, int rk, int di, int dj, int dk, int colors) {
		int[][] column = new int[J][K];
		for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				for (int cj = -rj; cj <= rj; cj++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K)
							if (Math.abs(cj) == rj || Math.abs(ck) == rk) {
								column[j + cj][k + ck] = 1;
							} else {
								column[j + cj][k + ck] = 2;
							}
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int cj = 0; cj < dj; cj++) {
							for (int ck = 0; ck < dk; ck++) {
								if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K
										&& column[j + cj][k + ck] > 0) {
									if (column[j + cj][k + ck] == 1) {
										set(i, j + cj, k + ck, di, 1, 1);
										setColors(i, j + cj, k + ck, di, 1, 1, colors);
									} else {
										clear(i, j + cj, k + ck, di, 1, 1);
									}
								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void tubeJAll(int stepi, int stepk, int ri, int rk) {
		DEFER = true;
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				set(i - ri, 0, k - rk, 2 * ri, J, 2 * rk);
				clear(i - ri + 1, 0, k - rk + 1, 2 * ri - 2, J, 2 * rk - 2);
			}
		}
		DEFER = false;
		map();
	}

	public void tubeJBlocks(float chance, int stepi, int stepk, int ri, int rk, int di, int dj, int dk) {
		int[][] column = new int[I][K];
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K)
							if (Math.abs(ci) == ri || Math.abs(ck) == rk) {
								column[i + ci][k + ck] = 1;
							} else {
								column[i + ci][k + ck] = 2;
							}

					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							for (int ck = 0; ck < dk; ck++) {
								if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K
										&& column[i + ci][k + ck] > 0) {
									if (column[i + ci][k + ck] == 1) {
										set(i + ci, j, k + ck, 1, dj, 1);
									} else {
										clear(i + ci, j, k + ck, 1, dj, 1);
									}

								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void tubeJBlocks(float chance, int stepi, int stepk, int ri, int rk, int di, int dj, int dk, int colors) {
		int[][] column = new int[I][K];
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int k = minK + stepk / 2; k < maxK; k += stepk) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K)
							if (Math.abs(ci) == ri || Math.abs(ck) == rk) {
								column[i + ci][k + ck] = 1;
							} else {
								column[i + ci][k + ck] = 2;
							}

					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							for (int ck = 0; ck < dk; ck++) {
								if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K
										&& column[i + ci][k + ck] > 0) {
									if (column[i + ci][k + ck] == 1) {
										set(i + ci, j, k + ck, 1, dj, 1);
										setColors(i + ci, j, k + ck, 1, dj, 1, colors);
									} else {
										clear(i + ci, j, k + ck, 1, dj, 1);
									}

								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void tubeKAll(int stepi, int stepj, int ri, int rj) {
		DEFER = true;
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
				set(i - ri, j - rj, 0, 2 * ri, 2 * rj, K);

				clear(i - ri + 1, j - rj + 1, 0, 2 * ri - 2, 2 * rj - 2, K);
			}
		}
		DEFER = false;
		map();
	}

	public void tubeKBlocks(float chance, int stepi, int stepj, int ri, int rj, int di, int dj, int dk) {
		int[][] column = new int[I][J];
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int cj = -rj; cj <= rj; cj++) {
						if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J)
							if (Math.abs(ci) == ri || Math.abs(cj) == rj) {
								column[i + ci][j + cj] = 1;
							} else {
								column[i + ci][j + cj] = 2;
							}
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							for (int cj = 0; cj < dj; cj++) {
								if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J
										&& column[i + ci][j + cj] > 0) {
									if (column[i + ci][j + cj] == 1) {
										set(i + ci, j + cj, k, 1, 1, dk);
									} else {
										clear(i + ci, j + cj, k, 1, 1, dk);
									}

								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void tubeKBlocks(float chance, int stepi, int stepj, int ri, int rj, int di, int dj, int dk, int colors) {
		int[][] column = new int[I][J];
		for (int i = minI + stepi / 2; i < maxI; i += stepi) {
			for (int j = minJ + stepj / 2; j < maxJ; j += stepj) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int cj = -rj; cj <= rj; cj++) {
						if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J)
							if (Math.abs(ci) == ri || Math.abs(cj) == rj) {
								column[i + ci][j + cj] = 1;
							} else {
								column[i + ci][j + cj] = 2;
							}
					}
				}
			}
		}
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						for (int ci = 0; ci < di; ci++) {
							for (int cj = 0; cj < dj; cj++) {
								if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J
										&& column[i + ci][j + cj] > 0) {
									if (column[i + ci][j + cj] == 1) {
										set(i + ci, j + cj, k, 1, 1, dk);
										setColors(i + ci, j + cj, k, 1, 1, dk, colors);
									} else {
										clear(i + ci, j + cj, k, 1, 1, dk);
									}

								}
							}
						}
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void wallAll() {
		DEFER = true;
		for (int i = minI; i < maxI; i++) {
			for (int j = minJ; j < maxJ; j++) {
				for (int k = minK; k < maxK; k++) {
					if (cubeGrid.isWall(i, j, k)) {
						cubeGrid.setBuffer(i, j, k, true);
					} else {

						cubeGrid.setBuffer(i, j, k, false);
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	public void wallBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = minI; i < maxI; i += di) {
			for (int j = minJ; j < maxJ; j += dj) {
				for (int k = minK; k < maxK; k += dk) {
					if (random(1.0) < chance) {
						wallOneBlock(i, j, k, di, dj, dk);
					} else {
						copyOneBlockToBuffer(i, j, k, di, dj, dk);
					}
				}
			}
		}
		cubeGrid.swap();
		DEFER = false;
		map();
	}

	void wallOneBlock(int si, int sj, int sk, int di, int dj, int dk) {
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					if (index(i, j, k) > -1) {
						if (cubeGrid.isWall(i, j, k)) {
							cubeGrid.setBuffer(i, j, k, true);
						} else {

							cubeGrid.setBuffer(i, j, k, false);
						}
					}
				}
			}
		}
	}

	public void xor(int i, int j, int k) {
		cubeGrid.xor(i, j, k, true);
		map();
	}

	public void xor(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < Math.min(blocki, maxI - minI); di++) {
			for (int dj = 0; dj < Math.min(blockj, maxJ - minJ); dj++) {
				for (int dk = 0; dk < Math.min(blockk, maxK - minK); dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1) {
						cubeGrid.xor(index, true);
					}
				}
			}
		}
		map();
	}

	public void xorBlocks(int n) {
		hexGrid.clear();
		cubeGrid.clear();
		DEFER = true;
		for (int r = 0; r < n; r++) {
			int sj = (int) random(J);
			int ej = (int) random(sj, J);
			int si = (int) random(I);
			int ei = (int) random(si, I);
			int sk = (int) random(K);
			int ek = (int) random(sk, K);
			xor(si, sj, sk, ei - si, ej - sj, ek - sk);
		}
		DEFER = false;
		map();
	}

	public void xorBlocks(int n, int is, int js, int ks, int ie, int je, int ke) {
		DEFER = true;
		for (int r = 0; r < n; r++) {
			int sj = (int) random(js, je);
			int ej = (int) random(sj, je);
			int si = (int) random(is, ie);
			int ei = (int) random(si, ie);
			int sk = (int) random(ks, ke);
			int ek = (int) random(sk, ke);
			xor(si, sj, sk, ei - si, ej - sj, ek - sk);
		}
		DEFER = false;
		map();
	}

	double[] coord = new double[] { 0, 0 };

	void point(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy) {
		coord = hexGrid.getGridCoordinates(q, r, ox, oy, sx, sy);
		point(pg, coord[0], coord[1]);

	}

	void point(PApplet pg, double q, double r, double ox, double oy, double sx, double sy) {
		point(pg.g, q, r, ox, oy, sx, sy);
	}

	void circle(PGraphics pg, double q, double r, double ox, double oy, double sx, double sy, double diameter) {
		coord = hexGrid.getGridCoordinates(q, r, ox, oy, sx, sy);
		
		circle(pg, coord[0], coord[1], diameter);

	}

	void circle(PApplet pg, double q, double r, double ox, double oy, double sx, double sy, double diameter) {
		circle(pg.g, q, r, ox, oy, sx, sy, diameter);

	}

	double[] coord2 = new double[] { 0, 0 };

	void line(PGraphics pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx, double sy) {
		coord = hexGrid.getGridCoordinates(q1, r1, ox, oy, sx, sy);
		coord2 = hexGrid.getGridCoordinates(q2, r2, ox, oy, sx, sy);
		line(pg, coord[0], coord[1], coord2[0], coord2[1]);

	}

	void line(PApplet pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx, double sy) {
		coord = hexGrid.getGridCoordinates(q1, r1, ox, oy, sx, sy);
		coord2 = hexGrid.getGridCoordinates(q2, r2, ox, oy, sx, sy);
		line(pg, coord[0], coord[1], coord2[0], coord2[1]);
	}

	void clippedLine(PApplet pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx, double sy,
			double xmin, double ymin, double xmax, double ymax) {
		clippedLine(pg.g, q1, r1, q2, r2, ox, oy, sx, sy, xmin, ymin, xmax, ymax);

	}

	void clippedLine(PGraphics pg, double q1, double r1, double q2, double r2, double ox, double oy, double sx,
			double sy, double xmin, double ymin, double xmax, double ymax) {

		coord = hexGrid.getGridCoordinates(q1, r1, ox, oy, sx, sy);
		coord2 = hexGrid.getGridCoordinates(q2, r2, ox, oy, sx, sy);
		if(USEMAP) {
			map.map(coord[0], coord[1],coord);
			
			map.map(coord2[0], coord2[1],coord2);
		}

		double x0 = coord[0];
		double y0 = coord[1];
		double x1 = coord2[0];
		double y1 = coord2[1];

		int outcode0 = computeOutCode(x0, y0, xmin, ymin, xmax, ymax);
		int outcode1 = computeOutCode(x1, y1, xmin, ymin, xmax, ymax);
		boolean accept = false;

		while (true) {
			if ((outcode0 | outcode1) == 0) {
				// bitwise OR is 0: both points inside window; trivially accept and exit loop
				accept = true;
				break;
			} else if ((outcode0 & outcode1) > 0) {
				// bitwise AND is not 0: both points share an outside zone (LEFT, RIGHT, TOP,
				// or BOTTOM), so both must be outside window; exit loop (accept is false)
				break;
			} else {
				// failed both tests, so calculate the line segment to clip
				// from an outside point to an intersection with clip edge
				double x, y;
				x = 0.0;
				y = 0.0;

				// At least one endpoint is outside the clip rectangle; pick it.
				int outcodeOut = outcode1 > outcode0 ? outcode1 : outcode0;

				// Now find the intersection point;
				// use formulas:
				// slope = (y1 - y0) / (x1 - x0)
				// x = x0 + (1 / slope) * (ym - y0), where ym is ymin or ymax
				// y = y0 + slope * (xm - x0), where xm is xmin or xmax
				// No need to worry about divide-by-zero because, in each case, the
				// outcode bit being tested guarantees the denominator is non-zero
				if ((outcodeOut & TOP) > 0) { // point is above the clip window
					x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0);
					y = ymax;
				} else if ((outcodeOut & BOTTOM) > 0) { // point is below the clip window
					x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0);
					y = ymin;
				} else if ((outcodeOut & RIGHT) > 0) { // point is to the right of clip window
					y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0);
					x = xmax;
				} else if ((outcodeOut & LEFT) > 0) { // point is to the left of clip window
					y = y0 + (y1 - y0) * (xmin - x0) / (x1 - x0);
					x = xmin;
				}

				// Now we move outside point to intersection point to clip
				// and get ready for next pass.
				if (outcodeOut == outcode0) {
					x0 = x;
					y0 = y;
					outcode0 = computeOutCode(x0, y0, xmin, ymin, xmax, ymax);
				} else {
					x1 = x;
					y1 = y;
					outcode1 = computeOutCode(x1, y1, xmin, ymin, xmax, ymax);
				}
			}
		}
		if (accept) {
			pg.line((float) x0, (float) y0, (float) x1, (float) y1);

		}

	}

	int INSIDE = 0; // 0000
	int LEFT = 1; // 0001
	int RIGHT = 2; // 0010
	int BOTTOM = 4; // 0100
	int TOP = 8; // 1000

	// Compute the bit code for a point (x, y) using the clip
	// bounded diagonally by (xmin, ymin), and (xmax, ymax)
	// ASSUME THAT xmax, xmin, ymax and ymin are global constants.
	int computeOutCode(double x, double y, double xmin, double ymin, double xmax, double ymax) {
		int code;
		code = INSIDE; // initialised as being inside of [[clip window]]
		if (x < xmin) // to the left of clip window
			code |= LEFT;
		else if (x > xmax) // to the right of clip window
			code |= RIGHT;
		if (y < ymin) // below the clip window
			code |= BOTTOM;
		else if (y > ymax) // above the clip window
			code |= TOP;
		return code;
	}

	final public void drawClippedLines(double xmin, double ymin, double xmax, double ymax) {
		for (WB_IsoGridLine line : hexGrid.lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				clippedLine(home, segment.getQ1() / 6.0, segment.getR1() / 6.0, segment.getQ2() / 6.0,
						segment.getR2() / 6.0, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L, xmin, ymin, xmax, ymax);
			}
		}
	}

	final public void drawClippedOutlines(double xmin, double ymin, double xmax, double ymax) {
		for (WB_IsoGridLine line : hexGrid.outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				clippedLine(home, segment.getQ1() / 6.0, segment.getR1() / 6.0, segment.getQ2() / 6.0,
						segment.getR2() / 6.0, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L, xmin, ymin, xmax, ymax);

			}
		}
	}

	final public void drawGridLinePoints(double diameter) {
		drawGridLinePoints(home.g, diameter);
	}

	final public void drawGridLinePoints(PGraphics pg, double diameter) {
		for (WB_IsoGridLine line : hexGrid.gridlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				circle(pg, segment.getQ1() / 6.0, segment.getR1() / 6.0, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L,
						diameter);
				circle(pg, segment.getQ2() / 6.0, segment.getR2() / 6.0, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L,
						diameter);
			}
		}
	}

	final public void drawGridLines() {
		drawGridLines(home.g);
	}

	final public void drawGridLines(PGraphics pg) {
		for (WB_IsoGridLine line : hexGrid.gridlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(pg, segment.getQ1() / 6.0, segment.getR1() / 6.0, segment.getQ2() / 6.0, segment.getR2() / 6.0,
						centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			}
		}
	}

	final public void drawHex(int q, int r) {
		double[] center = hexGrid.getGridCoordinates(q, r, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
		home.beginShape();
		for (int i = 0; i < 6; i++) {
			hexVertex(home.g, i, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);
		}
		home.endShape(PConstants.CLOSE);
	}

	final public void drawHexCenters() {
		for (WB_IsoGridCell cell : hexGrid.cells.values()) {
			point(home.g,cell.getQ(), cell.getR(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
		}
	}

	final public void drawHexCenters(double radius, int type) {
		double[] center = hexGrid.getGridCoordinates(1, -1, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
		int limit = (int) (2.0 * radius
				/ Math.sqrt(
						(center[0] - centerX) * (center[0] - centerX) + (center[1] - centerY) * (center[1] - centerY)))
				+ 1;
		for (int q = -limit; q <= limit; q++) {
			for (int r = -limit; r <= limit; r++) {
				if (((q + r) % 3 + 3) % 3 == type) {
					center = hexGrid.getGridCoordinates(q, r, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
					if (Math.sqrt((center[0] - centerX) * (center[0] - centerX)
							+ (center[1] - centerY) * (center[1] - centerY)) <= radius) {
						point(home,q,r, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			
					}
				}
			}
		}
	}

	final public void drawHexGrid() {
		for (WB_IsoGridCell cell : hexGrid.cells.values()) {
			drawHex(cell.getQ(), cell.getR());
		}
	}

	final public void drawHexGrid(double radius, int type) {
		double[] center = hexGrid.getGridCoordinates(1, -1, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
		int limit = (int) (2.0 * radius
				/ Math.sqrt(
						(center[0] - centerX) * (center[0] - centerX) + (center[1] - centerY) * (center[1] - centerY)))
				+ 1;
		for (int q = -limit; q <= limit; q++) {
			for (int r = -limit; r <= limit; r++) {
				if (((q + r) % 3 + 3) % 3 == type) {
					center = hexGrid.getGridCoordinates(q, r, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
					if (Math.sqrt((center[0] - centerX) * (center[0] - centerX)
							+ (center[1] - centerY) * (center[1] - centerY)) <= radius) {
						home.beginShape();
						for (int i = 0; i < 6; i++) {
							hexVertex(home.g, i, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);
						}
						home.endShape(PConstants.CLOSE);
					}
				}
			}
		}
	}

	final public void drawLine(double q1, double r1, double q2, double r2) {
		
		line(home.g,q1,r1,q2,r2, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
		point(home.g,q1,r1, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
		point(home.g,q2,r2, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
	}

	final public void drawLinePoints(double diameter) {
		drawLinePoints(home.g, diameter);
	}

	final public void drawLinePoints(PGraphics pg, double diameter) {
		for (WB_IsoGridLine line : hexGrid.lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				circle(pg, segment.getQ1() / 6.0, segment.getR1() / 6.0, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L,
						diameter);
				circle(pg, segment.getQ2() / 6.0, segment.getR2() / 6.0, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L,
						diameter);
			}
		}
	}

	final public void drawLines() {
		drawLines(home);
	}

	final public void drawLines(int type, double minValue, double maxValue) {
		for (WB_IsoGridLine line : hexGrid.lines) {
			if (line.getType() == type && line.getLineValue() >= minValue && line.getLineValue() < maxValue) {
				for (WB_IsoGridSegment segment : line.getSegments()) {
					line(home.g, segment.getQ1() / 6.0, segment.getR1() / 6.0, segment.getQ2() / 6.0,
							segment.getR2() / 6.0, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
					point(home.g, segment.getQ1() / 6.0, segment.getR1() / 6.0, centerX, centerY, L,
							(YFLIP ? -1.0 : 1.0) * L);
					point(home.g, segment.getQ2() / 6.0, segment.getR2() / 6.0, centerX, centerY, L,
							(YFLIP ? -1.0 : 1.0) * L);
				}
			}
		}
	}

	final public void drawLines(PGraphics pg) {
		for (WB_IsoGridLine line : hexGrid.lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(pg, segment.getQ1() / 6.0, segment.getR1() / 6.0, segment.getQ2() / 6.0, segment.getR2() / 6.0,
						centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			}
		}
	}
	
	final public void drawLines(PApplet pg) {
		for (WB_IsoGridLine line : hexGrid.lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(pg, segment.getQ1() / 6.0, segment.getR1() / 6.0, segment.getQ2() / 6.0, segment.getR2() / 6.0,
						centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			}
		}
	}

	final public void drawOrientation(int q, int r, double dx, double dy) {
		double[] point = hexGrid.getGridCoordinates(q, r, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
		home.text("(" + q + "," + r + ")", (float) (point[0] + dx), (float) (point[1] + dy));
	}

	final public void drawOutlinePoints(double diameter) {
		drawOutlinePoints(home.g, diameter);
	}

	final public void drawOutlinePoints(PGraphics pg, double diameter) {
		for (WB_IsoGridLine line : hexGrid.outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				circle(pg, segment.getQ1() / 6.0, segment.getR1() / 6.0, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L,
						diameter);
				circle(pg, segment.getQ2() / 6.0, segment.getR2() / 6.0, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L,
						diameter);
			}
		}
	}

	final public void drawOutlines() {
		drawOutlines(home);
	}

	final public void drawOutlines(PGraphics pg) {
		for (WB_IsoGridLine line : hexGrid.outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(pg, segment.getQ1() / 6.0, segment.getR1() / 6.0, segment.getQ2() / 6.0, segment.getR2() / 6.0,
						centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			}
		}
	}
	
	final public void drawOutlines(PApplet pg) {
		for (WB_IsoGridLine line : hexGrid.outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				line(pg, segment.getQ1() / 6.0, segment.getR1() / 6.0, segment.getQ2() / 6.0, segment.getR2() / 6.0,
						centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			}
		}
	}

	final public void drawPoint(double q, double r) {
		
		point(home.g,q, r, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
	}

	final public void drawPoint(double q, double r,double rad) {
		circle(home.g,q,r,2*rad);
	}

	final public void drawTriangle(double[] center, int f) {
		home.beginShape();
		triVertices(center, f);
		home.endShape(PConstants.CLOSE);
	}

	final public void drawTriangle(int q, int r, int f) {
		double[] center = hexGrid.getGridCoordinates(q, r, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
		home.beginShape();
		triVertices(center, f);
		home.endShape(PConstants.CLOSE);
	}

	final public void drawTriangleGrid() {
		for (WB_IsoGridCell cell : hexGrid.cells.values()) {
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				drawTriangle(cell.getQ(), cell.getR(), f);
			}
		}
	}

	final public void drawTriangleGrid(double radius) {
		double[] center = hexGrid.getGridCoordinates(1, -1, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
		int limit = (int) (2.0 * radius
				/ Math.sqrt(
						(center[0] - centerX) * (center[0] - centerX) + (center[1] - centerY) * (center[1] - centerY)))
				+ 1;
		for (int q = -limit; q <= limit; q++) {
			for (int r = -limit; r <= limit; r++) {
				if (((q + r) % 3 + 3) % 3 == 0) {
					center = hexGrid.getGridCoordinates(q, r, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
					if (Math.sqrt((center[0] - centerX) * (center[0] - centerX)
							+ (center[1] - centerY) * (center[1] - centerY)) <= radius) {
						for (int f = 0; f < getNumberOfTriangles(); f++) {
							drawTriangle(center, f);
						}
					}
				}
			}
		}
	}

	final public void drawTriangles() {
		drawTriangles(home.g);
	}

	final public void drawTriangles(double scaleI, double scaleJ, double scaleK, PImage[] textures,
			WB_IsoColor colors) {
		drawTriangles(home.g, scaleI, scaleJ, scaleK, textures, colors);
	}

	final public void drawTriangles(int colorIndex) {
		drawTriangles(home.g, colorIndex);
	}

	final public void drawTriangles(PGraphics pg) {
		double[] center;
		for (WB_IsoGridCell cell : hexGrid.cells.values()) {
			center = hexGrid.getGridCoordinates(cell.getQ(), cell.getR(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {
					pg.beginShape(PConstants.TRIANGLES);
					pg.fill(colorSources.get(cell.getColorSourceIndex(f)).getColor(pg, cell, f));
					triVertices(pg, center, f);
					pg.endShape();
				}
			}
		}
	}

	final public void drawTriangles(PGraphics pg, double scaleI, double scaleJ, double scaleK, PImage[] textures) {
		double[] center;
		int offsetU, offsetV;
		double scaleU, scaleV;
		for (WB_IsoGridCell cell : hexGrid.cells.values()) {
			center = hexGrid.getGridCoordinates(cell.getQ(), cell.getR(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {

					pg.beginShape(PConstants.TRIANGLES);
					pg.texture(textures[(cell.orientation[f] + 3 * cell.getColorSourceIndex(f)) % textures.length]);
					pg.tint(colorSources.get(cell.getColorSourceIndex(f)).getColor(pg, cell, f));
					offsetU = cell.getTriangleUOffset(f);
					offsetV = cell.getTriangleVOffset(f);

					switch (cell.getTriangleUDirection(f)) {
					case 0:
						scaleU = 1.0 / Math.max(1.0, scaleI * I);

						break;

					case 1:
						scaleU = 1.0 / Math.max(1.0, scaleJ * J);

						break;

					case 2:
						scaleU = 1.0 / Math.max(1.0, scaleK * K);
						break;

					default:
						scaleU = 1.0;

					}

					switch (cell.getTriangleVDirection(f)) {
					case 0:
						scaleV = 1.0 / Math.max(1.0, scaleI * I);

						break;

					case 1:
						scaleV = 1.0 / Math.max(1.0, scaleJ * J);

						break;

					case 2:
						scaleV = 1.0 / Math.max(1.0, scaleK * K);

						break;

					default:
						scaleV = 1.0;
					}

					triVertex(pg, f, 0, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 0) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(f, 0) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(f, 0) + offsetV));
					triVertex(pg, f, 1, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 1) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(f, 1) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(f, 1) + offsetV));
					triVertex(pg, f, 2, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 2) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(f, 2) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(f, 2) + offsetV));

					pg.endShape();
					pg.noTint();
				}
			}
		}
	}

	final public void drawTriangles(PGraphics pg, double scaleI, double scaleJ, double scaleK, PImage[] textures,
			WB_IsoColor colors) {
		double[] center;
		int offsetU, offsetV;
		double scaleU, scaleV;
		for (WB_IsoGridCell cell : hexGrid.cells.values()) {
			center = hexGrid.getGridCoordinates(cell.getQ(), cell.getR(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {

					pg.beginShape(PConstants.TRIANGLES);
					pg.texture(textures[(cell.orientation[f])]);// + 3 * cell.getColor(f)) % textures.length]);

					pg.tint(colors.getColor(pg, cell, f));
					offsetU = cell.getTriangleUOffset(f);
					offsetV = cell.getTriangleVOffset(f);

					switch (cell.getTriangleUDirection(f)) {
					case 0:
						scaleU = 1.0 / Math.max(1.0, scaleI * I);

						break;

					case 1:
						scaleU = 1.0 / Math.max(1.0, scaleJ * J);

						break;

					case 2:
						scaleU = 1.0 / Math.max(1.0, scaleK * K);
						break;

					default:
						scaleU = 1.0;

					}

					switch (cell.getTriangleVDirection(f)) {
					case 0:
						scaleV = 1.0 / Math.max(1.0, scaleI * I);

						break;

					case 1:
						scaleV = 1.0 / Math.max(1.0, scaleJ * J);

						break;

					case 2:
						scaleV = 1.0 / Math.max(1.0, scaleK * K);

						break;

					default:
						scaleV = 1.0;
					}

					triVertex(pg, f, 0, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 0) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(f, 0) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(f, 0) + offsetV));
					triVertex(pg, f, 1, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 1) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(f, 1) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(f, 1) + offsetV));
					triVertex(pg, f, 2, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 2) + offsetU),
							YFLIP ? scaleV * (cell.getTriangleV(f, 2) + offsetV)
									: 1.0 - scaleV * (cell.getTriangleV(f, 2) + offsetV));

					pg.endShape();
					pg.noTint();
				}
			}
		}
	}

	final public void drawTriangles(PGraphics pg, int colorIndex) {
		double[] center;
		for (WB_IsoGridCell cell : hexGrid.cells.values()) {
			center =hexGrid.getGridCoordinates(cell.getQ(), cell.getR(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1 && cell.getPart(f) % colorIndex == 0) {
					pg.beginShape(PConstants.TRIANGLES);
					pg.fill(colorSources.get(cell.getColorSourceIndex(f)).getColor(pg, cell, f));
					triVertices(pg, center, f);
					pg.endShape();
				}
			}
		}
	}

	final public void drawTriangles(PGraphics pg, PImage[] textures) {
		drawTriangles(pg, 1, 1, 1, textures);
	}

	final public void drawTriangles(PGraphics pg, PImage[] textures, WB_IsoColor colors) {

		drawTriangles(pg, 1, 1, 1, textures, colors);
	}

	final public void drawTriangles(PGraphics pg, WB_IsoColor colors) {
		double[] center;
		for (WB_IsoGridCell cell : hexGrid.cells.values()) {
			center = hexGrid.getGridCoordinates(cell.getQ(), cell.getR(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {
					pg.beginShape(PConstants.TRIANGLES);
					pg.fill(colors.getColor(pg, cell, f));
					triVertices(pg, center, f);
					pg.endShape();
				}
			}
		}
	}

	final public void drawTriangles(PImage[] textures) {
		drawTriangles(home.g, 1, 1, 1, textures);
	}

	final public void drawTriangles(PImage[] textures, WB_IsoColor colors) {
		drawTriangles(home.g, 1, 1, 1, textures, colors);
	}

	final public void drawTriangles(WB_IsoColor colors) {
		drawTriangles(home.g, colors);
	}

	private void triVertices(double[] center, int f) {
		triVertices(home.g, center,f);

	}

	private void triVertices(PGraphics pg, double[] center, int f) {
		triVertex(pg, f, 0, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);
		triVertex(pg, f, 1, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);
		triVertex(pg, f, 2, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);

	}

	void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy) {
		hexGrid.getTriangleCoordinates(t, i, ox, oy, sx, sy, coord);
		vertex(pg, coord[0], coord[1]);

	}

	void triVertex(PGraphics pg, int t, int i, double ox, double oy, double sx, double sy, double u, double v) {
		hexGrid.getTriangleCoordinates(t, i, ox, oy, sx, sy, coord);
		vertex(pg, coord[0], coord[1], u, v);

	}
	
	void hexVertex(PGraphics pg, int i, double ox, double oy, double sx, double sy) {
		hexGrid.getHexCoordinates(i, ox, oy, sx, sy, coord);
		vertex(pg, coord[0], coord[1]);

	}

	final void vertex(PGraphics pg, final double px, double py) {
		if (!USEMAP) {
			pg.vertex((float) px, (float) py);
		} else {
			map.map(px, py, coord);
			pg.vertex((float) coord[0], (float) coord[1]);
		}
	}

	final void vertex(PGraphics pg, final double px, double py, double u, double v) {
		if (!USEMAP) {
			pg.vertex((float) px, (float) py, (float) u, (float) v);
		} else {
			map.map(px, py, coord);
			pg.vertex((float) coord[0], (float) coord[1], (float) u, (float) v);
		}

	}



	void point(PGraphics pg, double x, double y) {
		if (!USEMAP) {
			pg.point((float) x, (float) y);
		} else {
			map.map(x, y, coord);
			pg.point((float) coord[0], (float) coord[1]);
		}
		

	}
	
	void point(PApplet pg, double x, double y) {
		point(pg.g,x,y);
		

	}

	void circle(PGraphics pg, double x, double y, double diameter) {
		
		if (!USEMAP) {
		pg.ellipse((float) x, (float) y, (float) diameter, (float) diameter);
		}else {
			map.map(x, y, coord);

			pg.ellipse((float) coord[0], (float) coord[1], (float) diameter, (float) diameter);
		}
	}
	
	void circle(PApplet pg, double x, double y, double diameter) {
		circle(pg.g,x,y,diameter);
	}

	void line(PGraphics pg, double x1, double y1, double x2, double y2) {
		if (!USEMAP) {
		pg.line((float) x1, (float) y1, (float) x2, (float) y2);
		}else {
			
			map.map(x1, y1, coord);
			map.map(x2, y2, coord2);
			
			pg.line((float) coord[0], (float) coord[1],(float) coord2[0], (float) coord2[1]);
		}
	}
	
	void line(PApplet pg, double x1, double y1, double x2, double y2) {
		if (!USEMAP) {
			pg.line((float) x1, (float) y1, (float) x2, (float) y2);
			}else {
				
				map.map(x1, y1, coord);
				map.map(x2, y2, coord2);
				
				pg.line((float) coord[0], (float) coord[1],(float) coord2[0], (float) coord2[1]);
			}
	}
	
	public static void main(String... args) {

		WB_IsoSystem6 iso = new WB_IsoSystem6(4, 64, 60, 58, 500, 500,
				new int[] { 0xff000000 | 255, 0xff000000 | 0, 0xff000000 | 128 }, (int) (Math.random() * 1000000),
				new PApplet());

		iso.invertAll();

		// step j, step k, size j, size k
		iso.barIAll(8, 16, 2, 2);
		// step i, step k, size i, size k
		iso.barJAll(16, 16, 4, 1);
		// step i, step j, size i, size j
		iso.barKAll(32, 8, 2, 8);

		int pal = iso.addColorSource(new int[] { 0xff000000 | 255, 0xff000000 | 60, 0xff000000 | 128 });
		/*
		 * for(int i=0;i<64;i++){ for(int j=0;j<60;j++){ for(int k=0;k<58;k++){
		 * if(random(100)<1.0) iso.setColors(i,j,k,pal); } } }
		 */

		iso.setColors(63, 59, 57, pal);
	}

}
