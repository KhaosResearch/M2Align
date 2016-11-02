//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetalmsa.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder.NSGAIIVariant;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;
import org.uma.jmetal.util.neighborhood.impl.L5;
import org.uma.jmetalmsa.algorithm.nsgaii.NSGAIIMSABuilder;
import org.uma.jmetalmsa.crossover.SPXMSACrossover;
import org.uma.jmetalmsa.mutation.ShiftClosedGapsMSAMutation;
import org.uma.jmetalmsa.objective.Objective;
import org.uma.jmetalmsa.objective.impl.PercentageOfAlignedColumnsObjective;
import org.uma.jmetalmsa.objective.impl.SumOfPairsObjective;
import org.uma.jmetalmsa.problem.MSAProblem;
import org.uma.jmetalmsa.util.distancematrix.impl.PAM250;
import org.uma.jmetalmsa.solution.MSASolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetalmsa.problem.BAliBASE_MSAProblem;


/**
 * Example of experimental study based on solving the ZDT problems with four versions of NSGA-II, each
 * of them applying a different crossover probability (from 0.7 to 1.0).
 *
 * This experiment assumes that the reference Pareto front are known, so the names of files containing
 * them and the directory where they are located must be specified.
 *
 * Six quality indicators are used for performance assessment.
 *
 * The steps to carry out the experiment are:
 * 1. Configure the experiment
 * 2. Execute the algorithms
 * 3. Compute the quality indicators
 * 4. Generate Latex tables reporting means and medians
 * 5. Generate Latex tables with the result of applying the Wilcoxon Rank Sum Test
 * 6. Generate Latex tables with the ranking obtained by applying the Friedman test
 * 7. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class IJISExperiment {
  
  private static String Algorithm;
  //private static int INDEPENDENT_RUNS=5;
  private static List<Problem<MSASolution>> problemList = new ArrayList<>();

  public static void main(String[] args) throws Exception {
    /*if (args.length != 8) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory  Algorithm RVGroup InitialProblem FinalProblem NumberOfCores INDEPENDENT_RUNS ") ;
    }*/
    String experimentBaseDirectory = args[0] ;
    String dataBaseDirectory = args[1]; //"/home/cristian/msa/msajmetalbiojava/"; 
    Algorithm = args[2];
    String RV = args[3] ;
    Integer Start =Integer.parseInt(args[4]) ;
    Integer End =  Integer.parseInt(args[5]) ;
    Integer NumCores = Integer.parseInt(args[6]) ; //40
    Integer INDEPENDENT_RUNS = Integer.parseInt(args[7]); //10
    Integer MaxEvals = Integer.parseInt(args[8]) ;  //25000
    Integer populationSize = Integer.parseInt(args[9]) ; //100
    boolean Multithreaded=Integer.parseInt(args[10])==1?true:false;  //0

    
    CrossoverOperator<MSASolution> crossover = new SPXMSACrossover(0.8);
    MutationOperator<MSASolution> mutation = new ShiftClosedGapsMSAMutation(0.2);
    SelectionOperator selection = new BinaryTournamentSelection(new RankingAndCrowdingDistanceComparator());
   
    problemList = new ArrayList<>();
    Addproblems(Start, End, RV, dataBaseDirectory);
    
//    Addproblems(1, 44, "12", dataBaseDirectory);
//    Addproblems(1, 41, "20", dataBaseDirectory);
//    Addproblems(1, 30, "30", dataBaseDirectory);
//    Addproblems(1, 49, "40", dataBaseDirectory);
//    Addproblems(1, 16, "50", dataBaseDirectory);
      
    List<TaggedAlgorithm<List<MSASolution>>> algorithmList = configureAlgorithmList(problemList, 
                crossover, mutation, selection, INDEPENDENT_RUNS, MaxEvals, populationSize,
                NumCores,Multithreaded) ;

    Experiment<MSASolution, List<MSASolution>> experiment =
        new ExperimentBuilder<MSASolution, List<MSASolution>>("Study")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory(experimentBaseDirectory + "/Study/referenceFront")
            .setIndicatorList(Arrays.asList(
                new Epsilon<MSASolution>(), new Spread<MSASolution>(), new GenerationalDistance<MSASolution>(),
                new PISAHypervolume<MSASolution>(),
                new InvertedGenerationalDistance<MSASolution>(),
                new InvertedGenerationalDistancePlus<MSASolution>())
            )
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(NumCores)
            .build();

    new ExecuteAlgorithms<>(experiment).run();
    // new PrintMSAs(experiment).run();
      
   // new GenerateReferenceParetoFront(experiment).run();
   // new ComputeQualityIndicators<>(experiment).run() ;
   // new GenerateLatexTablesWithStatistics(experiment).run() ;
    //new GenerateWilcoxonTestTablesWithR<>(experiment).run() ;
    //new GenerateFriedmanTestTables<>(experiment).run();
   // new GenerateBoxplotsWithR<>(experiment).setRows(1).setColumns(2).setDisplayNotch().run();
  }

  
  static void Addproblems(int Start, int End, String RV, String dataBaseDirectory) throws Exception{
      String ProblemName;
      
      List<Objective> objectiveList = new ArrayList<>() ;
      objectiveList.add(new SumOfPairsObjective(new PAM250())) ;
      objectiveList.add(new PercentageOfAlignedColumnsObjective()) ;

        
      for (int k=Start;k<=End;k++){
       
//       if(RV=="11"){
//         if (k==26 || k==30) continue;
//       }
//       
//       if(RV=="20"){
//         if (k==29) continue;
//       }
//       
//       if(RV=="30"){
//         if (k==9 || k==16 || k==20 || k==23) continue;
//       }

       ProblemName = "BB" + RV +"0";
        if (k<10) ProblemName= ProblemName + "0";
        ProblemName=ProblemName+k;

  
        problemList.add(new BAliBASE_MSAProblem(ProblemName, dataBaseDirectory, objectiveList));
      
      
    }
      
  }
  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of a
   * {@link TaggedAlgorithm}, which is a decorator for class {@link Algorithm}. The {@link TaggedAlgorithm}
   * has an optional tag component, that can be set as it is shown in this example, where four variants of a
   * same algorithm are defined.
   *
   * @param problemList
   * @return
   */
  static List<TaggedAlgorithm<List<MSASolution>>> configureAlgorithmList(
      List<Problem<MSASolution>> problemList, 
      CrossoverOperator<MSASolution> crossover,
      MutationOperator<MSASolution> mutation,
      SelectionOperator selection,int independentRuns, int MaxEvals, int popSize, int NumCores, boolean Multithreaded) {
    
    List<TaggedAlgorithm<List<MSASolution>>> algorithms = new ArrayList<>() ;

    for (int run = 0; run < independentRuns; run++) {

        if(Algorithm.equals("NSGAII")){
          for (Problem<MSASolution> problem : problemList) {
               
              SolutionListEvaluator<MSASolution> Evaluator;
              if (Multithreaded){
                    Evaluator=new MultithreadedSolutionListEvaluator<MSASolution>(NumCores, problem);
              }else{
                    Evaluator=new SequentialSolutionListEvaluator<MSASolution>();
              }
                
               Algorithm<List<MSASolution>> algorithm = new NSGAIIMSABuilder(problem,
                    crossover, mutation, NSGAIIVariant.NSGAII)
                    .setSelectionOperator(selection)
                    .setMaxEvaluations(MaxEvals)
                    .setPopulationSize(popSize)
                    .setSolutionListEvaluator(Evaluator)
                    .build();

              algorithms.add(new TaggedAlgorithm<List<MSASolution>>(algorithm, problem, run));
          }
       }

    }
    
    return algorithms ;
    
  }
  
}