package org.uma.jmetalmsa.solution.util;

import org.uma.jmetal.util.JMetalException;

import java.util.Arrays;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ArrayChar {
  private char[] array;

  public ArrayChar() {
    array = new char[0];
  }

  public ArrayChar(String string) {
    array = new char[string.length()];
    string.getChars(0, string.length(), array, 0);
  }

  public ArrayChar(char[] array) {
    this.array = new char[array.length];
    System.arraycopy(array, 0, this.array, 0, array.length);
  }

  public char[] getCharArray() {
    return array;
  }

  public void setCharArray(char[] array) {
    this.array = array;
  }

  public void append(String str) {
    int len = str.length();
    int size = array.length;
    expand(array.length + len);
    str.getChars(0, len, array, size);
  }

  public void append(char[] str) {
    int len = str.length;
    int size = array.length;
    expand(size + len);
    System.arraycopy(str, 0, array, size, len);
  }
  public void append(char symbol) {
    expand(array.length + 1);
    array[array.length - 1] = symbol;

  }

  public char charAt(int index) {
    return array[index];
  }

  public void setCharAt(int index, char c) {
    if (index >= array.length || index < 0) {
      throw new JMetalException("Index value (" + index + ") greater than size (" + array.length + ") or lower than 0");
    }
    array[index] = c;
  }

  public void insert(int index, char symbol) {

    if (index < 0 || index > array.length) {
      throw new JMetalException("Insert index (" + index + ") is incorrect");
    }
    int size = array.length;
    expand(array.length + 1);
    System.arraycopy(array, index, array, index + 1, size - index);
    array[index] = symbol;

  }

  //Insert l c's characters
  public void insert(int index, int l, char c) {

    char[] chars = new char[l];
    for (int i = 0; i < l; i++)
      chars[i] = c;

    insert(index, chars);
  }

  public void insert(int index, char[] chars) {

    if (index < 0 || index > array.length) {
      throw new JMetalException("Insert index (" + index + ") is incorrect");
    }

    int len1 = array.length;
    int len = chars.length;
    expand(array.length + len);
    System.arraycopy(array, index, array, index + len, len1 - index);
    System.arraycopy(chars, 0, array, index, len);
  }

  public void delete(int index) {
    if (index >= array.length) {
      throw new JMetalException("Index value (" + index + ") greater or equal than size (" + array.length + ")");
    }

    char[] array2 = new char[array.length - 1];
    if (index > 0)
      System.arraycopy(array, 0, array2, 0, index);
    if (index < array.length - 1)
      System.arraycopy(array, index + 1, array2, index, array.length - index - 1);

    array = array2;
    array2 = null;
  }

  public void move(int index1, int index2, int newpos) {

    int len = index2 - index1 + 1;
    char[] array2 = new char[len];
    System.arraycopy(array, index1, array2, 0, len);

    if (newpos > index2) {

      System.arraycopy(array, index2 + 1, array, index1, newpos - index2 - 1);

      System.arraycopy(array2, 0, array, index1 + (newpos - index2 - 1), len);

    } else {

      System.arraycopy(array, newpos, array, newpos + len, index1 - newpos);
      System.arraycopy(array2, 0, array, newpos, len);

    }
    array2 = null;
  }

  public String substring(int position, int length) {
    if ((position + length) > array.length) {
      throw new JMetalException("The position (" + position + ") + length (" + length + ") is " +
              "greater than the array size (" + array.length + ")");
    }

    char[] result = new char[length];

    System.arraycopy(array, position, result, 0, length);

    return new String(result);
  }

  public char[] substringChar(int position, int length) {
    if ((position + length) > array.length) {
      throw new JMetalException("The position (" + position + ") + length (" + length + ") is " +
              "greater than the array size (" + array.length + ")");
    }

    char[] result = new char[length];

    System.arraycopy(array, position, result, 0, length);

    return result;
  }

  public int getSize() {
    return array.length;
  }

  @Override
  public String toString() {
    return new String(array);
  }

  public char[] copyOf(char[] original, int newLength) {
    char[] copy = new char[newLength];
    System.arraycopy(original, 0, copy, 0,
            Math.min(original.length, newLength));
    return copy;
  }

  public void expand(int newCapacity) {
    array = Arrays.copyOf(array, newCapacity);
  }
}
