package org.pvv.shufflegene;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Representation of a nucleotide sequence as a graph with vertices spanning different bases.
 */
public class Traverse {
    public char start;
    public char end;
    // start, end and path of original sequence
    public int length;
    private HashMap<Character, ArrayList<Edge>> edgeMap;

    /**
     * Construct a traverse from a sequence of nucleotides
     *
     * @param sequence string of nucleotides
     */
    public Traverse(String sequence) {
        sequence = sequence.toUpperCase();
        setEdgeMap(sequence);
    }

    // Helper function to copy a list.
    private static <T> ArrayList<T> copyList(List<T> input) {
        ArrayList<T> result = new ArrayList<>(input);
        Collections.copy(input, result);
        return result;
    }

    /**
     * Set the edge map and associated variables based on an input string.
     * The edge map is a graph representation of the input sequence, it's stored
     * as a HashMap of lists graph edges, keyed by the starting point of the edge.
     * The sequence can be reconstructed by {@link #unsafeToString()}, which
     * pops the first edge starting with a given nucleotide sequentially until
     * there are no more edges left.
     *
     * @param sequence nucleotide sequence.
     */
    private void setEdgeMap(String sequence) {
        char[] nucleotides = sequence.toCharArray();
        this.start = nucleotides[0];
        this.end = nucleotides[nucleotides.length - 1];
        this.length = nucleotides.length;

        this.edgeMap = new HashMap<>();

        for (int i = 0; i < nucleotides.length - 1; i++) {
            ArrayList<Edge> edges = this.edgeMap.getOrDefault(nucleotides[i], new ArrayList<>());
            edges.add(new Edge(nucleotides[i], nucleotides[i + 1]));
            this.edgeMap.put(nucleotides[i], edges);
        }

    }

    /**
     * Remove the first occurrence of a given edge from the traverse.
     *
     * @param edge Pair of characters giving the start and end of the vertex.
     * @return whether the removal was successful or not.
     */
    public boolean removeEdge(Edge edge) {
        return edgeMap.get(edge.start).remove(edge);
    }

    /**
     * Append an edge to the end of the traverse.
     *
     * @param edge Pair of characters giving the start and end of the vertex.
     */
    public void appendEdge(Edge edge) {
        edgeMap.get(edge.start).add(edge);
    }

    /**
     * Perform a deep copy of edgeMap.
     *
     * @return a copy of edgeMap
     */
    private HashMap<Character, ArrayList<Edge>> copyEdgeMap() {
        return this.edgeMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> copyList(e.getValue()),
                        (v1, v2) -> {
                            throw new IllegalStateException("Duplicate key");
                        },
                        HashMap::new
                ));
    }

    /**
     * Convert the traverse to a string. This method is unsafe, since it only works when the traverse
     * is in a fully connected state.
     *
     * @return string with nucleotide sequence
     */
    public String unsafeToString() throws IllegalStateException {
        StringBuilder result = new StringBuilder();
        HashMap<Character, ArrayList<Edge>> edgeMapCopy = copyEdgeMap();

        char current = start;
        result.append(start);

        try {
            for (int i = 1; i < this.length; i++) {
                char next = edgeMapCopy.get(current).remove(0).end;
                result.append(next);
                current = next;
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException("Traverse is not fully connected and cannot be converted to a string.");
        }

        return result.toString();
    }

    /**
     * Get set of nucleotides that were present in the input
     *
     * @return Set of nucleotides represented as characters.
     */
    public Set<Character> alphabet() {
        return this.edgeMap.keySet();
    }

    /**
     * Get all edges that start at a given vertex
     *
     * @param startEdge which vertex to retrieve
     * @return List of all endpoints for vertices that started at startEdge
     */
    public ArrayList<Edge> getEdgeList(char startEdge) {
        return this.edgeMap.get(startEdge);
    }

    /**
     * Shuffle all vertices in edgeMap
     */
    void shuffleEdgeLists() {
        for (ArrayList<Edge> connections : edgeMap.values()) {
            Collections.shuffle(connections);
        }
    }
}