package org.uma.m2align.algorithm;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.m2align.problem.MSAProblem;
import org.uma.m2align.solution.MSASolution;

/**
 * Created by ajnebro on 16/11/14.
 */
public class M2AlignBuilder extends NSGAIIBuilder<MSASolution> {

  /**
   * M2AlignBuilder constructor
   */

  public M2AlignBuilder(Problem<MSASolution> problem,
      CrossoverOperator<MSASolution> crossoverOperator,
      MutationOperator<MSASolution> mutationOperator) {
    super(problem, crossoverOperator, mutationOperator);

  }

  public NSGAII<MSASolution> build() {
    NSGAII<MSASolution> algorithm;

    algorithm = new M2Align((MSAProblem) getProblem(), getMaxIterations(), getPopulationSize(),
        getCrossoverOperator(),
        getMutationOperator(), getSelectionOperator(), getSolutionListEvaluator());
    return algorithm;
  }
}
