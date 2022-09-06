package org.pvv.shufflegene;

import org.javatuples.Pair;

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
    private HashMap<Character, ArrayList<Character>> edgeMap;

    /**
     * Construct a traverse from a sequence of nucleotides
     *
     * @param sequence string of nucleotides
     */
    public Traverse(String sequence) {
        sequence = sequence.toUpperCase();
        setEdgeMap(sequence);
    }

    public Traverse(char start, char end, int length, HashMap<Character, ArrayList<Character>> edgeMap) {
        this.start = start;
        this.end = end;
        this.length = length;
        this.edgeMap = edgeMap;
    }

    // Helper function to copy a list.
    private static <T> ArrayList<T> copyList(List<T> input) {
        ArrayList<T> result = new ArrayList<>(input);
        Collections.copy(input, result);
        return result;
    }

    /**
     * Set the edge map and associated variables based on an input string.
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
            ArrayList<Character> edges = this.edgeMap.getOrDefault(nucleotides[i], new ArrayList<>());
            edges.add(nucleotides[i + 1]);
            this.edgeMap.put(nucleotides[i], edges);
        }

    }

    /**
     * Remove the first occurence of a given edge from the traverse.
     *
     * @param edge Pair of characters giving the start and end of the vertex.
     */
    public boolean removeEdge(Pair<Character, Character> edge) {
        return edgeMap.get(edge.getValue0()).remove(edge.getValue1());
    }

    /**
     * Append an edge to the end of the traverse.
     *
     * @param edge Pair of characters giving the start and end of the vertex.
     */
    public void appendEdge(Pair<Character, Character> edge) {
        edgeMap.get(edge.getValue0()).add(edge.getValue1());
    }

    /**
     * Perform a deep copy of edgeMap, used by {@link #deepCopy()}
     *
     * @return a copy of edgeMap
     */
    private HashMap<Character, ArrayList<Character>> copyEdgeMap() {
        HashMap<Character, ArrayList<Character>> traversal = this.edgeMap
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
        return traversal;
    }

    /**
     * Convert the traverse to a string.
     *
     * @return string with nucleotide sequence
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        HashMap<Character, ArrayList<Character>> edgeMapCopy = copyEdgeMap();

        char current = start;
        result.append(start);

        // Fixme: This only works if the traverse is in a valid ordering.
        // Otherwise it throws an out of bounds exception. Need to think of a better
        // way to handle this. Possibly with an unsafeToString and a safe toString with a wrap.
        try {
            for (int i = 1; i < this.length; i++) {
                char next = edgeMapCopy.get(current).remove(0);
                result.append(next);
                current = next;
            }
        } catch (IndexOutOfBoundsException e) {
            return super.toString();
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
     * Perform a deep copy of the traverse.
     *
     * @return a copy of this instance which can be mutated without modifying the original.
     */
    public Traverse deepCopy() {
        return new Traverse(this.start, this.end, this.length, copyEdgeMap());
    }

    /**
     * Get all edges that start at a given vertex
     *
     * @param startEdge which vertex to retrieve
     * @return List of all endpoints for vertices that started at startEdge
     */
    public ArrayList<Character> getEdgeList(char startEdge) {
        return this.edgeMap.get(startEdge);
    }

    /**
     * Shuffle all vertices in edgeMap
     */
    void shuffleEdgeLists() {
        for (ArrayList<Character> connections : edgeMap.values()) {
            Collections.shuffle(connections);
        }
    }
}