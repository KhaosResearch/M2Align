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
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
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

  private BasicMeasure<List<MSASolution>> solutionListMeasure;

  /**
   * Constructor
   */
  public NSGAIIMSA(MSAProblem problem, int maxEvaluations, int populationSize,
      CrossoverOperator<MSASolution> crossoverOperator,
      MutationOperator<MSASolution> mutationOperator,
      SelectionOperator<List<MSASolution>, MSASolution> selectionOperator,
      SolutionListEvaluator<MSASolution> evaluator) {
    super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator,
        selectionOperator, evaluator);

    initMeasures();
  }

  @Override
  protected List<MSASolution> createInitialPopulation() {
    return ((MSAProblem) getProblem()).createInitialPopulation(getMaxPopulationSize());
  }

  @Override
  protected List<MSASolution> reproduction(List<MSASolution> population) {
    int numberOfParents = crossoverOperator.getNumberOfRequiredParents();

    checkNumberOfParents(population, numberOfParents);

    List<MSASolution> offspringPopulation = new ArrayList<>(getMaxPopulationSize());
    for (int i = 0; i < getMaxPopulationSize(); i += numberOfParents) {
      List<MSASolution> parents = new ArrayList<>(numberOfParents);
      for (int j = 0; j < numberOfParents; j++) {
        parents.add(population.get(i + j));
      }

      List<MSASolution> offspring = crossoverOperator.execute(parents);

      for (MSASolution s : offspring) {
        mutationOperator.execute(s);
        offspringPopulation.add(s);
      }
    }

    return offspringPopulation;
  }

  @Override
  protected List<MSASolution> evaluatePopulation(List<MSASolution> population) {
    population = evaluator.evaluate(population, getProblem());
    //population.parallelStream().forEach(s -> getProblem().evaluate(s)) ;

    return population;
  }

  @Override
  protected void initProgress() {
    evaluations.reset(getMaxPopulationSize());
  }

  @Override
  protected void updateProgress() {
    evaluations.increment(getMaxPopulationSize());
    solutionListMeasure.push(getPopulation());
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return evaluations.get() >= maxEvaluations;
  }

  /* Measures code */
  private void initMeasures() {
    evaluations = new CountingMeasure(0);
    solutionListMeasure = new BasicMeasure<>();

    measureManager = new SimpleMeasureManager();
    measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
    measureManager.setPushMeasure("currentEvaluation", evaluations);
  }

  @Override
  public String getName() {
    return "NSGAIIMSA";
  }

  @Override
  public String getDescription() {
    return "NSGAIIMSA";
  }
}
