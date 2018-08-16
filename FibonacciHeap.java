import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap<E> {
	private int size = 0;
	private HeapNode minNode;
	private int markedNodes = 0;
	private int treeCount = 0;
	private static int cutsCounter = 0;
	private static int linksCounter = 0;

	/**
	 * public boolean empty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap is empty.
	 * 
	 */
	public boolean empty() {
		return minNode == null;
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts
	 * it into the heap.
	 */
	public HeapNode insert(int key, E value) {
		HeapNode newNode = new HeapNode(key, value);
		if (this.empty()) { // In case of an empty heap mark new node as minNode.
			this.minNode = newNode;
			this.size = 1;
			this.treeCount = 1;
		} else { // Meld two FibonacciHeaps, one of which with the new key.
			FibonacciHeap<E> fibHeap = new FibonacciHeap<>();
			newNode = fibHeap.insert(key, value);
			this.meld(fibHeap);
		}
		return newNode;
	}

	/*
	 * Insert y to x sibling list.
	 */
	private void insertToSiblingList(HeapNode x, HeapNode y) {
		HeapNode successor1 = y.next;
		HeapNode successor2 = x.next;

		// Merge the pointers of original heap with x.
		y.next = successor2;
		successor2.prev = y;
		x.next = successor1;
		successor1.prev = x;
	}

	/*
	 * Link nodes X and Y
	 */
	private HeapNode linkNodes(HeapNode x, HeapNode y) {
		linksCounter++;
		if (x.key < y.key) {
			if (x.child != null) {
				clearSiblingList(y);
				insertToSiblingList(x.child, y);
			} else {
				x.child = y;
			}
			y.parent = x;
			x.rank++;
			return x;
		} else {
			if (y.child != null) {
				clearSiblingList(x);
				insertToSiblingList(y.child, x);
			} else {
				y.child = x;
			}
			x.parent = y;
			y.rank++;
			return y;
		}
	}

	private void clearSiblingList(HeapNode x) {
		x.next = x;
		x.prev = x;
	}

	/*
	 * Create Root list, and update minNode after successive linking.
	 */
	private void createRootList(List<HeapNode> list) {
		minNode = new HeapNode(-1, null);
		treeCount = 0;
		for (int i = 0; i < list.size(); i++) {
			// Updating minNode
			if (list.get(i) != null) {
				if (minNode.key == -1) {
					minNode = list.get(i);
				}
				insertToRootList(list.get(i));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void successiveLinking(HeapNode x) {
		Object[] arr = new Object[(int) (2.25 * (Math.log(this.size)) + 1)];
		HeapNode tmpNode = x;
		HeapNode nxtNode = x.next;
		do {
			nxtNode = tmpNode.next;
			clearSiblingList(tmpNode);
			if (arr[tmpNode.rank] == null) {
				arr[tmpNode.rank] = tmpNode;
			} else {
				while (arr[tmpNode.rank] != null) {
					tmpNode = linkNodes((HeapNode) arr[tmpNode.rank], tmpNode);
					arr[tmpNode.rank - 1] = null;
				}
				arr[tmpNode.rank] = tmpNode;
			}
			tmpNode = nxtNode;
		} while (tmpNode != x);

		List<HeapNode> list = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			list.add((HeapNode) arr[i]);
		}
		createRootList(list);
	}

	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 */
	public E deleteMin() {
		this.size--;
		E val = this.minNode.value;
		HeapNode child = this.minNode.child;
		if (child != null)
			insertToRootList(child);

		HeapNode next = this.minNode.next;
		removeFromSiblingList(minNode);
		if (next != minNode) {
			successiveLinking(next);
		} else {
			if (child != null) {
				successiveLinking(child);

			} else {
				this.minNode = null;
			}
		}
		return val;

	}

	/**
	 * public HeapNode findMin()
	 *
	 * Return the node of the heap whose key is minimal.
	 *
	 */
	public HeapNode findMin() {
		return minNode;
	}

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap<E> heap2) {
		insertToRootList(heap2.minNode);
		this.size += heap2.size();
		this.treeCount += heap2.treeCount - 1;
	}

	/**
	 * public int size()
	 *
	 * Return the number of elements in the heap
	 * 
	 */
	public int size() {
		return this.size;
	}

	/**
	 * public int[] countersRep()
	 *
	 * Return a counters array, where the value of the i-th entry is the number of
	 * trees of order i in the heap.
	 * 
	 */
	public int[] countersRep() {
		int[] arr = new int[this.size];
		if (this.empty()) // Check if the heap is empty
			return arr;

		HeapNode tmpNode = this.minNode;
		if (tmpNode.next == this.minNode) { // In case there is only one object in the heap.
			arr[this.minNode.rank] = 1;
			arr = Arrays.copyOf(arr, this.minNode.rank + 1);
		} else { // Iterate over all the heaps in the Fibonacci heap.
			int max = 0;
			do {
				if (max < tmpNode.rank)
					max = tmpNode.rank;

				arr[tmpNode.rank] += 1;
				tmpNode = tmpNode.next;
			} while (tmpNode != this.minNode);
			arr = Arrays.copyOf(arr, max + 1);
		}
		return arr;
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap.
	 *
	 */
	public E delete(HeapNode x) {
		decreaseKey(x, x.key + 1);
		return deleteMin();
	}

	/*
	 * Remove X from its sibling pointers.
	 */
	private void removeFromSiblingList(HeapNode x) {
		HeapNode prev = x.prev;
		HeapNode next = x.next;

		next.prev = x.prev;
		prev.next = x.next;
		x.next = x;
		x.prev = x;
	}

	/*
	 * Insert x to root list.
	 */
	private void insertToRootList(HeapNode x) {
		treeCount++;
		insertToSiblingList(this.minNode, x);
		if (x.key < this.minNode.key)
			this.minNode = x;
	}

	/*
	 * Perform cascading cuts when x.mark is true.
	 */
	private void cascadingCuts(HeapNode x) {
		cutsCounter++;
		HeapNode parent = x.parent;
		parent.rank--;
		// Cutting X from its parent
		x.parent = null;
		if (parent.child == x) {
			parent.child = x.next == x ? null : x.next;
			if (parent.child != null)
				parent.child.parent = parent;
		}

		this.removeFromSiblingList(x);
		if (x.isMark()) {
			this.markedNodes--;
			x.mark = false;
		}
		this.insertToRootList(x);
		if (parent.mark) {
			this.markedNodes--;
			parent.mark = false;
			this.cascadingCuts(parent);
		} else {
			if (parent.parent != null) {
				this.markedNodes++;
				parent.mark = true;
			}
		}
	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. The structure of the
	 * heap should be updated to reflect this chage (for example, the cascading cuts
	 * procedure should be applied if needed).
	 */
	public void decreaseKey(HeapNode x, int delta) {
		x.key -= delta;
		if (x.parent != null && x.key < x.parent.key) {
			cascadingCuts(x);
		} else if (x.key < minNode.key) {
			this.minNode = x;
		}
	}

	/**
	 * public int potential()
	 *
	 * This function returns the current potential of the heap, which is: Potential
	 * = #trees + 2*#marked The potential equals to the number of trees in the heap
	 * plus twice the number of marked nodes in the heap.
	 */
	public int potential() {
		return this.markedNodes * 2 + this.treeCount;
	}

	/**
	 * public static int totalLinks()
	 *
	 * This static function returns the total number of link operations made during
	 * the run-time of the program. A link operation is the operation which gets as
	 * input two trees of the same rank, and generates a tree of rank bigger by one,
	 * by hanging the tree which has larger value in its root on the tree which has
	 * smaller value in its root.
	 */
	public static int totalLinks() {
		return linksCounter;
	}

	/**
	 * public static int totalCuts()
	 *
	 * This static function returns the total number of cut operations made during
	 * the run-time of the program. A cut operation is the operation which
	 * diconnects a subtree from its parent (during decreaseKey/delete methods).
	 */
	public static int totalCuts() {
		return cutsCounter; // should be replaced by student code
	}

	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap (for example
	 * HeapNode), do it in this file, not in another file
	 * 
	 */
	public class HeapNode {

		private int key;
		private int rank;
		private boolean mark;
		private HeapNode child;
		private HeapNode parent;
		private HeapNode next = this;
		private HeapNode prev = this;
		private E value;

		public E getValue() {
			return this.value;
		}

		public int getRank() {
			return rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		public boolean isMark() {
			return mark;
		}

		public void setMark(boolean mark) {
			this.mark = mark;
		}

		public HeapNode getNext() {
			return next;
		}

		public void setNext(HeapNode next) {
			this.next = next;
		}

		public HeapNode getPrev() {
			return prev;
		}

		public void setPrev(HeapNode prev) {
			this.prev = prev;
		}

		public HeapNode getChild() {
			return child;
		}

		public void setChild(HeapNode child) {
			this.child = child;
		}

		public HeapNode getParent() {
			return parent;
		}

		public void setParent(HeapNode parent) {
			this.parent = parent;
		}

		public HeapNode(int key, E value) {
			this.key = key;
			this.value = value;
		}

		public int getKey() {
			return this.key;
		}

	}
}
