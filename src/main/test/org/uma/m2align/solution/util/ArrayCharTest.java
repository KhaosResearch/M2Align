package org.uma.m2align.solution.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.uma.jmetal.util.JMetalException;

import mockit.Deencapsulation;


/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ArrayCharTest {

  @Test
  public void shouldConstructorWithStringParameterCreateTheRightObject() {
    String string = "hello" ;
    ArrayChar arrayChar = new ArrayChar(string) ;

    assertArrayEquals(string.toCharArray(), Deencapsulation.getField(arrayChar, "array")) ;
    assertEquals(string.length(), ((char[]) Deencapsulation.getField(arrayChar, "array")).length);
  }

  @Test
  public void shouldConstructorWithArrayOfCharParameterCreateTheRightObject() {
    String string = "hello" ;
    ArrayChar arrayChar = new ArrayChar(string.toCharArray()) ;

    assertArrayEquals(string.toCharArray(), Deencapsulation.getField(arrayChar, "array")) ;
    assertEquals(string.length(), ((char[]) Deencapsulation.getField(arrayChar, "array")).length);
  }

  @Test
  public void shouldGetCharArrayReturnTheRightArray() {
    String string = "hello" ;
    ArrayChar arrayChar = new ArrayChar() ;
    Deencapsulation.setField(arrayChar, "array", string.toCharArray());

    assertArrayEquals(string.toCharArray(), arrayChar.getCharArray());
  }

  @Test (expected = JMetalException.class)
  public void shouldSetCharAtReturnAnExceptionIfTheIndexIsNegative() {
    ArrayChar arrayChar = new ArrayChar("hello") ;

    arrayChar.setCharAt(-1, 'c');
  }

  @Test (expected = JMetalException.class)
  public void shouldSetCharAtReturnAnExceptionIfTheIndexIsLargerThanTheArrayLength() {
    ArrayChar arrayChar = new ArrayChar("hello") ;

    arrayChar.setCharAt(6, 'c');
  }

  @Test
  public void shouldSetCharAtAssignTheRightValue() {
    ArrayChar arrayChar = new ArrayChar("hello") ;

    arrayChar.setCharAt(0, 'v');
    arrayChar.setCharAt(4, 'z');
    assertEquals('v', arrayChar.getCharArray()[0]);
    assertEquals('z', arrayChar.getCharArray()[4]);
  }
}