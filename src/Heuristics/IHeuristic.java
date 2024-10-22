package Heuristics;

import Utils.*;

import java.util.List;

public interface IHeuristic {
    String getHeuristicName();
    void applyHeuristic(List<Request> requests);
    Solution getSolution();
}
