package org.uma.m2align.score.impl;

import org.uma.m2align.score.Score;
import org.uma.m2align.solution.MSASolution;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PercentageOfAlignedColumnsScore implements Score {
  @Override
  public <S extends MSASolution> double compute(S solution,char [][]decodedSequences) {
    int numberOfAlignedColumns = 0;
    int numberOfColumns = decodedSequences[0].length;
    char firstResidue;
    int j;
    Boolean isAligned;
    for (int i = 0; i < numberOfColumns; i++) {
        isAligned = true;   
        firstResidue = decodedSequences[0][i];
        if (!solution.isGap(firstResidue)) {
                for(j=1;j< decodedSequences.length;j++){
                    if (firstResidue != decodedSequences[j][i]) {
                        isAligned = false;
                        break;
                    }
                }
        }else isAligned = false;
        
        if (isAligned) numberOfAlignedColumns++;
      }

    double result = 100.0 * numberOfAlignedColumns / numberOfColumns;
    return result;
  }

  @Override
  public boolean isAMinimizationScore() {
    return false;
  }

  @Override
  public String getName() {
    return "TC";
  }

  @Override
  public String getDescription() {
    return "Percentage of aligned columns in a multiple sequence";
  }
}
