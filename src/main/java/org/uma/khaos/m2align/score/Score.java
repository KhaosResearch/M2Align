package org.uma.khaos.m2align.score;

import org.uma.jmetal.util.naming.DescribedEntity;
import org.uma.khaos.m2align.solution.MSASolution;

/**
 * Created by ajnebro on 14/6/16.
 */
public interface Score extends DescribedEntity {
  <S extends MSASolution> double compute(S solution, char [][]decodedSequences);

  boolean isAMinimizationScore();
}
