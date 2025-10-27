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
            minRadius = Math.max(minRadius, gap / 2.0);
        }

        minRadius = Math.max(minRadius,
                tunnelLength - sortedPosition.get(sortedPosition.size() - 1));

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
        if (edgeList == null || edgeList.isEmpty()) {
            return TopologyType.UNKNOWN;
        }

        // Step 1: Map each Vertex to a compact 0..n-1 id in O(1) expected time
        UnorderedMap<Integer, Integer> vertexToCompactId = new UnorderedMap<>();
        List<Integer> originalIds = new ArrayList<>();
        int nextId = 0;

        // First pass: collect all unique vertex IDs and assign compact IDs
        for (Edge<S, U> edge : edgeList) {
            int id1 = edge.getVertex1().getId();
            int id2 = edge.getVertex2().getId();

            if (vertexToCompactId.get(id1) == null) {
                vertexToCompactId.put(id1, nextId++);
                originalIds.add(id1);
            }
            if (vertexToCompactId.get(id2) == null) {
                vertexToCompactId.put(id2, nextId++);
                originalIds.add(id2);
            }
        }

        int n = nextId; // Total number of vertices

        // Step 2: Build UNDIRECTED adjacency using compact IDs
        List<Integer>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }

        for (Edge<S, U> edge : edgeList) {
            int compactId1 = vertexToCompactId.get(edge.getVertex1().getId());
            int compactId2 = vertexToCompactId.get(edge.getVertex2().getId());

            adj[compactId1].add(compactId2);
            adj[compactId2].add(compactId1);
        }

        // Step 3: Iterate components with an int[] stack (no Deque)
        boolean[] visited = new boolean[n];
        int treeComponents = 0;
        int graphComponents = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                // Explore component using array-based stack
                boolean hasCycle = exploreComponentWithStack(i, adj, visited);

                if (hasCycle) {
                    graphComponents++;
                } else {
                    treeComponents++;
                }
            }
        }

        int totalComponents = treeComponents + graphComponents;

        // Step 4: Map to your enum
        if (totalComponents == 1) {
            return (treeComponents == 1) ? TopologyType.CONNECTED_TREE :
                    TopologyType.CONNECTED_GRAPH;
        } else {
            if (treeComponents == totalComponents) {
                return TopologyType.FOREST;
            } else if (graphComponents == totalComponents) {
                return TopologyType.DISCONNECTED_GRAPH;
            } else {
                return TopologyType.HYBRID;
            }
        }
    }

    private static boolean exploreComponentWithStack(int start, List<Integer>[] adj,
                                                     boolean[] visited) {
        int n = adj.length;
        int[] stack = new int[n]; // Array-based stack
        int[] parent = new int[n]; // Track parent for cycle detection
        int stackSize = 0;
        boolean hasCycle = false;

        // Initialize
        for (int i = 0; i < n; i++) {
            parent[i] = -1;
        }

        stack[stackSize++] = start;
        visited[start] = true;
        parent[start] = -1; // Root has no parent

        while (stackSize > 0) {
            int current = stack[--stackSize]; // pop

            for (int neighbor : adj[current]) {
                if (!visited[neighbor]) {
                    // Push to stack
                    visited[neighbor] = true;
                    parent[neighbor] = current;
                    stack[stackSize++] = neighbor;
                } else if (neighbor != parent[current]) {
                    // Found a back edge - cycle detected!
                    hasCycle = true;
                }
            }
        }

        return hasCycle;
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
    private static void addEdgeToAdjList(UnorderedMap<Integer,
            List<Entry<Integer, Integer>>> adjList, int fromId, int toId, int weight) {
        List<Entry<Integer, Integer>> neighbors = adjList.get(fromId);
        if (neighbors == null) {
            neighbors = new ArrayList<>();
            adjList.put(fromId, neighbors);
        }
        neighbors.add(new Entry<>(toId, weight));
    }

    /** Helper to get all vertex IDs from edge list */
    private static <S, U> List<Entry<Integer,
            Integer>> getAllVerticesFromEdges(List<Edge<S, U>> edgeList) {
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
        if (tunnels == null || tunnels.isEmpty()) {
            return -1;
        }

        int n = tunnels.size();

        // Build adjacency matrix: which tunnels can flood which others
        boolean[][] canFlood = new boolean[n][n];

        // Initialize: every tunnel can flood itself
        for (int i = 0; i < n; i++) {
            canFlood[i][i] = true;
        }

        // Check all pairs of tunnels for flood relationships
        for (int i = 0; i < n; i++) {
            Tunnel tunnelI = tunnels.get(i);
            double x1 = tunnelI.getX();
            double y1 = tunnelI.getY();
            double r1 = tunnelI.getRadius();

            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue; // skip self (already true)
                }

                Tunnel tunnelJ = tunnels.get(j);
                double x2 = tunnelJ.getX();
                double y2 = tunnelJ.getY();

                // Calculate squared distance between tunnels
                double dx = x2 - x1;
                double dy = y2 - y1;
                double distanceSquared = dx * dx + dy * dy;

                // Check if tunnel I can flood tunnel J
                // tunnel I floods tunnel J if distance <= (r1 - epsilon)
                if (r1 > 0 && distanceSquared <= r1 * r1) {
                    canFlood[i][j] = true;
                }
            }
        }

        // Compute transitive closure using Floyd-Warshall
        // If A->B and B->C, then A->C
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (canFlood[i][k] && canFlood[k][j]) {
                        canFlood[i][j] = true;
                    }
                }
            }
        }

        // Find tunnel with maximum reachable count
        int maxFloodCount = -1;
        int bestTunnelId = -1;

        for (int i = 0; i < n; i++) {
            int floodCount = 0;
            for (int j = 0; j < n; j++) {
                if (canFlood[i][j]) {
                    floodCount++;
                }
            }

            // Update best tunnel
            if (floodCount > maxFloodCount || (floodCount == maxFloodCount
                    && tunnels.get(i).getId() < bestTunnelId)) {
                maxFloodCount = floodCount;
                bestTunnelId = tunnels.get(i).getId();
            }
        }

        return bestTunnelId;
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
