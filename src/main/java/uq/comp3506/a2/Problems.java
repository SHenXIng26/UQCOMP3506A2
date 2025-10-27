// @edu:student-assignment

package uq.comp3506.a2;

// You may wish to import more/other structures too
import uq.comp3506.a2.structures.Edge;
import uq.comp3506.a2.structures.Vertex;
import uq.comp3506.a2.structures.Entry;
import uq.comp3506.a2.structures.TopologyType;
import uq.comp3506.a2.structures.Tunnel;
import uq.comp3506.a2.structures.OrderedMap;
import uq.comp3506.a2.structures.UnorderedMap;
import uq.comp3506.a2.structures.Heap;

import java.util.ArrayList;
import java.util.List;

// This is part of COMP3506 Assignment 2. Students must implement their own solutions.

/**
 * Supplied by the COMP3506/7505 teaching team, Semester 2, 2025.
 * No bounds are provided. You should maximize efficiency where possible.
 * Below we use `S` and `U` to represent the generic data types that a Vertex
 * and an Edge can have, respectively, to avoid confusion between V and E in
 * typical graph nomenclature. That is, Vertex objects store data of type `S`
 * and Edge objects store data of type `U`.
 */
public class Problems {

    /**
     * Return a double representing the minimum radius of illumination required
     * to light the entire tunnel. Your answer will be accepted if
     * |your_ans - true_ans| is less than or equal to 0.000001
     * @param tunnelLength The length of the tunnel in question
     * @param lightIntervals The list of light intervals in [0, tunnelLength];
     * that is, all light interval values are >= 0 and <= tunnelLength
     * @return The minimum radius value required to illuminate the tunnel
     * or -1 if no light fittings are provided
     * Note: We promise that the input List will be an ArrayList.
     */
    public static double tunnelLighting(int tunnelLength, List<Integer> lightIntervals) {
        // check if not lights provided
        if (lightIntervals == null || lightIntervals.isEmpty()) {
            return -1;
        }

        // using the ordered map to sort the light position
        OrderedMap<Integer, Integer> sortedLights = new OrderedMap<>();
        // add all the light positions to the avl tree. only key is matter, not the value
        for (int position : lightIntervals) {
            sortedLights.put(position, 1); // value can be anything
        }

        // extract the sorted position using keysInRange
        List<Integer> sortedPosition = sortedLights.keysInRange(0, tunnelLength);

        // error return if the sorting does not work
        if (sortedPosition == null || sortedPosition.isEmpty()) {
            return -1;
        }

        // below is the calculation for the radius
        double minRadius = 0;

        // radius = max(p0, max(gaps/2), tunnelLength - pLast)
        // gaps = difference between adjacent light

        minRadius = Math.max(minRadius, sortedPosition.get(0));

        for (int i = 0; i < sortedPosition.size() - 1; i++) {
            double gap = sortedPosition.get(i + 1) - sortedPosition.get(i);
            minRadius = Math.max(minRadius, gap/2.0);
        }

        minRadius = Math.max(minRadius, tunnelLength - sortedPosition.get(sortedPosition.size() - 1));

        return minRadius;


    }

    /**
     * Compute the TopologyType of the graph as represented by the given edgeList.
     * @param edgeList The list of edges making up the graph G; each is of type
     *              Edge, which stores two vertices and a value. Vertex identifiers
     *              are NOT GUARANTEED to be contiguous or in a given range.
     * @return The corresponding TopologyType.
     * Note: We promise not to provide any self loops, double edges, or isolated
     * vertices.
     */
    public static <S, U> TopologyType topologyDetection(List<Edge<S, U>> edgeList) {
        // Build adjacency list and track all vertices
        UnorderedMap<Vertex<S>, List<Vertex<S>>> adjList = new UnorderedMap<>();
        List<Vertex<S>> allVertices = new ArrayList<>();
        UnorderedMap<Integer, Boolean> vertexSeen = new UnorderedMap<>(); // tack by ID

        for (Edge<S, U> edge : edgeList) {
            Vertex<S> v1 = edge.getVertex1();
            Vertex<S> v2 = edge.getVertex2();

            // Track unique vertices by ID
            if (vertexSeen.get(v1.getId()) == null) {
                vertexSeen.put(v1.getId(), true);
                allVertices.add(v1);
            }

            // Track unique vertices by ID
            if (vertexSeen.get(v2.getId()) == null) {
                vertexSeen.put(v2.getId(), true);
                allVertices.add(v2);
            }

            // Build adjacency list
            addToAdjList(adjList, v1, v2);
            addToAdjList(adjList, v2, v1);
        }

        if (allVertices.isEmpty()) {
            return TopologyType.UNKNOWN;
        }

        UnorderedMap<Vertex<S>, Boolean> visited = new UnorderedMap<>();
        int componentCount = 0;
        boolean hasTreeComponent = false;
        boolean hasGraphComponent = false;

        for (Vertex<S> vertex : allVertices) {
            if (visited.get(vertex) == null) {
                componentCount++;
                boolean hasCycle = hasCycleDFS(vertex, adjList, visited, null);

                if (hasCycle) {
                    hasGraphComponent = true;
                } else {
                    hasTreeComponent = true;
                }
            }
        }

        // Classification
        if (componentCount == 1) {
            return hasTreeComponent ? TopologyType.CONNECTED_TREE : TopologyType.CONNECTED_GRAPH;
        } else {
            if (hasTreeComponent && hasGraphComponent) {
                return TopologyType.HYBRID;
            } else if (hasTreeComponent) {
                return TopologyType.FOREST;
            } else {
                return TopologyType.DISCONNECTED_GRAPH;
            }
        }
    }

    // topologyDetection Helper Method below

    /** Helper to add to adjacency list */
    private static <S> void addToAdjList(UnorderedMap<Vertex<S>, List<Vertex<S>>> adjList,
                                         Vertex<S> vertex, Vertex<S> neighbor) {
        List<Vertex<S>> neighbors = adjList.get(vertex);
        if (neighbors == null) {
            neighbors = new ArrayList<>();
            adjList.put(vertex, neighbors);
        }
        neighbors.add(neighbor);
    }

    /** DFS cycle detection */
    private static <S> boolean hasCycleDFS(Vertex<S> current,
                                           UnorderedMap<Vertex<S>, List<Vertex<S>>> adjList,
                                           UnorderedMap<Vertex<S>, Boolean> visited,
                                           Vertex<S> parent) {
        visited.put(current, true);

        List<Vertex<S>> neighbors = adjList.get(current);
        if (neighbors == null) return false;

        for (Vertex<S> neighbor : neighbors) {
            if (visited.get(neighbor) == null) {
                if (hasCycleDFS(neighbor, adjList, visited, current)) {
                    return true;
                }
            } else if (!neighbor.equals(parent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compute the list of reachable destinations and their minimum costs.
     * @param edgeList The list of edges making up the graph G; each is of type
     *              Edge, which stores two vertices and a value. Vertex identifiers
     *              are NOT GUARANTEED to be contiguous or in a given range.
     * @param origin The origin vertex object.
     * @param threshold The total time the driver can drive before a break is required.
     * @return an ArrayList of Entry types, where the first element is the identifier
     *         of a reachable station (within the time threshold), and the second
     *         element is the minimum cost of reaching that given station. The
     *         order of the list is not important.
     * Note: We promise that S will be of Integer type.
     * Note: You should return the origin in your result with a cost of zero.
     */
    public static <S, U> List<Entry<Integer, Integer>> routeManagement(
            List<Edge<S, U>> edgeList, Vertex<S> origin, int threshold) {

        ArrayList<Entry<Integer, Integer>> result = new ArrayList<>();

        if (edgeList == null || edgeList.isEmpty() || origin == null) {
            return result;
        }

        // Build adjacency list: vertex ID -> list of (neighbor ID, weight)
        UnorderedMap<Integer, List<Entry<Integer, Integer>>> adjList = new UnorderedMap<>();

        for (Edge<S, U> edge : edgeList) {
            int id1 = edge.getVertex1().getId();
            int id2 = edge.getVertex2().getId();

            // Extract weight from edge data
            Integer weight = (Integer) edge.getData();

            // Add edge from id1 to id2
            addEdgeToAdjList(adjList, id1, id2, weight);
            // Add edge from id2 to id1
            addEdgeToAdjList(adjList, id2, id1, weight);
        }

        // Dijkstra's algorithm
        UnorderedMap<Integer, Integer> distances = new UnorderedMap<>();
        UnorderedMap<Integer, Boolean> visited = new UnorderedMap<>();

        // Priority queue: (distance, vertex ID)
        Heap<Integer, Integer> pq = new Heap<>();

        // Initialize
        int originId = origin.getId();
        distances.put(originId, 0);
        pq.insert(0, originId);

        while (!pq.isEmpty()) {
            // Get vertex with smallest distance
            Entry<Integer, Integer> current = pq.removeMin();
            int currentDist = current.getKey();
            int currentId = current.getValue();

            // Skip if we've already processed this vertex with a smaller distance
            if (visited.get(currentId) != null) {
                continue;
            }

            // Mark as visited
            visited.put(currentId, true);

            // If current distance exceeds threshold, we can stop (all remaining will be worse)
            if (currentDist > threshold) {
                continue;
            }

            // Explore neighbors
            List<Entry<Integer, Integer>> neighbors = adjList.get(currentId);
            if (neighbors != null) {
                for (Entry<Integer, Integer> neighbor : neighbors) {
                    int neighborId = neighbor.getKey();
                    int edgeWeight = neighbor.getValue();
                    int newDist = currentDist + edgeWeight;

                    // Only consider if within threshold
                    if (newDist <= threshold) {
                        Integer oldDist = distances.get(neighborId);

                        // If we found a shorter path or haven't visited yet
                        if (oldDist == null || newDist < oldDist) {
                            distances.put(neighborId, newDist);
                            pq.insert(newDist, neighborId);
                        }
                    }
                }
            }
        }

        // Collect results: all vertices with distance ≤ threshold
        // Since we can't iterate over UnorderedMap keys easily, we'll use a different approach
        for (Entry<Integer, Integer> entry : getAllVerticesFromEdges(edgeList)) {
            int vertexId = entry.getKey();
            Integer dist = distances.get(vertexId);

            // Include origin (distance 0) and all reachable vertices within threshold
            if (dist != null && dist <= threshold) {
                result.add(new Entry<>(vertexId, dist));
            }
        }

        // Add origin if not already included (should be included with distance 0)
        if (distances.get(originId) != null && distances.get(originId) <= threshold) {
            boolean originExists = false;
            for (Entry<Integer, Integer> entry : result) {
                if (entry.getKey().equals(originId)) {
                    originExists = true;
                    break;
                }
            }
            if (!originExists) {
                result.add(new Entry<>(originId, 0));
            }
        }

        return result;
    }

    /** Helper to add edge to adjacency list */
    private static void addEdgeToAdjList(UnorderedMap<Integer, List<Entry<Integer, Integer>>> adjList,
                                         int fromId, int toId, int weight) {
        List<Entry<Integer, Integer>> neighbors = adjList.get(fromId);
        if (neighbors == null) {
            neighbors = new ArrayList<>();
            adjList.put(fromId, neighbors);
        }
        neighbors.add(new Entry<>(toId, weight));
    }

    /** Helper to get all vertex IDs from edge list */
    private static <S, U> List<Entry<Integer, Integer>> getAllVerticesFromEdges(List<Edge<S, U>> edgeList) {
        UnorderedMap<Integer, Boolean> uniqueIds = new UnorderedMap<>();
        List<Entry<Integer, Integer>> allVertices = new ArrayList<>();

        for (Edge<S, U> edge : edgeList) {
            int id1 = edge.getVertex1().getId();
            int id2 = edge.getVertex2().getId();

            if (uniqueIds.get(id1) == null) {
                uniqueIds.put(id1, true);
                allVertices.add(new Entry<>(id1, 0)); // value doesn't matter
            }
            if (uniqueIds.get(id2) == null) {
                uniqueIds.put(id2, true);
                allVertices.add(new Entry<>(id2, 0));
            }
        }
        return allVertices;
    }

    /**
     * Compute the tunnel that if flooded will cause the maximal flooding of 
     * the network
     * @param tunnels A list of the tunnels to consider; see Tunnel.java
     * @return The identifier of the Tunnel that would cause maximal flooding.
     * Note that for Tunnel A to drain into some other tunnel B, the distance
     * from A to B must be strictly less than the radius of A plus an epsilon
     * allowance of 0.000001. 
     * Note also that all identifiers in tunnels are GUARANTEED to be in the
     * range [0, n-1] for n unique tunnels.
     */
    public static int totallyFlooded(List<Tunnel> tunnels) {
        return -1;
    }

    /**
     * Compute the number of sites that cannot be infiltrated from the given starting sites.
     * @param sites The list of unique site identifiers. A site identifier is GUARANTEED to be
     *              non-negative, starting from 0 and counting upwards to n-1.
     * @param rules The infiltration rule. The right-hand side of a rule is represented by a list
     *             of lists of site identifiers (as is done in the assignment specification). The
     *             left-hand side of a rule is given by the rule's index in the parameter `rules`
     *             (i.e. the rule whose left-hand side is 4 will be at index 4 in the parameter
     *              `rules` and can be accessed with `rules.get(4)`).
     * @param startingSites The list of site identifiers to begin your infiltration from.
     * @return The number of sites which cannot be infiltrated.
     */
    public static int susDomination(List<Integer> sites, List<List<List<Integer>>> rules,
                                    List<Integer> startingSites) {
        return -1;
    }
}
