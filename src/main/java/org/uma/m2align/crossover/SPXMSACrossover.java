package org.uma.m2align.crossover;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.m2align.solution.MSASolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Antonio J. Nebro
 * @version 1.0 Implements a single point crossover for MSA representation
 */
public class SPXMSACrossover implements CrossoverOperator<MSASolution> {
  private JMetalRandom randomGenerator;
  private double probability;

  public SPXMSACrossover(double probability) {
    if ((probability < 0) || (probability > 1)) {
      throw new JMetalException("Crossover probability value invalid: " + probability);
    }

    this.randomGenerator = JMetalRandom.getInstance();
    this.probability = probability;
  }

  @Override
  /**
   * Checks conditions and return the result of performing single point crossover
   * @param parents
   * @return
   */
  public List<MSASolution> execute(List<MSASolution> parents) {
    if (parents == null) {
      throw new JMetalException(this.getClass().getName() + "\tNull pointer exception");
    } else if (parents.size() < 2) {
      throw new JMetalException(
              this.getClass().getName() + "\tNot enough parents for crossover:\t" + parents.size());
    } else if (parents.size() > 2) {
      throw new JMetalException(
              this.getClass().getName() + "\tToo many parents for crossover:\t" + parents.size());
    } else if (parents.get(0).getNumberOfVariables() != parents.get(1).getNumberOfVariables()) {
      throw new JMetalException(this.getClass().getName() + "\tParents of different length\t");
    }

    return doCrossover(parents);
  }

  /**
   * Performs a single point crossover of two parents. Uses the same cutting point for all sequences
   *
   * @return a list containing the generated offspring
   */

  private List<MSASolution> doCrossover(List<MSASolution> parents) {

    MSASolution parent1 = parents.get(0);
    MSASolution parent2 = parents.get(1);

    List<MSASolution> children = new ArrayList<>();

    children.add(MSACrossover(parent1, parent2));
    children.add(MSACrossover(parent2, parent1));

    return children;
  }

  private MSASolution MSACrossover(MSASolution parentA, MSASolution parentB) {

    MSASolution child;

    if (this.randomGenerator.nextDouble() < this.probability) {

      int cut = selectRandomColumn(parentA);

      List<List<Integer>> gapsGroupFirstBloq = new ArrayList<List<Integer>>();
      List<Integer> carsCounterParentA = new ArrayList<Integer>();
      List<Integer> gapsGroup;
      int numgaps;


      for (int i = 0; i < parentA.getNumberOfVariables(); i++) {
        gapsGroup = parentA.getVariableValue(i);

        List<Integer> gaps = new ArrayList<Integer>();
        numgaps = 0;
        for (int j = 1; j < gapsGroup.size(); j += 2) {
          if (cut >= gapsGroup.get(j)) {
            gaps.add(gapsGroup.get(j - 1));
            gaps.add(gapsGroup.get(j));
            numgaps += gapsGroup.get(j) - gapsGroup.get(j - 1) + 1;
          } else {
            if (cut >= gapsGroup.get(j - 1)) {
              gaps.add(gapsGroup.get(j - 1));
              gaps.add(cut);
              numgaps += cut - gapsGroup.get(j - 1) + 1;
            }
            break;
          }
        }
        gapsGroupFirstBloq.add(gaps);
        carsCounterParentA.add(cut - numgaps + 1);
      }

      int carsCountParentB;
      List<Integer> positionsToCutParentB = new ArrayList<Integer>();

      for (int i = 0; i < parentB.getNumberOfVariables(); i++) {
        gapsGroup = parentB.getVariableValue(i);

        if (gapsGroup.size() > 0) {
          carsCountParentB = 0;
          for (int j = 0; j < gapsGroup.size(); j += 2) {
            if (j > 0)
              carsCountParentB += gapsGroup.get(j) - gapsGroup.get(j - 1) - 1;
            else
              carsCountParentB += gapsGroup.get(j);

            if (carsCountParentB >= carsCounterParentA.get(i)) {
              positionsToCutParentB.add(gapsGroup.get(j) - (carsCountParentB - carsCounterParentA.get(i)));
              break;
            }
          }

          if (carsCountParentB < carsCounterParentA.get(i)) {
            if (gapsGroup.size() > 0) {
              carsCountParentB = gapsGroup.get(gapsGroup.size() - 1) + (carsCounterParentA.get(i) - carsCountParentB) + 1;
              //if(carsCountParentB >= parent2.sizeAligment ) carsCountParentB=parent2.sizeAligment-1;
              positionsToCutParentB.add(carsCountParentB);
            }
          }
        } else {  //SeqB has not Gaps
          positionsToCutParentB.add(carsCounterParentA.get(i));
        }
      }

      Integer MinPos = Collections.min(positionsToCutParentB);
      int pos;
      List<Integer> gaps;
      int lastGap, posA;
      for (int i = 0; i < parentB.getNumberOfVariables(); i++) {
        posA = cut;
        pos = positionsToCutParentB.get(i);
        gaps = gapsGroupFirstBloq.get(i);
        if (pos > MinPos) {
          if (gaps.size() > 0) {
            lastGap = gaps.get(gaps.size() - 1);
            if (lastGap != posA) {
              gaps.add(posA + 1);
              gaps.add(posA + (pos - MinPos));
            } else {
              gaps.set(gaps.size() - 1, posA + (pos - MinPos));
            }
          } else {
            gaps.add(posA + 1);
            gaps.add(posA + (pos - MinPos));
          }

          posA += (pos - MinPos);
        }

        gapsGroup = parentB.getVariableValue(i);
        for (int j = 0; j < gapsGroup.size(); j += 2) {

          if (gapsGroup.get(j) >= pos) {
            gaps.add(posA + (gapsGroup.get(j) - pos) + 1);
            gaps.add(posA + (gapsGroup.get(j + 1) - pos) + 1);
          }
        }
      }

      child = new MSASolution(parentA.getMSAProblem(), gapsGroupFirstBloq);

      child.mergeGapsGroups();

    } else {

      child = new MSASolution(parentA);

    }
    return child;
  }


  /**
   * Select a column randomly
   */
  public int selectRandomColumn(MSASolution solution) {
    return randomGenerator.nextInt(1, solution.getAlignmentLength() - 1);
  }

  @Override
  public int getNumberOfRequiredParents() {
    return 2;
  }

  @Override
  public int getNumberOfGeneratedChildren() {
    return 2;
  }
}

