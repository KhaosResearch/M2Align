package org.uma.m2align.algorithm.nsgaii;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.SteadyStateNSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.m2align.problem.MSAProblem;
import org.uma.m2align.solution.MSASolution;

import java.util.List;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class ssNSGAIIMSA extends SteadyStateNSGAII<MSASolution> {

    /**
   * Constructor
   */
  public ssNSGAIIMSA(MSAProblem problem, int maxIterations, int populationSize,
                    CrossoverOperator<MSASolution> crossoverOperator,
                    MutationOperator<MSASolution> mutationOperator,
                    SelectionOperator<List<MSASolution>, MSASolution> selectionOperator,
                    SolutionListEvaluator<MSASolution> evaluator) {
    super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator) ;
  }

  @Override
  protected List<MSASolution> createInitialPopulation() {
      return ((MSAProblem)getProblem()).createInitialPopulation(getMaxPopulationSize());
  }

  @Override
  public String getName() {
    return "ssNSGAII";
  }

  @Override
  public String getDescription() {
    return "ssNSGAII";
  }
}
