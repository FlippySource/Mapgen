import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Mapgen {
	private Random rand = new Random();
	private int [][] board;
	public static void main(String[] args) {

		Mapgen mapgen = new Mapgen();
		int boardx = 1000;
		int boardy = 1000;
		mapgen.board = new int[boardx][boardy]; // 0 is empty else floor #
		int min = 5;
		int max = 10;
		Random random = new Random();
		int roomcount = random.nextInt((max - min) + 1) + min;
		int pixelfills = (boardx * boardy) / 5;

		mapgen.recursiveBranchingFill(boardx, boardy, roomcount, pixelfills, mapgen, random);
		
		/*//and again...
		Mapgen mapgen2 = new Mapgen();
		mapgen2.board = new int[boardx][boardy]; // 0 is empty else floor #
		mapgen2.recursiveBranchingFill(boardx, boardy, roomcount, pixelfills, mapgen, random);
		mapgen.merge(mapgen2.board);
		*/
		
		mapgen.bridgeBuilder(6, 50);
		mapgen.smoothBoard();

		mapgen.makeImage(boardx, boardy);

	}
	
	public int[][] getBoard()
	{
		return board;
	}
	
	private BufferedImage makeImage(int boardx, int boardy) {

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

	private void randomFill(int boardx, int boardy, int roomcount, int pixelfills, Random random) {
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
	}

	private void recursiveBranchingFill(int boardx, int boardy, int roomcount, int pixelfills,
			Mapgen mapgen, Random random) { // fill 1 random, recurse once for each created pixel with x chance of
											// adjacent
		int pixelfill = pixelfills;

		while (pixelfill > 0) {
			int pickx = random.nextInt(boardx - 1);
			int picky = random.nextInt(boardy - 1);
			if (board[pickx][picky] == 0) {
				board[pickx][picky] = 1;
			}

			ArrayList<Pixel> picks = recurseFill(boardx, boardy, random, pickx, picky, pixelfill, 0);

			for (int i = 0; i < picks.size(); i++) {
				Pixel temp = (Pixel) picks.get(i);
				board[temp.x][temp.y] = temp.val;

			}

			pixelfill -= picks.size();
			picks = new ArrayList<Pixel>();
		}

	}

	private ArrayList<Pixel> recurseFill(int boardx, int boardy, Random rand, int px, int py, int pixelfills,
			int depth) {// something is exiting its recursion early and not handing back up some
						// information
		if (depth > 1200) {
			// makeImage(boardx, boardy, board);
			return (new ArrayList<Pixel>());
		}

		if (px == 999 || px == 0 || py == 999 || py == 0) {
			return (new ArrayList<Pixel>());
		}
		try {

			if ((rand.nextInt((10) + 1)) >= 4.1) {

				int nx = 0;
				int ny = 0;
				ArrayList<Pixel> out = new ArrayList<Pixel>();
				ArrayList<Pixel> choices = new ArrayList<Pixel>();

				for(int i = -1; i <= 1; i++)
				{
					for (int j = -1; j <= 1; j++)
					{
						if (board[px + i][py +  j] == 0) 
						{
							if (!( i== 0 && j == 0))
							{
								choices.add(new Pixel(px + i, py + j, 1));
							}
						}
					}
				}

				if (choices.size() == 0) {
					return (new ArrayList<Pixel>());
				}

				out.add(choices.get(rand.nextInt(choices.size())));
				ny = ((Pixel) out.get(0)).y;
				nx = ((Pixel) out.get(0)).x;

				board[nx][ny] = 1;
				if (pixelfills - out.size() <= 0) {
					return out;
				}

				out.addAll(recurseFill(boardx, boardy, rand, nx, ny, pixelfills - out.size(), depth + 1));

				if (pixelfills - out.size() <= 0) {
					return out;
				}

				for (int i = 0; i < out.size(); i++) {
					Pixel temp = (Pixel) out.get(i);

					board[temp.x][temp.y] = temp.val;
				}
				out.addAll(recurseFill(boardx, boardy, rand, px, py, pixelfills - out.size(), depth + 1));

				return out;

			}

		}

		catch (Exception e) {
			System.out.println(e);
		}

		return (new ArrayList<Pixel>());
	}

	private void smoothBoard() {

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
	}

	private void bridgeBuilder(int bridgeWidth, int bridgeLength) {
		int xlen = board.length;
		int ylen = board [0].length;
		int curlen = bridgeLength;
		int curwid = bridgeWidth;
		//horizontal first
		for (int x = 0; x < board.length - 2; x++) 
		{
			curlen = (x + bridgeLength >= xlen - 1)? (xlen - (x + 1)) :  bridgeLength;
			for (int y = 0; y < board[x].length - 2; y++) {
				if (board [x][y] == 1 && board [x + 1][y] + board[x + 2][y] == 0)
				{
					curwid = (y + bridgeWidth >= ylen - 1)?  (ylen - (y + 1)) : bridgeWidth;
					for (int i = 2; i < curlen; i++)
					{
						if (board[x + i][y] == 1)//fill one pixel if opp then for each width do it again even if redundant
						{
							if(rand.nextInt(100) == 0)
							{
								bridge( x, y, curwid, curlen, 'H');
							}
							break;
						}
					}
				}
			}
		} // horizontal done
		//vert next
		for (int x = 0; x < board.length - 2; x++) 
		{
			curwid = (x + bridgeWidth >= xlen - 1)? (xlen - (x + 1)) : bridgeWidth;
			for (int y = 0; y < board[x].length - 2; y++) {
				if (board [x][y] == 1 && board [x][y + 1] + board[x][y + 2] == 0)
				{
					curlen = (y + bridgeLength >= ylen - 1)? (ylen - (y + 1)) : bridgeLength;
					for (int i = 2; i < curlen; i++)
					{
						if (board[x][y + i] == 1)//fill one pixel if opp then for each width do it again even if redundant
						{
							if(rand.nextInt(100) == 0) //maybe relate chance to length
							{
								bridge(x, y, curwid, curlen, 'V');
							}
							break;
						}
					}
				}
			}
		}
		
	}
	
	private void bridge(int x, int y, int curwid, int curlen, char dir)
	{
	
		for( int i = 0; i < curlen; i++)
		{
			for( int j = 0; j < curwid; j++)
			{
				if( dir == 'H') //h for horizontal, probably v for vertical
				{
					board [x + i][ y + j] = 1;
				}
				else
				{
					board [x + j][ y + i] = 1;
				}
			}
		}
	}

	private void merge(int[][] board2)
	{
		for (int i = 0; i < board.length; i++)
		{
			for (int j = 0; j < board[i].length; j++)
			{
				if(board[i][j] == 1 || board2[i][j] == 1)
				{
					board[i][j] = 1;
				}
			}
		}
	}
}
