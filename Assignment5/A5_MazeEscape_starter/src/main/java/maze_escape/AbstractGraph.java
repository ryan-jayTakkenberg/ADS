package maze_escape;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public abstract class AbstractGraph<V> {

    /** Graph representation:
     *  this class implements graph search algorithms on a graph with abstract vertex type V
     *  for every vertex in the graph, its neighbours can be found by use of abstract method getNeighbours(fromVertex)
     *  this abstraction can be used for both directed and undirected graphs
     **/

    public AbstractGraph() { }

    /**
     * retrieves all neighbours of the given fromVertex
     * if the graph is directed, the implementation of this method shall follow the outgoing edges of fromVertex
     * @param fromVertex
     * @return
     */
    public abstract Set<V> getNeighbours(V fromVertex);

    /**
     * retrieves all vertices that can be reached directly or indirectly from the given firstVertex
     * if the graph is directed, only outgoing edges shall be traversed
     * firstVertex shall be included in the result as well
     * if the graph is connected, all vertices shall be found
     * @param firstVertex   the start vertex for the retrieval
     * @return
     */
    public Set<V> getAllVertices(V firstVertex) {
        Set<V> visitedVertices = new HashSet<>();
        Set<V> allVertices = new HashSet<>();
        getAllVerticesRecursive(firstVertex, visitedVertices, allVertices);
        return allVertices;
    }

    private void getAllVerticesRecursive(V currentVertex, Set<V> visitedVertices, Set<V> allVertices) {
        if (!visitedVertices.contains(currentVertex)) {
            visitedVertices.add(currentVertex);
            allVertices.add(currentVertex);
            Set<V> neighbours = getNeighbours(currentVertex);
            for (V neighbour : neighbours) {
                getAllVerticesRecursive(neighbour, visitedVertices, allVertices);
            }
        }
    }


    /**
     * Formats the adjacency list of the subgraph starting at the given firstVertex
     * according to the format:
     *  	vertex1: [neighbour11,neighbour12,…]
     *  	vertex2: [neighbour21,neighbour22,…]
     *  	…
     * Uses a pre-order traversal of a spanning tree of the sub-graph starting with firstVertex as the root
     * if the graph is directed, only outgoing edges shall be traversed
     * , and using the getNeighbours() method to retrieve the roots of the child subtrees.
     * @param firstVertex
     * @return
     */
    public String formatAdjacencyList(V firstVertex) {
        StringBuilder stringBuilder = new StringBuilder("Graph adjacency list:\n");
        Set<V> visitedVertices = new HashSet<>();
        formatAdjacencyListRecursive(firstVertex, visitedVertices, stringBuilder);
        return stringBuilder.toString();
    }

    private void formatAdjacencyListRecursive(V currentVertex, Set<V> visitedVertices, StringBuilder stringBuilder) {
        if (!visitedVertices.contains(currentVertex)) {
            visitedVertices.add(currentVertex);

            // Get neighbors in the desired order (assumed order: alphabetical)
            List<V> neighbours = getNeighbours(currentVertex).stream()
                    .sorted(Comparator.comparing(Object::toString))
                    .collect(Collectors.toList());

            // Append the current vertex to the StringBuilder
            stringBuilder.append(currentVertex).append(": [");

            // Append the neighbors of the current vertex
            for (V neighbour : neighbours) {
                stringBuilder.append(neighbour).append(",");
            }

            // Remove the trailing comma if there are neighbors
            if (!neighbours.isEmpty()) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }

            // Close the list
            stringBuilder.append("]\n");

            // Recursively call for each neighbor
            for (V neighbour : neighbours) {
                formatAdjacencyListRecursive(neighbour, visitedVertices, stringBuilder);
            }
        }
    }



    /**
     * represents a directed path of connected vertices in the graph
     */
    public class GPath {
        private Deque<V> vertices = new LinkedList<>();
        private double totalWeight = 0.0;
        private Set<V> visited = new HashSet<>();

        /**
         * representation invariants:
         * 1. vertices contains a sequence of vertices that are neighbours in the graph,
         *    i.e. FOR ALL i: 1 < i < vertices.length: getNeighbours(vertices[i-1]).contains(vertices[i])
         * 2. a path with one vertex equal start and target vertex
         * 3. a path without vertices is empty, does not have a start nor a target
         * totalWeight is a helper attribute to capture total path length from a function on two neighbouring vertices
         * visited is a helper set to be able to track visited vertices in searches, only for analysis purposes
         **/
        private static final int DISPLAY_CUT = 10;
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(
                    String.format("Weight=%.2f Length=%d visited=%d (",
                            this.totalWeight, this.vertices.size(), this.visited.size()));
            String separator = "";
            int count = 0;
            final int tailCut = this.vertices.size()-1 - DISPLAY_CUT;
            for (V v : this.vertices) {
                // limit the length of the text representation for long paths.
                if (count < DISPLAY_CUT || count > tailCut) {
                    sb.append(separator).append(v.toString());
                    separator = ", ";
                } else if (count == DISPLAY_CUT) {
                    sb.append(separator).append("...");
                }
                count++;
            }
            sb.append(")");
            return sb.toString();
        }

        /**
         * recalculates the total weight of the path from a given weightMapper that calculates the weight of
         * the path segment between two neighbouring vertices.
         * @param weightMapper
         */
        public void reCalculateTotalWeight(BiFunction<V,V,Double> weightMapper) {
            this.totalWeight = 0.0;
            V previous = null;
            for (V v: this.vertices) {
                // the first vertex of the iterator has no predecessor and hence no weight contribution
                if (previous != null) this.totalWeight += weightMapper.apply(previous, v);
                previous = v;
            }
        }

        public Queue<V> getVertices() {
            return this.vertices;
        }

        public double getTotalWeight() {
            return this.totalWeight;
        }

        public Set<V> getVisited() { return this.visited; }

        public void setVertices(Deque<V> vertices) {
            this.vertices = vertices;
        }

        public void setTotalWeight(double totalWeight) {
            this.totalWeight = totalWeight;
        }

        public void setVisited(Set<V> visited) {
            this.visited = visited;
        }

        public void addVertex(V vertex) {
            vertices.addLast(vertex);
        }
        public void removeLastVertex() {
            if (!vertices.isEmpty()) {
                vertices.removeLast();
            }
        }
    }

    /**
     * Uses a depth-first search algorithm to find a path from the startVertex to targetVertex in the subgraph
     * All vertices that are being visited by the search should also be registered in path.visited
     * @param startVertex
     * @param targetVertex
     * @return  the path from startVertex to targetVertex
     *          or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath depthFirstSearch(V startVertex, V targetVertex) {
        if (startVertex == null || targetVertex == null) {
            return null;
        }

        Set<V> visitedVertices = new HashSet<>();
        GPath path = new GPath();

        if (startVertex.equals(targetVertex)) {
            path.addVertex(startVertex);
            path.visited.add(startVertex);
            return path;
        }

        if (depthFirstSearchRecursive(startVertex, targetVertex, visitedVertices, path)) {
            return path;
        } else {
            return null;
        }
    }

    private boolean depthFirstSearchRecursive(V currentVertex, V targetVertex, Set<V> visitedVertices, GPath path) {
        visitedVertices.add(currentVertex);
        path.addVertex(currentVertex);

        if (currentVertex.equals(targetVertex)) {
            return true; // Target vertex found
        }

        Set<V> neighbours = getNeighbours(currentVertex);
        for (V neighbour : neighbours) {
            if (!visitedVertices.contains(neighbour)) {
                if (depthFirstSearchRecursive(neighbour, targetVertex, visitedVertices, path)) {
                    return true; // Target vertex found in the subtree
                }
            }
        }

        // If the target vertex is not found in this subtree, backtrack
        path.removeLastVertex();
        return false;
    }


    /**
     * Uses a breadth-first search algorithm to find a path from the startVertex to targetVertex in the subgraph
     * All vertices that are being visited by the search should also be registered in path.visited
     * @param startVertex
     * @param targetVertex
     * @return  the path from startVertex to targetVertex
     *          or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath breadthFirstSearch(V startVertex, V targetVertex) {
        if (startVertex == null || targetVertex == null) {
            return null;
        }

        Set<V> visitedVertices = new HashSet<>();
        GPath path = new GPath();

        if (startVertex.equals(targetVertex)) {
            path.addVertex(startVertex);
            path.visited.add(startVertex);
            return path;
        }

        Queue<V> queue = new LinkedList<>();
        queue.add(startVertex);
        visitedVertices.add(startVertex);

        while (!queue.isEmpty()) {
            V currentVertex = queue.poll();
            path.addVertex(currentVertex);

            if (currentVertex.equals(targetVertex)) {
                return path; // Target vertex found
            }

            Set<V> neighbours = getNeighbours(currentVertex);
            for (V neighbour : neighbours) {
                if (!visitedVertices.contains(neighbour)) {
                    visitedVertices.add(neighbour);
                    queue.add(neighbour);
                }
            }
        }

        return null; // Target vertex not found
    }

    // helper class to build the spanning tree of visited vertices in dijkstra's shortest path algorithm
    // your may change this class or delete it altogether follow a different approach in your implementation
    private class MSTNode implements Comparable<MSTNode> {
        protected V vertex;                // the graph vertex that is concerned with this MSTNode
        protected V parentVertex = null;     // the parent's node vertex that has an edge towards this node's vertex
        protected boolean marked = false;  // indicates DSP processing has been marked complete for this vertex
        protected double weightSumTo = Double.MAX_VALUE;   // sum of weights of current shortest path towards this node's vertex

        private MSTNode(V vertex) {
            this.vertex = vertex;
        }

        // comparable interface helps to find a node with the shortest current path, sofar
        @Override
        public int compareTo(MSTNode otherMSTNode) {
            return Double.compare(weightSumTo, otherMSTNode.weightSumTo);
        }
    }

    /**
     * Calculates the edge-weighted shortest path from the startVertex to targetVertex in the subgraph
     * according to Dijkstra's algorithm of a minimum spanning tree
     * @param startVertex
     * @param targetVertex
     * @param weightMapper   provides a function(v1,v2) by which the weight of an edge from v1 to v2
     *                       can be retrieved or calculated
     * @return  the shortest path from startVertex to targetVertex
     *          or null if target cannot be matched with a vertex in the sub-graph from startVertex
     */
    public GPath dijkstraShortestPath(V startVertex, V targetVertex,
                                         BiFunction<V,V,Double> weightMapper) {

        if (startVertex == null || targetVertex == null) return null;

        // Initialise the result path of the search
        GPath path = new GPath();
        path.visited.add(startVertex);

        // Easy target
        if (startVertex.equals(targetVertex)) {
            path.vertices.add(startVertex);
            return path;
        }

        // A minimum spanning tree which tracks for every visited vertex:
        // a) Its (parent) predecessor in the currently shortest path towards this visited vertex
        // b) The total weight of the currently shortest path towards this visited vertex
        // c) A mark, indicating whether the current path towards this visited vertex is the final shortest.
        Map<V, MSTNode> minimumSpanningTree = new HashMap<>();

        // Initialise the minimum spanning tree with the startVertex
        MSTNode nearestMSTNode = new MSTNode(startVertex);
        nearestMSTNode.weightSumTo = 0.0;
        minimumSpanningTree.put(startVertex, nearestMSTNode);

        // Priority queue to keep track of the nearest MSTNode
        PriorityQueue<MSTNode> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(nearestMSTNode);

        while (!priorityQueue.isEmpty()) {
            // Dequeue the nearest MSTNode
            nearestMSTNode = priorityQueue.poll();

            // Mark the current node as final
            nearestMSTNode.marked = true;

            // TODO: Register the visited vertex for statistical purposes
            path.visited.add(nearestMSTNode.vertex);

            // Continue Dijkstra's algorithm to process nearestMSTNode
            Set<V> neighbours = getNeighbours(nearestMSTNode.vertex);

            for (V neighbour : neighbours) {
                // Skip if the neighbour is already marked as final
                if (minimumSpanningTree.containsKey(neighbour) && minimumSpanningTree.get(neighbour).marked) {
                    continue;
                }

                // Calculate the potential new total weight
                double potentialWeight = nearestMSTNode.weightSumTo + weightMapper.apply(nearestMSTNode.vertex, neighbour);

                // Update the minimum spanning tree if a shorter path is found
                MSTNode neighbourMSTNode = minimumSpanningTree.get(neighbour);
                if (neighbourMSTNode == null || potentialWeight < neighbourMSTNode.weightSumTo) {
                    neighbourMSTNode = new MSTNode(neighbour);
                    neighbourMSTNode.parentVertex = nearestMSTNode.vertex;
                    neighbourMSTNode.weightSumTo = potentialWeight;

                    // Enqueue the updated MSTNode
                    priorityQueue.add(neighbourMSTNode);
                    minimumSpanningTree.put(neighbour, neighbourMSTNode);
                }
            }

            if (nearestMSTNode.vertex.equals(targetVertex)) {
                // Target vertex found, construct the path and return
                while (nearestMSTNode != null) {
                    path.vertices.addFirst(nearestMSTNode.vertex);
                    nearestMSTNode = minimumSpanningTree.get(nearestMSTNode.parentVertex);
                }
                path.reCalculateTotalWeight(weightMapper);
                return path;
            }
        }

        return null; // No path found
    }




}
