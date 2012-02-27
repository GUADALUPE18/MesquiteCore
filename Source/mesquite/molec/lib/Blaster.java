/* Mesquite source code.  Copyright 1997-2011 W. Maddison and D. Maddison.
Version 2.75, September 2011.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
*/
package mesquite.molec.lib;

import java.awt.*;

import mesquite.categ.lib.DNAData;
import mesquite.lib.*;
import mesquite.lib.characters.CharacterData;


/* ======================================================================== */

public abstract class Blaster extends MesquiteModule   {
	protected boolean blastx = false;

   	 public Class getDutyClass() {
   	 	return Blaster.class;
   	 }
 	public String getDutyName() {
 		return "Blaster";
   	 }
 	 
   	 public String[] getDefaultModule() {
   	 	return new String[] {"#NCBIBlaster"};
   	 }
   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from
   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/
   	public abstract boolean initialize();
   	
	public abstract void blastForMatches(String blastType, String sequenceName, String sequence, boolean isNucleotides, int numHits, int maxTime, double eValueCutoff, StringBuffer blastResponse, boolean writeTime);
	
	public abstract String getFastaFromIDs(String[] idList,  boolean isNucleotides, StringBuffer fastaBlastResults);
		
	public abstract String getTaxonomyFromID(String id, boolean isNucleotides, boolean writeLog, StringBuffer report);


	 
	 
	public  void postProcessingCleanup(BLASTResults blastResult){
	}
	public boolean isBlastx() {
		return blastx;
	}
	public void setBlastx(boolean blastx) {
		this.blastx = blastx;
	}
   	

}
