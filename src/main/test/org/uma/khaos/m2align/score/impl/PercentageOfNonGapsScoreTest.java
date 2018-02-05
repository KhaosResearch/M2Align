package org.uma.khaos.m2align.score.impl;

import static org.junit.Assert.assertEquals;

import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;
import org.uma.khaos.m2align.solution.MSASolution;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PercentageOfNonGapsScoreTest {
  private static final double EPSILON = 0.00000000001;
  private PercentageOfNonGapsScore objective;

  @Mocked
  MSASolution solution;

  @Before
  public void startup() {
    objective = new PercentageOfNonGapsScore();
  }

  @Test
  public void shouldComputeReturnOneHundredIfTheSequenceHasNoGaps() {
    /* Assumed sequences:
          char[][]decodedSequence = new char[][]{
              {'B', 'C', 'D'},
              {'B', 'E', 'C'},
              {'L', 'L', 'L'},
              {'T', 'C', 'D'},
      };
     */
    new Expectations() {{
      solution.getNumberOfGaps();
      result = 0;
      times = 1;

      solution.getNumberOfVariables();
      result = 4;
      times = 1;

      solution.getAlignmentLength();
      result = 3;
      times = 1;
    }};

    assertEquals(100.0, objective.compute(solution, solution.decodeToMatrix()), EPSILON);
  }

  @Test
  public void shouldComputeReturnTheCorrectValueIfTheSequenceHasTwoGaps() {
    /* Assumed sequences:
          char[][]decodedSequence = new char[][]{
              {'B', 'C', 'D', '-'},
              {'B', '-', 'C', 'D'},
              {'L', 'L', 'L', 'G'},
              {'T', 'C', 'D', 'B'},
      };
     */
    new Expectations() {{
      solution.getNumberOfGaps();
      result = 2;
      times = 1;

      solution.getNumberOfVariables();
      result = 4;
      times = 1;

      solution.getAlignmentLength();
      result = 4;
      times = 1;
    }};

    assertEquals(100.0 * 14.0/(4*4), objective.compute(solution, solution.decodeToMatrix()), EPSILON);
  }

  @Test
  public void shouldComputeReturnTheCorrectValueIfTheSequenceHasManyGaps() {
    /* Assumed sequences:
        B----CD-
        B-CD----
        LL----LG
        --TC--DB
        -BC-B-HE
     */

    new Expectations() {{
      solution.getNumberOfGaps();
      result = 21;
      times = 1;

      solution.getNumberOfVariables();
      result = 5;
      times = 1;

      solution.getAlignmentLength();
      result = 8;
      times = 1;
    }};

    assertEquals(100.0 * (5*8-21)/(5*8), objective.compute(solution, solution.decodeToMatrix()), EPSILON) ;
  }

  @Test
  public void shouldComputeReturnTheCorrectValueWhenTheSequenceHasTwoAlignedColumns() {
    /* Assumed sequences:
            {'G', '-','-','-','-','-','-','E','R', 'S', 'L', 'A', 'A','-','-', 'T', 'L','V','-'},
            {'N', 'A','I','L','A','H','-','E', 'R', '-', '-', '-','-','-', '-','-', 'L', 'S','I'},
            {'N', 'G','Y','L','F','I','-','E', '-', '-', '-', 'Q','-','-', '-', '-','L','-', 'N'},
            {'G', 'L','V','S','D','V','F','E', 'A', 'R', 'H', '-','-','M', 'Q','R','L','-', '-'},
     */

    new Expectations() {{
      solution.getNumberOfGaps();
      result = 28;
      times = 1;

      solution.getNumberOfVariables();
      result = 4;
      times = 1;

      solution.getAlignmentLength();
      result = 19;
      times = 1;
    }};

    assertEquals(100.0 * (4*19-28)/(4*19), objective.compute(solution, solution.decodeToMatrix()), EPSILON) ;
  }
}