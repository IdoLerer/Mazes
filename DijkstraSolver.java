import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraSolver implements MazeSolver {
	
	FibonacciHeap<Maze.MazeNode> heap = new FibonacciHeap<>();
	Map<Maze.MazeNode, Integer> distance = new HashMap<>();
	Set<Maze.MazeNode> visited = new HashSet<>();
	Map<Maze.MazeNode, Maze.MazeNode> prev = new HashMap<>();
	Map<Maze.MazeNode, FibonacciHeap<Maze.MazeNode>.HeapNode> heapNodes = new HashMap<>();
	
	@Override
	public List<Maze.MazeNode> solve(Maze maze) {

		distance.put(maze.start, 0);
		heapNodes.put(maze.start, heap.insert(0, maze.start));
		while (!heap.empty()) {
			Maze.MazeNode node = heap.deleteMin();
			if (node == maze.end) {
				return generateSolutionList(maze.start, maze.end);
			}
			visited.add(node);
			for (Maze.MazeNode neighbor : node.neighbors) {
				if (neighbor != null && !visited.contains(neighbor)) {
					int dist = distance.get(node) + length(node, neighbor);
					if (!distance.containsKey(neighbor)) {
						distance.put(neighbor, dist);
						heapNodes.put(neighbor, heap.insert(dist, neighbor));
						prev.put(neighbor, node);
					} else {
						if (dist < distance.get(neighbor)) {
							distance.put(neighbor, dist);
							prev.put(neighbor, node);
							heap.decreaseKey(heapNodes.get(neighbor), distance.get(neighbor) - dist);
						}
					}
				}
			}
		}
		return null;
	}
	private int length(Maze.MazeNode x, Maze.MazeNode y) {
		return Math.abs(x.position[0] - y.position[0]) + Math.abs(x.position[1] - y.position[1]);
	}
	
	private List<Maze.MazeNode> generateSolutionList(Maze.MazeNode start, Maze.MazeNode end){
		LinkedList<Maze.MazeNode> list = new LinkedList<>();
		while(end != start) {
			list.addFirst(end);
			end = prev.get(end);
		}
		list.addFirst(end);
		return list;
	}
}
