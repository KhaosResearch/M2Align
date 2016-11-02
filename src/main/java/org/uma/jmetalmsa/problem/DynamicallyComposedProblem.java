package org.uma.jmetalmsa.problem;

import org.uma.jmetal.problem.impl.AbstractGenericProblem;
import org.uma.jmetalmsa.objective.Objective;
import org.uma.jmetalmsa.solution.MSASolution;

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

    for (int i = 0 ; i < getNumberOfObjectives(); i++) {
      if (objectiveList.get(i).isAMinimizationObjective()) {
        solution.setObjective(i, objectiveList.get(i).compute(solution));
      } else {
        solution.setObjective(i, -1.0 * objectiveList.get(i).compute(solution));
      }
    }
  }
  
  public List<Objective> getObjectiveList(){
      return objectiveList;
  }
}
