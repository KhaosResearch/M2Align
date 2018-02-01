package org.uma.khaos.m2align.score.impl;

import org.uma.khaos.m2align.score.Score;
import org.uma.khaos.m2align.solution.MSASolution;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PercentageOfNonGapsScore implements Score {
  @Override
  public <S extends MSASolution> double compute(S solution, char [][]decodedSequences) {
    double numberOfGaps = solution.getNumberOfGaps();
    double totalNumberOfElements = solution.getNumberOfVariables() * solution.getAlignmentLength();
    double nonGapsPercentage=100 * (1 - (numberOfGaps / totalNumberOfElements));

    return nonGapsPercentage;
  }

  @Override
  public boolean isAMinimizationScore() {
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
