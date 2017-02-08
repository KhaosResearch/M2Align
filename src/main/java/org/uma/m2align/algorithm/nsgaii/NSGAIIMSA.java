package org.uma.m2align.algorithm.nsgaii;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.m2align.problem.MSAProblem;
import org.uma.m2align.solution.MSASolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class NSGAIIMSA extends NSGAIIMeasures<MSASolution> {
  private CountingMeasure evaluations;
  private DurationMeasure durationMeasure;
  private SimpleMeasureManager measureManager;

  private BasicMeasure<List<MSASolution>> solutionListMeasure;
  private BasicMeasure<Integer> numberOfNonDominatedSolutionsInPopulation;

  /**
   * Constructor
   */
  public NSGAIIMSA(MSAProblem problem, int maxIterations, int populationSize,
                   CrossoverOperator<MSASolution> crossoverOperator,
                   MutationOperator<MSASolution> mutationOperator,
                   SelectionOperator<List<MSASolution>, MSASolution> selectionOperator,
                   SolutionListEvaluator<MSASolution> evaluator) {
    super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);


    initMeasures();
  }

  @Override
  protected List<MSASolution> createInitialPopulation() {
    return ((MSAProblem) getProblem()).createInitialPopulation(getMaxPopulationSize());
  }

  @Override
  protected List<MSASolution> reproduction(List<MSASolution> population) {
    int numberOfParents = crossoverOperator.getNumberOfParents() ;

    checkNumberOfParents(population, numberOfParents);

    List<List<MSASolution>> offspairs = new ArrayList<>();
    for (int i = 0; i < population.size(); i+=2) {
      List<MSASolution> tmpList = new ArrayList<>() ;
      tmpList.add(population.get(i));
      tmpList.add(population.get(i + 1)) ;

      offspairs.add(tmpList) ;
    }

    List<MSASolution> offspringPopulation = offspairs
            .parallelStream()
            .map(pair -> crossoverOperator.execute(pair))
            .flatMap(pair -> pair.stream())
            .map(solution -> mutationOperator.execute(solution))
            .collect(Collectors.toList());

    //offspringPopulation.parallelStream().forEach(solution -> getProblem().evaluate(solution));

/*
    List<MSASolution> offspringPopulation = new ArrayList<MSASolution>(getMaxPopulationSize());
    for (int i = 0; i < getMaxPopulationSize(); i += numberOfParents) {
      List<MSASolution> parents = new ArrayList<MSASolution>(numberOfParents);
      for (int j = 0; j < numberOfParents; j++) {
        parents.add(population.get(i+j));
      }

      List<MSASolution> offspring = crossoverOperator.execute(parents);

      for(MSASolution s: offspring){
        mutationOperator.execute(s);
        offspringPopulation.add(s);
      }
    }
*/
    return offspringPopulation;
  }

  @Override protected List<MSASolution> evaluatePopulation(List<MSASolution> population) {
    //population.parallelStream().forEach(s -> getProblem().evaluate(s)) ;

    return population;
  }

  /* Measures code */
  private void initMeasures() {
    durationMeasure = new DurationMeasure();
    evaluations = new CountingMeasure(0);
    numberOfNonDominatedSolutionsInPopulation = new BasicMeasure<>();
    solutionListMeasure = new BasicMeasure<>();

    measureManager = new SimpleMeasureManager();
    measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
    measureManager.setPullMeasure("currentEvaluation", evaluations);
    measureManager.setPullMeasure("numberOfNonDominatedSolutionsInPopulation", numberOfNonDominatedSolutionsInPopulation);

    measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
    measureManager.setPushMeasure("currentEvaluation", evaluations);
  }

  @Override
  public String getName() {
    return "NSGAII";
  }

  @Override
  public String getDescription() {
    return "NSGAII";
  }
}
