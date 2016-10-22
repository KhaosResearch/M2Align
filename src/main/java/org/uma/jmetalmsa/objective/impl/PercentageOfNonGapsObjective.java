package org.uma.jmetalmsa.objective.impl;

import org.uma.jmetalmsa.objective.Objective;
import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.solution.MSASolution;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PercentageOfNonGapsObjective implements Objective {
  @Override
  public <S extends MSASolution> double compute(S solution, char[][] decodedSequences, DynamicallyComposedProblem<S> decomposedProblem) {
    
    double numberOfGaps=solution.getNumberOfGaps();
    double totalNumberOfElements = solution.getNumberOfVariables() * solution.getAlignmentLength();
    double NonGapsP=100 * (1 - (numberOfGaps / totalNumberOfElements));
    return NonGapsP;
    

  }

  @Override
  public boolean isAMinimizationObjective() {
    return false;
  }

  @Override
  public String getName() {
    return "SP";
  }

  @Override
  public String getDescription() {
    return "Percentage of gaps in a multiple sequence";
  }
}
