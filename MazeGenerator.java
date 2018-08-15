import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MazeGenerator {

	public static byte[][] generateRandomMazeByteMap(int height, int width) {
		//the byteMap to be returned
		byte[][] pixels = new byte[height][width];
		
		//inner class used to represent inner walls in the maze
		class Wall {
			UnionFind<Maze.MazeNode>.UFNode x;
			UnionFind<Maze.MazeNode>.UFNode y;

			public Wall(UnionFind<Maze.MazeNode>.UFNode x, UnionFind<Maze.MazeNode>.UFNode y) {
				this.x = x;
				this.y = y;
			}

		}
		/*a union-find data structure, initialized to have a set for every cell in the maze.
		 * when all the cells are in the same set, the maze is complete.
		*/
		Maze maze = new Maze();
		UnionFind<Maze.MazeNode> uf = new UnionFind<>();
		int rows = (height - 1) / 2;
		int cols = (width - 1) / 2;
		@SuppressWarnings("unchecked")
		UnionFind<Maze.MazeNode>.UFNode[][] ufN = new UnionFind.UFNode[rows][cols];
		
		
		Maze.MazeNode mn;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				mn = maze.new MazeNode(1 + i * 2, 1 + j * 2);
				pixels[mn.position[0]][mn.position[1]] = -1;
				ufN[i][j] = uf.makeSet(mn);
			}
		}
		
		List<Wall> walls = new LinkedList<>();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols - 1; j++) {
				walls.add(new Wall(ufN[i][j], ufN[i][j + 1]));
			}
		}
		for (int i = 0; i < rows - 1; i++) {
			for (int j = 0; j < cols; j++) {
				walls.add(new Wall(ufN[i][j], ufN[i + 1][j]));
			}
		}
		
		Collections.shuffle(walls);
		Wall wall;
		for (Iterator<Wall> iter = walls.iterator(); iter.hasNext();) {
			wall = iter.next();
			if (uf.find(wall.x) != uf.find(wall.y)) {
				uf.union(wall.x, wall.y);
				iter.remove();
				int x = (wall.x.getInfo().position[0] + wall.y.getInfo().position[0]) / 2;
				int y = (wall.x.getInfo().position[1] + wall.y.getInfo().position[1]) / 2;
				pixels[x][y] = -1;
			}
		}
		pixels[0][1] = -1;
		pixels[height-1][width-2] = -1;
		return pixels;
	}
	
	public static BufferedImage generateImageFromByteMap(byte[][] map) {
		BufferedImage bi = new BufferedImage(map[0].length, map.length, BufferedImage.TYPE_BYTE_GRAY);
		byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		int i = 0;
		for (byte[] bs : map) {
			for (byte b : bs) {
				pixels[i] = b;
				i++;
			}
		}
		return bi;
	}
	
	public static BufferedImage generateRandomMazeImage(int height, int width) {
		return generateImageFromByteMap(generateRandomMazeByteMap(height, width));
	}
}
