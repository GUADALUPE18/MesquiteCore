/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.minimal.ManageMesquiteBlock;/*~~  */import java.util.*;import java.awt.*;import java.io.*;import mesquite.lib.*;import mesquite.lib.duties.*;/** Manages the Mesquite block of a nexus file, which contains scripts for Mesquite and its modules. */public class ManageMesquiteBlock extends ScriptingManager {	static final int currentScriptVersion = 2;	MesquiteBlock currentBlock = null;  //TODO: must allow more than one, one for each file	EditRawNexusBlock editor;	static boolean warnVersion = true;	// need vector of MesquiteScript objects that are FileElements		boolean debugging = false;	Random rng;	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		rng = new Random(System.currentTimeMillis());		return true;	}		/*.................................................................................................................*/	/*.................................................................................................................*/ 	/** A method called immediately after the file has been read in.*/ 	public void projectEstablished() {		getFileCoordinator().addMenuItem(MesquiteTrunk.editMenu, "Edit Mesquite script", makeCommand("showScript",  this));		MesquiteFile homeFile = getProject().getHomeFile();		MesquiteBlock b =makeBlock(homeFile, "");  //merely a placeholder		b.setName("AUTO*");		addNEXUSBlock(b);		//getFileCoordinator().addMenuItem("-", null);		super.projectEstablished(); 	}	public Class getElementClass(){		return MesquiteBlock.class;	}	/*.................................................................................................................*/	public MesquiteModule showElement(FileElement e, CommandRecord commandRec){		//TODO:		if (e != null)			alert("Sorry, the " + e.getTypeName() + "  cannot be shown by this means yet.");		return null;	}	/*.................................................................................................................*/	public void elementDisposed(FileElement e){		//if (blocks !=null)		//	blocks.removeElement(e);				//todo: here should fire any editor employees currently editing this block	}	/*.................................................................................................................*/	public NexusBlock elementAdded(FileElement block){		return null;	}	/*.................................................................................................................*/	private MesquiteBlock makeBlock(MesquiteFile file, String contents) {		MesquiteBlock b = new MesquiteBlock(file, this);		b.setName("Mesquite Block");		if (contents !=null)			b.setText(contents);	//	if (blocks==null)	//		blocks = new Vector();	//	blocks.addElement(b);		return b;	}	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Calls up a window in which to display and edit a Mesquite script block", "[number of block]", commandName, "showScript")) {      	 		MesquiteBlock b = null;    	 		NexusBlock[] blocks = getProject().getNexusBlocks(MesquiteBlock.class, null);    	 		if (blocks == null || blocks.length == 0) {    	 			b = makeBlock(null, "BEGIN MESQUITE;"+ StringUtil.lineEnding() + "END;"); //TODO: into which file assigned? (dialog box if more than one file opened)    	 		}    	 		else if (StringUtil.blank(arguments)) {	    	 			//have dialog requesting which??	    	 			b = ((MesquiteBlock)blocks[0]);	    	 	}	    	 	else {    	 			int whichBlock = MesquiteInteger.fromString(arguments);	    	 		if (whichBlock >=0 && whichBlock < blocks.length)	    	 			b = ((MesquiteBlock)blocks[whichBlock]);   	 		}    	 		if (b!=null) {    	 			if (editor==null)    	 				editor = (EditRawNexusBlock)hireEmployee(commandRec, EditRawNexusBlock.class, "To edit Mesquite block");	    			if (editor!=null) {	    				editor.editNexusBlock(b, true);	    				b.setEditor(editor);		 			if (!commandRec.scripting() && editor.getModuleWindow()!=null)		 				editor.getModuleWindow().setVisible(true);	    			}	    			return editor;    			}    	 	}    	 	else if (checker.compare(this.getClass(), "Gets snapshots from module of given identification number", "[identification number of module]", commandName, "getSnapshots")) {      	 		int w = MesquiteInteger.fromString(arguments);    	 		MesquiteModule mb = MesquiteTrunk.mesquiteTrunk.findEmployeeWithIDNumber(w);    	 		if (mb!=null) {    	 			return Snapshot.getSnapshotCommands(mb, null, "\t");    	 		}    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }	/*.................................................................................................................*/	public NexusBlockTest getNexusBlockTest(){ return new MesquiteBlockTest();}	/*.................................................................................................................*/	//this should pretty print the raw block	public String pretty(String raw){		return raw;	}		/*.................................................................................................................*/	public NexusBlock readNexusBlock(MesquiteFile file, String name, FileBlock block, StringBuffer blockComments, String fileReadingArguments, CommandRecord commandRec){		String fRA = parser.getFirstToken(fileReadingArguments);		while (!StringUtil.blank(fRA)) {			if (fRA.equalsIgnoreCase(StringUtil.argumentMarker + "fuseTaxaCharBlocks"))				return null;			fRA = parser.getNextToken();		}		incrementMenuResetSuppression();		String blockString = block.toString();		MesquiteBlock b = makeBlock(file, blockString);		Parser commandParser = new Parser();		commandParser.setString(blockString);		MesquiteInteger startCharC = new MesquiteInteger(0);		int scriptVersion = -1;		boolean ignoreScriptVersion = true;		boolean forceIgnore = false;		String s;		int previousStart = startCharC.getValue();		while (!StringUtil.blank(s=commandParser.getNextCommand(startCharC))) {			String commandName = parser.getFirstToken(s);			String commandArguments = s.substring(parser.getPosition(), s.length()-1);			if ("BEGIN".equalsIgnoreCase(commandName)) {							}			else if ("TITLE".equalsIgnoreCase(commandName)) {				b.setName(commandArguments);				if ("AUTO".equalsIgnoreCase(b.getName())) //warns about script version only if AUTO-saved script					ignoreScriptVersion = false;			}			else if ("SCRIPTVERSION".equalsIgnoreCase(commandName)) { 				scriptVersion = MesquiteInteger.fromString(commandArguments);			}			else if ("MESQUITESCRIPTVERSION".equalsIgnoreCase(commandName)) { 				scriptVersion = MesquiteInteger.fromString(commandArguments);			}			else if ("IGNORESCRIPTVERSION".equalsIgnoreCase(commandName)) { 				forceIgnore = true;			}			else {				startCharC.setValue(previousStart);				Puppeteer p = new Puppeteer(this);				CommandRecord cRecord = new CommandRecord(true);				cRecord.setScriptingFile(file);				Thread mt = Thread.currentThread();				if (mt instanceof CommandRecordHolder) 					((CommandRecordHolder)mt).setCommandRecord(cRecord);				if (mt instanceof MesquiteThread) {					ProgressIndicator pi = ((MesquiteThread)mt).getProgressIndicator();					if (pi !=null)						cRecord.setProgressIndicator(pi);				}				p.execute(getFileCoordinator(), blockString, startCharC, "", false, b, file, cRecord);			}			previousStart = startCharC.getValue();		}		if ("AUTO".equalsIgnoreCase(b.getName())){  //			boolean found = false;			NexusBlock[] blocks = getProject().getNexusBlocks(MesquiteBlock.class, null);			if (blocks !=null)				for (int i=0; i<blocks.length && !found; i++) {					MesquiteBlock bb = ((MesquiteBlock)blocks[i]);					if ("AUTO*".equalsIgnoreCase(bb.getName())) {						found = true;						removeNEXUSBlock(bb);					}				}		}		if (!ignoreScriptVersion && !forceIgnore && scriptVersion < currentScriptVersion) {			String warning = "The Mesquite script in this file appears to be of an older version.  Changes in the scripting language may cause some problems (e.g., traced characters may not appear).  If a problem appears, you may be able to solve it by using Save As to save the file, closing the file, then reopening it.";			if (commandRec.scripting() || !warnVersion)				logln("\n" + warning +"\n");			else if (!AlertDialog.query(containerOfModule(), "Old Script Version",warning + "\n\nYou may suppress future warnings of this type within this run of Mesquite.", "Continue", "Suppress warnings")){				warnVersion = false;			}		}		decrementMenuResetSuppression();		return b;	}	/*.................................................................................................................*/	public String getAutoBlock(MesquiteFile file){		if (file!= getProject().getHomeFile())			return "";		FileCoordinator fCoord = getFileCoordinator();				String snapshot = ""; //here use getIDSnapshots for all FileElementManager employees of the fCoord; avoids problems of assigning character matrix ID after taxon list window shown		Snapshot idfcs = fCoord.getIDSnapshot(file);		if (idfcs != null)			snapshot += idfcs.toString(file, "\t\t");		EmployeeVector femps = fCoord.getEmployeeVector();		for (int i=0; i<femps.size(); i++){			if (femps.elementAt(i) instanceof FileElementManager){				FileElementManager fem = (FileElementManager)femps.elementAt(i);				String ids = Snapshot.getIDSnapshotCommands(fem, file, "\t\t");				if (!StringUtil.blank(ids)) {					snapshot +="\t\tgetEmployee " + StringUtil.tokenize(fCoord.getEmployeeReference(fem)) + ";" + StringUtil.lineEnding();//quote					snapshot += "\t\ttell It;" + StringUtil.lineEnding();					snapshot += ids;					snapshot += "\t\tendTell;" + StringUtil.lineEnding();				}							}		}		snapshot += Snapshot.getSnapshotCommands(fCoord, file, "\t");		if (!StringUtil.blank(snapshot))			return "Begin MESQUITE;" + StringUtil.lineEnding()  + "\t\t" + getVersionCommand() + StringUtil.lineEnding() + "\t\tTITLE AUTO;"+ StringUtil.lineEnding()  + "\t\ttell ProjectCoordinator;"+ StringUtil.lineEnding() +  snapshot+ "\t\tendTell;"+ StringUtil.lineEnding() + "end;" + StringUtil.lineEnding();		else return "";	}	/*.................................................................................................................*/		/*.................................................................................................................*/	private String getVersionCommand(){		return "MESQUITESCRIPTVERSION " + currentScriptVersion +";";	}	/*.................................................................................................................*/    	 public String getName() {		return "Manage MESQUITE block";   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Manages MESQUITE block (including compose snapshot of current state of file in MESQUITE block \"Auto\" in NEXUS file)." ;   	 }   	 }	class MesquiteBlock extends NexusBlockEditableRaw {	String blockContents = "";	public MesquiteBlock(MesquiteFile f, MesquiteModule mb){		super(f,mb);	}	public void written() {		MesquiteMessage.warnProgrammer("written Mesquiteblock");	}	public boolean contains(FileElement e) {		return false;	}	public boolean mustBeAfter(NexusBlock block){	//TODO: ONLY if not read in from file or if AUTO		return true;			}	public String getBlockName(){		return "MESQUITE";	}	public void setText(String contents) {		blockContents = contents;	}	public String getText() {		return blockContents;	}	public String getNEXUSBlock(){		if (getEditor()!=null)			getEditor().recordBlock(this);	 	if (getName().equalsIgnoreCase("AUTO"))	 		return ((ManageMesquiteBlock)getManager()).getAutoBlock(getFile());	 	else if (getName().equalsIgnoreCase("AUTO*")) {	 		setName("AUTO");	 		return ((ManageMesquiteBlock)getManager()).getAutoBlock(getFile());	 	}	 	else {	 		return blockContents;	 	}	 		}}/* ======================================================================== */class MesquiteBlockTest extends NexusBlockTest  {	public MesquiteBlockTest () {	}	public  boolean readsWritesBlock(String blockName, FileBlock block){ //returns whether or not can deal with block		return blockName.equalsIgnoreCase("MESQUITE");	}}