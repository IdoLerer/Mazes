import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Application {
	public static void main(String[] args) {
		byte[][] mazeByteMap = MazeGenerator.generateRandomMazeByteMap(151, 221, 99);
		BufferedImage bi = MazeGenerator.generateImageFromByteMap(mazeByteMap);
		try {
			ImageIO.write(bi, "jpg", new File("Maze.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Maze maze = new Maze(bi);
		maze.drawRoute(bi, maze.solve(new DijkstraSolver()), (byte)127);
		
		BufferedImage bi2 = MazeGenerator.generateImageFromByteMap(mazeByteMap);
		maze.drawRoute(bi2, maze.solve(), (byte)127);
		
		try {
			ImageIO.write(bi2, "jpg", new File("Solution.jpg"));
			ImageIO.write(bi, "jpg", new File("ShortestPathSolution.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
