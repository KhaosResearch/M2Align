# M2Align: Multi-objective Multiple Sequence Alignment 
M2Align is a software project aimed at solving multiple sequence alignment (MSA) problems by using multi-objective metaheuristics. It is based on the jMetal multi-objective framework, which is extended with an encoding for representing MSA solutions. 

Currently it contains an implementation of the NSGA-II algorithm configured with a single-point crossover and shift closed gaps mutation operators, and three objectives to optimize: STRIKE, percentage of alignment columns and percentage of non gaps. These settings corresponds to the [MOSAStrE algorithm](http://bioinformatics.oxfordjournals.org/content/early/2013/06/21/bioinformatics.btt360.abstract). 

## Summary of features
M2Align containts the following features:
* Implementation of the MO-SAStrE algorithm
* The algorithm can run in parallel in multi-core systems
* The included dataset is BAliBASE v3.0

## Requirements
To use M2Align the following software packages are required:
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html?ssSourceSiteId=otnes)
* [Apache Maven](https://maven.apache.org/)
* [Git](https://git-scm.com/)
* [Strike Contacts Generator](https://github.com/cristianzambrano/strikeContactGenerator)

## Downloading and compiling
To download M2Align just clone the Git repository hosted in GitHub:
```
git clone https://github.com/jMetal/M2Align.git
```
Once cloned, you can compile the software and generate a jar file with the following command:
```
mvn package
```
This sentence will generate a directory called `target` which will contain a file called `jMetalMSA-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Download PDB files and Generation of the Strike Contact Matrix files

M2Align provides a utility to download the PDB structures files from the [Protein Data Bank](http://www.rcsb.org) and generate the Strike Contact Matrix (with the utility [STRIKE Contact Matrix Generator](https://github.com/cristianzambrano/strikeContactGenerator)) automatically. 

To execute this utility and get all the structural information requeried by STRIKE measure, run this command:

````
java -cp target/m2align-1.0-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetalmsa.strike.GenerateStructuralInformation sequencesFileName outputDirectory pathToStrike_contactgenerator
```

* sequencesFileName: the filename of the sequences dataset (in FASTA Format). The names of the sequences must be correctly defined, because, the four first letters will be the `Sequence Key` or `PDB ID` to search the Structural Information (PDB) into the database of the Protein Data Bank.
* outputDirectory: The Path where all files (PDB's (*.pdb) and Strike Contact Matrix (*.contacts)) will be save.
* pathToStrike_contactgenerator: The full path of the  STRIKE Contact Matrix Generator executable ,for instance: `/home/m2align/strike/bin/strike_pdbcontactsgenerator`.

The Contacts files for each sequence of the dataset will be created into the outputDirectory. M2Align will read the contact files to evaluate the STRIKE metric of the aligments. 

## Runing M2Align

To execute the algorithm to align a particular dataset of sequences, just run this command:

````
java -cp target/m2align-1.0-SNAPSHOT-jar-with-dependencies.jar org.uma.m2align.runner.MOSAStrERunner sequencesFileName PDB_ContactsDataDirectory listOfPreComputedAlignments NumberOfEvaluations PopulationSize NumberOfCores
```
* sequencesFileName: the filename of the sequences dataset (in FASTA Format).
* dataDirectory: The Path that contains the Structural Information files (PDB's (*.pdb) and Strike Contact Matrix (*.contacts)) of the sequences to align and the Pre-Computed alignments to use to generate the Initial population of the algorithm.  
* listOfPreComputedAlignments: A list of filenames of the pre-alignments separated by `-`, only the file names must be defined, because M2Align will be search these files into the `dataDirectory`.
* NumberOfEvaluations: Number of the Maximun Evaluations of the algorithm.
* PopulationSize: Size of the population of the algorithm
* NumberOfCores: Number of cores to use for the execution of the Algorithm, greater than 1 enable the Parallel features.

To execute the algorithm to solve a problem in Balibase, just run this command:

````
java -cp target/m2align-1.0-SNAPSHOT-jar-with-dependencies.jar org.uma.m2align.runner.MOSAStrERunner_BAliBASE balibaseProblemName dataDirectory NumberOfEvaluations PopulationSize NumberOfCores
```
* balibaseProblemName: the BAliBASE instance name, for instance `BB12001`. 
* dataDirectory: The Path that contains the Structural Information files (PDB's (*.pdb) and Strike Contact Matrix (*.contacts)) of the sequences to align and the Pre-Computed alignments to use to generate the Initial population of the algorithm.  
* NumberOfEvaluations: Number of the Maximun Evaluations of the algorithm.
* PopulationSize: Size of the population of the algorithm
* NumberOfCores: Number of cores to use for the execution of the Algorithm, greater than 1 enable the Parallel features.

For solving BAliBASE problems, M2Align searches the Sequences Files in FASTA format, the Contacts files and the pre-computed alignments, as follows:

* Directory with the PDB Files:   dataDirectory + /aligned/strike/ + Group + / + balibaseProblemName + /
* Balibase Directory: dataDirectory + /bb3_release/ + Group + /
* Directory with the PreAlignments:  dataDirectory + /aligned/ + Group + / + balibaseProblemName;


##Results 

The output of the program are two files:
* `VAR.tsv`: contains the Pareto front approximation. For each solution, this file contains a line with the values of the three objectives.
* `FUN.tsv`: contains the Pareto set approximation. Each solution is represented in FASTA format.
