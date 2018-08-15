
public class UnionFind<T> {
	
	public UFNode makeSet(T info) {
		return new UFNode(info);
	}
	
	// Recursive find with path compression.
	
	public UFNode find(UFNode x) {
	   if (x.parent != x)
	     x.parent = find(x.parent);
	   return x.parent;
	}
	
	public void union(UFNode x, UFNode y) {
		link(find(x), find(y));
	}
	
	private void link(UFNode x, UFNode y) {
		if (x.rank > y.rank) {
			y.parent = x;
		} else {
			x.parent = y;
			if (x.rank == y.rank) {
				y.rank++;
			}
		}
	}
	
	public class UFNode {
		private int rank;
		private T info;
		private UFNode parent;

		private UFNode(T info) {
			this.rank = 0;
			this.info = info;
			this.parent = this;
		}
		
		public T getInfo() {
			return this.info;
		}
	}
}
