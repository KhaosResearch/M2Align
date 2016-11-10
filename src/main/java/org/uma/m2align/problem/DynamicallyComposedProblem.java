package org.uma.m2align.problem;

import org.uma.jmetal.problem.impl.AbstractGenericProblem;
import org.uma.m2align.objective.Objective;
import org.uma.m2align.solution.MSASolution;

import java.util.List;

public abstract class DynamicallyComposedProblem<S extends MSASolution> extends AbstractGenericProblem<S> {
  private List<Objective> objectiveList ;

  public DynamicallyComposedProblem(List<Objective> objectiveList) {
    this.objectiveList = objectiveList ;
    setNumberOfObjectives(objectiveList.size());
  }

  @Override
  public void evaluate(S solution) {
    solution.removeGapColumns();
     char[][] decodedSequences = solution.decodeToMatrix();

    for (int i = 0 ; i < getNumberOfObjectives(); i++) {
      if (objectiveList.get(i).isAMinimizationObjective()) {
        solution.setObjective(i, objectiveList.get(i).compute(solution,decodedSequences));
      } else {
        solution.setObjective(i, -1.0 * objectiveList.get(i).compute(solution,decodedSequences));
      }
    }
    
    decodedSequences=null;
    
  }
  
  public List<Objective> getObjectiveList(){
      return objectiveList;
  }
}
