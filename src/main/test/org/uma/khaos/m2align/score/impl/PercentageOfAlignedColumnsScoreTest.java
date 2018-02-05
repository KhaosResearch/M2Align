package org.uma.khaos.m2align.score.impl;

import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import org.uma.khaos.m2align.solution.MSASolution;

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
  public void shouldComputeReturnTheCorrectValueWhenTheSequenceHasTwoAlignedColumnsCaseA() {
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

  @Test
  public void shouldComputeReturnTheCorrectValueWhenTheSequenceHasTwoAlignedColumnsCaseB() {
    new Expectations() {
      {
        char[][] decodedSequence = new char[][]{
            {'G', '-','-','-','-','-','-','E','R', 'S', 'L', 'A', 'A','-','-', 'T', 'L','V','-'},
            {'N', 'A','I','L','A','H','-','E', 'R', '-', '-', '-','-','-', '-','-', 'L', 'S','I'},
            {'N', 'G','Y','L','F','I','-','E', '-', '-', '-', 'Q','-','-', '-', '-','L','-', 'N'},
            {'G', 'L','V','S','D','V','F','E', 'A', 'R', 'H', '-','-','M', 'Q','R','L','-', '-'},
        } ;
        solution.decodeToMatrix() ;
        result = decodedSequence;
        times = 1;
      }};

    assertEquals(100.0 * 2.0/19.0, objective.compute(solution, solution.decodeToMatrix()), EPSILON) ;
  }
}