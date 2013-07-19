package dbg;

import java.io.FileInputStream;
import java.util.Properties;

public class DebugReader {
	//DEBUG switches
	public boolean traceBPEliteMap =false;
	public boolean checkOnePhaseAddOnly = false;
	public boolean checkSuccessfulAddToCache = false;
	public boolean printFullProgramWithCache = false;
	public boolean showDotsToMarkProgress = false;
	public boolean showInstanceClassLabels = false;
	public boolean showGenNR = false;
	public boolean showBlueprintComponentFitness = false;
	public boolean showNumberOfInstanceRegisters = false;
	public boolean showBlueprintComponentFitness2 = false;
	public boolean showSingleBPRegs = false;
	public boolean showFitnessLists = false;
	public boolean traceBlueprintSearch = false;
	public boolean traceFlow = false;
	public boolean checkInitialBPfitness = false;
	public boolean traceBestSearch=false;
	public boolean traceCacheIndices=false;
	public boolean initialIndices=false;
	public boolean checkClassifCache=false;
	public boolean checkFBF=false;
	public boolean printAllSlots=false;
	public boolean consideredChanges=false;
	public boolean factorUsedCount=false;
	public boolean factorUsedCountShorten=false;
	public boolean showUsed=false;
	public boolean showAllFitnesses=false;
	public boolean showFitnessAverages=false;
	public boolean showInactive=false;
	public boolean showSearchHits=false;
	public boolean showsearchtype=false;
public DebugReader() {
		readProperties();
	}



private void readProperties(){	
	String propertiesFilename = "hcs.dbg.txt" ;
	Properties properties = new Properties();

	FileInputStream in;
	try {
		in = new FileInputStream(propertiesFilename);
		properties.load(in);
		System.out.println("HCSPROPS ok");
		in.close();
	} catch (Exception e) {
		System.out.println("Error HCSPROPS properties");
		System.exit(1);
		e.printStackTrace();
	}

	if(properties.containsKey("traceBPEliteMap")){
		traceBPEliteMap = Boolean.parseBoolean((String)properties.get("traceBPEliteMap"));
	}if(properties.containsKey("checkOnePhaseAddOnly")){
		checkOnePhaseAddOnly = Boolean.parseBoolean((String)properties.get("checkOnePhaseAddOnly"));
	}if(properties.containsKey("checkSuccessfulAddToCache")){
		checkSuccessfulAddToCache = Boolean.parseBoolean((String)properties.get("checkSuccessfulAddToCache"));
	}if(properties.containsKey("printFullProgramWithCache")){
		printFullProgramWithCache = Boolean.parseBoolean((String)properties.get("printFullProgramWithCache"));
	}if(properties.containsKey("showDotsToMarkProgress")){
		showDotsToMarkProgress = Boolean.parseBoolean((String)properties.get("showDotsToMarkProgress"));
	}if(properties.containsKey("showInstanceClassLabels")){
		showInstanceClassLabels = Boolean.parseBoolean((String)properties.get("showInstanceClassLabels"));
	}if(properties.containsKey("showGenNR")){
		showGenNR = Boolean.parseBoolean((String)properties.get("showGenNR"));
	}if(properties.containsKey("showBlueprintComponentFitness")){
		showBlueprintComponentFitness = Boolean.parseBoolean((String)properties.get("showBlueprintComponentFitness"));
	}if(properties.containsKey("showNumberOfInstanceRegisters")){
		showNumberOfInstanceRegisters = Boolean.parseBoolean((String)properties.get("showNumberOfInstanceRegisters"));
	}if(properties.containsKey("showBlueprintComponentFitness2")){
		showBlueprintComponentFitness2 = Boolean.parseBoolean((String)properties.get("showBlueprintComponentFitness2"));
	}if(properties.containsKey("showSingleBPRegs")){
		showSingleBPRegs = Boolean.parseBoolean((String)properties.get("showSingleBPRegs"));
	}if(properties.containsKey("showFitnessLists")){
		showFitnessLists = Boolean.parseBoolean((String)properties.get("showFitnessLists"));
	}if(properties.containsKey("traceBlueprintSearch")){
		traceBlueprintSearch = Boolean.parseBoolean((String)properties.get("traceBlueprintSearch"));
	}if(properties.containsKey("traceFlow")){
		traceFlow = Boolean.parseBoolean((String)properties.get("traceFlow"));
	}if(properties.containsKey("checkInitialBPfitness")){
		checkInitialBPfitness = Boolean.parseBoolean((String)properties.get("checkInitialBPfitness"));
	}if(properties.containsKey("traceBestSearch")){
		traceBestSearch = Boolean.parseBoolean((String)properties.get("traceBestSearch"));
	}if(properties.containsKey("traceCacheIndices")){
		traceCacheIndices = Boolean.parseBoolean((String)properties.get("traceCacheIndices"));
	}if(properties.containsKey("initialIndices")){
		initialIndices = Boolean.parseBoolean((String)properties.get("initialIndices"));
	}if(properties.containsKey("checkClassifCache")){
		checkClassifCache = Boolean.parseBoolean((String)properties.get("checkClassifCache"));
	}if(properties.containsKey("checkFBF")){
		checkFBF = Boolean.parseBoolean((String)properties.get("checkFBF"));
	}if(properties.containsKey("printAllSlots")){
		printAllSlots = Boolean.parseBoolean((String)properties.get("printAllSlots"));
	}if(properties.containsKey("consideredChanges")){
		consideredChanges = Boolean.parseBoolean((String)properties.get("consideredChanges"));
	}if(properties.containsKey("factorUsedCount")){
		factorUsedCount = Boolean.parseBoolean((String)properties.get("factorUsedCount"));
	}if(properties.containsKey("factorUsedCountShorten")){
		factorUsedCountShorten = Boolean.parseBoolean((String)properties.get("factorUsedCountShorten"));
	}if(properties.containsKey("showUsed")){
		showUsed = Boolean.parseBoolean((String)properties.get("showUsed"));
	}if(properties.containsKey("showAllFitnesses")){
		showAllFitnesses = Boolean.parseBoolean((String)properties.get("showAllFitnesses"));
	}if(properties.containsKey("showFitnessAverages")){
		showFitnessAverages = Boolean.parseBoolean((String)properties.get("showFitnessAverages"));
	}if(properties.containsKey("showInactive")){
		showInactive = Boolean.parseBoolean((String)properties.get("showInactive"));
	}if(properties.containsKey("showSearchHits")){
		showSearchHits = Boolean.parseBoolean((String)properties.get("showSearchHits"));
	}if(properties.containsKey("showsearchtype")){
		showsearchtype = Boolean.parseBoolean((String)properties.get("showsearchtype"));
	}
	
	
	}

}
