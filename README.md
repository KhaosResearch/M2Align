# M2Align: Multi-Objective Multiple Sequence Alignment

M2Align is a software tool aimed at solving multiple sequence alignment (MSA) problems with a multi-objective metaheuristic. It is implemented by using the [jMetal framework](https://github.com/jMetal/jMetal), which is extended with an encoding for representing MSA solutions and its corresponding mutation and crossover operators, and it is able of reducing the computing time by exploiting the computing capabilities of common multi-core CPU computers. 

The tool is described in the paper: "M2Align: parallel multiple sequence alignment with a multi-objective metaheuristic". Cristian Zambrano-Vega, Antonio J. Nebro José García-Nieto, José F. Aldana-Montes. Bioinformatics, Volume 33, Issue 19, 1 October 2017, Pages 3011–3017 ([DOI](https://doi.org/10.1093/bioinformatics/btx338)).

## Summary of features
M2Align has the following features:
* Evolutionary algorithm based on NSGA-II.
* Implemented in Java by using the jMetal framework.
* The variation operators are single-point crossover and closed gap shifting.
* It allows to optimize the following scores: totally conserved columns, non-gaps pecentage, and STRIKE.
* The algorithm can run in parallel in multi-core systems.
* The encoding is based on storing gap information.
* If PDB structures are not available, the Sum-of-pairs score is provided as an alternative to STRIKE.
* The project has MIT license.

## Requirements
To use M2Align the following software packages are required:
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html?ssSourceSiteId=otnes)
* [Apache Maven](https://maven.apache.org/)
* [Git](https://git-scm.com/)
* [Strike Contacts Generator](https://github.com/cristianzambrano/strikeContactGenerator)

## Downloading and compiling
To download M2Align just clone the Git repository hosted in GitHub:
```
git clone https://github.com/KhaosResearch/M2Align.git
```
Once cloned, you can compile the software and generate a jar file with the following command:

```
mvn package
```

This sentence will generate a directory called `target` which will contain a file called `m2align-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Download PDB files and Generation of the Strike Contact Matrix files

M2Align provides a utility to download the PDB structures files from the [Protein Data Bank](http://www.rcsb.org) and generate the Strike Contact Matrix (with the utility [STRIKE Contact Matrix Generator](https://github.com/cristianzambrano/strikeContactGenerator)) automatically. 

To execute this utility and get all the structural information requeried by STRIKE measure, run this command:

```java
java -cp target/m2align-1.0-SNAPSHOT-jar-with-dependencies.jar org.uma.khaos.m2align.strike.GenerateStructuralInformation sequencesFileName outputDirectory pathToStrike_contactgenerator

```

* sequencesFileName: the filename of the sequences dataset (in FASTA Format). The names of the sequences must be correctly defined, because, the four first letters will be the `Sequence Key` or `PDB ID` to search the Structural Information (PDB) into the database of the Protein Data Bank.
* outputDirectory: The Path where all files (PDB's (*.pdb) and Strike Contact Matrix (*.contacts)) will be save.
* pathToStrike_contactgenerator: The full path of the  STRIKE Contact Matrix Generator executable ,for instance: `/home/m2align/strike/bin/strike_pdbcontactsgenerator`.

The Contacts files for each sequence of the dataset will be created into the outputDirectory. M2Align will read the contact files to evaluate the STRIKE metric of the aligments. 

## Runing M2Align

To execute the MO-SAStrE algorithn to align a particular dataset of sequences, just run this command:

``` java
java -cp target/m2align-1.0-SNAPSHOT-jar-with-dependencies.jar org.uma.khaos.m2align.runner.M2AlignRunner sequencesFileName PDB_ContactsDataDirectory listOfPreComputedAlignments NumberOfEvaluations PopulationSize NumberOfCores
```
* sequencesFileName: the filename of the sequences dataset (in FASTA Format).
* dataDirectory: The Path that contains the Structural Information files (PDB's (*.pdb) and Strike Contact Matrix (*.contacts)) of the sequences to align and the Pre-Computed alignments to use to generate the Initial population of the algorithm.  
* listOfPreComputedAlignments: A list of filenames of the pre-alignments separated by `-`, only the file names must be defined, because M2Align will be search these files into the `dataDirectory`.
* NumberOfEvaluations: Number of the Maximun Evaluations of the algorithm.
* PopulationSize: Size of the population of the algorithm
* NumberOfCores: Number of cores to use for the execution of the Algorithm, greater than 1 enable the Parallel features.

To execute the algorithm to solve a problem in Balibase, just run this command:

``` java
java -cp target/m2align-1.0-SNAPSHOT-jar-with-dependencies.jar org.uma.khaos.m2align.runner.M2AlignBALIBASE balibaseProblemName dataDirectory NumberOfEvaluations PopulationSize NumberOfCores
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

## Results 

The output of the program are two files:
* `VAR.tsv`: contains the Pareto front approximation. For each solution, this file contains a line with the values of the three objectives.
* `FUN.tsv`: contains the Pareto set approximation. Each solution is represented in FASTA format.

## Commands to precompute alignments (needed for generating the initial population)
Assuming that the FASTA file containing the sequences to align is named "FASTA_FILE", the sintax to run the different tools are:
```
clustalw2 -infile=FASTA_FILE -outfile=FASTA_FILE_clu" -output=FASTA -ALIGN -QUIET -OUTORDER=input
mafft --auto --inputorder --quiet FASTA_FILE > FASTA_FILE_mafft
t_coffee FASTA_FILE -output fasta
muscle -in FASTA_FILE -fastaout FASTA_FILE_muscle" -quiet
java -jar /usr/local/bin/retalign.jar -out FASTA_FILE_retalign" FASTA_FILE
kalign -c input -f fasta -q -i FASTA_FILE -o FASTA_FILE_kalign
probcons FASTA_FILE > FASTA_FILE_probcons
fsa --refinement 100 FASTA_FILE > FASTA_FILE_fsa
```

## Example Solving BB11003 dataset of BAliBASE

Given the set of unligned protein sequences from BAliBASE (BB11003.tfa), to generate multiobjetive multiple sequence alignment with M2Align, follow next steps:

* Downdoad the PDB files of the protein sequences 
	Go to https://www.rcsb.org/ and search by PDB ID: 1ad3, 1uzb, 1eyy and 1o20

* Save the downloaded .pdb files into a same directory, "example"

* Generate the contact files of these 4 PDB files, using the STRIKE Contact Matrix Generator, with these commands

```
STRIKECONTACTGENERATOR_PATH/bin/strike_pdbcontactsgenerator 1ad3_A 1ad3_A.pdb A ./
STRIKECONTACTGENERATOR_PATH/bin/strike_pdbcontactsgenerator 1o20_A 1o20_A.pdb A ./
STRIKECONTACTGENERATOR_PATH/bin/strike_pdbcontactsgenerator 1eyy_A 1eyy_A.pdb A ./
STRIKECONTACTGENERATOR_PATH/bin/strike_pdbcontactsgenerator 1uzb_A 1uzb_A.pdb A ./
```
 
The contacts files must be created: 1ad3_A.contacts, 1o20_A.contacts, 1eyy_A.contacts and 1uzb_A.contacts.
 
* Generate the Pre-alignments using state-of-the-art MSA software
 
To create the pre-alignments for the initial population of the algorithm, run the script gen_prealignments.sh of the example folder
```
 ./gen_prealignments.sh
```

* Finally execute M2Align using the command:
```
java -cp target/m2align-1.3-SNAPSHOT-jar-with-dependencies.jar org.uma.khaos.m2align.runner.M2AlignRunner example/BB11003.tfa example/pdb/ BB11003.tfa_clu-BB11003.tfa_fsa-BB11003.fasta_aln-BB11003.tfa_probcons-BB11003.tfa_kalign 25000 100 8
```

## Web based tools for visualizing alignments
* [Alignment Annotator](http://www.bioinformatics.org/strap/aa/)
* [MSAViewer](http://msa.biojs.net/app/)
