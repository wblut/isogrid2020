package wblut.isogrid;

public class WB_PixelFont {

	public static final int code3x3(char c) {
		/*
		 * 1 | 2 | 4
		 * 8 | 16 | 32
		 * 64 | 128 | 256
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

	

	public static boolean[][][] getChar3x3(char c, int extrude, boolean flipX, boolean flipY) {
		int id=0;
		int code = code3x3(c);
		boolean[][][] grid = new boolean[3][3][extrude];
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 3; i++) {
				if (((code >> id) & 1) == 1) {
					for (int k = 0; k < extrude; k++) {
						grid[flipX?2-i:i][flipY?2-j:j][k] = true;
					}
				}
				id++;
			}
		}
		
		return grid;
	}

	public static boolean[][][] getText3x3(String text, int extrude, int spacing, int whitespace, int padding, boolean flipX, boolean flipY) {
		String lctext = text.toLowerCase();
		if(flipX) lctext=new StringBuilder(lctext).reverse().toString();
		int spaces = 0;
		int chars=0;
		for (int i = 0; i < lctext.length(); i++) {
			if (Character.isWhitespace(lctext.charAt(i))) {
				spaces++;
			}else{
				chars++;
			}
		}
		
		int gridsize = 3*chars+whitespace*spaces+(chars+spaces-1)*spacing+2*padding;
		boolean[][][] charGrid;
		boolean[][][] grid = new boolean[gridsize][3][extrude];
		int gridIndex = padding;
		for (int i = 0; i < lctext.length(); i++) {
			if (Character.isWhitespace(lctext.charAt(i))) {
				gridIndex +=whitespace+spacing;
			} else {
				charGrid = getChar3x3(lctext.charAt(i), extrude, flipX, flipY);
				for (int ci = 0; ci < 3; ci++) {
					for (int cj = 0; cj < 3; cj++) {
						for (int ck = 0; ck < extrude; ck++) {
							grid[gridIndex + ci][cj][ck] = charGrid[ci][cj][ck];
						}
					}
				}
				gridIndex += 3+spacing;
			}

		}

		return grid;
	}
	
	public static final int[] code3xN(char c, boolean variant) {
		/*
		 * 
		 * 1 | 2 | 4
		 * 
		 * 0
		 * 1
		 * 2
		 * 3
		 * ...
		 * 3
		 * 4
		 * 5
		 * 6
		 * 
		 * 
		 * 7 = moveback
		 */
		switch (c) {
		case '/':
			return new int[] { 4, 4, 2, 2, 1, 1, 1,0};
		case 'a':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 7, 5, 5,0 };
			} else {
				return new int[] { 7, 5, 7, 5, 5, 5, 5,0 };
			}

		case 'b':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 3, 5, 7,0 };
			} else {
				return new int[] { 7, 5, 3, 5, 5, 5, 7,0 };
			}
		case 'c':
			return new int[] { 7, 1, 1, 1, 1, 1, 7,0 };
		case 'd':
			return new int[] { 3, 5, 5, 5, 5, 5, 3,0 };
		case 'e':
			if (variant) {
				return new int[] { 7, 1, 1, 1, 3, 1, 7 ,0};
			} else {
				return new int[] { 7, 1, 3, 1, 1, 1, 7 ,0};
			}
		case 'f':
			if (variant) {
				return new int[] { 7, 1, 1, 1, 3, 1, 1 ,0};
			} else {
				return new int[] { 7, 1, 3, 1, 1, 1, 1 ,0};
			}
		case 'g':
			if (variant) {
				return new int[] { 6, 1, 1, 1, 5, 5, 7 ,0};
			} else {
				return new int[] { 6, 1, 1, 5, 5, 5, 7 ,0};
			}
		case 'h':
			if (variant) {
				return new int[] { 5, 5, 5, 5, 7, 5, 5 ,0};
			} else {
				return new int[] { 5, 5, 7, 5, 5, 5, 5 ,0};
			}
		case 'i':
			return new int[] { 7, 2, 2, 2, 2, 2, 7 ,0};
		case 'j':
			return new int[] { 7, 2, 2, 2, 2, 2, 3 ,0};
		case 'k':
			if (variant) {
				return new int[] { 5, 5, 5, 5, 3, 5, 5 ,0};
			} else {
				return new int[] { 5, 5, 3, 5, 5, 5, 5 ,0};
			}
		case 'l':
			return new int[] { 1, 1, 1, 1, 1, 1, 7 ,0};
		case 'm':
			return new int[] { 5, 7, 7, 5, 5, 5, 5 ,0};
		case 'n':
			return new int[] { 3, 7, 5, 5, 5, 5, 5 ,0};
		case 'o':
			return new int[] { 2, 5, 5, 5, 5, 5, 2 ,0};
		case 'p':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 7, 1, 1 ,0};
			} else {
				return new int[] { 7, 5, 7, 1, 1, 1, 1 ,0};
			}
		case 'q':
			return new int[] { 2, 5, 5, 5, 5, 5, 6 ,0};
		case 'r':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 3, 5, 5 ,0};
			} else {
				return new int[] { 7, 5, 3, 5, 5, 5, 5 ,0};
			}
		case 's':
			if (variant) {
				return new int[] { 7, 1, 1, 1, 7, 4, 7 ,0};
			} else {
				return new int[] { 7, 1, 7, 4, 4, 4, 7 ,0};
			}
		case 't':
			return new int[] { 7, 2, 2, 2, 2, 2, 2 ,0};
		case 'u':
			return new int[] { 5, 5, 5, 5, 5, 5, 7 ,0};
		case 'v':
			return new int[] { 5, 5, 5, 5, 5, 5, 2 ,0};
		case 'w':
			return new int[] { 5, 5, 5, 5, 7, 7, 2 ,0};
		case 'x':
			if (variant) {
				return new int[] { 5, 5, 5, 5, 2, 5, 5 ,0};
			} else {
				return new int[] { 5, 5, 2, 5, 5, 5, 5 ,0};
			}
		case 'y':
			if (variant) {
				return new int[] { 5, 5, 5, 5, 5, 2, 2 ,0};
			} else {
				return new int[] { 5, 5, 2, 2, 2, 2, 2 ,0};
			}
		case 'z':
			if (variant) {
				return new int[] { 7, 4, 4, 4, 7, 1, 7 ,0};
			} else {
				return new int[] { 7, 4, 7, 1, 1, 1, 7 ,0};
			}
		case '0':
			return new int[] { 7, 5, 5, 5, 5, 5, 7 ,0};
		case '1':
			return new int[] { 3, 2, 2, 2, 2, 2, 7 ,0};
		case '2':
			if (variant) {
				return new int[] { 7, 4, 4, 4, 7, 1, 7 ,0};
			} else {
				return new int[] { 7, 4, 7, 1, 1, 1, 7 ,0};
			}
		case '3':
			if (variant) {
				return new int[] { 7, 4, 4, 4, 7, 4, 7 ,0};
			} else {
				return new int[] { 7, 4, 7, 4, 4, 4, 7 ,0};
			}
		case '4':
			if (variant) {
				return new int[] { 5, 5, 5, 5, 7, 4, 4 ,0};
			} else {
				return new int[] { 5, 5, 7, 4, 4, 4, 4 ,0};
			}
		case '5':
			if (variant) {
				return new int[] { 7, 1, 1, 1, 7, 4, 7 ,0};
			} else {
				return new int[] { 7, 1, 7, 4, 4, 4, 7 ,0};
			}
		case '6':
			if (variant) {
				return new int[] { 7, 1, 1, 1, 7, 5, 7 ,0};
			} else {
				return new int[] { 7, 1, 7, 5, 5, 5, 7 ,0};
			}
		case '7':
			return new int[] { 7, 4, 4, 4, 4, 4, 4 ,0};
		case '8':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 7, 5, 7 ,0};
			} else {
				return new int[] { 7, 5, 7, 5, 5, 5, 7 ,0};
			}
		case '9':
			if (variant) {
				return new int[] { 7, 5, 5, 5, 7, 4, 7 ,0};
			} else {
				return new int[] { 7, 5, 7, 4, 4, 4, 7 ,0};
			}
		case '.':
			return new int[] { 0, 0, 0, 0, 0, 0, 1,2 };
		case ',':
			return new int[] { 0, 0, 0, 0, 0, 1, 1,2 };
		case '\'':
			return new int[] { 1, 1, 0, 0, 0, 0, 0,2 };
		case '-':
			return new int[] { 0, 0, 7, 0, 0, 0, 0 ,0};
		case ':':
			return new int[] { 0, 2, 0, 0, 0, 2, 0,2 };
		case '?':
			return new int[] { 7, 4, 3, 1, 1, 0, 1,0 };
		case '!':
			return new int[] { 1, 1, 1, 1, 1, 0, 1 ,2};
		default:
			return new int[] { 0, 0, 0, 0, 0, 0, 0,0 };
		}
	}

	public static boolean[][][] getChar3xN(char c, int N, int extrude, boolean variant, boolean flipX, boolean flipY) {
		int[] codes = code3xN(c, (N <= 5) ? false : variant);
		int code;
		boolean[][][] grid = new boolean[3][N][extrude];
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
					for (int k = 0; k < extrude; k++) {
						grid[flipX?2-i:i][flipY?N-1-j:j][k] = true;
					}
				}
			}
		}
		return grid;
	}



	public static boolean[][][] getText3xN(String text, int N, int extrude, int spacing,int whitespace, int padding,boolean alt, boolean flipX, boolean flipY) {
		String lctext = text.toLowerCase();
		if(flipX) lctext=new StringBuilder(lctext).reverse().toString();
		int spaces = 0;
		int chars=0;
		for (int i = 0; i < lctext.length(); i++) {
			if (Character.isWhitespace(lctext.charAt(i))) {
				spaces++;
			}else{
				chars++;
			}
		}
		
		int gridsize = 3*chars+whitespace*spaces+(chars+spaces-1)*spacing+2*padding;
		boolean[][][] charGrid;
		boolean[][][] grid = new boolean[gridsize][N][extrude];
		int gridIndex = padding;
		for (int i = 0; i < lctext.length(); i++) {
			if (Character.isWhitespace(lctext.charAt(i))) {
				gridIndex += spacing+whitespace;
			} else {
				charGrid = getChar3xN(lctext.charAt(i), N, extrude, alt, flipX, flipY);
				for (int ci = 0; ci < 3; ci++) {
					for (int cj = 0; cj < N; cj++) {
						for (int ck = 0; ck < extrude; ck++) {
							grid[gridIndex + ci][cj][ck] = charGrid[ci][cj][ck];
						}
					}
				}
				int[] codes = code3xN(lctext.charAt(i), (N <= 5) ? false : alt);
				gridIndex += 3+spacing-codes[7];
			}

		}

		return grid;
	}


	
}
