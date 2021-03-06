package org.uma.khaos.m2align.problem.impl;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.uma.khaos.m2align.problem.MSAProblem;
import org.uma.khaos.m2align.score.Score;

/**
 * Class representing problems from the BAliBASE dataset
 *
 * @author Antonio J. Nebro
 * @author Cristian Zambrano
 *
 * @version 1.0
 */
public class BAliBASEMSAProblem extends MSAProblem {

  public String PDBPath;
  public String InstanceBalibase;
  public String BalibasePath;
  public String PreComputedPath;

  /**
   * Constructor
   */
  public BAliBASEMSAProblem(String problemName, String dataBaseDirectory, List<Score> scoreList)
      throws IOException, CompoundNotFoundException {
    super(scoreList);

    setName(problemName);

    setPaths(problemName, dataBaseDirectory);
    List<String> dataFiles = new ArrayList<>();
    dataFiles.add(PreComputedPath + ".tfa_clu");
    dataFiles.add(PreComputedPath + ".tfa_muscle");
    dataFiles.add(PreComputedPath + ".tfa_kalign");
    dataFiles.add(PreComputedPath + ".tfa_retalign");
    dataFiles.add(PreComputedPath + ".fasta_aln");
    dataFiles.add(PreComputedPath + ".tfa_probcons");
    dataFiles.add(PreComputedPath + ".tfa_mafft");
    dataFiles.add(PreComputedPath + ".tfa_fsa");

    listOfSequenceNames = new ArrayList<>();
    listOfSequenceNames = readSeqNameFromAlignment(BalibasePath + InstanceBalibase + ".tfa");

    originalSequences = readDataFromFastaFile(BalibasePath + InstanceBalibase + ".tfa");

    setNumberOfVariables(originalSequences.size());
    setNumberOfConstraints(0);

    listOfPrecomputedStringAlignments = readPreComputedAlignments(dataFiles);
  }

  public void setPaths(String problemName, String dataBaseDirectory) {

    String Group = "RV" + problemName.substring(2, 4).toString();

    //Directory with the PDB Files
    PDBPath = dataBaseDirectory + "/aligned/strike/" + Group + "/" + problemName + "/";

    //Balibase Directory
    BalibasePath = dataBaseDirectory + "/bb3_release/" + Group + "/";

    InstanceBalibase = problemName;

    //Directory with the PreAlignments
    PreComputedPath = dataBaseDirectory + "/aligned/" + Group + "/" + InstanceBalibase;
  }
}
