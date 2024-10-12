package Heuristics;

import Utils.Request;

import java.util.List;

public interface IHeuristic {
    String getHeuristicName();
    void applyHeuristic(List<Request> requests);
}
