/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ofd5;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ian Currie
 */
public class alg1 {
    public static void main(String[] args) throws Exception{
        String csv_delim = args[0];
        Integer threshold = Integer.parseInt(args[1]);
        String fileName = args[2];
        
        List<String> ontologyAttributes = new ArrayList<>();
        for(int i=3; i<args.length; i++){
            ontologyAttributes.add(args[i]);
        }
        
        OFD5.ofdAlgorithm1(csv_delim, threshold, ontologyAttributes, fileName);
    }
}
