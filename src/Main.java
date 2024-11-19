
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Heuristics.*;
import IO.*;
import Utils.*;
import localsearch.*;

public class Main {
    static Path baseDirectory = Paths.get("./TestInstances");

    public static void main(String[] args) {
        boolean processAllInputFiles = false;
        List<Path> inputFiles;
        // Create a LinkedHashMap to maintain insertion order
        Map<String, LocalSearchResult> localSearchResults = new LinkedHashMap<>();

        if (processAllInputFiles)
            inputFiles = getInputFiles();
        else {
            inputFiles = new ArrayList<>();
            inputFiles.add(baseDirectory.resolve("n50 t50 LonLr/cap100_n50_t50_LonLr_3.txt"));
        }

        var treeType = BalancedTreeType.BCHTRB; //change this to BCHTRB or BCHTAVL to test different tree types
        var nrOfIterations = 10000; // i in results filename
        var nrOfTrees = 10; // j in results filename = nr of trees used to remove nodes from (generate neighbor)
        boolean deepCopyRollback = true; // change this to true to test deep copy rollback

        //inputFiles.forEach(System.out::println);
        for (var f : inputFiles){
            System.out.println();
            System.out.println("Processing file " + f);
            System.out.println("----------------------------------------------------------------------------------------");

            InputReader inputReader = new InputReader(f.toString());
            List<Request> requests = inputReader.getRequests();


            HeuristicRunner runner = new HeuristicRunner();
            IHeuristic bcht;
            LocalSearchResult result;

            switch (treeType) {
                case BCHT:
                    bcht = new BCHT<IntervalTree>(inputReader, new IntervalTreeFactory(), "BCHT");
                    runner.run(bcht, requests);
                    var localSearchBCHT = new LocalSearchGeneric<IntervalTree, IntervalNode>(bcht.getSolution(), bcht, deepCopyRollback,inputReader);
                    result = localSearchBCHT.run(nrOfTrees);
                    break;
                case BCHTRB:
                    bcht = new BCHT<RBIntervalTree>(inputReader, new RBIntervalTreeFactory(), "BCHTRB");
                    runner.run(bcht, requests);
                    //var initialSolution = new Solution<>(bcht.getSolution());
                    var localSearchRB = new LocalSearchGeneric<RBIntervalTree, RBIntervalNode>(bcht.getSolution(), bcht, deepCopyRollback, inputReader);
                    result = localSearchRB.run(nrOfTrees);
//                    if (!Validator.validate(f.toString(),treeType.name())){
//                        System.out.println("result of file " + f.toString() + " contains violations!!!");
//                    }
                    break;
                case BCHTAVL:
                default:
                    bcht = new BCHT<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
                    //  bcht = new BestCapacityHeuristic<AVLIntervalTree>(inputReader, new AVLIntervalTreeFactory(), "BCHTAVL");
                    runner.run(bcht, requests);
                    var localSearchAVL = new LocalSearchGeneric<AVLIntervalTree, AVLIntervalNode>(bcht.getSolution(), bcht, deepCopyRollback, inputReader);
                    result = localSearchAVL.run(nrOfTrees);
                    break;
            }

            if (!Validator.validate(f.toString(),treeType.name())){
                System.out.println("result of file " + f.toString() + " contains violations!!!");
            }

            localSearchResults.put(f.toString(), result);
        }

        // Write the results to a CSV file if we processed all files
        if (processAllInputFiles) {
            String suffix = deepCopyRollback ? "_RDC" : "_RM";
            String outputCsvFile = "local_search_results_" + treeType.name() + "_i" + nrOfIterations + "_j" + nrOfTrees + suffix  + "_MT" +  ".csv";
            LocalSearchResultsWriter.writeToCsv(localSearchResults, outputCsvFile);
        }
    }

    private static List<Path> getInputFiles(){
        try (Stream<Path> files = Files.walk(baseDirectory)) {
            List<Path> txtFilePaths = files.filter(Files::isRegularFile)               // Only select regular files (not directories)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .map(baseDirectory::relativize)
                    .map(path -> baseDirectory.resolve(path))  // Prepend baseDirectory to make full path
                    .collect(Collectors.toList());

            //txtFilePaths.forEach(System.out::println);
            return txtFilePaths;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("files not found");
            return List.of(); // Return an empty list
        }
    }
}
