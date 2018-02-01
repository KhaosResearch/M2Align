package org.uma.khaos.m2align.util;

import org.uma.khaos.m2align.solution.MSASolution;

public class MSADistance {

  public static final int LevenshteinDistance = 1;
  public static final int HammingDistance = 2;
  public static final int MetAlDistance = 3;

  public int DistanceMetric;

  public MSADistance() {
    DistanceMetric = LevenshteinDistance;
  }

  public MSADistance(int _DistanceMetric) {
    DistanceMetric = _DistanceMetric;
  }


  public double getLevenshteinDistance(char[] s, char[] t) {
    int n = s.length; // length of s
    int m = t.length; // length of t

    if (n == 0) {
      return m;
    } else if (m == 0) {
      return n;
    }

    if (n > m) {
      char[] tmp = s;
      s = t;
      t = tmp;
      n = m;
      m = t.length;
      tmp = null;
    }

    int p[] = new int[n + 1];
    int d[] = new int[n + 1];
    int _d[];

    int i;
    int j;

    char t_j; // jth character of t

    int cost; // cost

    for (i = 0; i <= n; i++) {
      p[i] = i;
    }

    for (j = 1; j <= m; j++) {
      t_j = t[j - 1];
      d[0] = j;

      for (i = 1; i <= n; i++) {
        cost = s[i - 1] == t_j ? 0 : 1;
        d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
      }

      _d = p;
      p = d;
      d = _d;
    }

    // our last action in the above loop was to switch d and p, so p now
    // actually has the most recent cost counts
    return p[n];
  }

  private double getHammingDistance(char[] A, char[] B, int MaxLen, int MinLen) {

    double distance = 0;
    for (int j = 0; j < MinLen; j++) {
      if (A[j] != B[j]) {
        distance++;
      }
    }
    distance += MaxLen - MinLen;
    return distance;
  }
  
  
   public double getLevenshteinDistance(MSASolution solA, MSASolution solB) {
        double distance=0.0;
        
        char [][]decodedSequencesA = solA.decodeToMatrix();
        char [][]decodedSequencesB = solB.decodeToMatrix();
                
        for (int j = 0; j < solA.getNumberOfVariables(); ++j) {
            distance+= getLevenshteinDistance(decodedSequencesA[j],decodedSequencesB[j]);
      }
       
       return distance;
   }
}
 