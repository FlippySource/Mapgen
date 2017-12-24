import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Mapgen {
	public static void main(String[] args) {

		Mapgen mapgen = new Mapgen();
		int boardx = 1000;
		int boardy = 1000;
		int[][] board = new int[boardx][boardy]; // 0 is empty else floor #
		int min = 5;
		int max = 10;
		Random random = new Random();
		int roomcount = random.nextInt((max - min) + 1) + min;
		int pixelfills = (boardx * boardy) / 5;

		board = mapgen.recursiveBranchingFill(board, boardx, boardy, roomcount, pixelfills, mapgen, random);
		board = mapgen.smoothBoard(board);

		mapgen.makeImage(boardx, boardy, board);

	}

	public BufferedImage makeImage(int boardx, int boardy, int[][] board) {

		BufferedImage tempimage = new BufferedImage(boardx + 1, boardy + 1, BufferedImage.TYPE_3BYTE_BGR);
		for (int x = 0; x < boardx; x++) {
			for (int y = 0; y < boardy; y++) {
				if (board[x][y] == 1) {
					tempimage.setRGB(x, y, 15132390);
				}

				else {
					tempimage.setRGB(x, y, 5263440);
				}
			}
		}
		try {

			ImageIO.write(tempimage, "BMP", new File("output.bmp"));

		} catch (Exception e) {
			System.out.println(e);
		}
		return tempimage;
	}

	public int[][] randomFill(int[][] board, int boardx, int boardy, int roomcount, int pixelfills, Random random) {
		for (int x = 0; x < boardx; x++) {
			for (int y = 0; y < boardy; y++) {
				board[x][y] = 0;
			}
		}

		for (int i = 0; i < pixelfills;) {
			int pickx = random.nextInt((boardx - 0));
			int picky = random.nextInt((boardy - 0));

			if (board[pickx][picky] == 0) {
				board[pickx][picky] = 1;
				i++;
			}
		}
		return board;
	}

	public int[][] recursiveBranchingFill(int[][] board, int boardx, int boardy, int roomcount, int pixelfills,
			Mapgen mapgen, Random random) { // fill 1 random, recurse once for each created pixel with x chance of
											// adjacent
		int pixelfill = pixelfills;
		int[][] bd = board;
		int errorcount = 0;

		while (pixelfill > 0) {
			int pickx = random.nextInt(boardx - 1);
			int picky = random.nextInt(boardy - 1);
			if (bd[pickx][picky] == 0) {
				bd[pickx][picky] = 1;
			}

			ArrayList picks = recurseFill(bd, boardx, boardy, random, pickx, picky, pixelfill, 0);

			for (int i = 0; i < picks.size(); i++) {
				Pixel temp = (Pixel) picks.get(i);
				bd[temp.x][temp.y] = temp.val;

			}

			pixelfill -= picks.size();
			picks = new ArrayList();
		}

		return bd;

	}

	public ArrayList recurseFill(int[][] bd, int boardx, int boardy, Random rand, int px, int py, int pixelfills,
			int depth) {// something is exiting its recursion early and not handing back up some
						// information
		int[][] board = bd;
		if (depth > 1200) {
			// makeImage(boardx, boardy, board);
			return (new ArrayList());
		}

		if (px == 999 || px == 0 || py == 999 || py == 0) {
			return (new ArrayList());
		}
		try {

			if ((rand.nextInt((10) + 1)) >= 4.1) {

				int nx = 0;
				int ny = 0;
				ArrayList out = new ArrayList();
				ArrayList choices = new ArrayList();

				if (board[px + 1][py + 1] == 0) {
					choices.add(new Pixel(px + 1, py + 1, 1));
				}

				if (board[px][py + 1] == 0) {
					choices.add(new Pixel(px, py + 1, 1));
				}

				if (board[px + 1][py] == 0) {
					choices.add(new Pixel(px + 1, py, 1));
				}

				if (board[px - 1][py - 1] == 0) {
					choices.add(new Pixel(px - 1, py - 1, 1));
				}

				if (board[px - 1][py] == 0) {
					choices.add(new Pixel(px - 1, py, 1));
				}

				if (board[px][py - 1] == 0) {
					choices.add(new Pixel(px, py - 1, 1));
				}

				if (board[px - 1][py + 1] == 0) {
					choices.add(new Pixel(px - 1, py + 1, 1));
				}

				if (board[px + 1][py - 1] == 0) {
					choices.add(new Pixel(px + 1, py - 1, 1));
				}

				if (choices.size() == 0) {
					return (new ArrayList());
				}

				out.add(choices.get(rand.nextInt(choices.size())));
				ny = ((Pixel) out.get(0)).y;
				nx = ((Pixel) out.get(0)).x;

				board[nx][ny] = 1;
				if (pixelfills - out.size() <= 0) {
					return out;
				}

				out.addAll(recurseFill(board, boardx, boardy, rand, nx, ny, pixelfills - out.size(), depth + 1));

				if (pixelfills - out.size() <= 0) {
					return out;
				}

				for (int i = 0; i < out.size(); i++) {
					Pixel temp = (Pixel) out.get(i);

					board[temp.x][temp.y] = temp.val;
				}
				out.addAll(recurseFill(board, boardx, boardy, rand, px, py, pixelfills - out.size(), depth + 1));

				return out;

			}

		}

		catch (Exception e) {
			System.out.println(e);
		}

		return (new ArrayList());
	}

	public int[][] smoothBoard(int[][] board) {

		int changes = 1;
		while (changes > 0) {
			changes = 0;
			for (int x = 1; x < board.length - 2; x++) {
				for (int y = 1; y < board[0].length - 2; y++) {
					int val = board[x][y];
					int samecount = 0;
					if (board[x][y + 1] == val) {
						samecount++;
					}
					if (board[x - 1][y] == val) {
						samecount++;
					}
					if (board[x][y - 1] == val) {
						samecount++;
					}
					if (board[x + 1][y] == val) {
						samecount++;
					}
					if (samecount < 2) {
						if (val == 0) {
							board[x][y] = 1;
						} else {
							board[x][y] = 0;
						}
						changes++;
					}
				}
			}
		}

		return board;
	}

	public int[][] bridgeBuilder(int[][] board) {
		int last = 0;
		int segStart = 0;
		int segEnd = 0;
		for (int y = 0; y < board[0].length; y++) {
			for (int x = 0; x < board.length; x++) {
				if (board[x][y] == 1 && last == 0) {
					segStart = x;
				}

				else if (board[x][y] == 0 && last == 1) {
					segEnd = x;
					// find if there is 1 within 1/12 of the board below to bridge too (at least x
					// len?)
				}

				last = board[x][y];

			}

		}
		return board;
	}

}