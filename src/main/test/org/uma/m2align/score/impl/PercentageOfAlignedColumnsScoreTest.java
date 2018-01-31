package org.uma.m2align.score.impl;

import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import org.uma.m2align.solution.MSASolution;

import static org.junit.Assert.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PercentageOfAlignedColumnsScoreTest {
  private static final double EPSILON = 0.00000000001 ;

  private PercentageOfAlignedColumnsScore objective ;

  @Mocked
  MSASolution solution ;

  @Before
  public void startup() {
    objective = new PercentageOfAlignedColumnsScore() ;
  }

  @Test
  public void shouldComputeReturnTheCorrectValueWhenTheSequenceOnlyHasOneAlignedColumn() {
    new Expectations() {{
      char[][]decodedSequence = new char[][]{
              {'-', 'B', 'C', '-', 'D'},
              {'-', 'B', 'E', 'C', '-'},
              {'-', '-', 'L', 'Ñ', '-'},
              {'-', 'T', 'C', 'D', 'G'},
      };
      solution.decodeToMatrix() ;
      result = decodedSequence;
      times = 1;
    }};

    assertEquals(100.0 * 1.0/5.0, objective.compute(solution, solution.decodeToMatrix()), EPSILON) ;
  }

  @Test
  public void shouldComputeReturnTheCorrectValueWhenTheSequenceHasTwoAlignedColumns() {
    new Expectations() {
      {
        char[][] decodedSequence = new char[][]{
                {'-', 'B', 'C', '-', 'D', '-'},
                {'-', 'B', 'E', 'C', '-', '-'},
                {'-', '-', 'L', 'N', '-', '-'},
                {'-', 'T', 'C', 'D', 'G', '-'},
                {'-', 'B', 'C', 'D', 'G', '-'},
        } ;
        solution.decodeToMatrix() ;
        result = decodedSequence;
        times = 1;
      }};

    assertEquals(100.0 * 2.0/6.0, objective.compute(solution, solution.decodeToMatrix()), EPSILON) ;
  }
}