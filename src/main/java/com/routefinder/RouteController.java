package com.routefinder;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class RouteController {

    static class Edge {
        public String from;
        public String to;
        public int cost;
    }

    static class RequestData {
        public List<String> nodes;
        public List<Edge> edges;
        public String source;
        public String destination;
        public String algorithm;
    }

    static class ResponseData {
        public List<String> path;
        public int cost;

        ResponseData(List<String> path, int cost) {
            this.path = path;
            this.cost = cost;
        }
    }

    @PostMapping("/find-path")
    public ResponseData findPath(@RequestBody RequestData data) {
        try {
            // Validate input
            if (data.nodes == null || data.edges == null || data.source == null || 
                data.destination == null || data.algorithm == null) {
                throw new IllegalArgumentException("Missing required fields");
            }

            Map<String, List<Edge>> graph = buildGraph(data.nodes, data.edges);

            switch (data.algorithm.toLowerCase()) {
                case "dfs":
                    return dfs(data.source, data.destination, graph);
                case "bfs":
                    return bfs(data.source, data.destination, graph);
                default:
                    return dijkstra(data.source, data.destination, graph);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseData(Collections.emptyList(), -1);
        }
    }

    private Map<String, List<Edge>> buildGraph(List<String> nodes, List<Edge> edges) {
        Map<String, List<Edge>> graph = new HashMap<>();
        for (String node : nodes) {
            graph.put(node, new ArrayList<>());
        }
        for (Edge e : edges) {
            graph.get(e.from).add(e);
            Edge reverse = new Edge();
            reverse.from = e.to;
            reverse.to = e.from;
            reverse.cost = e.cost;
            graph.get(e.to).add(reverse);
        }
        return graph;
    }

    private ResponseData dijkstra(String start, String end, Map<String, List<Edge>> graph) {
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(Map.Entry.comparingByValue());

        for (String node : graph.keySet()) dist.put(node, Integer.MAX_VALUE);
        dist.put(start, 0);
        pq.add(new AbstractMap.SimpleEntry<>(start, 0));

        while (!pq.isEmpty()) {
            Map.Entry<String, Integer> entry = pq.poll();
            String curr = entry.getKey();
            if (curr.equals(end)) break;
            for (Edge edge : graph.get(curr)) {
                int newDist = dist.get(curr) + edge.cost;
                if (newDist < dist.get(edge.to)) {
                    dist.put(edge.to, newDist);
                    prev.put(edge.to, curr);
                    pq.add(new AbstractMap.SimpleEntry<>(edge.to, newDist));
                }
            }
        }

        List<String> path = buildPath(start, end, prev);
        return new ResponseData(path, path.isEmpty() ? -1 : dist.get(end));
    }

    private ResponseData dfs(String start, String end, Map<String, List<Edge>> graph) {
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();
        boolean found = dfsHelper(start, end, graph, visited, parent);
        List<String> path = buildPath(start, end, parent);
        int cost = found ? calculatePathCost(path, graph) : -1;
        return new ResponseData(path, cost);
    }

    private boolean dfsHelper(String current, String end, Map<String, List<Edge>> graph,
                            Set<String> visited, Map<String, String> parent) {
        if (current.equals(end)) return true;
        visited.add(current);
        for (Edge edge : graph.get(current)) {
            if (!visited.contains(edge.to)) {
                parent.put(edge.to, current);
                if (dfsHelper(edge.to, end, graph, visited, parent)) return true;
            }
        }
        return false;
    }

    private ResponseData bfs(String start, String end, Map<String, List<Edge>> graph) {
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(end)) break;
            for (Edge edge : graph.get(current)) {
                if (!visited.contains(edge.to)) {
                    visited.add(edge.to);
                    parent.put(edge.to, current);
                    queue.offer(edge.to);
                }
            }
        }

        List<String> path = buildPath(start, end, parent);
        int cost = path.isEmpty() ? -1 : calculatePathCost(path, graph);
        return new ResponseData(path, cost);
    }

    private List<String> buildPath(String start, String end, Map<String, String> parent) {
        List<String> path = new ArrayList<>();
        for (String at = end; at != null; at = parent.get(at)) path.add(at);
        Collections.reverse(path);
        return path.isEmpty() || !path.get(0).equals(start) ? Collections.emptyList() : path;
    }

    private int calculatePathCost(List<String> path, Map<String, List<Edge>> graph) {
        int cost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            for (Edge edge : graph.get(path.get(i))) {
                if (edge.to.equals(path.get(i + 1))) {
                    cost += edge.cost;
                    break;
                }
            }
        }
        return cost;
    }
}