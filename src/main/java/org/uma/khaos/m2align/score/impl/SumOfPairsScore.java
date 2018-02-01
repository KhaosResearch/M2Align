package org.uma.khaos.m2align.score.impl;

import org.uma.khaos.m2align.score.Score;
import org.uma.khaos.m2align.solution.MSASolution;
import org.uma.khaos.m2align.util.distancematrix.DistanceMatrix;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SumOfPairsScore implements Score {
  private DistanceMatrix distanceMatrix;

  public SumOfPairsScore(DistanceMatrix distanceMatrix) {
    this.distanceMatrix = distanceMatrix;
  }

  @Override
  public <S extends MSASolution> double compute(S solution,char [][]decodedSequences) {
    double sumOfPairs = 0;

    int numberOfVariables = decodedSequences.length;
    int lengthSequences = decodedSequences[0].length;

    for (int i = 0; i < (numberOfVariables - 1); i++) {
      for (int j = i + 1; j < numberOfVariables; j++) {
        for (int k = 0; k < lengthSequences; k++) {
          sumOfPairs += distanceMatrix.getDistance(decodedSequences[i][k], decodedSequences[j][k]);
        }
      }
    }

    return sumOfPairs;
  }

  @Override
  public boolean isAMinimizationScore() {
    return false;
  }

  @Override
  public String getName() {
    return "SOP";
  }

  @Override
  public String getDescription() {
    return "Sum of pairs";
  }
}
