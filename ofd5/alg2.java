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
public class alg2 {
    public static void main(String[] args) throws Exception{
        String fileName = "clinicaltrials_" + args[0] + ".csv";
        List<String> ontologyAttributes = new ArrayList<>();
        for(int i=1; i<args.length; i++){
            ontologyAttributes.add(args[i]);
        }
        
        OFD5.ofdAlgorithm2(",", 3, ontologyAttributes, fileName);
    }
}
