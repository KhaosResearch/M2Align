package org.uma.khaos.m2align.algorithm;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.khaos.m2align.problem.MSAProblem;
import org.uma.khaos.m2align.solution.MSASolution;

/**
 * Class implementing the M2Align algorithm published in:
 * M2Align: parallel multiple sequence alignment with a multi-objective metaheuristic
 * Cristian Zambrano-Vega Antonio J. Nebro José García-Nieto José F. Aldana-Montes
 * Bioinformatics, Volume 33, Issue 19, 1 October 2017, Pages 3011–3017.
 *
 * DOI: https://doi.org/10.1093/bioinformatics/btx338
 *
 * @author Antonio J. Nebro
 * @author Cristian Zambrano
 * @version 1.1
 */
public class M2Align extends NSGAIIMeasures<MSASolution> {

  private BasicMeasure<List<MSASolution>> solutionListMeasure;
  protected DurationMeasure durationMeasure ;


  /**
   * Constructor
   */
  public M2Align(MSAProblem problem, int maxEvaluations, int populationSize,
      CrossoverOperator<MSASolution> crossoverOperator,
      MutationOperator<MSASolution> mutationOperator,
      SelectionOperator<List<MSASolution>, MSASolution> selectionOperator,
      SolutionListEvaluator<MSASolution> evaluator) {
    super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator,
        selectionOperator, evaluator);

    initMeasures();
  }

  @Override
  public void run() {
    durationMeasure.reset();
    durationMeasure.start();
    super.run();
    durationMeasure.stop();
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
    durationMeasure = new DurationMeasure() ;

    measureManager = new SimpleMeasureManager();
    measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
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
