// ParameterizedTree.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.tree;

import pal.misc.*;
import java.io.*;


/**
 * abstract base class for a tree with an Parameterized interface
 *
 * @version $Id: ParameterizedTree.java,v 1.6 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Alexei Drummond
 * @author Korbinian Strimmer
 */
public abstract class ParameterizedTree implements Parameterized, Tree
{
	//
	// Public stuff
	//

	public void setBaseTree(Tree tree)
	{
		this.tree = tree;
		
		// make consistent
		createNodeList();
	}

	public Tree getBaseTree() {
		return tree;
	}
	
	// interface tree
	
	/**
	 * Returns the root node of this tree.
	 */
	public final Node getRoot() {
		return tree.getRoot();
	}

	/**
	 * returns a count of the number of external nodes (tips) in this
	 * tree.
	 */
	public final int getExternalNodeCount() {
		return tree.getExternalNodeCount();
	}
	
	/**
	 * returns a count of the number of internal nodes (and hence clades)
	 * in this tree.
	 */
	public final int getInternalNodeCount() {
		return tree.getInternalNodeCount();
	}

	/**
	 * returns the ith external node in the tree.
	 */
	public final Node getExternalNode(int i) {
		return tree.getExternalNode(i);
	}
	
	/**
	 * returns the ith internal node in the tree.
	 */
	public final Node getInternalNode(int i) {
		return tree.getInternalNode(i);
	}

	/**
	 * This method is called to ensure that the calls to other methods
	 * in this interface are valid.
	 */
	public final void createNodeList() {
		tree.createNodeList();
	}

	public final int getUnits() {
		return tree.getUnits();
	}

	public final void setUnits(int units) {
		tree.setUnits(units);
	}

	public String toString() {
		StringWriter sw = new StringWriter();
		TreeUtils.printNH(this, new PrintWriter(sw), true, false);

		return sw.toString();
	}
	
	// interface parameterized (remains abstract)
	
	/**
	 * The non-parameterized tree that this parameterized tree is
	 * based on.
	 */
	private Tree tree;

}

