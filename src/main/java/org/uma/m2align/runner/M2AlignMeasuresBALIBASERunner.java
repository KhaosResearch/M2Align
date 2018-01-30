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

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder.NSGAIIVariant;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.m2align.algorithm.M2Align;
import org.uma.m2align.algorithm.M2AlignBuilder;
import org.uma.m2align.crossover.SPXMSACrossover;
import org.uma.m2align.mutation.ShiftClosedGapsMSAMutation;
import org.uma.m2align.problem.BAliBASEMSAProblem;
import org.uma.m2align.score.Score;
import org.uma.m2align.score.impl.PercentageOfAlignedColumnsScore;
import org.uma.m2align.score.impl.PercentageOfNonGapsScore;
import org.uma.m2align.score.impl.StrikeScore;
import org.uma.m2align.solution.MSASolution;


/**
 * Class to configure and run the M2Align algorithm to solve a problem from the BALIBASE dataset
 * providing runtime information
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class M2AlignMeasuresBALIBASERunner {
  /**
   * Arguments: instance,  dataDirectory, maxEvaluations populationSize numberOfCores
   * @param args Command line arguments.
   */
  public static void main(String[] args) throws Exception {
    BAliBASEMSAProblem problem;
    M2Align algorithm;
    CrossoverOperator<MSASolution> crossover;
    MutationOperator<MSASolution> mutation;
    SelectionOperator selection;

    if (args.length != 5) {
      throw new JMetalException("Wrong number of arguments") ;
    }
    String instance = args[0];
    String dataDirectory = args[1];
    Integer maxEvaluations = Integer.parseInt(args[2]);
    Integer populationSize = Integer.parseInt(args[3]);
    Integer numberOfCores = Integer.parseInt(args[4]);
    
    crossover = new SPXMSACrossover(0.8);
    mutation = new ShiftClosedGapsMSAMutation(0.2);
    selection = new BinaryTournamentSelection(new RankingAndCrowdingDistanceComparator());

    List<Score> scoreList = new ArrayList<>();

    StrikeScore objStrike = new StrikeScore();
    scoreList.add(objStrike);
    scoreList.add(new PercentageOfAlignedColumnsScore());
    scoreList.add(new PercentageOfNonGapsScore());

    problem = new BAliBASEMSAProblem(instance, dataDirectory, scoreList);

    objStrike.initializeParameters(problem.PDBPath, problem.getListOfSequenceNames());

    SolutionListEvaluator<MSASolution> evaluator;

    if (numberOfCores == 1) {
      evaluator = new SequentialSolutionListEvaluator<>();

    } else {
      evaluator = new MultithreadedSolutionListEvaluator<MSASolution>(numberOfCores, problem);
    }

    algorithm = (M2Align) new M2AlignBuilder(problem, crossover, mutation)
            .setSelectionOperator(selection)
            .setMaxEvaluations(maxEvaluations)
            .setPopulationSize(populationSize)
            .setSolutionListEvaluator(evaluator)
            .build();

    System.out.println("Algorithm " + algorithm.getName() + " running") ;

    MeasureManager measureManager = algorithm.getMeasureManager() ;

    DurationMeasure currentComputingTime =
        (DurationMeasure) measureManager.<Long>getPullMeasure("currentExecutionTime");

    BasicMeasure<List<MSASolution>> solutionListMeasure =
        (BasicMeasure<List<MSASolution>>) measureManager.<List<MSASolution>> getPushMeasure("currentPopulation");
    CountingMeasure iterationMeasure =
        (CountingMeasure) measureManager.<Long>getPushMeasure("currentEvaluation");

    solutionListMeasure.register(new SolutionListListener());
    iterationMeasure.register(new IterationListener());

    Thread algorithmThread = new Thread(algorithm) ;
    algorithmThread.start();

    algorithmThread.join();
    List<MSASolution> population = algorithm.getResult();
    long computingTime = currentComputingTime.get() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    
    for (MSASolution solution : population) {
      for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
        if (!scoreList.get(i).isAMinimizationScore()) {
          solution.setObjective(i, -1.0 * solution.getObjective(i));
        }
      }
    }
       
    DefaultFileOutputContext varFile = new  DefaultFileOutputContext("VAR." + instance + ".tsv");
    varFile.setSeparator("\n");
    DefaultFileOutputContext funFile = new  DefaultFileOutputContext("FUN." + instance + ".tsv");
    funFile.setSeparator("\t");
   
    new SolutionListOutput(population)
            .setVarFileOutputContext(varFile)
            .setFunFileOutputContext(funFile)
            .print();

    evaluator.shutdown();
  }

  private static class SolutionListListener implements MeasureListener<List<MSASolution>> {
    private int counter = 0 ;

    @Override  public void measureGenerated(List<MSASolution> solutions) {
      System.out.println("PUSH MEASURE. Counter = " + counter+ " First solution: " + solutions.get(0)) ;
      if ((counter % 10 == 0)) {
        System.out.println("PUSH MEASURE. Counter = " + counter+ " First solution: " + solutions.get(0)) ;
      }
      counter ++ ;
    }
  }

  private static class IterationListener implements MeasureListener<Long> {
    @Override  public void measureGenerated(Long value) {
      System.out.println("PUSH MEASURE. Iteration: " + value) ;
      if ((value % 50 == 0)) {
        System.out.println("PUSH MEASURE. Iteration: " + value) ;
      }
    }
  }
}

