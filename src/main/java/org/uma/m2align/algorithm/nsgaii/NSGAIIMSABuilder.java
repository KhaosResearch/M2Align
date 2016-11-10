package org.uma.m2align.algorithm.nsgaii;

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
public class NSGAIIMSABuilder extends NSGAIIBuilder<MSASolution> {

  /**
   * NSGAIIBuilder constructor
   *
   * @param problem
   * @param crossoverOperator
   * @param mutationOperator
   */
  NSGAIIVariant Variant;
    
  public NSGAIIMSABuilder(Problem<MSASolution> problem,
      CrossoverOperator<MSASolution> crossoverOperator,
      MutationOperator<MSASolution> mutationOperator, NSGAIIVariant _Variant) {
    super(problem, crossoverOperator, mutationOperator);
    
    Variant = _Variant;

  }

  public NSGAII<MSASolution>  build() {
    NSGAII<MSASolution> algorithm = null ;
    
    if (Variant.equals(NSGAIIVariant.NSGAII)) {
    
        algorithm = new NSGAIIMSA((MSAProblem) getProblem(), getMaxIterations(), getPopulationSize(), getCrossoverOperator(),
                      getMutationOperator(), getSelectionOperator(), getSolutionListEvaluator());
    
    } else if (Variant.equals(NSGAIIVariant.SteadyStateNSGAII)) {
      
        algorithm = new ssNSGAIIMSA((MSAProblem) getProblem(), getMaxIterations(), getPopulationSize(), getCrossoverOperator(),
                      getMutationOperator(), getSelectionOperator(), getSolutionListEvaluator() );
    
    }
    return algorithm ;
  }
}
