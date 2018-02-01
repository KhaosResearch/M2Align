package org.uma.khaos.m2align.util.distancematrix.impl;

import org.junit.Test;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PAM250Test {
  private PAM250 matrix ;

  @Test
  public void shouldDefaultGapPenaltyValueBe8() {
    matrix = new PAM250() ;
    assertEquals(-8, matrix.getGapPenalty());
  }

  @Test
  public void shouldConstructorWithPenaltyValueModifyTheGapPenalty() {
    matrix = new PAM250(-4) ;

    assertEquals(-4, matrix.getGapPenalty());
  }

  @Test
  public void shouldGetDistanceReturnTheGapPenaltyIfACharIsAGap() {
    matrix = new PAM250() ;

    assertEquals(1, matrix.getDistance('-', '-'));
    assertEquals(matrix.getGapPenalty(), matrix.getDistance('A', '-'));
    assertEquals(matrix.getGapPenalty(), matrix.getDistance('-', 'A'));
  }

  @Test
  public void shouldGetDistanceReturnOneIfTheeCharsAreAGap() {
    matrix = new PAM250() ;

    assertEquals(1, matrix.getDistance('-', '-'));
  }

  @Test
  public void shouldGetDistanceReturnTheCorrectValueIfTheCharsAreNotGaps() {
    matrix = new PAM250() ;

    assertEquals(-2, matrix.getDistance('A', 'R'));
    assertEquals(-3, matrix.getDistance('N', 'F'));
    assertEquals(-3, matrix.getDistance('X', 'C'));
    assertEquals(+5, matrix.getDistance('I', 'I'));
    assertEquals(+4, matrix.getDistance('V', 'V'));
  }

  @Test (expected = JMetalException.class)
  public void shouldGetDistanceThrowAnExceptionIfACharIsInvalid() {
    matrix = new PAM250() ;

    matrix.getDistance('J', 'A') ;
  }
}
