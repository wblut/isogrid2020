package wblut.isogrid;

public class WB_PixelFont {

	public static final int code3x3(char c) {
		/*
		 * 1 | 2 | 4 8 | 16 | 32 64 | 128 | 256
		 */
		switch (c) {
		case 'a':
			return 378;
		case 'b':
			return 251;
		case 'c':
			return 463;
		case 'd':
			return 235;
		case 'e':
			return 479;
		case 'f':
			return 95;
		case 'g':
			return 507;
		case 'h':
			return 381;
		case 'i':
			return 471;
		case 'j':
			return 230;
		case 'k':
			return 349;
		case 'l':
			return 457;
		case 'm':
			return 383;
		case 'n':
			return 363;
		case 'o':
			return 495;
		case 'p':
			return 127;
		case 'q':
			return 426;
		case 'r':
			return 347;
		case 's':
			return 214;
		case 't':
			return 151;
		case 'u':
			return 493;
		case 'v':
			return 173;
		case 'w':
			return 509;
		case 'x':
			return 341;
		case 'y':
			return 149;
		case 'z':
			return 403;
		case '0':
			return 495;
		case '1':
			return 467;
		case '2':
			return 403;
		case '3':
			return 503;
		case '4':
			return 317;
		case '5':
			return 214;
		case '6':
			return 505;
		case '7':
			return 295;
		case '8':
			return 511;
		case '9':
			return 319;
		case '-':
			return 56;
		case '.':
			return 64;
		case ',':
			return 72;
		case ':':
			return 65;
		case '\'':
			return 2;
		case '!':
			return 73;
		case '?':
			return 91;
		default:
			return 0;
		}
	}

	

	public static boolean[][][] getChar3x3(char c, int thickness, boolean flipX, boolean flipY) {
		int id=0;
		int code = code3x3(c);
		boolean[][][] grid = new boolean[3][3][thickness];
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 3; i++) {
				if (((code >> id) & 1) == 1) {
					for (int k = 0; k < thickness; k++) {
						grid[flipX?2-i:1][flipY?2-j:1][k] = true;
					}
				}
				id++;
			}
		}
		
		return grid;
	}

	public static boolean[][][] getText3x3(String text, int thickness, int spacing, boolean flipX, boolean flipY) {
		String lctext = text.toLowerCase();
		if(flipX) lctext=new StringBuilder(lctext).reverse().toString();
		int count = 0;
		for (int i = 0; i < lctext.length(); i++) {
			if (Character.isWhitespace(lctext.charAt(i)))
				count++;
		}
		int gridsize = 4*lctext.length() + count * (spacing - 4);
		boolean[][][] charGrid;
		boolean[][][] grid = new boolean[gridsize][3][thickness];
		int gridIndex = 0;
		for (int i = 0; i < lctext.length(); i++) {
			if (Character.isWhitespace(lctext.charAt(i))) {
				gridIndex += spacing+1;
			} else {
				charGrid = getChar3x3(text.charAt(i), thickness, flipX, flipY);
				for (int ci = 0; ci < 3; ci++) {
					for (int cj = 0; cj < 3; cj++) {
						for (int ck = 0; ck < thickness; ck++) {
							grid[gridIndex + ci][cj][ck] = charGrid[ci][cj][ck];
						}
					}
				}
				gridIndex += 4;
			}

		}

		return grid;
	}
	
	public static final int[] code3xN(char c, boolean variant) {
		/*
		 * 
		 * 1 | 2 | 4
		 * 
		 * 0 1 2
		 * 
		 * 3 . . 3
		 * 
		 * 4 5 6
		 * 
		 */
		switch (c) {
		case '/':
			return new int[] { 4, 4, 2, 2, 1, 1, 1 };
		case 'a':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 7, 5, 5 };
			} else {
				return new int[] { 7, 5, 7, 5, 5, 5, 5 };
			}

		case 'b':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 3, 5, 7 };
			} else {
				return new int[] { 7, 5, 3, 5, 5, 5, 7 };
			}
		case 'c':
			return new int[] { 7, 1, 1, 1, 1, 1, 7 };
		case 'd':
			return new int[] { 3, 5, 5, 5, 5, 5, 3 };
		case 'e':
			if (variant) {
				return new int[] { 7, 1, 1, 1, 3, 1, 7 };
			} else {
				return new int[] { 7, 1, 3, 1, 1, 1, 7 };
			}
		case 'f':
			if (variant) {
				return new int[] { 7, 1, 1, 1, 3, 1, 1 };
			} else {
				return new int[] { 7, 1, 3, 1, 1, 1, 1 };
			}
		case 'g':
			if (variant) {
				return new int[] { 6, 1, 1, 1, 5, 5, 7 };
			} else {
				return new int[] { 6, 1, 1, 5, 5, 5, 7 };
			}
		case 'h':
			if (variant) {
				return new int[] { 5, 5, 5, 5, 7, 5, 5 };
			} else {
				return new int[] { 5, 5, 7, 5, 5, 5, 5 };
			}
		case 'i':
			return new int[] { 7, 2, 2, 2, 2, 2, 7 };
		case 'j':
			return new int[] { 7, 2, 2, 2, 2, 2, 3 };
		case 'k':
			if (variant) {
				return new int[] { 5, 5, 5, 5, 3, 5, 5 };
			} else {
				return new int[] { 5, 5, 3, 5, 5, 5, 5 };
			}
		case 'l':
			return new int[] { 1, 1, 1, 1, 1, 1, 7 };
		case 'm':
			return new int[] { 5, 7, 7, 5, 5, 5, 5 };
		case 'n':
			return new int[] { 3, 7, 5, 5, 5, 5, 5 };
		case 'o':
			return new int[] { 2, 5, 5, 5, 5, 5, 2 };
		case 'p':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 7, 1, 1 };
			} else {
				return new int[] { 7, 5, 7, 1, 1, 1, 1 };
			}
		case 'q':
			return new int[] { 2, 5, 5, 5, 5, 5, 6 };
		case 'r':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 3, 5, 5 };
			} else {
				return new int[] { 7, 5, 3, 5, 5, 5, 5 };
			}
		case 's':
			if (variant) {
				return new int[] { 7, 1, 1, 1, 7, 4, 7 };
			} else {
				return new int[] { 7, 1, 7, 4, 4, 4, 7 };
			}
		case 't':
			return new int[] { 7, 2, 2, 2, 2, 2, 2 };
		case 'u':
			return new int[] { 5, 5, 5, 5, 5, 5, 7 };
		case 'v':
			return new int[] { 5, 5, 5, 5, 5, 5, 2 };
		case 'w':
			return new int[] { 5, 5, 5, 5, 7, 7, 2 };
		case 'x':
			if (variant) {
				return new int[] { 5, 5, 5, 5, 2, 5, 5 };
			} else {
				return new int[] { 5, 5, 2, 5, 5, 5, 5 };
			}
		case 'y':
			if (variant) {
				return new int[] { 5, 5, 5, 5, 5, 2, 2 };
			} else {
				return new int[] { 5, 5, 2, 2, 2, 2, 2 };
			}
		case 'z':
			if (variant) {
				return new int[] { 7, 4, 4, 4, 7, 1, 7 };
			} else {
				return new int[] { 7, 4, 7, 1, 1, 1, 7 };
			}
		case '0':
			return new int[] { 7, 5, 5, 5, 5, 5, 7 };
		case '1':
			return new int[] { 3, 2, 2, 2, 2, 2, 7 };
		case '2':
			if (variant) {
				return new int[] { 7, 4, 4, 4, 7, 1, 7 };
			} else {
				return new int[] { 7, 4, 7, 1, 1, 1, 7 };
			}
		case '3':
			if (variant) {
				return new int[] { 7, 4, 4, 4, 7, 4, 7 };
			} else {
				return new int[] { 7, 4, 7, 4, 4, 4, 7 };
			}
		case '4':
			if (variant) {
				return new int[] { 5, 5, 5, 5, 7, 4, 4 };
			} else {
				return new int[] { 5, 5, 7, 4, 4, 4, 4 };
			}
		case '5':
			if (variant) {
				return new int[] { 7, 1, 1, 1, 7, 4, 7 };
			} else {
				return new int[] { 7, 1, 7, 4, 4, 4, 7 };
			}
		case '6':
			if (variant) {
				return new int[] { 7, 1, 1, 1, 7, 5, 7 };
			} else {
				return new int[] { 7, 1, 7, 5, 5, 5, 7 };
			}
		case '7':
			return new int[] { 7, 4, 4, 4, 4, 4, 4 };
		case '8':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 7, 5, 7 };
			} else {
				return new int[] { 7, 5, 7, 5, 5, 5, 7 };
			}
		case '9':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 7, 4, 7 };
			} else {
				return new int[] { 7, 5, 7, 4, 4, 4, 7 };
			}
		case '.':
			return new int[] { 0, 0, 0, 0, 0, 0, 1 };
		case ',':
			return new int[] { 0, 0, 0, 0, 0, 1, 1 };
		case '\'':
			return new int[] { 1, 1, 0, 0, 0, 0, 0 };
		case '-':
			return new int[] { 0, 0, 7, 0, 0, 0, 0 };
		case ':':
			return new int[] { 0, 2, 0, 0, 0, 2, 0 };
		case '?':
			return new int[] { 7, 4, 3, 1, 1, 0, 1 };
		case '!':
			return new int[] { 1, 1, 1, 1, 1, 0, 1 };
		default:
			return new int[] { 0, 0, 0, 0, 0, 0, 0 };
		}
	}

	public static boolean[][][] getChar3xN(char c, int N, int thickness, boolean variant, boolean flipX, boolean flipY) {
		int[] codes = code3xN(c, (N <= 5) ? false : variant);
		int code;
		boolean[][][] grid = new boolean[3][N][thickness];
		for (int j = 0; j < N; j++) {
			if (j < 3) {
				code = codes[j];
			} else if (j == N - 3) {
				code = codes[4];
			} else if (j == N - 2) {
				code = codes[5];
			} else if (j == N - 1) {
				code = codes[6];
			} else {
				code = codes[3];
			}
			for (int i = 0; i < 3; i++) {
				if (((code >> i) & 1) == 1) {
					for (int k = 0; k < thickness; k++) {
						grid[flipX?2-i:i][flipY?N-1-j:j][k] = true;
					}
				}
			}
		}
		return grid;
	}



	public static boolean[][][] getText3xN(String text, int N, int thickness, int spacing, boolean alt, boolean flipX, boolean flipY) {
		String lctext = text.toLowerCase();
		if(flipX) lctext=new StringBuilder(lctext).reverse().toString();
		int count = 0;
		for (int i = 0; i < lctext.length(); i++) {
			if (Character.isWhitespace(lctext.charAt(i)))
				count++;
		}
		int gridsize = 4*lctext.length() + count * (spacing - 4);
		boolean[][][] charGrid;
		boolean[][][] grid = new boolean[gridsize][N][thickness];
		int gridIndex = 0;
		for (int i = 0; i < lctext.length(); i++) {
			if (Character.isWhitespace(lctext.charAt(i))) {
				gridIndex += spacing+1;
			} else {
				charGrid = getChar3xN(text.charAt(i), N, thickness, alt, flipX, flipY);
				for (int ci = 0; ci < 3; ci++) {
					for (int cj = 0; cj < N; cj++) {
						for (int ck = 0; ck < thickness; ck++) {
							grid[gridIndex + ci][cj][ck] = charGrid[ci][cj][ck];
						}
					}
				}
				gridIndex += 4;
			}

		}

		return grid;
	}

	
}
