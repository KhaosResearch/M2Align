package org.uma.khaos.m2align.score.impl;

import java.util.List;
import org.uma.khaos.m2align.score.Score;
import org.uma.khaos.m2align.solution.MSASolution;
import org.uma.khaos.m2align.strike.Strike;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StrikeScore implements Score {
  private Strike strike ;
  private List<StringBuilder> listOfSequenceNames ;

   public void initializeParameters(String pdbPath, List<StringBuilder> listOfSequenceNames) {
    strike = new Strike(pdbPath, listOfSequenceNames) ;
    this.listOfSequenceNames = listOfSequenceNames ;
  }

  @Override
  public <S extends MSASolution> double compute(S solution, char [][]decodedSequences) {
    return strike.compute(decodedSequences, listOfSequenceNames, false) ;
  }

  @Override
  public boolean isAMinimizationScore() {
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
