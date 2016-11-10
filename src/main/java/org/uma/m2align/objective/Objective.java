package org.uma.m2align.objective;

import org.uma.jmetal.util.naming.DescribedEntity;
import org.uma.m2align.solution.MSASolution;

/**
 * Created by ajnebro on 14/6/16.
 */
public interface Objective extends DescribedEntity {
  <S extends MSASolution> double compute(S solution, char[][] decodedSequences);

  boolean isAMinimizationObjective();
}
