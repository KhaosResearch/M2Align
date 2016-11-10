package org.uma.m2align.objective.impl;

import org.uma.m2align.objective.Objective;
import org.uma.m2align.util.distancematrix.DistanceMatrix;
import org.uma.m2align.solution.MSASolution;

public class SumOfPairsObjective implements Objective {
  private DistanceMatrix distanceMatrix;

  public SumOfPairsObjective(DistanceMatrix distanceMatrix) {
    this.distanceMatrix = distanceMatrix;
  }

  @Override
  public <S extends MSASolution> double compute(S solution,char [][]decodedSequences ) {
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
  public boolean isAMinimizationObjective() {
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
