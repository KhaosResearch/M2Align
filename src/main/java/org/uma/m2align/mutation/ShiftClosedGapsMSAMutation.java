package org.uma.m2align.mutation;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.m2align.solution.MSASolution;

import java.util.List;

public class ShiftClosedGapsMSAMutation implements MutationOperator<MSASolution> {
  private double mutationProbability;
  private JMetalRandom randomGenerator;
 

  public ShiftClosedGapsMSAMutation(double mutationProbability) {
    if ((mutationProbability < 0) || (mutationProbability > 1)) {
      throw new JMetalException("Mutation probability value invalid: " + mutationProbability);
    }
    this.mutationProbability = mutationProbability;

    randomGenerator = JMetalRandom.getInstance();
  }

  @Override
  public MSASolution execute(MSASolution solution) {
    if (null == solution) {
      throw new JMetalException("Null parameter");
    }

    doMutation(solution);
    return solution;
  }

  /**
   * Performs the operation
   */
  public void doMutation(MSASolution solution) {
    if (randomGenerator.nextDouble() < mutationProbability) {
      int selectedSequence = getSelectedSequenceWithClosedGaps(solution);

      int originalSizeAlignment = solution.getAlignmentLength();

      List<Integer> gapsGroup = solution.getVariableValue(selectedSequence);
      int posClosedGaps = getClosedGaps(gapsGroup);

      Integer start, numberofgaps;
      start = gapsGroup.get(posClosedGaps);
      numberofgaps = gapsGroup.get(posClosedGaps + 1) - start + 1;

      gapsGroup.remove(posClosedGaps);
      gapsGroup.remove(posClosedGaps);

      for (int i = posClosedGaps; i < gapsGroup.size() - 1; i += 2) {
        gapsGroup.set(i, gapsGroup.get(i) - numberofgaps);
        gapsGroup.set(i + 1, gapsGroup.get(i + 1) - numberofgaps);
      }

      Integer newpos = 0;
      do {
        newpos = randomGenerator.nextInt(0, originalSizeAlignment - numberofgaps);

      } while (newpos == start);

      boolean added = false;
      for (int i = 0; i < gapsGroup.size() - 1; i += 2) {
        //If the newpos is not between the selected group of gaps
        if (newpos >= gapsGroup.get(i) && newpos <= gapsGroup.get(i + 1)) {

          gapsGroup.set(i + 1, gapsGroup.get(i + 1) + numberofgaps);
          added = true;

        } else if (gapsGroup.get(i) > newpos) {
          gapsGroup.set(i, gapsGroup.get(i) + numberofgaps);
          gapsGroup.set(i + 1, gapsGroup.get(i + 1) + numberofgaps);

          if (!added) {
            gapsGroup.add(i, newpos);
            gapsGroup.add(i + 1, newpos + numberofgaps - 1);
            added = true;
            i += 2;
          }
        }
      }

      if (!added) {
        gapsGroup.add(newpos);
        gapsGroup.add(newpos + numberofgaps - 1);
      }

      solution.setVariableValue(selectedSequence, gapsGroup);
      
      solution.mergeGapsGroups();
    }
  }


  public Integer getClosedGaps(List<Integer> gapsGroup) {
    //The SelectedSequence has minimum a ClosedGaps group
    Integer posClosedGaps;

    posClosedGaps = randomGenerator.nextInt(0, (gapsGroup.size() / 2) - 1);
    posClosedGaps *= 2;

    return posClosedGaps;
  }

  public int getSelectedSequenceWithClosedGaps(MSASolution solution) {
    int selectedSequence = -1;
    do {
      selectedSequence = randomGenerator.nextInt(0, solution.getNumberOfVariables() - 1);
    }
    while (solution.getVariableValue(selectedSequence).size() == 0 || !hasSequenceClosedGaps(solution.getVariableValue(selectedSequence)));

    // System.out.print ("Seleccionada " + selectedSequence +  " " +  solution.getVariableValue(selectedSequence) +  "\n");
    return selectedSequence;
  }

  public boolean hasSequenceClosedGaps(List<Integer> gapsGroup) {

    for (int i = 0; i < gapsGroup.size() - 1; i += 2) {
      if ((gapsGroup.get(i + 1) - gapsGroup.get(i)) > 0) {
        return true;
      }
    }

    return false;
  }

  public double getMutationProbability() {
    return mutationProbability;
  }
}

