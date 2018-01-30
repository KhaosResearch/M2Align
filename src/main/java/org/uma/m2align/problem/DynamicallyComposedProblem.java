package org.uma.m2align.problem;

import java.util.List;
import org.uma.jmetal.problem.impl.AbstractGenericProblem;
import org.uma.m2align.score.Score;
import org.uma.m2align.solution.MSASolution;

public abstract class DynamicallyComposedProblem<S extends MSASolution> extends AbstractGenericProblem<S> {
  private List<Score> scoreList ;

  public DynamicallyComposedProblem(List<Score> scoreList) {
    this.scoreList = scoreList ;
    setNumberOfObjectives(scoreList.size());
  }

  @Override
  public void evaluate(S solution) {
    solution.removeGapColumns();

    char [][]decodedSequences= solution.decodeToMatrix() ;
    
    for (int i = 0 ; i < getNumberOfObjectives(); i++) {
      if (scoreList.get(i).isAMinimizationScore()) {
        solution.setObjective(i, scoreList.get(i).compute(solution,decodedSequences));
      } else {
        solution.setObjective(i, -1.0 * scoreList.get(i).compute(solution,decodedSequences));
      }
    }
    
    decodedSequences=null;
  }
  
  public List<Score> getScoreList(){
      return scoreList;
  }
}
