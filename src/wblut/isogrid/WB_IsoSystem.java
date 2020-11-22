package wblut.isogrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.rng.RandomProviderState;
import org.apache.commons.rng.RestorableUniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public abstract class WB_IsoSystem<IHG extends WB_IsoHexGrid> {
	WB_IsoHexGrid grid;
	WB_CubeGrid cubes;
	double L;
	int I, J, K, JK, IJK;
	double centerX, centerY;
	PApplet home;
	int seed;
	int[] colors;
	int numPalettes;
	int numParts;
	int numRegions;
	RestorableUniformRandomProvider randomGen;
	RandomProviderState state;
	boolean DEFER;
	boolean GLOBALDEFER;
	boolean YFLIP;

	WB_IsoSystem() {

	}

	WB_IsoSystem(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed, boolean full,
			PApplet home) {
		randomGen = RandomSource.create(RandomSource.MT);
		state = randomGen.saveState();
		this.home = home;
		this.L = L;
		this.I = Math.max(1, I);
		this.J = Math.max(1, J);
		this.K = Math.max(1, K);
		JK = this.J * this.K;
		IJK = this.I * JK;
		this.colors = colors;
		this.centerX = centerX;
		this.centerY = centerY;
		this.cubes = new WB_CubeGrid(this.I, this.J, this.K);
		this.seed = seed;
		setGrid();
		if (full) {
			set(0, 0, 0, I, J, K);
			mapVoxelsToHexGrid();
		}
		DEFER = false;
		GLOBALDEFER = false;
		YFLIP = true;

	}

	WB_IsoSystem(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed, PApplet home) {
		this(L, I, J, K, centerX, centerY, colors, seed, true, home);

	}

	WB_IsoSystem(WB_IsoSystem<IHG> iso) {
		randomGen = RandomSource.create(RandomSource.MT);
		state = randomGen.saveState();
		this.home = iso.home;
		this.L = iso.L;
		this.I = iso.I;
		this.J = iso.J;
		this.K = iso.K;
		IJK = I * J * K;
		this.colors = new int[iso.colors.length];
		System.arraycopy(iso.colors, 0, this.colors, 0, iso.colors.length);
		this.numPalettes = iso.numPalettes;
		this.centerX = iso.centerX;
		this.centerY = iso.centerY;
		this.cubes = new WB_CubeGrid(iso.cubes);
		this.seed = iso.seed;
		try {
			grid = iso.grid.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {

			e.printStackTrace();
		}
		mapVoxelsToHexGrid();
		DEFER = false;
		GLOBALDEFER = false;
		YFLIP = true;

	}

	abstract void setGrid();

	public WB_IsoHexGrid getGrid() {
		return grid;

	}

	abstract int getNumberOfTriangles();

	public int getNumberOfParts() {
		return numParts;
	}

	public int getNumberOfRegions() {
		return numRegions;
	}

	public int getNumberOfPalettes() {
		return numPalettes;
	}

	final public void setRNGSeed(long seed) {
		randomGen = RandomSource.create(RandomSource.MT, seed);
		state = randomGen.saveState();
	}

	final public void resetRNG() {
		randomGen.restoreState(state);
	}

	final void map() {
		if (!deferred()) {
			mapVoxelsToHexGrid();
			cubes.reset();
			numParts = cubes.labelParts();
			grid.setParts(cubes);
			numRegions = ((this.getNumberOfTriangles() == 3) ? 3 : 10) * numParts;
			cubes.setVisibility();
			grid.setVisibility(cubes);
			grid.collectRegions();
			grid.collectLines();
		}
	}

	public abstract void mapVoxelsToHexGrid();

	public void setDeferred(boolean b) {
		GLOBALDEFER = b;
	}

	public boolean deferred() {
		return DEFER || GLOBALDEFER;
	}

	public void setYFlip(boolean b) {
		YFLIP = b;
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

	public void fill() {
		set(0, 0, 0, I, J, K);
		map();
	}

	public void blocks(int n) {
		grid.clear();
		cubes.clear();
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

	public void subdivide(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
					if (random(1.0) < chance) {
						clear(i, j, k, di, dj, dk);
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void sliceIAll(int on, int off) {
		DEFER = true;
		for (int i = on; i < I; i += on + off) {
			clear(i, 0, 0, off, J, K);
		}
		DEFER = false;
		map();
	}

	public void sliceJAll(int on, int off) {
		DEFER = true;
		for (int j = on; j < J; j += on + off) {
			clear(0, j, 0, I, off, K);
		}
		DEFER = false;
		map();
	}

	public void sliceKAll(int on, int off) {
		DEFER = true;
		for (int k = on; k < K; k += on + off) {
			clear(0, 0, k, I, J, off);
		}
		DEFER = false;
		map();
	}

	public void sliceIBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[I];
		for (int i = 0; i < I; i += on + off) {
			for (int l = 0; l < on; l++) {
				if (i + l < I) {
					layer[i + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void sliceJBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[J];
		for (int j = 0; j < J; j += on + off) {
			for (int l = 0; l < on; l++) {
				if (j + l < J) {
					layer[j + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void sliceKBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[K];
		for (int k = 0; k < K; k += on + off) {
			for (int l = 0; l < on; l++) {
				if (k + l < K) {
					layer[k + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void layerIAll(int on, int off) {
		DEFER = true;
		for (int i = on; i < I; i += on + off) {
			set(i, 0, 0, off, J, K);
		}
		DEFER = false;
		map();
	}

	public void layerJAll(int on, int off) {
		DEFER = true;
		for (int j = on; j < J; j += on + off) {
			set(0, j, 0, I, off, K);
		}
		DEFER = false;
		map();
	}

	public void layerKAll(int on, int off) {
		DEFER = true;
		for (int k = on; k < K; k += on + off) {
			set(0, 0, k, I, J, off);
		}
		DEFER = false;
		map();
	}

	public void layerIBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[I];
		for (int i = 0; i < I; i += on + off) {
			for (int l = 0; l < on; l++) {
				if (i + l < I) {
					layer[i + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void layerJBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[J];
		for (int j = 0; j < J; j += on + off) {
			for (int l = 0; l < on; l++) {
				if (j + l < J) {
					layer[j + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void layerKBlocks(float chance, int on, int off, int di, int dj, int dk) {
		boolean[] layer = new boolean[K];
		for (int k = 0; k < K; k += on + off) {
			for (int l = 0; l < on; l++) {
				if (k + l < K) {
					layer[k + l] = true;
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void perforateIAll(int stepj, int stepk, int rj, int rk) {
		DEFER = true;
		for (int j = stepj; j < J; j += stepj) {
			for (int k = stepk; k < K; k += stepk) {
				clear(0, j - rj, k - rk, I, 2 * rj, 2 * rk);
			}
		}
		DEFER = false;
		map();
	}

	public void perforateJAll(int stepi, int stepk, int ri, int rk) {
		DEFER = true;
		for (int i = stepi; i < I; i += stepi) {
			for (int k = stepk; k < K; k += stepk) {
				clear(i - ri, 0, k - rk, 2 * ri, J, 2 * rk);
			}
		}
		DEFER = false;
		map();
	}

	public void perforateKAll(int stepi, int stepj, int ri, int rj) {
		DEFER = true;
		for (int i = stepi; i < I; i += stepi) {
			for (int j = stepj; j < J; j += stepj) {
				clear(i - ri, j - rj, 0, 2 * ri, 2 * rj, K);
			}
		}
		DEFER = false;
		map();
	}

	public void perforateIBlocks(float chance, int stepj, int stepk, int rj, int rk, int di, int dj, int dk) {
		boolean[][] column = new boolean[J][K];
		for (int j = stepj; j < J; j += stepj) {
			for (int k = stepk; k < K; k += stepk) {
				for (int cj = -rj; cj <= rj; cj++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K)
							column[j + cj][k + ck] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void perforateJBlocks(float chance, int stepi, int stepk, int ri, int rk, int di, int dj, int dk) {
		boolean[][] column = new boolean[I][K];
		for (int i = stepi; i < I; i += stepi) {
			for (int k = stepk; k < K; k += stepk) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K)
							column[i + ci][k + ck] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void perforateKBlocks(float chance, int stepi, int stepj, int ri, int rj, int di, int dj, int dk) {
		boolean[][] column = new boolean[I][J];
		for (int i = stepi; i < I; i += stepi) {
			for (int j = stepj; j < J; j += stepj) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int cj = -rj; cj <= rj; cj++) {
						if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J)
							column[i + ci][j + cj] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void barIAll(int stepj, int stepk, int rj, int rk) {
		DEFER = true;
		for (int j = stepj; j < J; j += stepj) {
			for (int k = stepk; k < K; k += stepk) {
				set(0, j - rj, k - rk, I, 2 * rj, 2 * rk);
			}
		}
		DEFER = false;
		map();
	}

	public void barJAll(int stepi, int stepk, int ri, int rk) {
		DEFER = true;
		for (int i = stepi; i < I; i += stepi) {
			for (int k = stepk; k < K; k += stepk) {
				set(i - ri, 0, k - rk, 2 * ri, J, 2 * rk);
			}
		}
		DEFER = false;
		map();
	}

	public void barKAll(int stepi, int stepj, int ri, int rj) {
		DEFER = true;
		for (int i = stepi; i < I; i += stepi) {
			for (int j = stepj; j < J; j += stepj) {
				set(i - ri, j - rj, 0, 2 * ri, 2 * rj, K);
			}
		}
		DEFER = false;
		map();
	}

	public void barIBlocks(float chance, int stepj, int stepk, int rj, int rk, int di, int dj, int dk) {
		boolean[][] column = new boolean[J][K];
		for (int j = stepj; j < J; j += stepj) {
			for (int k = stepk; k < K; k += stepk) {
				for (int cj = -rj; cj <= rj; cj++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (j + cj >= 0 && j + cj < J && k + ck >= 0 && k + ck < K)
							column[j + cj][k + ck] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void barJBlocks(float chance, int stepi, int stepk, int ri, int rk, int di, int dj, int dk) {
		boolean[][] column = new boolean[I][K];
		for (int i = stepi; i < I; i += stepi) {
			for (int k = stepk; k < K; k += stepk) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int ck = -rk; ck <= rk; ck++) {
						if (i + ci >= 0 && i + ci < I && k + ck >= 0 && k + ck < K)
							column[i + ci][k + ck] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void barKBlocks(float chance, int stepi, int stepj, int ri, int rj, int di, int dj, int dk) {
		boolean[][] column = new boolean[I][J];
		for (int i = stepi; i < I; i += stepi) {
			for (int j = stepj; j < J; j += stepj) {
				for (int ci = -ri; ci <= ri; ci++) {
					for (int cj = -rj; cj <= rj; cj++) {
						if (i + ci >= 0 && i + ci < I && j + cj >= 0 && j + cj < J)
							column[i + ci][j + cj] = true;
					}
				}
			}
		}
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
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

	public void invertAll() {
		not(0, 0, 0, I, J, K);
		map();
	}

	public void invertBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
					if (random(1.0) < chance) {
						not(i, j, k, di, dj, dk);
					}
				}
			}
		}
		DEFER = false;
		map();
	}

	public void wallAll() {
		DEFER = true;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					if (cubes.isWall(i, j, k)) {
						cubes.setBuffer(i, j, k, true);
					} else {

						cubes.setBuffer(i, j, k, false);
					}
				}
			}
		}
		cubes.swap();
		DEFER = false;
		map();
	}

	public void wallBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
					if (random(1.0) < chance) {
						wallOneBlock(i, j, k, di, dj, dk);
					} else {
						copyOneBlockToBuffer(i, j, k, di, dj, dk);
					}
				}
			}
		}
		cubes.swap();
		DEFER = false;
		map();
	}

	void wallOneBlock(int si, int sj, int sk, int di, int dj, int dk) {
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					if (index(i, j, k) > -1) {
						if (cubes.isWall(i, j, k)) {
							cubes.setBuffer(i, j, k, true);
						} else {

							cubes.setBuffer(i, j, k, false);
						}
					}
				}
			}
		}
	}

	void copyOneBlockToBuffer(int si, int sj, int sk, int di, int dj, int dk) {
		int index;
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					index = index(i, j, k);
					if (index > -1) {
					cubes.setBuffer(index, cubes.get(index));
					}
				}
			}
		}
	}

	public void edgeAll() {
		DEFER = true;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int k = 0; k < K; k++) {
					if (cubes.isEdge(i, j, k)) {
						cubes.setBuffer(i, j, k, true);
					} else {

						cubes.setBuffer(i, j, k, false);
					}
				}
			}
		}
		cubes.swap();
		DEFER = false;
		map();
	}

	public void edgeBlocks(double chance, int di, int dj, int dk) {
		DEFER = true;
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				for (int k = 0; k < K; k += dk) {
					if (random(1.0) < chance) {
						edgeOneBlock(i, j, k, di, dj, dk);
					} else {
						copyOneBlockToBuffer(i, j, k, di, dj, dk);
					}
				}
			}
		}
		cubes.swap();
		DEFER = false;
		map();
	}

	void edgeOneBlock(int si, int sj, int sk, int di, int dj, int dk) {
		for (int i = si; i < si + di; i++) {
			for (int j = sj; j < sj + dj; j++) {
				for (int k = sk; k < sk + dk; k++) {
					if (index(i,j,k) > -1) {
					if (cubes.isEdge(i, j, k)) {
						cubes.setBuffer(i, j, k, true);
					} else {

						cubes.setBuffer(i, j, k, false);
					}
					}
				}
			}
		}
	}

	public void openIAll() {
		for (int j = 0; j < J; j++) {
			for (int k = 0; k < K; k++) {
				openLeftI(j, k);
				openRightI(j, k);
			}
		}
		map();
	}

	public void openJAll() {
		for (int i = 0; i < I; i++) {
			for (int k = 0; k < K; k++) {
				openLeftJ(i, k);
				openRightJ(i, k);
			}
		}
		map();
	}

	public void openKAll() {
		for (int j = 0; j < J; j++) {
			for (int i = 0; i < I; i++) {
				openLeftK(i, j);
				openRightK(i, j);
			}
		}
		map();
	}

	public void openIBlocks(double chance, int dj, int dk) {
		for (int j = 0; j < J; j += dj) {
			for (int k = 0; k < K; k += dk) {
				if (random(1.0) < chance) {
					for (int cj = 0; cj < dj; cj++) {
						for (int ck = 0; ck < dk; ck++) {
							if(j+cj<J && k+ck<K) {
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

	public void openJBlocks(double chance, int di, int dk) {
		for (int i = 0; i < I; i += di) {
			for (int k = 0; k < K; k += dk) {
				if (random(1.0) < chance) {
					for (int ci = 0; ci < di; ci++) {
						for (int ck = 0; ck < dk; ck++) {
							if(i+ci<I && k+ck<K) {
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

	public void openKBlocks(double chance, int di, int dj) {
		for (int i = 0; i < I; i += di) {
			for (int j = 0; j < J; j += dj) {
				if (random(1.0) < chance) {
					for (int ci = 0; ci < di; ci++) {
						for (int cj = 0; cj < dj; cj++) {
							if(j+cj<J && i+ci<I) {
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
		while (i < I && !cubes.get(i, j, k)) {
			i++;
		}
		if (i == I)
			return;
		if (i == I - 1 || !cubes.get(i + 1, j, k))
			cubes.set(i, j, k, false);

	}

	void openRightI(int j, int k) {
		int i = I - 1;
		while (i >= 0 && !cubes.get(i, j, k)) {
			i--;
		}
		if (i == -1)
			return;
		if (i == 0 || !cubes.get(i - 1, j, k))
			cubes.set(i, j, k, false);

	}

	void openLeftJ(int i, int k) {
		int j = 0;
		while (index(i,j,k)>-1 && !cubes.get(i, j, k)) {
			j++;
		}
		if (j == J)
			return;
		if (j == J - 1 || !cubes.get(i, j + 1, k))
			cubes.set(i, j, k, false);

	}

	void openRightJ(int i, int k) {
		int j = J - 1;
		while (j >= 0 && !cubes.get(i, j, k)) {
			j--;
		}
		if (j == -1)
			return;
		if (j == 0 || !cubes.get(i, j - 1, k))
			cubes.set(i, j, k, false);

	}

	void openLeftK(int i, int j) {
		int k = 0;
		while (k < K && !cubes.get(i, j, k)) {
			k++;
		}
		if (k == K)
			return;
		if (k == K - 1 || !cubes.get(i, j, k + 1))
			cubes.set(i, j, k, false);

	}

	void openRightK(int i, int j) {
		int k = K - 1;
		while (k >= 0 && !cubes.get(i, j, k)) {
			k--;
		}
		if (k == -1)
			return;
		if (k == 0 || !cubes.get(i, j, k - 1))
			cubes.set(i, j, k, false);

	}

	public void refresh() {
		mapVoxelsToHexGrid();
	}

	public void set(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < blocki; di++) {
			for (int dj = 0; dj < blockj; dj++) {
				for (int dk = 0; dk < blockk; dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1) {
						cubes.set(index, true);
					}
				}
			}
		}
		map();
	}

	public void clear(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < blocki; di++) {
			for (int dj = 0; dj < blockj; dj++) {
				for (int dk = 0; dk < blockk; dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1) {
						cubes.set(index, false);
					}
				}
			}
		}
		map();
	}

	public void and(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < blocki; di++) {
			for (int dj = 0; dj < blockj; dj++) {
				for (int dk = 0; dk < blockk; dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1) {
						cubes.and(index, true);
					}
				}
			}
		}
		map();
	}

	public void or(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < blocki; di++) {
			for (int dj = 0; dj < blockj; dj++) {
				for (int dk = 0; dk < blockk; dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1) {
						cubes.or(index, true);
					}
				}
			}
		}
		map();
	}

	public void xor(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < blocki; di++) {
			for (int dj = 0; dj < blockj; dj++) {
				for (int dk = 0; dk < blockk; dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1) {
						cubes.xor(index, true);
					}
				}
			}
		}
		map();
	}

	public void not(int i, int j, int k, int blocki, int blockj, int blockk) {
		for (int di = 0; di < blocki; di++) {
			for (int dj = 0; dj < blockj; dj++) {
				for (int dk = 0; dk < blockk; dk++) {
					int index = index(i + di, j + dj, k + dk);
					if (index > -1) {
						cubes.not(index);
					}
				}
			}
		}
		map();
	}

	public void set(int i, int j, int k) {
		cubes.set(i, j, k, true);
		map();
	}

	public void clear(int i, int j, int k) {
		cubes.set(i, j, k, false);
		map();
	}

	public void and(int i, int j, int k) {
		cubes.and(i, j, k, true);
		map();
	}

	public void or(int i, int j, int k) {
		cubes.or(i, j, k, true);
		map();
	}

	public void xor(int i, int j, int k) {
		cubes.xor(i, j, k, true);
		map();
	}

	public void not(int i, int j, int k) {
		cubes.not(i, j, k);
		map();
	}

	final public void drawOrientation(int q, int r, double dx, double dy) {
		double[] point = getGridCoordinates(q, r);
		home.text("(" + q + "," + r + ")", (float) (point[0] + dx), (float) (point[1] + dy));
	}

	final public void drawPoint(double q, double r) {
		double[] point = getGridCoordinates(q, r);
		home.point((float) point[0], (float) point[1]);
	}

	final public void drawPoint(double rad, double q, double r) {
		double[] point = getGridCoordinates(q, r);
		home.ellipse((float) point[0], (float) point[1], 2 * (float) rad, 2 * (float) rad);
	}

	final public void drawLine(double q1, double r1, double q2, double r2) {
		double[] point1 = getGridCoordinates(q1, r1);
		double[] point2 = getGridCoordinates(q2, r2);
		home.line((float) point1[0], (float) point1[1], (float) point2[0], (float) point2[1]);
		home.point((float) point1[0], (float) point1[1]);
		home.point((float) point2[0], (float) point2[1]);
	}

	final public void drawHex(int q, int r) {
		double[] center = getGridCoordinates(q, r);
		home.beginShape();
		for (int i = 0; i < 6; i++) {
			grid.hexVertex(home.g, i, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);
		}
		home.endShape(PConstants.CLOSE);
	}

	final public void drawHexGrid() {
		for (WB_IsoGridCell cell : grid.cells.values()) {
			drawHex(cell.getQ(), cell.getR());
		}
	}

	final public void drawHexGrid(double radius, int type) {
		double[] center = getGridCoordinates(1, -1);
		int limit = (int) (2.0 * radius
				/ Math.sqrt(
						(center[0] - centerX) * (center[0] - centerX) + (center[1] - centerY) * (center[1] - centerY)))
				+ 1;
		for (int q = -limit; q <= limit; q++) {
			for (int r = -limit; r <= limit; r++) {
				if (((q + r) % 3 + 3) % 3 == type) {
					center = getGridCoordinates(q, r);
					if (Math.sqrt((center[0] - centerX) * (center[0] - centerX)
							+ (center[1] - centerY) * (center[1] - centerY)) <= radius) {
						home.beginShape();
						for (int i = 0; i < 6; i++) {
							grid.hexVertex(home.g, i, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);
						}
						home.endShape(PConstants.CLOSE);
					}
				}
			}
		}
	}

	final public void drawHexCenters() {
		for (WB_IsoGridCell cell : grid.cells.values()) {
			drawPoint(cell.getQ(), cell.getR());
		}
	}

	final public void drawHexCenters(double radius, int type) {
		double[] center = getGridCoordinates(1, -1);
		int limit = (int) (2.0 * radius
				/ Math.sqrt(
						(center[0] - centerX) * (center[0] - centerX) + (center[1] - centerY) * (center[1] - centerY)))
				+ 1;
		for (int q = -limit; q <= limit; q++) {
			for (int r = -limit; r <= limit; r++) {
				if (((q + r) % 3 + 3) % 3 == type) {
					center = getGridCoordinates(q, r);
					if (Math.sqrt((center[0] - centerX) * (center[0] - centerX)
							+ (center[1] - centerY) * (center[1] - centerY)) <= radius) {
						home.point((float) center[0], (float) center[1]);
					}
				}
			}
		}
	}

	final public void drawTriangle(int q, int r, int f) {
		double[] center = getGridCoordinates(q, r);
		home.beginShape();
		triVertices(center, f);
		home.endShape(PConstants.CLOSE);
	}

	final public void drawTriangleGrid() {
		for (WB_IsoGridCell cell : grid.cells.values()) {
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				drawTriangle(cell.getQ(), cell.getR(), f);
			}
		}
	}

	final public void drawTriangleGrid(double radius) {
		double[] center = getGridCoordinates(1, -1);
		int limit = (int) (2.0 * radius
				/ Math.sqrt(
						(center[0] - centerX) * (center[0] - centerX) + (center[1] - centerY) * (center[1] - centerY)))
				+ 1;
		for (int q = -limit; q <= limit; q++) {
			for (int r = -limit; r <= limit; r++) {
				if (((q + r) % 3 + 3) % 3 == 0) {
					center = getGridCoordinates(q, r);
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

	final public void drawTriangle(double[] center, int f) {
		home.beginShape();
		triVertices(center, f);
		home.endShape(PConstants.CLOSE);
	}

	final public void drawLinesSVG() {
		for (WB_IsoGridLine line : grid.lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				grid.line(home, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), centerX, centerY, L,
						(YFLIP ? -1.0 : 1.0) * L);
			}
		}
	}

	final public void drawLines() {
		drawLines(home.g);
	}

	final public void drawLines(PGraphics home) {
		for (WB_IsoGridLine line : grid.lines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				grid.line(home, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), centerX, centerY, L,
						(YFLIP ? -1.0 : 1.0) * L);
				grid.point(home, segment.getQ1(), segment.getR1(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
				grid.point(home, segment.getQ2(), segment.getR2(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			}
		}
	}

	final public void drawOutlines(PGraphics home) {
		for (WB_IsoGridLine line : grid.outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				grid.line(home, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), centerX, centerY, L,
						(YFLIP ? -1.0 : 1.0) * L);
				grid.point(home, segment.getQ1(), segment.getR1(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
				grid.point(home, segment.getQ2(), segment.getR2(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
			}
		}
	}

	final public void drawOutlines() {
		drawOutlines(home.g);
	}

	final public void drawOutlinesSVG() {
		for (WB_IsoGridLine line : grid.outlines) {
			for (WB_IsoGridSegment segment : line.getSegments()) {
				grid.line(home, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), centerX, centerY, L,
						(YFLIP ? -1.0 : 1.0) * L);

			}
		}
	}

	final public void drawLines(int type, double minValue, double maxValue) {
		for (WB_IsoGridLine line : grid.lines) {
			if (line.getType() == type && line.getLineValue() >= minValue && line.getLineValue() < maxValue) {
				for (WB_IsoGridSegment segment : line.getSegments()) {
					grid.line(home.g, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), centerX,
							centerY, L, (YFLIP ? -1.0 : 1.0) * L);
					grid.point(home.g, segment.getQ1(), segment.getR1(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
					grid.point(home.g, segment.getQ2(), segment.getR2(), centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
				}
			}
		}
	}

	private void triVertices(double[] center, int f) {
		grid.triVertex(home.g, f, 0, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);
		grid.triVertex(home.g, f, 1, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);
		grid.triVertex(home.g, f, 2, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);

	}

	private void triVertices(PGraphics pg, double[] center, int f) {
		grid.triVertex(pg, f, 0, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);
		grid.triVertex(pg, f, 1, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);
		grid.triVertex(pg, f, 2, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L);

	}

	final public void drawTriangles() {
		double[] center;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {
					home.beginShape(PConstants.TRIANGLES);
					home.fill(colors[cell.palette[f] * ((getNumberOfTriangles() == 3) ? 3 : 10) + cell.orientation[f]]);
					triVertices(center, f);
					home.endShape();
				}
			}
		}
	}

	final public void drawTriangles(WB_IsoColor colors) {
		double[] center;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {
					home.beginShape(PConstants.TRIANGLES);
					home.fill(colors.color(home.g, cell, f));
					triVertices(center, f);
					home.endShape();
				}
			}
		}
	}

	final public void drawTriangles(PImage[] textures) {
		drawTriangles(1, 1, 1, textures);
	}

	final public void drawTriangles(double scaleI, double scaleJ, double scaleK, PImage[] textures) {
		double[] center;
		int offsetU, offsetV;
		double scaleU, scaleV;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {

					home.beginShape(PConstants.TRIANGLES);
					home.texture(textures[cell.orientation[f]]);

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

					grid.triVertex(home.g, f, 0, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 0) + offsetU),
							(YFLIP ? -1.0 : 1.0) * scaleV * (cell.getTriangleV(f, 0) + offsetV));
					grid.triVertex(home.g, f, 1, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 1) + offsetU),
							(YFLIP ? -1.0 : 1.0) * scaleV * (cell.getTriangleV(f, 1) + offsetV));
					grid.triVertex(home.g, f, 2, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 2) + offsetU),
							(YFLIP ? -1.0 : 1.0) * scaleV * (cell.getTriangleV(f, 2) + offsetV));

					home.endShape();
				}
			}
		}
	}

	final public void drawTriangles(PImage[] textures, WB_IsoColor color) {
		drawTriangles(1, 1, 1, textures, color);

	}

	final public void drawTriangles(double scaleI, double scaleJ, double scaleK, PImage[] textures, WB_IsoColor color) {
		double[] center;
		int offsetU, offsetV;
		double scaleU, scaleV;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {

					home.beginShape(PConstants.TRIANGLES);
					home.tint(color.color(home.g, cell, f));
					home.texture(textures[cell.orientation[f]]);

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

					grid.triVertex(home.g, f, 0, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 0) + offsetU),
							(YFLIP ? -1.0 : 1.0) * scaleV * (cell.getTriangleV(f, 0) + offsetV));
					grid.triVertex(home.g, f, 1, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 1) + offsetU),
							(YFLIP ? -1.0 : 1.0) * scaleV * (cell.getTriangleV(f, 1) + offsetV));
					grid.triVertex(home.g, f, 2, center[0], center[1], L, (YFLIP ? -1.0 : 1.0) * L,
							scaleU * (cell.getTriangleU(f, 2) + offsetU),
							(YFLIP ? -1.0 : 1.0) * scaleV * (cell.getTriangleV(f, 2) + offsetV));

					home.endShape();
				}
			}
		}
	}

	final public void centerGrid() {
		double[] center = grid.getGridCoordinates(I / 2.0 - K / 2.0, J / 2.0 - K / 2.0, centerX, centerY, L,
				(YFLIP ? -1.0 : 1.0) * L);
		home.translate((float) (centerX - center[0]), (float) (centerY - (float) center[1]));

	}

	final public double[] getGridCoordinates(double q, double r) {
		return grid.getGridCoordinates(q, r, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
	}

	final public int[] getTriangleAtGridCoordinates(double x, double y) {
		return grid.getTriangleAtGridCoordinates(x, y, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
	}

	int index(final int i, final int j, final int k) {

		if (i > -1 && j > -1 && k > -1 && i < I && j < J && k < K) {
			return k + j * K + i * JK;
		} else {
			return -1;
		}
	}

	final double random(double v) {
		return randomGen.nextDouble() * v;
	}

	final double random(double v, double w) {
		return v + randomGen.nextDouble() * (w - v);
	}

	int[] createDualPalette(int[] palette) {
		if (palette.length % 3 != 0)
			throw new IllegalArgumentException("Palette length should be a mutiple of 3.");
		home.pushStyle();
		home.colorMode(PConstants.RGB);
		int numberOfPalettes = palette.length / 3;
		int[] colors = new int[10 * numberOfPalettes * 10];
		float hsqrt2 = (float) Math.sqrt(2.0) * 0.5f;
		float hsqrt3 = (float) Math.sqrt(3.0) * 0.5f;

		float[][] normals = new float[10][3];

		normals[0] = new float[] { 1, 0, 0 };
		normals[1] = new float[] { 0, 1, 0 };
		normals[2] = new float[] { 0, 0, 1 };
		normals[3] = new float[] { hsqrt2, hsqrt2, 0 };
		normals[4] = new float[] { hsqrt2, 0, hsqrt2 };
		normals[5] = new float[] { 0, hsqrt2, hsqrt2 };
		normals[6] = new float[] { hsqrt3, hsqrt3, hsqrt3 };
		normals[7] = new float[] { -hsqrt3, hsqrt3, hsqrt3 };
		normals[8] = new float[] { hsqrt3, -hsqrt3, hsqrt3 };
		normals[9] = new float[] { hsqrt3, hsqrt3, -hsqrt3 };

		for (int p = 0; p < numberOfPalettes; p++) {

			float[][] light = new float[][] {

					{ 1, 0, 0, (palette[3 * p] >> 16) & 0xff, (palette[3 * p] >> 8) & 0xff, palette[3 * p] & 0xff },
					{ 0, 1, 0, (palette[3 * p + 1] >> 16) & 0xff, (palette[3 * p + 1] >> 8) & 0xff,
							palette[3 * p + 1] & 0xff },
					{ 0, 0, 1, (palette[3 * p + 2] >> 16) & 0xff, (palette[3 * p + 2] >> 8) & 0xff,
							palette[3 * p + 2] & 0xff } };

			for (int i = 0; i < 10; i++) {
				float red, green, blue, dot;
				red = green = blue = 0;
				for (int l = 0; l < 3; l++) {
					dot = (float) Math.max(0,
							normals[i][0] * light[l][0] + normals[i][1] * light[l][1] + normals[i][2] * light[l][2]);
					red += dot * light[l][3];
					green += dot * light[l][4];
					blue += dot * light[l][5];
				}

				float max = (float) Math.max(red, Math.max(green, blue));
				if (max > 400.0f) {
					red *= 400.0f / max;
					green *= 400.0f / max;
					blue *= 400.0f / max;
				}
				colors[10 * p + i] = home.color((float) Math.max(Math.min(red, 255), 0),
						(float) Math.max(Math.min(green, 255), 0), (float) Math.max(Math.min(blue, 255), 0));
			}

		}
		home.popStyle();
		return colors;
	}

}
