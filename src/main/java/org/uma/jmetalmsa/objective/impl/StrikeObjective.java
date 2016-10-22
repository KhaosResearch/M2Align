package org.uma.jmetalmsa.objective.impl;

import org.uma.jmetalmsa.objective.Objective;
import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.strike.Strike;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StrikeObjective implements Objective {
  private Strike strike ;
  private List<StringBuilder> listOfSequenceNames ;

  public void initializeParameters(String pdbPath, String instanceBalibase, List<StringBuilder> listOfSequenceNames) {
    strike = new Strike(pdbPath, instanceBalibase + ".con") ;
    this.listOfSequenceNames = listOfSequenceNames ;
  }

  @Override
  public <S extends MSASolution> double compute(S solution, char[][] decodedSequences, DynamicallyComposedProblem<S> decomposedProblem) {
    return strike.compute(decodedSequences, listOfSequenceNames, false) ;
  }

  @Override
  public boolean isAMinimizationObjective() {
    return false;
  }

  @Override
  public String getName() {
    return "Strike objective " ;
  }

  @Override
  public String getDescription() {
    return "Strike objective";
  }
}
