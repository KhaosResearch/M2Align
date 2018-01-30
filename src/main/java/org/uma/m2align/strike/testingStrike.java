/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.m2align.strike;

/**
 * @author Cristian
 */
public class testingStrike {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    PDB pdb = new PDB();
    pdb.read_pdb("C:\\msa\\aligned\\strike\\RV11\\BB11001\\1aab_.pdb", true);


  }

}
