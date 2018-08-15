import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LeftTurnSolver implements MazeSolver {

	@Override
	public List<Maze.MazeNode> solve(Maze maze) {
		LinkedList<Maze.MazeNode> list = new LinkedList<>();
		Set<Maze.MazeNode> visited = new HashSet<>();

		Maze.MazeNode start = maze.start;
		Maze.MazeNode end = maze.end;

		finder: while (true) {
			visited.add(start);
			if (start == end) {
				list.addFirst(end);
				return list;
			}
			for (Maze.MazeNode neighbor : start.neighbors) {
				if (neighbor != null && !visited.contains(neighbor)) {
					list.add(start);
					start = neighbor;
					continue finder;
				}
			}
			if (list.isEmpty())
				break;
			else
				start = list.removeLast();
		}
		return null;
	}
}
