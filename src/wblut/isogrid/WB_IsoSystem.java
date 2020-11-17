package wblut.isogrid;

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
	RestorableUniformRandomProvider randomGen;
	RandomProviderState state;
	private boolean DEFER;
	boolean GLOBALDEFER;
	boolean YFLIP;

	WB_IsoSystem() {

	}

	public WB_IsoSystem(double L, int I, int J, int K, double centerX, double centerY, int[] colors, int seed,
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
		set(0, 0, 0, I, J, K);
		mapVoxelsToHexGrid();
		DEFER = false;
		GLOBALDEFER = false;
		YFLIP = true;

	}

	public WB_IsoSystem(WB_IsoSystem<IHG> iso) {
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

	abstract int getNumberOfTriangles();

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
					if (cubes.isWall(i, j, k)) {
						cubes.setBuffer(i, j, k, true);
					} else {

						cubes.setBuffer(i, j, k, false);
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
					cubes.setBuffer(index, cubes.get(index));

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
					if (cubes.isEdge(i, j, k)) {
						cubes.setBuffer(i, j, k, true);
					} else {

						cubes.setBuffer(i, j, k, false);
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
							openLeftI(j + cj, k + ck);
							openRightI(j + cj, k + ck);
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
							openLeftJ(i + ci, k + ck);
							openRightJ(i + ci, k + ck);
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
							openLeftJ(i + ci, j + cj);
							openRightJ(i + ci, j + cj);
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
		while (j < J && !cubes.get(i, j, k)) {
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
				grid.line(home.g, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), centerX, centerY,
						L, (YFLIP ? -1.0 : 1.0) * L);
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
				grid.line(home.g, segment.getQ1(), segment.getR1(), segment.getQ2(), segment.getR2(), centerX, centerY,
						L, (YFLIP ? -1.0 : 1.0) * L);

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

	final public void drawTriangles(double I, double J, double K, PImage[] textures) {
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
						scaleU = 1.0 / Math.max(1.0, I);

						break;

					case 1:
						scaleU = 1.0 / Math.max(1.0, J);

						break;

					case 2:
						scaleU = 1.0 / Math.max(1.0, K);
						break;

					default:
						scaleU = 1.0;

					}

					switch (cell.getTriangleVDirection(f)) {
					case 0:
						scaleV = 1.0 / Math.max(1.0, I);

						break;

					case 1:
						scaleV = 1.0 / Math.max(1.0, J);

						break;

					case 2:
						scaleV = 1.0 / Math.max(1.0, K);

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

	final public void drawTriangles(double I, double J, double K, PImage[] textures, float ho, float hf, float hr,
			float zo, float zf, float zo2, float zf2, float oo, float of) {
		double[] center;
		float hue;
		int offsetU, offsetV;
		double scaleU, scaleV;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {

					home.beginShape(PConstants.TRIANGLES);
					home.texture(textures[cell.orientation[f]]);
					home.colorMode(PConstants.HSB);
					hue = (hf * cell.part[f]) % hr;
					hue = ho + hue;
					if (hue < 0)
						hue += 256 * ((int) (hue / (-256)) + 1);
					home.tint(hue % 256, zo + zf * cell.z[f], (oo + of * cell.orientation[f] + zo2 + zf2 * cell.z[f]));
					home.colorMode(PConstants.RGB);

					offsetU = cell.getTriangleUOffset(f);
					offsetV = cell.getTriangleVOffset(f);

					switch (cell.getTriangleUDirection(f)) {
					case 0:
						scaleU = 1.0 / Math.max(1.0, I);
						break;

					case 1:
						scaleU = 1.0 / Math.max(1.0, J);
						break;

					case 2:
						scaleU = 1.0 / Math.max(1.0, K);
						break;

					default:
						scaleU = 1.0;
					}

					switch (cell.getTriangleVDirection(f)) {
					case 0:
						scaleV = 1.0 / Math.max(1.0, I);
						break;

					case 1:
						scaleV = 1.0 / Math.max(1.0, J);
						break;

					case 2:
						scaleV = 1.0 / Math.max(1.0, K);
						break;

					default:
						scaleV = 1.0;
					}

					grid.triVertex(home.g, f, 0, center[0], center[1], L, L,
							scaleU * (cell.getTriangleU(f, 0) + offsetU), scaleV * (cell.getTriangleV(f, 0) + offsetV));
					grid.triVertex(home.g, f, 1, center[0], center[1], L, L,
							scaleU * (cell.getTriangleU(f, 1) + offsetU), scaleV * (cell.getTriangleV(f, 1) + offsetV));
					grid.triVertex(home.g, f, 2, center[0], center[1], L, L,
							scaleU * (cell.getTriangleU(f, 2) + offsetU), scaleV * (cell.getTriangleV(f, 2) + offsetV));

					home.endShape();
				}
			}
		}
	}

	final public void drawTrianglesRegion() {
		double[] center;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {

					home.colorMode(PConstants.HSB);
					home.fill((cell.region[f] * 37) % 256, 255, 255 * (float) cell.drop[f]);
					home.colorMode(PConstants.RGB);
					home.beginShape(PConstants.TRIANGLES);
					triVertices(center, f);

					home.endShape();
				}
			}
		}
	}

	final public void drawTrianglesRegion(PGraphics home) {
		double[] center;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {
					home.colorMode(PConstants.HSB);
					home.fill((cell.region[f] * 37) % 256, 255, 255);
					home.colorMode(PConstants.RGB);
					home.beginShape(PConstants.TRIANGLES);
					triVertices(home, center, f);
					home.endShape();
				}
			}
		}
	}

	final public void drawTrianglesIJK(PGraphics home, float L, float M, float N) {
		double[] center;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {

					home.fill(cell.getCube(f)[0] * 256.0f / L, cell.getCube(f)[1] * 256.0f / M,
							cell.getCube(f)[2] * 256.0f / N);

					home.beginShape(PConstants.TRIANGLES);
					triVertices(home, center, f);

					home.endShape();
				}
			}
		}
	}

	final public void drawTrianglesZ(float hf, int counter) {
		drawTrianglesZ(home.g, hf, counter);
	}

	final public void drawTrianglesZ(PGraphics home, float hf, int counter) {
		double[] center;
		float hue;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {
					home.colorMode(PConstants.HSB);
					hue = hf * cell.z[f] + counter;
					if (hue < 0)
						hue += 256 * ((int) (hue / (-256)) + 1);
					home.fill(hue % 256, 255, 255);
					home.colorMode(PConstants.RGB);
					home.beginShape(PConstants.TRIANGLES);
					triVertices(home, center, f);
					home.endShape();
				}
			}
		}
	}

	final public void drawTrianglesZRegion(float hf, int counter) {
		drawTrianglesZRegion(home.g, hf, counter);
	}

	final public void drawTrianglesZRegion(PGraphics home, float hf, int counter) {
		double[] center;
		float hue;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {
					home.colorMode(PConstants.HSB);
					hue = hf * cell.z[f] + counter + 37 * cell.region[f];
					if (hue < 0)
						hue += 256 * ((int) (hue / (-256)) + 1);
					home.fill(hue % 256, 255, 255);
					home.colorMode(PConstants.RGB);
					home.beginShape(PConstants.TRIANGLES);
					triVertices(home, center, f);

					home.endShape();
				}
			}
		}
	}

	final public void drawTrianglesPart(PGraphics home, float ho, float hf, float hr, float zo, float zf, float zo2,
			float zf2, float oo, float of) {
		double[] center;
		float hue;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {
					home.colorMode(PConstants.HSB);
					hue = (hf * cell.part[f]) % hr;
					hue = ho + hue;
					if (hue < 0)
						hue += 256 * ((int) (hue / (-256)) + 1);
					home.fill(hue % 256, zo + zf * cell.z[f], oo + of * cell.orientation[f] + zo2 + zf2 * cell.z[f]);
					home.colorMode(PConstants.RGB);
					home.beginShape(PConstants.TRIANGLES);
					triVertices(home, center, f);

					home.endShape();
				}
			}
		}
	}

	final public void drawTrianglesPart(float ho, float hf, float hr, float zo, float zf, float zo2, float zf2,
			float oo, float of) {
		drawTrianglesPart(home.g, ho, hf, hr, zo, zf, zo2, zf2, oo, of);

	}

	final public void drawTrianglesWithPart(PGraphics home, int part) {
		double[] center;

		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1 && cell.part[f] == part) {

					home.beginShape(PConstants.TRIANGLES);
					triVertices(home, center, f);

					home.endShape();
				}
			}
		}
	}

	final public void drawTriangles(int[] colors) {
		double[] center;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1) {

					home.fill(colors[10 * cell.palette[f] + cell.orientation[f]]);
					home.stroke(colors[10 * cell.palette[f] + cell.orientation[f]]);
					home.beginShape(PConstants.TRIANGLES);
					triVertices(center, f);

					home.endShape();
				}
			}
		}
	}

	final public void drawTriangles(int zmin, int zmax) {
		double[] center;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1 && cell.z[f] >= zmin && cell.z[f] < zmax) {
					home.beginShape(PConstants.TRIANGLES);
					triVertices(center, f);
					home.endShape();
				}
			}
		}
	}

	final public void drawTriangles(int[] colors, int zmin, int zmax) {
		double[] center;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1 && cell.z[f] >= zmin && cell.z[f] < zmax) {

					home.fill(colors[10 * cell.palette[f] + cell.orientation[f]]);
					home.noStroke();// stroke(colors[10 * cell.colorIndices[f] + cell.orientations[f]]);
					home.beginShape(PConstants.TRIANGLES);
					triVertices(center, f);
					home.endShape();
				}
			}
		}
	}

	final public void drawTriangles(int[] colors, int zmin, int zmax, int znear, int zfar, int mini, int maxi, int minj,
			int maxj, int mink, int maxk) {
		double[] center;
		for (WB_IsoGridCell cell : grid.cells.values()) {
			center = getGridCoordinates(cell.getQ(), cell.getR());
			for (int f = 0; f < cell.getNumberOfTriangles(); f++) {
				if (cell.orientation[f] > -1 && cell.z[f] >= zmin && cell.z[f] < zmax && cell.cubei[f] >= mini
						&& cell.cubei[f] < maxi && cell.cubej[f] >= minj && cell.cubej[f] < maxj
						&& cell.cubek[f] >= mink && cell.cubek[f] < maxk) {
					home.fill(color(colors[10 * cell.palette[f] + cell.orientation[f]], cell.z[f], zfar, znear));
					home.stroke(color(colors[10 * cell.palette[f] + cell.orientation[f]], cell.z[f], zfar, znear));
					home.beginShape(PConstants.TRIANGLES);
					triVertices(center, f);

					home.endShape();
				}
			}
		}
	}

	final public double[] getGridCoordinates(double q, double r) {
		return grid.getGridCoordinates(q, r, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
	}

	final public int[] getTriangleAtGridCoordinates(double x, double y) {
		return grid.getTriangleAtGridCoordinates(x, y, centerX, centerY, L, (YFLIP ? -1.0 : 1.0) * L);
	}

	private int index(final int i, final int j, final int k) {

		if (i > -1 && j > -1 && k > -1 && i < I && j < J && k < K) {
			return k + j * K + i * JK;
		} else {
			return -1;
		}
	}

	final static int color(final int color, int z, int zmin, int zmax) {
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = (color) & 0xff;
		double f = (z - zmin) / (double) (zmax - zmin);
		f = Math.min(Math.max(f, 0.0), 1.0);
		return 255 << 24 | ((int) Math.round(f * r)) << 16 | ((int) Math.round(f * g)) << 8 | ((int) Math.round(f * b));
	}

	final double random(double v) {
		return randomGen.nextDouble() * v;
	}

	final double random(double v, double w) {
		return v + randomGen.nextDouble() * (w - v);
	}

}
