package Heuristics;

import IO.InputReader;
import Utils.*;

import java.util.List;

public interface IHeuristic {
    String getHeuristicName();
    void applyHeuristic(List<Request> requests);
    Solution getSolution();

    InputReader getInputReader();
}

