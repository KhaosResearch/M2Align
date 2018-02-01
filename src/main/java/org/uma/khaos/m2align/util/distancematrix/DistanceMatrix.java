package org.uma.khaos.m2align.util.distancematrix;

import java.io.Serializable;

/**
 * Created by ajnebro on 4/6/15.
 */
public interface DistanceMatrix extends Serializable{
  public int getDistance(char char1, char char2) ;
  public int getGapPenalty() ;
}
