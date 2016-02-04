package org.sagarius.radix.client.collect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> {
	private T value;
	private TreeNode<T> parent;

	@Override
	public String toString() {
		return "TreeNode [value=" + value + ", parent=" + parent
				+ ", children=" + children + "]";
	}

	private List<TreeNode<T>> children;

	public TreeNode() {
	}

	public TreeNode(T value) {
		this.setValue(value);
	}

	public List<TreeNode<T>> getChildren() {
		if (children == null) {
			children = new LinkedList<TreeNode<T>>();
		}
		return children;
	}

	public int getNumberOfChildren() {
		return children == null ? 0 : children.size();
	}

	public void addChild(TreeNode<T> child) {
		child.setParent(this);
		getChildren().add(child);
	}

	public void removeChild(TreeNode<T> child) {
		getChildren().remove(child);
	}

	public void removeChild(int index) {
		getChildren().remove(index);
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setParent(TreeNode<T> parent) {
		this.parent = parent;
	}

	public TreeNode<T> getParent() {
		return parent;
	}

	public List<T> getLeaves() {
		List<T> output = new ArrayList<T>();
		addLeaves(this, output);
		return output;
	}

	public void addLeaves(TreeNode<T> node, List<T> list) {
		if (node.getNumberOfChildren() == 0) {
			list.add(node.value);
			return;
		}
		List<TreeNode<T>> children = node.children;
		for (TreeNode<T> child : children) {
			addLeaves(child, list);
		}
	}
}
