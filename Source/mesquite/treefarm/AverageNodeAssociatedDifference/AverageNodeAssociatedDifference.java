

/* Mesquite source code, Treefarm package.  Copyright 1997-2010 W. Maddison, D. Maddison and P. Midford. 
Version 2.74, October 2010.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
 */
package mesquite.treefarm.AverageNodeAssociatedDifference;
/*~~  */

import java.util.*;
import java.awt.*;

import mesquite.lib.*;
import mesquite.lib.duties.*;
import mesquite.trees.lib.*;



/* ======================================================================== */
public class AverageNodeAssociatedDifference extends DistanceBetween2Trees {
	String valueToConsider;
	MesquiteBoolean absoluteDifference= new MesquiteBoolean(false);
	MesquiteBoolean listAllNodes= new MesquiteBoolean(false);
	MesquiteBoolean verboseOutput= new MesquiteBoolean(false);
	StringArray terminalsAbove, terminalsBelow, otherTerminalsAbove, otherTerminalsBelow;
	boolean isDistance = false;
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
		isDistance = (getHiredAs() == DistanceBetween2Trees.class);
		addMenuItem(null, "Choose Values To Show...", makeCommand("chooseValues",  this));
		addCheckMenuItem(null, "Verbose Output to Log", makeCommand("toggleVerboseOutput",  this), verboseOutput);
		addCheckMenuItem(null, "Absolute Value", makeCommand("absoluteValue",  this), absoluteDifference);
		addCheckMenuItem(null, "List All Nodes", makeCommand("toggleListAllNodes",  this), listAllNodes);
		valueToConsider = "";
		return true;
	}
	public void employeeQuit(MesquiteModule m){
		iQuit();
	}
	public boolean largerIsFurther(){  
		return false;
	}
	private void visitOriginal(Tree tree,int node,  Tree otherTree, MesquiteInteger numConsistent, DoubleArray array1, DoubleArray array2, MesquiteDouble totalDiff){
		if (tree.nodeIsInternal(node)){
			Bits b = tree.getTerminalTaxaAsBits(node);

			double d1 = array1.getValue(node);
			if (otherTree.isClade(b)){  // has same clade
				numConsistent.increment();
				int node2 = otherTree.mrca(b);
				if (tree.getRoot()!=node && otherTree.getRoot()!=node2) {   // only do it if neither is the root
					double d2 = array2.getValue(node2);
					//	Debugg.println("    d1: " + d1 + ", d2: " + d2);
					double diff = 0.0;
					if (MesquiteDouble.isCombinable(d1) && MesquiteDouble.isCombinable(d2)){
						if (absoluteDifference.getValue()) {
							if (d1>d2)
								diff = d1-d2;
							else
								diff= d2-d1;
						}
						else
							diff = d1-d2;
						if (verboseOutput.getValue())
							Debugg.println("" + node + "\t"+MesquiteDouble.toStringNoNegExponential(diff)+"\t"+d1+"\t"+d2);
						totalDiff.add(diff);
					} else if (verboseOutput.getValue() && listAllNodes.getValue())
						if (MesquiteDouble.isCombinable(d1))
							Debugg.println("" + node + "\t-\t" + d1 + "\t-");
						else
							Debugg.println("" + node + "\t-\t-\t-");
				}
			} else if (verboseOutput.getValue()&& listAllNodes.getValue()) {
				if (tree.getRoot()!=node) {   // only do it if neither is the root
					//Debugg.println("    not present: " + d1);
					Debugg.println("" + node + "\t-\t" + d1 + "\t-");
				}
			}
			for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter))
				visitOriginal(tree, daughter, otherTree, numConsistent, array1, array2, totalDiff);

		}
	}
	MesquiteInteger pos = new MesquiteInteger();
	/*.................................................................................................................*/
	public Object doCommand(String commandName, String arguments, CommandChecker checker) {
		if (checker.compare(this.getClass(), "Shows dialog box to choose what values to display", null, commandName, "chooseValues")) {
			//			showAssociatedChoiceDialog((Associable)tree, "Values to Average Differences", this);
		}
		else if (checker.compare(this.getClass(), "Sets which value to use for calculation", "[on or off]", commandName, "setValueName")) {
			String name = parser.getFirstToken(arguments);
		}
		else if (checker.compare(this.getClass(), "Sets whether absolute value is shown or raw value", "", commandName, "absoluteValue")) {
 			boolean current = absoluteDifference.getValue();
 			absoluteDifference.toggleValue(parser.getFirstToken(arguments));
	 			if (current!=absoluteDifference.getValue())
	 				parametersChanged();
	}
		else if (checker.compare(this.getClass(), "Sets whether verbose output is listed to the log file or not", "", commandName, "toggleVerboseOutput")) {
 			boolean current = verboseOutput.getValue();
 			verboseOutput.toggleValue(parser.getFirstToken(arguments));
	 			if (current!=verboseOutput.getValue())
	 				parametersChanged();
	}
		else if (checker.compare(this.getClass(), "Sets whether all nodes are listed in the verbose output, or just the ones in both trees and both with associated values", "", commandName, "toggleListAllNodes")) {
 			boolean current = listAllNodes.getValue();
 			listAllNodes.toggleValue(parser.getFirstToken(arguments));
	 			if (current!=listAllNodes.getValue())
	 				parametersChanged();
	}
		else
			return  super.doCommand(commandName, arguments, checker);
		return null;
	}

	/*.................................................................................................................*/
	public  boolean showAssociatedChoiceDialog(Associable tree, String message, MesquiteModule module) {
		if (tree == null)
			return false;
		MesquiteInteger buttonPressed = new MesquiteInteger(1);
		ListableVector v = new ListableVector();
		int num = tree.getNumberAssociatedDoubles();
		if (num==1){
			DoubleArray da = tree.getAssociatedDoubles(0);
			valueToConsider = da.getName();
			return true;
		}
		String[] associatedNames = new String[num];
		boolean[] shown = new boolean[num]; //bigger than needed probably
		int valueShown = -1;
		for (int i = 0; i< num; i++){
			DoubleArray da = tree.getAssociatedDoubles(i);
			if (da != null){
				v.addElement(new MesquiteString(da.getName(), ""), false);
				associatedNames[i] = da.getName();
				if (valueToConsider.equalsIgnoreCase(da.getName()))
					valueShown=i;
			}
		}
		if (num==0)
			module.alert("This Tree has no values associated with nodes");
		else {
			ExtensibleDialog queryDialog = new ExtensibleDialog(module.containerOfModule(), message,  buttonPressed);
			queryDialog.addLabel(message, Label.CENTER);

			RadioButtons radios = queryDialog.addRadioButtons(associatedNames, valueShown); 			

			queryDialog.completeAndShowDialog(true);

			boolean ok = (queryDialog.query()==0);

			if (ok) {
				valueToConsider = associatedNames[radios.getValue()];
			}

			queryDialog.dispose();
			return ok;

		}
		return false;
	}

	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from
   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/
	public void initialize(Tree t1, Tree t2) {
	}

	MesquiteTree tree1eq, tree2eq;
	/*.................................................................................................................*/
	public void calculateNumber(Tree tree1, Tree tree2, MesquiteNumber result, MesquiteString resultString) {
		if (result==null)
			return;
		clearResultAndLastResult(result);
		if (tree1 == null)
			return;
		if (tree2 == null)
			return;
		if (StringUtil.blank(valueToConsider)) {
			boolean ok = showAssociatedChoiceDialog((Associable)tree1, "Values to Average Differences", this);
			if (!ok) {
				iQuit();
				return;
			}
			//		Debugg.println("node associated value: " + valueToConsider);
		}

		DoubleArray array1 = null;
		DoubleArray array2 = null;

		boolean found = false;
		int num = tree1.getNumberAssociatedDoubles();
		for (int i = 0; i< num; i++){
			array1 = tree1.getAssociatedDoubles(i);
			if (valueToConsider.equalsIgnoreCase(array1.getName())) {
				found=true;
				break;
			}
		}
		if (!found)
			return;
		found=false;
		num = tree2.getNumberAssociatedDoubles();
		for (int i = 0; i< num; i++){
			array2 = tree2.getAssociatedDoubles(i);
			if (valueToConsider.equalsIgnoreCase(array2.getName())){
				found=true;
				break;
			}
		}
		if (!found)
			return;
		
		if (verboseOutput.getValue()) {
			Debugg.println("\n\n\n=============== original: " + tree1.getName() + " ======");
			Debugg.println("=============== comparison: " + tree2.getName() + " ======\n");
		}

		//		Debugg.println("1: " + array1.toString());
		//		Debugg.println("2: " + array2.toString());
		//		Debugg.println("\n\n");


		MesquiteInteger numCon = new MesquiteInteger(0);
		MesquiteDouble totalDiff = new MesquiteDouble(0.0);

		int numCladeTree1 = tree1.numberOfInternalsInClade(tree1.getRoot());
		int numCladeTree2 = tree2.numberOfInternalsInClade(tree2.getRoot());

		visitOriginal(tree1, tree1.getRoot(), tree2, numCon, array1, array2, totalDiff);
		if (tree1.getTerminalTaxaAsBits(tree1.getRoot()).equals(tree2.getTerminalTaxaAsBits(tree2.getRoot()))){   // remove root value
			numCon.decrement();
		}

		int numC = numCon.getValue();
		double totalD = totalDiff.getValue();

		//		Debugg.println("\n\n numC: " + numC);
		//		Debugg.println(" totalD: " + totalD);



		double avg = 0.0;
		if (numC>0)
			avg = totalD/numC;

		result.setValue(avg);
		if (resultString!=null) {
			resultString.setValue("Fraction Shared Clades: "+ result.toString());
		}
		saveLastResult(result);
		saveLastResultString(resultString);
	}
	/*.................................................................................................................*/
	public boolean isPrerelease(){
		return true;
	}

	/*.................................................................................................................*
    	 public String getParameters() {
		return "Shared Clades";
   	 }
	/*.................................................................................................................*/
	public String getName() {
		return "Average of Values Associated with Nodes";
	}
	/*.................................................................................................................*/
	public String getExplanation() {
		return "Calculates the average value of values associated with nodes (excludes the clade consisting of all taxa).";
	}
	/*.................................................................................................................*/
	public boolean showCitation(){
		return true;
	}
	/*.................................................................................................................*/
	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer
	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.
	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/
	public int getVersionOfFirstRelease(){
		return NEXTRELEASE;  
	}
}