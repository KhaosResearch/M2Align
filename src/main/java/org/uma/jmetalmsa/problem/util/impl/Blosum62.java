package org.uma.jmetalmsa.problem.util.impl;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetalmsa.problem.util.DistanceMatrix;

import java.util.HashMap;
import java.util.Map;

/**
 * Blosum62 substitution matrix
 *
 *  BLOSUM Clustered Scoring Matrix in 1/2 Bit Units
 #  Blocks Database = /data/blocks_5.0/blocks.dat
 #  Cluster Percentage: >= 62
 #  Entropy =   0.6979, Expected =  -0.5209
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class Blosum62 implements DistanceMatrix {
  private static final int DEFAULT_GAP_PENALTY = -8 ;
  private int g;// GAP PENALTY

  public int[][] Blosum62 = new int[][] {
        //A   R   N   D   C   Q   E   G   H   I   L   K   M   F   P   S   T   W   Y   V   B   Z   X  *
        //0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20  21  22 23
/* A */ { 4, -1, -2, -2,  0, -1, -1,  0, -2, -1, -1, -1, -1, -2, -1,  1,  0, -3, -2,  0, -2, -1,  0, g},
/* R */ {-1,  5,  0, -2, -3,  1,  0, -2,  0, -3, -2,  2, -1, -3, -2, -1, -1, -3, -2, -3, -1,  0 ,-1, g},
/* N */ {-2,  0,  6,  1, -3,  0,  0,  0,  1, -3, -3,  0, -2, -3, -2,  1,  0, -4, -2, -3,  3,  0, -1, g},
/* D */ {-2, -2,  1,  6, -3,  0,  2, -1, -1, -3, -4, -1, -3, -3, -1,  0, -1, -4, -3, -3,  4,  1, -1, g},
/* C */ { 0, -3, -3, -3,  9, -3, -4, -3, -3, -1, -1, -3, -1, -2, -3, -1, -1, -2, -2, -1, -3, -3, -2, g},
/* Q */ {-1,  1,  0,  0, -3,  5,  2, -2,  0, -3, -2,  1,  0, -3, -1,  0, -1, -2, -1, -2,  0,  3, -1, g},
/* E */ {-1,  0,  0,  2, -4,  2,  5, -2,  0, -3, -3,  1, -2, -3, -1,  0, -1, -3, -2, -2,  1,  4, -1, g},
/* G */ { 0, -2,  0, -1, -3, -2, -2,  6, -2, -4, -4, -2, -3, -3, -2,  0, -2, -2, -3, -3, -1, -2, -1, g},
/* H */ {-2,  0,  1, -1, -3,  0,  0, -2,  8, -3, -3, -1, -2, -1, -2, -1, -2, -2,  2, -3,  0,  0, -1, g},
/* I */ {-1, -3, -3, -3, -1, -3, -3, -4, -3,  4,  2, -3,  1,  0, -3, -2, -1, -3, -1,  3, -3, -3, -1, g},
/* L */ {-1, -2, -3, -4, -1, -2, -3, -4, -3,  2,  4, -2,  2,  0, -3, -2, -1, -2, -1,  1, -4, -3, -1, g},
/* K */ {-1,  2,  0, -1, -3,  1,  1, -2, -1, -3, -2,  5, -1, -3, -1,  0, -1, -3, -2, -2,  0,  1, -1, g},
/* M */ {-1, -1, -2, -3, -1,  0, -2, -3, -2,  1,  2, -1,  5,  0, -2, -1, -1, -1, -1,  1, -3, -1, -1, g},
/* F */ {-2, -3, -3, -3, -2, -3, -3, -3, -1,  0,  0, -3,  0,  6, -4, -2, -2,  1,  3, -1, -3, -3, -1, g},
/* P */ {-1, -2, -2, -1, -3, -1, -1, -2, -2, -3, -3, -1, -2, -4,  7, -1, -1, -4, -3, -2, -2, -1, -2, g},
/* S */ { 1, -1,  1,  0, -1,  0,  0,  0, -1, -2, -2,  0, -1, -2, -1,  4,  1, -3, -2, -2,  0,  0,  0, g},
/* T */ { 0, -1,  0, -1, -1, -1, -1, -2, -2, -1, -1, -1, -1, -2, -1,  1,  5, -2, -2,  0, -1, -1,  0, g},
/* W */ {-3, -3, -4, -4, -2, -2, -3, -2, -2, -3, -2, -3, -1,  1, -4, -3, -2, 11,  2, -3, -4, -3, -2, g},
/* Y */ {-2, -2, -2, -3, -2, -1, -2, -3,  2, -1, -1, -2, -1,  3, -3, -2, -2,  2,  7, -1, -3, -2, -1, g},
/* V */ { 0, -3, -3, -3, -1, -2, -2, -3, -3,  3,  1, -2,  1, -1, -2, -2,  0, -3, -1,  4, -3, -2, -1, g},
/* B */ {-2, -1,  4,  4, -3,  0,  1, -1,  0, -3, -4,  0, -3, -3, -2,  0, -1, -4, -3, -3,  4,  1, -1, g},
/* Z */ {-1,  0,  0,  1, -3,  4,  4, -2,  0, -3, -3,  1, -1, -3, -1,  0, -1, -3, -2, -2,  1,  4, -1, g},
/* X */ { 0, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2,  0,  0, -2, -1, -1, -1, -1, -1, g},
/* - */ { g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g,  g, 1}
  };

  public static Map<Character, Integer> map = new HashMap<>() ;

  public Blosum62(int gapPenalty) {
    g = gapPenalty ;
    for (int i = 0; i < (Blosum62.length - 1); i++) {
      Blosum62[i][23] = g ;
    }

    for (int i = 0; i < (Blosum62.length - 1); i++) {
      Blosum62[23][i] = g ;
    }

    map.put('A', 0) ;
    map.put('R', 1) ;
    map.put('N', 2) ;
    map.put('D', 3) ;
    map.put('C', 4) ;
    map.put('Q', 5) ;
    map.put('E', 6) ;
    map.put('G', 7) ;
    map.put('H', 8) ;
    map.put('I', 9) ;
    map.put('L', 10) ;
    map.put('K', 11) ;
    map.put('M', 12) ;
    map.put('F', 13) ;
    map.put('P', 14) ;
    map.put('S', 15) ;
    map.put('T', 16) ;
    map.put('W', 17) ;
    map.put('Y', 18) ;
    map.put('V', 19) ;
    map.put('B', 20) ;
    map.put('Z', 21) ;
    map.put('X', 22) ;
    map.put('-', 23) ;
  }

  public Blosum62() {
    this(DEFAULT_GAP_PENALTY) ;
  }

   private int get(char c) {
    switch (c) {
      case 'A': return 0 ;
      case 'R': return 1 ;
      case 'N': return 2 ;
      case 'D': return 3 ;
      case 'C': return 4 ;
      case 'Q': return 5 ;
      case 'E': return 6 ;
      case 'G': return 7 ;
      case 'H': return 8 ;
      case 'I': return 9 ;
      case 'L': return 10;
      case 'K': return 11;
      case 'M': return 12;
      case 'F': return 13;
      case 'P': return 14;
      case 'S': return 15;
      case 'T': return 16;
      case 'W': return 17;
      case 'Y': return 18;
      case 'V': return 19;
      case 'B': return 20;
      case 'Z': return 21;
      case 'X': return 22;
      case '-': return 23;
      default: throw new JMetalException("Invalid char: " + c) ;
    }
  }

  @Override
  public int getDistance(char a1, char a2) {
    return Blosum62[get(a1)][get(a2)] ;
  }

  @Override
  public int getGapPenalty() {
    return g;
  }

  @Override
  public String toString() {
    String result = "    " ;
    for (int i = 0 ; i < Blosum62.length; i++) {
      if (i < 10) {
        result += "   " + i;
      } else {
        result += "  " + i;
      }
    }
    result += "\n    " ;
    for (int i = 0 ; i < Blosum62.length; i++) {
      result += "----";

    }
    result += "\n" ;

    for (int i = 0 ; i < Blosum62.length; i++) {
      if (i > 9) {
        result += "" + i + " | ";
      } else {
        result += " " + i + " | ";
      }
      for (int j = 0 ; j < Blosum62.length; j++) {
        int value = Blosum62[i][j] ;
        if ((value < 0) || (value > 9)) {
          result += " " + Blosum62[i][j] + " ";
        } else {
          result += "  " + Blosum62[i][j] + " ";
        }
      }
      result += "\n" ;
    }

    return result ;
  }
}
