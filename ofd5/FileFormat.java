/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ofd5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Ian Currie
 */
public class FileFormat {
    public static List<List<String>> csvInput(String delim, String file) throws IOException{
        ArrayList<List<String>> csvList = new ArrayList<>();
        String lineString;
        ArrayList<String> line;
        BufferedReader br;
        
        br = new BufferedReader(new FileReader(new File(file)));
        while((lineString = br.readLine()) != null){
            line = new ArrayList<>(Arrays.asList(lineString.split(delim)));
            csvList.add(line);
        }
        br.close();
        return csvList;
    }
    
    public static Map<String, List<String>> toMap(List<List<String>> csvList){
        Map<String, List<String>> finalMap = new HashMap<>();
        
        for(int i = 0; i<csvList.get(0).size(); i++){
            List<String> values = new ArrayList<>();
            for(int j = 1; j< csvList.size(); j++){
                values.add(csvList.get(j).get(i));
            }
            finalMap.put(csvList.get(0).get(i),values);
        }
        return finalMap;
    }
    
    public static Map<Set<String>, List<List<Integer>>> getEquiv(Map<String, List<String>> columns, Integer length, List<String> ontologyAttributes){
        Map<Set<String>, List<List<Integer>>> map = new HashMap<>();
        Iterator<Map.Entry<String,List<String>>> columnIt = columns.entrySet().iterator();
        List<Integer> temp = new ArrayList<>();
        while(columnIt.hasNext()){
            List<List<Integer>> listOfValues = new ArrayList<>();
            Map.Entry<String,List<String>> entry = columnIt.next();
            String attribute = entry.getKey();
            List<String> column = entry.getValue();
            for(int i=0; i<column.size(); i++){
                List<Integer> values = new ArrayList<>();
                if(!temp.contains(i)){
                    values.add(i);
                    temp.add(i);
                    for(int j = i+1; j<column.size(); j++){
                        if(column.get(j).equals(column.get(i))){
                            values.add(j);
                            temp.add(j);
                        }
                    }
                    listOfValues.add(values);
                }
            }
            temp.clear();
            if(listOfValues.size()<length || ontologyAttributes.contains(attribute)){
                Set<String> attSet = new HashSet<>();
                attSet.add(attribute);
                map.put(attSet,listOfValues);
            }
        }
        return map;
    }
}