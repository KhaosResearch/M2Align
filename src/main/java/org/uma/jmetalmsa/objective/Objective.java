package org.uma.jmetalmsa.objective;

import org.uma.jmetal.util.naming.DescribedEntity;
import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.solution.MSASolution;

/**
 * Created by ajnebro on 14/6/16.
 */
public interface Objective extends DescribedEntity {
  public <S extends MSASolution> double compute(
          S solution, char[][] decodedSequences,
          DynamicallyComposedProblem<S> decomposedProblem);

  public boolean isAMinimizationObjective();
}
