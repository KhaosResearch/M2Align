package org.uma.jmetalmsa.problem;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalmsa.crossover.SPXMSACrossover;
import org.uma.jmetalmsa.objective.Objective;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.solution.util.ArrayChar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MSAProblem extends DynamicallyComposedProblem<MSASolution> {
  public String PDBPath;
  public String InstanceBalibase;
  public String BalibasePath;
  public String PreComputedPath;
  public List<ArrayChar> originalSequences;
  public List<List<ArrayChar>> listOfPrecomputedStringAlignments;
  private List<StringBuilder> listOfSequenceNames;


  /**
   * Constructor
   */
  public MSAProblem(String problemName, String dataBaseDirectory, List<Objective> objectiveList)
          throws IOException, CompoundNotFoundException {
    super(objectiveList);

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
    listOfSequenceNames = readSeqNameFromAlignment(dataFiles.get(0));

    listOfPrecomputedStringAlignments = readPreComputedAlignments(dataFiles);

    originalSequences = readDataFromFastaFile(BalibasePath + InstanceBalibase + ".tfa");

    setNumberOfVariables(listOfPrecomputedStringAlignments.get(0).size());
    setNumberOfConstraints(0);
  }


  public List<MSASolution> createInitialPopulation(int Size) {
    List<MSASolution> population = new ArrayList<>(Size);

    JMetalRandom randomGenerator = JMetalRandom.getInstance();

    for (List<ArrayChar> sequenceList : listOfPrecomputedStringAlignments) {

      MSASolution newIndividual = new MSASolution(sequenceList, this);
      population.add(newIndividual);
    }

    int parent1, parent2;
    List<MSASolution> children, parents;
    SPXMSACrossover crossover = new SPXMSACrossover(1);

    while (population.size() < Size) {
      parents = new ArrayList<>();

      parent1 = randomGenerator.nextInt(0, population.size() - 1);
      do {
        parent2 = randomGenerator.nextInt(0, population.size() - 1);
      } while (parent1 == parent2);
      parents.add(population.get(parent1));
      parents.add(population.get(parent2));

      children = crossover.execute(parents);

      population.add(children.get(0));
      population.add(children.get(1));

    }
    return population;
  }


  public List<List<ArrayChar>> readPreComputedAlignments(List<String> dataFiles) {
    List<List<ArrayChar>> listPreAlignments = new ArrayList<>();
    for (String dataFile : dataFiles) {
      try {
        listPreAlignments.add(readDataFromFastaFile(dataFile));
      } catch (Exception e) {
        throw new JMetalException("Error reading data from fasta files " + dataFile);
      }
    }
    return listPreAlignments;
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

  /**
   * CreateSolution() method
   */
  @Override
  public MSASolution createSolution() {
    return new MSASolution(this);
  }

  /**
   * Read data from a FASTA file
   */
  public List<ArrayChar> readDataFromFastaFile(String dataFile) throws IOException, CompoundNotFoundException {

    List<ArrayChar> sequenceList = new ArrayList<>();

    LinkedHashMap<String, ProteinSequence>
            sequences = FastaReaderHelper.readFastaProteinSequence(new File(dataFile));

    for (Map.Entry<String, ProteinSequence> entry : sequences.entrySet()) {
      sequenceList.add(new ArrayChar(entry.getValue().getSequenceAsString()));
    }

    return sequenceList;
  }

  public List<StringBuilder> getListOfSequenceNames() {
    return listOfSequenceNames;
  }


  /**
   * Read Sequences Name from a FASTA file
   */
  public List<StringBuilder> readSeqNameFromAlignment(String dataFile)
          throws IOException, CompoundNotFoundException {

    List<StringBuilder> SeqNameList = new ArrayList<>();

    LinkedHashMap<String, ProteinSequence>
            sequences = FastaReaderHelper.readFastaProteinSequence(new File(dataFile));

    for (Map.Entry<String, ProteinSequence> entry : sequences.entrySet()) {
      SeqNameList.add(new StringBuilder(entry.getValue().getOriginalHeader()));
    }

    return SeqNameList;
  }

  public void printSequenceListToFasta(List<ArrayChar> solutionList, String fileName) throws Exception {
    List<ProteinSequence> proteinSequences = new ArrayList<>();
    for (ArrayChar sequence : solutionList) {
      proteinSequences.add(new ProteinSequence(sequence.toString()));
    }

    FastaWriterHelper.writeProteinSequence(new File(fileName), proteinSequences);
  }
}
