# jMetalMSA: Multiple Sequence Alignment with Multi-Objective Metaheuristics
jMetalMSA is a software project aimed at solving multiple sequence alignment (MSA) problems by using multi-objective metaheuristics. It is based on the jMetal multi-objective framework, which is extended with an encoding for representing MSA solutions. 

Currently it contains an implementation of the NSGA-II algorithm configured with a single-point crossover and shift closed gaps mutation operators, and three objectives to optimize: STRIKE, percentage of alignment columns and percentage of non gaps. These settings corresponds to the [MOSAStrE algorithm](http://bioinformatics.oxfordjournals.org/content/early/2013/06/21/bioinformatics.btt360.abstract). 

## Requirements
To use jMetalMSA the following software packages are required:
* [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html?ssSourceSiteId=otnes)
* [Apache Maven](https://maven.apache.org/)
* [Git](https://git-scm.com/)

## Downloading and compiling
To download jMetalMSA just clone the Git repository hosted in GitHub:
```
git clone https://github.com/jMetal/jMetalMSA.git
```
Once cloned, you can compile the software and generate a jar file with the following command:
```
mvn package
```
