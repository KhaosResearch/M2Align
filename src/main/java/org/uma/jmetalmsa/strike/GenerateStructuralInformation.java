/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.jmetalmsa.strike;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

import org.uma.jmetal.util.JMetalException;


public class GenerateStructuralInformation {

    public static void main(String[] args) throws Exception {
        
         if (args.length != 3) {
             throw new JMetalException("Wrong number of arguments") ;
        }

         String dataFile=args[0];//alignment file
         String path=args[1];  //Path to save th pdb's files
         String strikeGeneratorPath=args[2];  //Path to StrikeGenator Executable
         
         String sequenceName;
         String pdbFileName;
         
        LinkedHashMap<String, ProteinSequence>
            sequences = FastaReaderHelper.readFastaProteinSequence(new File(dataFile));

        for (Map.Entry<String, ProteinSequence> entry : sequences.entrySet()) {
            
              sequenceName=entry.getValue().getOriginalHeader();
              pdbFileName= path + "/" + sequenceName +".pdb";
            
              if (downloadPDB(sequenceName, pdbFileName)){
                 System.out.println("PDBFile of Sequence Name " + sequenceName + " has been downloaded " + pdbFileName);
                  
                 if(exeStrikeContactGenerator(strikeGeneratorPath, sequenceName,  pdbFileName, "#", path)){
                     
                     System.out.println("Contact File of Sequence " + sequenceName + " has been created.");
                     
                 }else{
                     
                     System.out.println("ERROR. Generating Contact File of Sequence " + sequenceName);
                     
                 }
                  
                
              }

        }
        
       
    }
    
    
   public static boolean downloadPDB(String sequenceKey, String pdbFileName){
        

        try {
            
            URL url = new URL("http://www.rcsb.org/pdb/downloadFile.do?fileFormat=pdb&compression=NO&structureId="+ sequenceKey.substring(0, 4));
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            PrintWriter writer = new PrintWriter(pdbFileName, "UTF-8");

            String inputLine;
            while ((inputLine = in.readLine()) != null){
                writer.write(inputLine + "\n");               
            }
            writer.close();
            in.close();
            
            return true;
            
            
        } catch (Exception e) {
            
            System.out.println("Error, can`t download PDB file to Sequence Key " + sequenceKey.substring(0, 4) +"\n" + e.getMessage());
            return false;
        }
        
    }
   
   
  
    
    
    public static boolean exeStrikeContactGenerator(String exeStrike, String seqName, String pdbFileName, String Chain, String pathToSaveContact)
      throws Exception {

        try {

            String Comando = exeStrike + " " + seqName + " " + pdbFileName + " " + Chain + " " + pathToSaveContact;
 
            Process p = Runtime.getRuntime().exec(Comando);
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String value = br.readLine();
            br.close();

           return value.equals("1");

        } catch (Exception e) {
            
          e.printStackTrace();
          return false;
          
        }

    }
            
    
}
