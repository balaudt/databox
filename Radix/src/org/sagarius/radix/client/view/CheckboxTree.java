package org.sagarius.radix.client.view;

import java.util.List;

import org.sagarius.radix.client.collect.TreeNode;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * Two conditions has to be satisfied for a CheckBoxTree:
 * <ul>
 * <li>When any of the non-leaf node's value changes, all the descendant's value
 * should be changed to that value</li>
 * <li>When any of the non-root node's value changes, the value of it's parent
 * should be compatible with that of its children [ascending till the root]</li>
 * </ul>
 * 
 * @author bala
 * 
 */
public class CheckboxTree {
	private TreeNode<CheckBox> rootNode;

	public TreeNode<CheckBox> getRootNode() {
		return rootNode;
	}

	public CheckboxTree(CheckBox rootElement) {
		rootNode = new TreeNode<CheckBox>(rootElement);
	}

	public void addChild(CheckBox child) {
		rootNode.addChild(new TreeNode<CheckBox>(child));
	}

	public void addChild(CheckboxTree tree) {
		rootNode.addChild(tree.rootNode);
	}

	public void listen() {
		applyDescendRule(rootNode);
		List<TreeNode<CheckBox>> children = rootNode.getChildren();
		for (TreeNode<CheckBox> child : children) {
			applyAscendRule(child);
		}
	}

	public void applyAscendRule(final TreeNode<CheckBox> node) {
		ascendRule(node);
		List<TreeNode<CheckBox>> children = node.getChildren();
		for (TreeNode<CheckBox> child : children) {
			applyAscendRule(child);
		}
	}

	public void ascendRule(final TreeNode<CheckBox> node) {
		CheckBox nodeBox = node.getValue();
		nodeBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				Boolean nodeValue = event.getValue();
				List<TreeNode<CheckBox>> siblings = node.getParent()
						.getChildren();
				boolean allEqual = true;
				for (TreeNode<CheckBox> sibling : siblings) {
					if (!sibling.getValue().getValue().equals(nodeValue)) {
						allEqual = false;
						break;
					}
				}
				ascendRuleRecursive(node.getParent(), allEqual ? nodeValue
						: false);
			}
		});
	}

	public void ascendRuleRecursive(TreeNode<CheckBox> node, boolean value) {
		if (node.getValue().getValue().equals(value)) {
			return;
		}
		node.getValue().setValue(value);
		TreeNode<CheckBox> parent = node.getParent();
		if (parent == null) {
			return;
		}
		List<TreeNode<CheckBox>> siblings = parent.getChildren();
		boolean allEqual = true;
		for (TreeNode<CheckBox> sibling : siblings) {
			if (!sibling.getValue().getValue().equals(value)) {
				allEqual = false;
				break;
			}
		}
		ascendRuleRecursive(node.getParent(), allEqual ? value : false);
	}

	public void applyDescendRule(TreeNode<CheckBox> node) {
		if (node.getNumberOfChildren() == 0) {
			return;
		}
		descendRule(node);
		List<TreeNode<CheckBox>> children = node.getChildren();
		for (TreeNode<CheckBox> child : children) {
			applyDescendRule(child);
		}
	}

	public void descendRule(final TreeNode<CheckBox> node) {
		CheckBox nodeBox = node.getValue();
		nodeBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				List<TreeNode<CheckBox>> children = node.getChildren();
				for (TreeNode<CheckBox> child : children) {
					descendRuleRecursive(child, event.getValue());
				}
			}
		});
	}

	public void descendRuleRecursive(TreeNode<CheckBox> descendant,
			boolean value) {
		CheckBox childBox = descendant.getValue();
		childBox.setValue(value);
		List<TreeNode<CheckBox>> children = descendant.getChildren();
		for (TreeNode<CheckBox> child : children) {
			descendRuleRecursive(child, value);
		}
	}

	public List<CheckBox> getLeaves() {
		return rootNode.getLeaves();
	}
}
