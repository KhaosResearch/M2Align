package org.uma.jmetalmsa.problem.util;

import java.io.Serializable;

/**
 * Created by ajnebro on 4/6/15.
 */
public interface DistanceMatrix extends Serializable{
  public int getDistance(char char1, char char2) ;
  public int getGapPenalty() ;
}
