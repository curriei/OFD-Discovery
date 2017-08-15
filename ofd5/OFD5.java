package ofd5;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Ian Currie       email: curriei@mcmaster.ca
 */
public class OFD5 { 
    

    //CSV_FOLDER, ONTOLOGY_LOCATION, & OUTPUT_FOLDER MUST be updated to their proper locations on your machine.
    //CSV_FOLDER contains the original dataset stored in a *.csv file.  
    private static final String CSV_FOLDER = "/Users/ircur/Desktop/";
    //ONTOLOGY_LOCATION contains the RDF_OFD.py file, it is where algorithm1 will output ontology columns, and where algorithm2 will expect to find ontology.csv files
    private static final String ONTOLOGY_LOCATION = "/Users/ircur/Desktop/";
    //OUTPUT_FOLDER will be the folder to which the code outputs the final output *.txt file.
    private static final String OUTPUT_FOLDER = "/Users/ircur/Desktop/";
    
    private static final Map<Set<String>, List<List<Integer>>> PART = new HashMap<>();
    private static final Set<Set<String>> passedOFD = new HashSet<>();
    private static final Map<String,Set<Set<String>>> fd = new HashMap<>();
    private static final Map<String,Set<Set<String>>> isaofd = new HashMap<>();
    private static final Map<String,Set<Set<String>>> synofd = new HashMap<>();
    private static final Map<Integer,Double> times = new HashMap<>();
    
    /*//For testing purposes only
    public static void main(String[] args) throws Exception{
        String fileName = "clinicaltrials_7x50.csv";
        List<String> ontologyAttributes = new ArrayList<>();
        ontologyAttributes.add("countrycode");
        ontologyAttributes.add("medicine");
        ontologyAttributes.add("disease");
        ofdAlgorithm2(",",3,ontologyAttributes,fileName);
    }*/
    
    //For running the code, ofdAlgorithm1 is run first, then RDF_OFD.py, then ofdAlgorithm2
    public static void ofdAlgorithm1(String csv_delim, Integer threshold_value, List<String> ontologyAttributes, String fileName) throws Exception{
        List<List<String>> csvList;
        
        csvList = FileFormat.csvInput(csv_delim, CSV_FOLDER + fileName);
        OntFormat.outputColumns(csvList, ontologyAttributes, ONTOLOGY_LOCATION);
    }
    
    public static void ofdAlgorithm2(String csv_delim, Integer threshold_value, List<String> ontologyAttributes, String fileName) throws Exception{
        List<List<String>> csvList;
        List<String> attributeList;
        Map<String, List<String>> csvMap;
        Integer length;
        
        csvList = FileFormat.csvInput(csv_delim, CSV_FOLDER+fileName);
        length = csvList.size()-1;
        
        csvMap = FileFormat.toMap(csvList);
        OntFormat.getOnt(ontologyAttributes, csv_delim, threshold_value, ONTOLOGY_LOCATION, length);
        
        long start = System.nanoTime();
        
        PART.putAll(FileFormat.getEquiv(csvMap, length, ontologyAttributes));
        
        attributeList = csvList.get(0);
        List<String> attList = new ArrayList<>();
        attributeList.forEach((attribute) -> {
            Set<String> attSet = new HashSet<>();
            attSet.add(attribute);
            if (PART.containsKey(attSet)) {
                attList.add(attribute);
            }
        });
        
        travLattice(attList, ontologyAttributes, length, threshold_value);
        
        long stop = System.nanoTime();
        double dur = ((double) stop - start)/1000000000;
        
        finalOutput(fileName, csv_delim, dur);
    }
    
    //traverses the right side of the OFD's, and finds all OFD's to each given attribute.
    private static void travLattice(List<String> attList, List<String> ontAttList, Integer length, Integer threshold){
        for(String att:attList){
            passedOFD.clear();
            fd.put(att, new HashSet<>());
            if(ontAttList.contains(att)){
                synofd.put(att, new HashSet<>());
                isaofd.put(att, new HashSet<>());
            }
            List<String> leftSide = new ArrayList<>(attList);
            leftSide.remove(att);
            findOFD(1,att, ontAttList, leftSide, length, threshold);
        }
    }
    
    //recursively calls through the different "levels" of OFD's (level 1 finds 1:1 relationships, level 2 finds 2:1, etc.)
    private static void findOFD(Integer level, String att, List<String> ontAtts, List<String> attList, Integer length, Integer threshold){
        Set<String> leftKeySet = new HashSet<>();
        List<List<Integer>> baseSep = new ArrayList<>();
        times.putIfAbsent(level, 0D);
        
        
        Long start = System.nanoTime();
        
        boolean findMore = leftSideRec(leftKeySet, att, level, 1, baseSep, ontAtts, attList, length, threshold);
        
        Long stop = System.nanoTime();
        Double dur = times.get(level) + ((double) stop - start)/1000000000;
        times.replace(level,dur);
        
        if(findMore && level<attList.size()-1){
            findOFD(level+1,att,ontAtts, attList, length, threshold);
        }
    }
    
    //recursively goes through all combinations of the currently remaining attributes and determines if an OFD exists with that set of left Side attributes
    private static boolean leftSideRec(Set<String> leftKeySet, String rAtt, Integer maxLevel, Integer currentLevel, List<List<Integer>> separation, List<String> ontAtts, List<String> attList, Integer length, Integer threshold){
        List<String> attRem = new ArrayList<>(attList);
        boolean findMore = false;
        
        for(String lAtt:attList){
            attRem.remove(lAtt);
            Set<String> currentLeftKeySet = new HashSet<>(leftKeySet);
            currentLeftKeySet.add(lAtt);
            if(passedOFD.stream().map((ofd)->{return currentLeftKeySet.containsAll(ofd);}).noneMatch((temp)->(temp))){
                List<List<Integer>> newSep;
                if(PART.containsKey(currentLeftKeySet)){    // if the current separation has already been determined, it avoids recalculating it
                    newSep = PART.get(currentLeftKeySet);
                }else{
                    Set<String> lAttSet = new HashSet<>();
                    lAttSet.add(lAtt);
                    newSep = separation.stream().flatMap(sl1 -> PART.get(lAttSet).stream().map(sl2 -> {     //calculates the new separation
                        List<Integer> lout = new ArrayList<>();
                        lout.addAll(sl1);
                        lout.retainAll(sl2);
                        return lout;
                    })).filter(l -> l.size() > 0).distinct().collect(Collectors.toList());
                    PART.put(currentLeftKeySet, newSep);
                }
                if(newSep.size()!= length){
                    if(currentLevel<maxLevel){
                        findMore = leftSideRec(currentLeftKeySet,rAtt,maxLevel,currentLevel+1,newSep,ontAtts,attRem, length, threshold) || findMore;
                    }else{
                        Set<String> keySet = new HashSet<>(currentLeftKeySet);
                        keySet.add(rAtt);
                        if(fdHolds(rAtt,keySet,newSep)){
                            passedOFD.add(currentLeftKeySet);
                            fd.get(rAtt).add(currentLeftKeySet);
                        }else if(ontAtts.contains(rAtt)){
                            Integer value = ofdHolds(rAtt,keySet,newSep, threshold);
                            switch (value) {
                                case 1:
                                    passedOFD.add(currentLeftKeySet);
                                    synofd.get(rAtt).add(currentLeftKeySet);
                                    break;
                                case 2:
                                    passedOFD.add(currentLeftKeySet);
                                    isaofd.get(rAtt).add(currentLeftKeySet);
                                    break;
                                default:
                                    findMore = true;
                                    break;
                            }        
                        }else{
                            findMore = true;
                        }
                    }
                }
            }
        }
        return findMore;
    }
    
    //Using partitions, fdHolds determines if a Functional dependancy holds
    private static boolean fdHolds(String rAtt, Set<String> keySet, List<List<Integer>> sep){
        if(PART.containsKey(keySet) && sep.size() == PART.get(keySet).size()){
            return true;
        }else{
            Set<String> rAttS = new HashSet<>();
            rAttS.add(rAtt);
            List<List<Integer>> allSep;
            allSep = sep.stream().flatMap(sl1 -> PART.get(rAttS).stream().map(sl2 -> {      //same process used in leftSideRec(), calculates the new separation
                List<Integer> lout = new ArrayList<>();
                lout.addAll(sl1);
                lout.retainAll(sl2);
                return lout;
            })).filter(l -> l.size() > 0).distinct().collect(Collectors.toList());
            PART.put(keySet, allSep);
            return sep.size() == allSep.size();
        }
    }
    
    //Using partitions determined in fdHolds, ofdHolds determines if isa or synonym ontologies allow the data to satisfy the OFD
    private static Integer ofdHolds(String rAtt, Set<String> keySet, List<List<Integer>> sep, Integer threshold){
        List<List<Integer>> rSep = new ArrayList<>(PART.get(keySet));
        List<List<Integer>> lSep = new ArrayList<>(sep);
            
        Boolean isaHolds = false;
        Boolean synHolds = true;
        
        while(!lSep.isEmpty()){
            List<Integer> equiv = lSep.get(0);
            if(equiv.size() == 1 || rSep.contains(equiv)){
                rSep.remove(equiv);
                lSep.remove(equiv);
            }else{
                List<Integer> check = new ArrayList<>();
                List<List<Integer>> remove = new ArrayList<>();
                for(List<Integer> rEquiv:rSep){
                    if(lSep.get(0).contains(rEquiv.get(0))){
                        check.add(rEquiv.get(0));
                        remove.add(rEquiv);
                    }
                }
                rSep.removeAll(remove);
                lSep.remove(equiv);
                Boolean isa = false;
                List<List<String>> table = OntFormat.getTable(rAtt);
                String first = table.get(check.get(0)).get(0);
                if(synHolds){
                    for(Integer index:check){
                        if(!first.equals(table.get(index).get(0))){
                            isa = true;
                            synHolds = false;
                            break;
                        }
                    }
                }
                if(isa && threshold>0 && check.size() <= threshold + 1){
                    Integer maxIndex = 0;
                    Integer startIndex = check.get(0);
                    for(Integer index:check){                   //finds the value in check with the value at the lowest point on the ontology
                        Integer in = table.get(index).indexOf(first);
                        
                        if(in > maxIndex){
                            maxIndex = in;
                            startIndex = index;
                        }
                    }
                    List<String> compare = table.get(startIndex);
                    for(Integer index:check){                       //determines if all other values in check are ancestors of the lowest point within the threshold
                        if(!compare.contains(table.get(index).get(0))){
                            return 0;
                        }
                    } 
                    isaHolds = true;
                }
            }
        }
        if(isaHolds){
            return 2;
        }else if(synHolds){
            return 1;
        }else{
            return 0;
        }
    }

    
    //finalOutput outputs all OFD's to a .txt file with the name INPUTFILENAME_out.txt
    //can also output information about the time of the experiment, see below.
    private static void finalOutput(String fileName , String csv_delim, double dur) throws IOException{
        String outFileName = fileName.substring(0, fileName.length()-4) + "_out.txt";                       
        PrintWriter output = new PrintWriter(new FileWriter(OUTPUT_FOLDER + outFileName, false));
        
        Iterator<Map.Entry<String, Set<Set<String>>>> fdIt = fd.entrySet().iterator();
        
        output.println("FD's found:");
        while(fdIt.hasNext()){
            Map.Entry<String, Set<Set<String>>> ofdSet = fdIt.next();
            Set<Set<String>> LHSset = ofdSet.getValue();
            LHSset.stream().map((preLHS) -> {
                String LHS = "\t";
                LHS = preLHS.stream().map((LHSvalue) -> LHSvalue + csv_delim).reduce(LHS, String::concat);
                return LHS;
            }).map((LHS) -> LHS.substring(0, LHS.length()-1)).map((LHS) -> {
                LHS += "-->" + ofdSet.getKey();
                return LHS;
            }).forEachOrdered((LHS) -> {
                output.println(LHS);
            });
        }    
        output.println("");
        output.println("Synonym OFD's found:");
        Iterator<Map.Entry<String, Set<Set<String>>>> synofdIt = synofd.entrySet().iterator();
        while(synofdIt.hasNext()){
            Map.Entry<String, Set<Set<String>>> ofdSet = synofdIt.next();
            Set<Set<String>> LHSset = ofdSet.getValue();
            LHSset.stream().map((preLHS) -> {
                String LHS = "\t";
                LHS = preLHS.stream().map((LHSvalue) -> LHSvalue + csv_delim).reduce(LHS, String::concat);
                return LHS;
            }).map((LHS) -> LHS.substring(0, LHS.length()-1)).map((LHS) -> {
                LHS += "-->" + ofdSet.getKey();
                return LHS;
            }).forEachOrdered((LHS) -> {
                output.println(LHS);
            });
        }  
        output.println("");
        output.println("IsA OFD's found:");
        Iterator<Map.Entry<String, Set<Set<String>>>> isaofdIt = isaofd.entrySet().iterator();
        while(isaofdIt.hasNext()){
            Map.Entry<String, Set<Set<String>>> ofdSet = isaofdIt.next();
            Set<Set<String>> LHSset = ofdSet.getValue();
            LHSset.stream().map((preLHS) -> {
                String LHS = "\t";
                LHS = preLHS.stream().map((LHSvalue) -> LHSvalue + csv_delim).reduce(LHS, String::concat);
                return LHS;
            }).map((LHS) -> LHS.substring(0, LHS.length()-1)).map((LHS) -> {
                LHS += "-->" + ofdSet.getKey();
                return LHS;
            }).forEachOrdered((LHS) -> {
                output.println(LHS);
            });
        }  
        
        //to view times of the experiment, uncomment the below.
        /*
        long min = Math.round(Math.floor(dur/60));
        double sec = dur % 60;
        
        output.println("");
        output.println("===============================================");
        output.println();
        output.print("\n\nTotal Duration: ");
        output.print(min);
        output.print(" minutes, ");
        output.print(sec);
        output.print(" seconds.\n");
        
        output.println();
        for(Integer i = 1; i<=times.size(); i++){
            Double time = times.get(i);
            Long lMin = Math.round(Math.floor(time/60));
            Double lSec = time % 60;
            output.println(lMin.toString()+" minutes, "+ lSec.toString() + " seconds spent at level " + i.toString() +".");
        }*/
        output.close();
    }
}
