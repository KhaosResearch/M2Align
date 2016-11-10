package org.uma.m2align.solution;

import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;
import org.uma.m2align.problem.MSAProblem;
import org.uma.m2align.solution.util.ArrayChar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MSASolution extends AbstractGenericSolution<List<Integer>, MSAProblem> {

  public static final char GAP_IDENTIFIER = '-';

  List<Integer> sizeOriginalSequences;


  /**
   * Constructor
   */
  public MSASolution(MSAProblem problem) {
    super(problem);
    setAttributesSeqName(problem.getListOfSequenceNames());
    setSizeOfOriginalSequences(problem.originalSequences);
  }

  public MSASolution(List<ArrayChar> AlignedSeqs, MSAProblem problem) {
    super(problem);

    setAttributesSeqName(problem.getListOfSequenceNames());
    setSizeOfOriginalSequences(problem.originalSequences);
    encode(AlignedSeqs);
  }

  /**
   * Constructor
   */
  public MSASolution(MSAProblem problem, List<List<Integer>> gapsGroups) {
    super(problem);

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      setVariableValue(i, gapsGroups.get(i));
    }

    setSizeOfOriginalSequences(problem.originalSequences);
    setAttributesSeqName(problem.getListOfSequenceNames());
  }

  /**
   * Copy Constructor
   */
  public MSASolution(MSASolution solution) {
    super(solution.problem);

    for (int i = 0; i < getNumberOfVariables(); i++) {

      List<Integer> gapsGroup = new ArrayList();
      for (int j = 0; j < solution.getVariableValue(i).size(); j++)
        gapsGroup.add(solution.getVariableValue(i).get(j));

      setVariableValue(i, gapsGroup);
    }

    for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i));
    }

    attributes = new HashMap(solution.attributes);

    sizeOriginalSequences = new ArrayList(solution.sizeOriginalSequences.size());
    for (int i = 0; i < solution.sizeOriginalSequences.size(); i++) {
      sizeOriginalSequences.add(solution.sizeOriginalSequences.get(i));
    }
  }
  
  public void setSizeOfOriginalSequences(List<ArrayChar> originalSequences) {
    sizeOriginalSequences = new ArrayList(originalSequences.size());
    for (int i = 0; i < originalSequences.size(); i++)
      sizeOriginalSequences.add(originalSequences.get(i).getSize());
  }


/*
  public List<List<Integer>> encode(List<ArrayChar> alignedSequences) {
    List<List<Integer>> gapsGroups = new ArrayList<>();

    for (int i = 0; i < alignedSequences.size(); i++) {
      gapsGroups.add(getGapsGroup(alignedSequences.get(i)));
      setVariableValue(i, gapsGroups.get(i));
    }

    return gapsGroups;
  }
*/

  public void encode(List<ArrayChar> alignedSequences) {
    for (int i = 0; i < alignedSequences.size(); i++) {
      setVariableValue(i, getGapsGroup(alignedSequences.get(i)));
    }
  }

  public List<Integer> getGapsGroup(ArrayChar sequence) {
    List<Integer> gapsGroups = new ArrayList<>();
    Boolean GapOpen = false;
    int start = 0;
    for (int i = 0; i < sequence.getSize(); i++) {
      if (sequence.charAt(i) == '-') {
        if (!GapOpen) {
          GapOpen = true;
          start = i;
        }
      } else {
        if (GapOpen) {
          gapsGroups.add(start);
          gapsGroups.add(i - 1);
          GapOpen = false;
        }

      }
    }

    if (GapOpen) {
      gapsGroups.add(start);
      gapsGroups.add(sequence.getSize() - 1);
    }

    return gapsGroups;
  }

  public List<ArrayChar> decode() {
    return decode(getOriginalSequences());
  }

  public List<ArrayChar> decode(List<ArrayChar> originalSeqs) {
    List<ArrayChar> alignedSequences = new ArrayList<ArrayChar>();

    for (int i = 0; i < getNumberOfVariables(); i++) {
      alignedSequences.add(decode(originalSeqs.get(i).getCharArray(), getVariableValue(i), getAlignmentLength(i)));
    }

    return alignedSequences;
  }

  public ArrayChar decode(char[] OriginalSeq, List<Integer> gapsGroups, int size) {
    char[] alignedSequence = new char[size];
    int j, l, p;

    for (j = 0; j < gapsGroups.size() - 1; j += 2) {
      for (l = gapsGroups.get(j); l <= gapsGroups.get(j + 1); l++) alignedSequence[l] = '-';
    }

    p = 0;
    for (j = 0; j < size; j++) {
      if (alignedSequence[j] != '-') alignedSequence[j] = OriginalSeq[p++];
    }

    return new ArrayChar(alignedSequence);
  }

  public char[][] decodeToMatrix() {
//    if (decodedSolution == null) {
//      decodedSolution = decodeToMatrix(getOriginalSequences()) ;
//    }
//    return decodedSolution ;
    return decodeToMatrix(getOriginalSequences());
  }

  public char[][] decodeToMatrix(List<ArrayChar> originalSeqs) {

    char[][] alignedSequences = new char[getNumberOfVariables()][];

    for (int i = 0; i < getNumberOfVariables(); i++) {
      alignedSequences[i] = decodeOneSequenceToArray(
              originalSeqs.get(i).getCharArray(),
              getVariableValue(i),
              getAlignmentLength(i));
    }

    return alignedSequences;
  }

  public char[] decodeOneSequenceToArray(char[] OriginalSeq, List<Integer> gapsGroups, int size) {
    char[] alignedSequence = new char[size];
    int j, l, p;

    for (j = 0; j < gapsGroups.size() - 1; j += 2) {
      for (l = gapsGroups.get(j); l <= gapsGroups.get(j + 1); l++)
        alignedSequence[l] = '-';
    }

    p = 0;
    for (j = 0; j < size; j++) {
      if (alignedSequence[j] != '-')
        alignedSequence[j] = OriginalSeq[p++];
    }

    return alignedSequence;
  }

  public void mergeGapsGroups() {
    for (int i = 0; i < getNumberOfVariables(); i++) {
      for (int j = 1; j < getVariableValue(i).size() - 2; j += 2) {

        if (getVariableValue(i).get(j) + 1 == getVariableValue(i).get(j + 1)) {
          getVariableValue(i).set(j, getVariableValue(i).get(j + 2));
          getVariableValue(i).remove(j + 1);
          getVariableValue(i).remove(j + 1);
          j -= 2;
        }
      }
    }
  }

  public void removeGapColumns() {
    List<Integer> gapsGroups;
    List<Integer> gapColumnsIndex = new ArrayList();
    List<Integer> gapColumnsIndexGG ;
    int j, k;

    gapsGroups = getVariableValue(0);
    for (j = 0; j < gapsGroups.size() - 1; j += 2) {
      gapColumnsIndexGG = getGapsColumns(1, gapsGroups.get(j), gapsGroups.get(j + 1));

      for (k = 0; k < gapColumnsIndexGG.size(); k++) {
        gapColumnsIndex.add(gapColumnsIndexGG.get(k));
      }
    }

    for (j = 0; j < gapColumnsIndex.size(); j++) {
      removeGapColumn(gapColumnsIndex.get(j) - j);
    }
  }

  private List<Integer> getGapsColumns(int varIndex, int RMin, int RMax) {
    List<Integer> gapsGroups;
    List<Integer> gapColumnsIndexGG ;
    List<Integer> gapColumnsIndex = new ArrayList();
    int j, k, RMinAux, RMaxAux;
    int numberOfVariables = this.getNumberOfVariables();

    gapsGroups = getVariableValue(varIndex);
    for (j = 0; j < gapsGroups.size() - 1; j += 2) {

      if (RMax < gapsGroups.get(j))
        break;

      RMinAux = RMin > gapsGroups.get(j) ? RMin : gapsGroups.get(j);
      RMaxAux = RMax < gapsGroups.get(j + 1) ? RMax : gapsGroups.get(j + 1);

      if (RMinAux <= RMaxAux) { //GapsIntersection
        if (varIndex == numberOfVariables - 1) { //last sequence into the GapsRange
          gapColumnsIndex.add(RMinAux);
          gapColumnsIndex.add(RMaxAux);
        } else {

          gapColumnsIndexGG = getGapsColumns(varIndex + 1, RMinAux, RMaxAux);

          if (varIndex == numberOfVariables - 2) {
            if (gapColumnsIndexGG.size() > 0) {
              for (k = gapColumnsIndexGG.get(0); k <= gapColumnsIndexGG.get(1); k++) {
                gapColumnsIndex.add(k);
              }
            }
          } else {
            for (k = 0; k < gapColumnsIndexGG.size(); k++) {
              gapColumnsIndex.add(gapColumnsIndexGG.get(k));
            }
          }
        }
      }
    }

    return gapColumnsIndex;
  }

  public void removeGapColumn(int index) {

    List<Integer> gapsGroups;
    int i, j, k;
    for (i = 0; i < this.getNumberOfVariables(); i++) {
      gapsGroups = this.getVariableValue(i);

      for (j = 0; j < gapsGroups.size() - 1; j += 2) {
        if (index >= gapsGroups.get(j) && index <= gapsGroups.get(j + 1)) {
          for (k = j + 1; k < gapsGroups.size(); k++) {
            gapsGroups.set(k, gapsGroups.get(k) - 1);
          }
          if (gapsGroups.get(j) > gapsGroups.get(j + 1)) {
            gapsGroups.remove(j);
            gapsGroups.remove(j);
          }

          break;
        }
      }
    }
  }

  public boolean isGapColumn(int index) {
    List<Integer> gapsGroups;
    boolean gapColumn;
    int i, j;
    for (i = 0; i < getNumberOfVariables(); i++) {
      gapsGroups = getVariableValue(i);
      gapColumn = false;
      for (j = 0; j < gapsGroups.size() - 1; j += 2) {
        if (index >= gapsGroups.get(j) && index <= gapsGroups.get(j + 1)) {
          gapColumn = true;
          break;
        }
      }

      if (!gapColumn) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String getVariableValueString(int i) {

    String Sequence;

    Sequence = ">" + getAttribute("SeqName" + i).toString() + "\n" +
            decode(getOriginalSequences().get(i).getCharArray(), getVariableValue(i), getAlignmentLength(i)).toString();

    if (i >= this.getNumberOfVariables() - 1)
      Sequence += "\n";

    return Sequence;
  }

  public List<Integer> getVariableListInteger(int i) {
    return getVariableValue(i);
  }

  public List<ArrayChar> getOriginalSequences() {
    return this.problem.originalSequences;
  }

  public MSAProblem getMSAProblem() {
    return this.problem;
  }

  public boolean isValid() {
    int sizeAlignment = getAlignmentLength();
    for (int k = 1; k < getNumberOfVariables(); k++) {
      if (sizeAlignment != getOriginalSequences().get(k).getSize() + getNumberOfGaps(k)) {

        System.out.println("Error Solution, " + k + " sequence has a wrong Length (OSeqLenghth: " + getOriginalSequences().get(k).getSize() +
                ") + NumOfGaps: " + getNumberOfGaps(k) + " is not equal to Size: " + sizeAlignment);
        return false;
      }

    }

    return true;
  }

  @Override
  public MSASolution copy() {
    return new MSASolution(this);
  }

  /*
   * Returns the number of gaps of a sequence
   * @param index
   * @return
   */
  public int getNumberOfGaps() {

    int numberOfGaps = 0;
    int i;
    for (i = 0; i < this.getNumberOfVariables(); i++) {
      numberOfGaps += getNumberOfGaps(i);
    }
    return numberOfGaps;
  }

  public int getNumberOfGaps(Integer index) {

    int numberOfGaps = 0;
    int j;
    List<Integer> gapsGroup = this.getVariableValue(index);
    for (j = 0; j < gapsGroup.size(); j += 2) {
      numberOfGaps += gapsGroup.get(j + 1) - gapsGroup.get(j) + 1;
    }
    return numberOfGaps;
  }


  /**
   * Set The Attributes with  the sequence name list
   */
  public void setAttributesSeqName(List<StringBuilder> SeqNames) {

    for (int i = 0; i < getNumberOfVariables(); i++) {
      if (i < SeqNames.size())
        this.setAttribute("SeqName" + i, SeqNames.get(i).toString());
      else
        this.setAttribute("SeqName" + i, "");
    }
  }

  @Override
  public String toString() {
    char[][] sequences = decodeToMatrix();
    String alignment = "";

    for (int i = 0; i < getNumberOfVariables(); i++) {
      alignment += ">" + getAttribute("SeqName" + i).toString() + "\n";
      int compoundCount = 0;
      for (int j = 0; j < sequences[i].length; j++) {
        alignment += sequences[i][j];
        compoundCount++;
        if (compoundCount == 60) {
          alignment += "\n";
          compoundCount = 0;
        }
      }

      //if (sequences[i].length % 60 != 0) {
        alignment += "\n";
      //}
    }

    return alignment;
  }

  /**
   * Write the MultipleSequenceAlignmentSolution in Fasta format
   */
  public void printSolutionToFasta(String fileName) throws Exception {

    List<ProteinSequence> proteinSequenceList = convertSolutionToProteinSequenceList();
    FastaWriterHelper.writeProteinSequence(new File(fileName), proteinSequenceList);
  }

  /**
   * Create a Protein sequence list from the sequences stored in the variables
   */
  public List<ProteinSequence> convertSolutionToProteinSequenceList() throws Exception {

    List<ProteinSequence> proteinSequenceList = new ArrayList<>(getNumberOfVariables());
    List<ArrayChar> Seqs = decode();

    for (int i = 0; i < getNumberOfVariables(); i++) {
      proteinSequenceList.add(new ProteinSequence(Seqs.get(i).toString()));
      proteinSequenceList.get(i).setOriginalHeader(getAttribute("SeqName" + i).toString());
    }

    return proteinSequenceList;
  }


  public int getAlignmentLength(int varIndex) {
    return sizeOriginalSequences.get(varIndex) + getNumberOfGaps(varIndex);
  }

  public int getAlignmentLength() {
    return sizeOriginalSequences.get(0) + getNumberOfGaps(0);
  }
  public boolean isGap(char symbol) {
    return symbol == GAP_IDENTIFIER;
  }
}

