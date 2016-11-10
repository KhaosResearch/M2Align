package org.uma.m2align.problem;

import org.uma.jmetal.util.JMetalException;
import org.uma.m2align.objective.Objective;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;

public class StandardMSAProblem extends MSAProblem {

  /**
   * Constructor
   */
  public StandardMSAProblem(String msaProblemFileName, String dataBaseDirectory, String preComputedFiles, List<Objective> objectiveList)
          throws IOException, CompoundNotFoundException {
    super(objectiveList);

    setName(msaProblemFileName);

    String[] preComputedFilesList = preComputedFiles.split("-");
    
    if(preComputedFilesList.length<2){
         throw new JMetalException("Wrong number of Pre-computed Alignments, Minimum 2 files are required") ;
    }
    
    List<String> dataFiles = new ArrayList<>();
     
    for(String preComputedFile: preComputedFilesList){
        dataFiles.add(dataBaseDirectory + preComputedFile);
    }

    listOfSequenceNames = readSeqNameFromAlignment(msaProblemFileName);

    listOfPrecomputedStringAlignments = readPreComputedAlignments(dataFiles);

    originalSequences = readDataFromFastaFile(msaProblemFileName);

    setNumberOfVariables(listOfPrecomputedStringAlignments.get(0).size());
    setNumberOfConstraints(0);
  }


  
}
