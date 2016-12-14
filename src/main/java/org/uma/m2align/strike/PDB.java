/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.m2align.strike;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

public class PDB {
    
    private PDB_chain _first_chain;
    private TreeMap<String, PDB_chain> _pdb_chains = new TreeMap<String, PDB_chain>();
    private int _num_chains;

    private TreeMap<String, String> _aa = new TreeMap<String, String>();
    private String _pdb_f;

    private String activeString;
    private int activePosition;
        
    public PDB()
	{
		_aa.put("ALA", "A");
		_aa.put("ARG", "R");
		_aa.put("ASN", "N");
		_aa.put("ASP", "D");
		_aa.put("ASX", "B");
		_aa.put("CYS", "C");
		_aa.put("GLU", "E");
		_aa.put("GLN", "Q");
		_aa.put("GLX", "Z");
		_aa.put("GLY", "G");
		_aa.put("HIS", "H");
		_aa.put("ILE", "I");
		_aa.put("LEU", "L");
		_aa.put("LYS", "K");
		_aa.put("MET", "M");
		_aa.put("PHE", "F");
		_aa.put("PRO", "P");
		_aa.put("SER", "S");
		_aa.put("THR", "T");
		_aa.put("TRP", "W");
		_aa.put("TYR", "Y");
		_aa.put("VAL", "V");
		_aa.put("XAA", "X");
		_aa.put("CSE", "U");
	}
    
    private String _aa3_1(String amino_accid_name)
    {
            if (_aa.containsKey(amino_accid_name))
            {
                    return _aa.get(amino_accid_name);
            }
            else
            {
                    return "#";
            }
    }
    
    /**
     * Reads a pdb file and saves the content in the object.
     *
     * \param pdb_f The name of the pdb_file.
     * \param ignore_H If set to true hydrogen atoms will be ignored.
     */
    public final void read_pdb(String pdb_f, boolean ignore_H)
    {
            _num_chains = 0;
            String last_chain = " ";
            String tmp_name = strChr(pdb_f, '/');
            if (tmp_name == null)
              {
                      tmp_name = pdb_f;
              }
              else
              {
                     _pdb_f = tmp_name;
              }
              String line;
              int file_res_ID;
              int old_res_ID = -9999;
              char iCode;
              char  old_iCode = '%';
              int res_ID = 0;
              int atom_serial_number = 0;
              String amino_accid;
              String chain_ID;
              String atom_name;
              double x, y, z;
              int i, j;
              String tmp_aa;
              
             PDB_chain chain_it = null;
              
            try{
                BufferedReader reader = new BufferedReader(new FileReader(pdb_f));
                while ((line = reader.readLine()) != null)
                {
                    if(line.substring(0, 6).equals("ENDMDL")) {
                        while ((line = reader.readLine()) != null){
                            if(line.substring(0, 4).equals("ATOM")) {
                                   if (!last_chain.equals(line.charAt(11))){
                                                break;
                                    }
                            }
                        }
                    }

                    if(line.substring(0, 6).equals("SEQRES")) {
                            System.out.println(line);
                            if (!last_chain.equals(line.charAt(11)))
                            {
                                    last_chain = line.substring(11,12);
                                    System.out.println(last_chain);
                                    ++_num_chains;
                                    
                                   _pdb_chains.put(last_chain, new PDB_chain(last_chain.charAt(0)));
                                   chain_it = _pdb_chains.get(last_chain);
                                   if (_num_chains == 1)
                                    {
                                       _first_chain = chain_it;
                                    }
                            }

                            strTok(line.substring(16), " \n");
                            while ((tmp_aa = strTok(null, " \n")) != null)
                            {
                                System.out.println(tmp_aa);    
                                amino_accid = _aa3_1(tmp_aa);

                                    if (!amino_accid.equals("#"))
                                    {
                                            chain_it.push_back_seq_res(amino_accid);
                                    }
                                    else
                                    {
                                            continue;
                                    }
                            }

                            System.out.println(_num_chains);
                    }else{

                        if(line.substring(0, 4).equals("ATOM")) 
                        {
                            chain_ID = line.substring(21,22);
                        
                            if (!last_chain.equals(chain_ID))
                            {
                                    last_chain = chain_ID;
                                    //Cases with ATOM-FIELD but no SEQRES FIELD are ignored!
                                    if (!_pdb_chains.containsKey(last_chain))
                                    {
                                            System.out.println("Warning: Chain " +  last_chain + " does not have an SEQRES field and is ignored");
                                            while ((line = reader.readLine()) != null){
                                                if(line.substring(0, 3).equals("TER")) 
                                                            break;
                                                
                                            }
                                            continue;
                                    }
                                    old_iCode = '%';
                                    old_res_ID = -999;
                                    res_ID = 0;
                            }
                            
                            atom_name="";
                            for (i = 12; i < 16; ++i)
                            {
                                if (line.charAt(i) != ' ')
                                {
                                    atom_name = atom_name + line.charAt(i);
                                }
                            }
                            if ((ignore_H) && (atom_name.charAt(0) == 'H'))
                            {
                                    continue;
                            }

                            ++atom_serial_number;
                            amino_accid = _aa3_1(line.substring(17,20));
                            if (amino_accid == "#")
                            {
                                    continue;
                            }

                            iCode = line.charAt(26);
                            file_res_ID = Integer.parseInt(line.substring(22,26).trim());

                            if ((old_res_ID != file_res_ID) || (iCode != old_iCode))
                            {
                                    ++res_ID;
                                    old_res_ID = file_res_ID;
                                    old_iCode = iCode;
                                     //printf("last_chain: %c %c %s\n", last_chain, amino_accid, line);
                                    chain_it._atom_list.add(new TreeMap<String, Atom>());
                                    chain_it.push_back_atom_res(amino_accid);
                            }
                            
                            x=Double.parseDouble(line.substring(30,38));
                            y=Double.parseDouble(line.substring(38,46));
                            z=Double.parseDouble(line.substring(46,54));
                            chain_it._atom_list.get(res_ID - 1).put(atom_name, new Atom(chain_ID, res_ID, amino_accid.charAt(0), atom_name, atom_serial_number, x, y, z));
                    }
                 }

              }
             reader.close();
           }
            catch (IOException e)
            {
                System.out.println("ERROR: Could not open PDB file: " + pdb_f + ". Error " + e.toString());
                System.exit(2);
            }
    }
    
    
    public String strChr(String stringToSearch, char charToFind){
        int index = stringToSearch.indexOf(charToFind);
        if (index > -1)
                return stringToSearch.substring(index);
        else
                return null;
    }


    public  String strTok(String stringToTokenize, String delimiters)
    {
            if (stringToTokenize != null)
            {
                    activeString = stringToTokenize;
                    activePosition = -1;
            }

            //the stringToTokenize was never set:
            if (activeString == null)
                    return null;

            //all tokens have already been extracted:
            if (activePosition == activeString.length())
                    return null;

            //bypass delimiters:
            activePosition++;
            while (activePosition < activeString.length() && delimiters.indexOf(activeString.charAt(activePosition)) > -1)
            {
                    activePosition++;
            }

            //only delimiters were left, so return null:
            if (activePosition == activeString.length())
                    return null;

            //get starting position of string to return:
            int startingPosition = activePosition;

            //read until next delimiter:
            do
            {
                    activePosition++;
            } while (activePosition < activeString.length() && delimiters.indexOf(activeString.charAt(activePosition)) == -1);

            return activeString.substring(startingPosition, activePosition);
    }

}
