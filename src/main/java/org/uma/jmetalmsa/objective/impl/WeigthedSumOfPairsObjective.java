package org.uma.jmetalmsa.objective.impl;

import org.uma.jmetalmsa.objective.Objective;
import org.uma.jmetalmsa.problem.DynamicallyComposedProblem;
import org.uma.jmetalmsa.problem.util.DistanceMatrix;
import org.uma.jmetalmsa.solution.MSASolution;
import org.uma.jmetalmsa.solution.util.ArrayChar;
import org.uma.jmetalmsa.util.MSADistance;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class WeigthedSumOfPairsObjective implements Objective {
  public DistanceMatrix sustitutionMatrix;
  public MSADistance distanceMatrix;
  public double[][] weightMatrix;
  public double weightGapOpen, weightGapExtend;
  double wSOP = Double.MIN_VALUE;

  public WeigthedSumOfPairsObjective(DistanceMatrix sustitutionMatrix, double _weightGapOpen, double _weightGapExtend) {

    this.sustitutionMatrix = sustitutionMatrix;
    distanceMatrix = new MSADistance();
    weightGapOpen = _weightGapOpen;
    weightGapExtend = _weightGapExtend;

  }

  public void initializeWeightMatrix(List<ArrayChar> originalSequences) {

    weightMatrix = getWMatrix(originalSequences);

  }

  @Override
  public <S extends MSASolution> double compute(S solution, char[][] decodedSequences, DynamicallyComposedProblem<S> decomposedProblem) {
    int lengthSequences = solution.getAlignmentLength();
    int NumVars = solution.getNumberOfVariables();

    double SumOfPairs = 0;
    double SumOfPairsOfLthColumn;
    int i, j;

    for (int l = 0; l < lengthSequences; l++) {
      SumOfPairsOfLthColumn = 0;

      for (i = 0; i < NumVars - 1; i++) {
        for (j = i + 1; j < NumVars; j++)
          SumOfPairsOfLthColumn += weightMatrix[i][j] * sustitutionMatrix.getDistance(decodedSequences[i][l], decodedSequences[j][l]);
      }

      SumOfPairs += SumOfPairsOfLthColumn;
    }

    double AffineGapPenaltyScore = 0;
    for (i = 0; i < NumVars; i++) {
      AffineGapPenaltyScore += getAffineGapPenalty(solution.getVariableValue(i));
    }

    return SumOfPairs - AffineGapPenaltyScore;

  }

  public double getAffineGapPenalty(List<Integer> gapsGroups) {
    int weightToOpenTheGap = 0, weightToExtendTheGap = 0;

    weightToOpenTheGap = gapsGroups.size() / 2;

    for (int i = 0; i < gapsGroups.size(); i += 2) {
      weightToExtendTheGap += gapsGroups.get(i + 1) - gapsGroups.get(i);
    }

    return (weightGapOpen * weightToOpenTheGap) + (weightGapExtend * weightToExtendTheGap);
  }

  public double[][] getWMatrix(List<ArrayChar> Seqs) {

    int NumSeqs = Seqs.size();
    char[] Si;
    char[] Sj;

    double[][] wMatrix = new double[NumSeqs - 1][];

    for (int i = 0; i < NumSeqs - 1; ++i) {
      wMatrix[i] = new double[NumSeqs];
      for (int j = i + 1; j < NumSeqs; ++j) {
        Si = Seqs.get(i).getCharArray();
        Sj = Seqs.get(j).getCharArray();

        wMatrix[i][j] = 1 - (distanceMatrix.getLevenshteinDistance(Si, Sj) / (Si.length > Sj.length ? Si.length : Sj.length));

      }
    }

    return wMatrix;
  }

  public void printWMatrix(double[][] W) {

    for (int i = 0; i < W.length; ++i) {
      for (int j = 0; j <= W.length; ++j) {
        System.out.printf("%.4f\t", W[i][j]);
      }
      System.out.println();
    }

  }


  @Override
  public boolean isAMinimizationObjective() {
    return false;
  }

  @Override
  public String getName() {
    return "wSOP";
  }

  @Override
  public String getDescription() {
    return "Weighted sum of pairs";
  }
}
