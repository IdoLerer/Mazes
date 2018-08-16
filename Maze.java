import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class Maze {
	MazeNode start;
	MazeNode end;

	/**
	 * 
	 * neighbors := [top, right, bottom, left]
	 * 
	 *
	 */
	public class MazeNode {
		int[] position = new int[2];
		MazeNode[] neighbors = new MazeNode[4];

		public MazeNode(int row, int col) {
			position[0] = row;
			position[1] = col;
		}

		@Override
		public String toString() {
			return "(" + position[0] + "," + position[1] + ")";
		}
	}

	public Maze() {

	}

	public Maze(BufferedImage bi) {
		byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		int width = bi.getWidth();
		int height = bi.getHeight();
		MazeNode[] topRow = new MazeNode[width];
		for (int i = 0; i < width; i++) {
			if (pixels[i] != 0) {
				start = new MazeNode(0, i);
				topRow[i] = start;
				break;
			}
		}
		MazeNode prev = null;
		MazeNode cur = null;
		int offset = 0;
		for (int i = 1; i < height - 1; i += 2) {
			for (int j = 1; j < width - 1; j += 2) {
				offset = i * width;
				// if wall on left:
				if (pixels[offset + j - 1] == 0) {
					// if wall on left and no wall on right, new node
					if (pixels[offset + j + 1] != 0) {
						prev = new MazeNode(i, j);
						if (pixels[offset - width + j] != 0) {
							prev.neighbors[0] = topRow[j];
							topRow[j].neighbors[2] = prev;
						}
						topRow[j] = prev;
					} else {
						// if wall on left and on top, new node.
						if (pixels[offset - width + j] == 0) {
							prev = new MazeNode(i, j);
							topRow[j] = prev;
						} else {
							// if wall on left, right and bottom, new node.
							if (pixels[offset + width + j] == 0) {
								prev = new MazeNode(i, j);
								// there must be a node above
								prev.neighbors[0] = topRow[j];
								topRow[j].neighbors[2] = prev;
								topRow[j] = prev;
							}
						}
					}
					// if no wall on left:
				} else {
					// if no wall on left and wall on right, new node.
					if (pixels[offset + j + 1] == 0) {
						cur = new MazeNode(i, j);
						prev.neighbors[1] = cur;
						cur.neighbors[3] = prev;
						if (pixels[offset - width + j] != 0) {
							cur.neighbors[0] = topRow[j];
							topRow[j].neighbors[2] = cur;
						}
						prev = cur;
						topRow[j] = prev;
					} else {
						// if no wall on left, right and top, new node.
						if (pixels[offset - width + j] != 0) {
							cur = new MazeNode(i, j);
							prev.neighbors[1] = cur;
							cur.neighbors[3] = prev;
							cur.neighbors[0] = topRow[j];
							topRow[j].neighbors[2] = cur;
							prev = cur;
							topRow[j] = prev;
							// if no wall on left and bottom, new node
						} else {
							if (pixels[offset + width + j] != 0) {
								cur = new MazeNode(i, j);
								prev.neighbors[1] = cur;
								cur.neighbors[3] = prev;
								prev = cur;
								topRow[j] = prev;
							}
						}
					}
				}
			}
		}
		offset = (height - 1) * width;
		for (int i = offset; i < width * height; i++) {
			if (pixels[i] != 0) {
				end = new MazeNode(height - 1, i - offset);
				if (pixels[i - width] != 0) {
					end.neighbors[0] = topRow[i - offset];
					topRow[i - offset].neighbors[2] = end;
				}
				break;
			}
		}
	}

	public List<Maze.MazeNode> solve() {
		return solve(new LeftTurnSolver());
	}

	public List<Maze.MazeNode> solve(MazeSolver solver) {
		return solver.solve(this);
	}

	public void drawRoute(BufferedImage bi, List<MazeNode> list, byte color) {
		byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		int offset = bi.getWidth();
		MazeNode prev = list.get(0);
		pixels[prev.position[0] * offset + prev.position[1]] = color;
		MazeNode cur;
		for (int i = 1; i < list.size(); i++) {
			cur = list.get(i);
			// if horizontal
			if (prev.position[0] == cur.position[0]) {
				int min = Math.min(prev.position[1], cur.position[1]);
				int max = Math.max(prev.position[1], cur.position[1]);
				for (int j = min; j <= max; j++) {
					pixels[offset*cur.position[0] + j] = color;
				}
			}
			//if vertical
			if (prev.position[1] == cur.position[1]) {
				int min = Math.min(prev.position[0], cur.position[0]);
				int max = Math.max(prev.position[0], cur.position[0]);
				for (int j = min; j <= max; j++) {
					pixels[offset*j + cur.position[1]] = color;
				}
			}
			prev = cur;
		}
	}

	public static void main(String[] args) {
		BufferedImage bi = MazeGenerator.generateRandomtMazeImage(1151,1201, 98);
		try {
			ImageIO.write(bi, "jpg", new File("Maze.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Maze maze = new Maze(bi);
		maze.drawRoute(bi, maze.solve(), (byte)110);
		maze.drawRoute(bi, maze.solve(new DijkstraSolver()), (byte)150);
		try {
			ImageIO.write(bi, "jpg", new File("Solution.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
