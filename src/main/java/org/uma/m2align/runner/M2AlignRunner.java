//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.m2align.runner;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder.NSGAIIVariant;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.m2align.algorithm.nsgaii.NSGAIIMSABuilder;
import org.uma.m2align.crossover.SPXMSACrossover;
import org.uma.m2align.mutation.ShiftClosedGapsMSAMutation;
import org.uma.m2align.score.impl.PercentageOfAlignedColumnsScore;
import org.uma.m2align.score.impl.PercentageOfNonGapsScore;
import org.uma.m2align.score.impl.StrikeScore;
import org.uma.m2align.solution.MSASolution;

import java.util.ArrayList;
import java.util.List;
import org.uma.m2align.problem.StandardMSAProblem;
import org.uma.m2align.score.Score;


/**
 * Class to configure and run the M2Align algorithm for solving a MSA problem
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class M2AlignRunner {
  /*
   * Arguments: msaFileName,  dataDirectory, preComputedAlignments, maxEvaluations populationSize numberOfCores
   * @param args Command line arguments.
   */
  public static void main(String[] args) throws Exception {
    StandardMSAProblem problem;
    Algorithm<List<MSASolution>> algorithm;
    CrossoverOperator<MSASolution> crossover;
    MutationOperator<MSASolution> mutation;
    SelectionOperator selection;

    if (args.length != 6) {
      throw new JMetalException("Wrong number of arguments") ;
    }

    String msaFile = args[0];
    String dataDirectory = args[1];
    String preComputedAlignments = args[2];
    Integer maxEvaluations = Integer.parseInt(args[3]);
    Integer populationSize = Integer.parseInt(args[4]);
    Integer numberOfCores = Integer.parseInt(args[5]);
    
    crossover = new SPXMSACrossover(0.8);
    mutation = new ShiftClosedGapsMSAMutation(0.2);
    selection = new BinaryTournamentSelection(new RankingAndCrowdingDistanceComparator());

    List<Score> scoreList = new ArrayList<>();

    StrikeScore objStrike = new StrikeScore();
    scoreList.add(objStrike);
    scoreList.add(new PercentageOfAlignedColumnsScore());
    scoreList.add(new PercentageOfNonGapsScore());

    problem = new StandardMSAProblem(msaFile, dataDirectory, preComputedAlignments, scoreList);

    objStrike.initializeParameters(dataDirectory, problem.getListOfSequenceNames());

    SolutionListEvaluator<MSASolution> evaluator;

    if (numberOfCores == 1) {
      evaluator = new SequentialSolutionListEvaluator<>();

    } else {
      evaluator = new MultithreadedSolutionListEvaluator<MSASolution>(numberOfCores, problem);
    }

    algorithm = new NSGAIIMSABuilder(problem, crossover, mutation, NSGAIIVariant.NSGAII)
            .setSelectionOperator(selection)
            .setMaxEvaluations(maxEvaluations)
            .setPopulationSize(populationSize)
            .setSolutionListEvaluator(evaluator)
            .build();


    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute();

    List<MSASolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    
    for (MSASolution solution : population) {
      for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
        if (!scoreList.get(i).isAMinimizationScore()) {
          solution.setObjective(i, -1.0 * solution.getObjective(i));
        }
      }
    }

    DefaultFileOutputContext varFile = new  DefaultFileOutputContext("VAR.tsv");
    varFile.setSeparator("\n");
    DefaultFileOutputContext funFile = new  DefaultFileOutputContext("FUN.tsv");
    funFile.setSeparator("\t");

   
    new SolutionListOutput(population)
            .setVarFileOutputContext(varFile)
            .setFunFileOutputContext(funFile)
            .print();

    evaluator.shutdown();
  }
}
