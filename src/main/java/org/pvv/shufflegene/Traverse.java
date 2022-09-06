package org.pvv.shufflegene;

import org.javatuples.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Representation of a dinucleotide sequence as a graph with vertices spanning different bases.
 */
public class Traverse {
    private static final String ALPHABET = "ACGT";
    public char start;
    public char end;
    // start, end and path of original sequence
    public int length;
    private HashMap<Character, ArrayList<Character>> edgeMap;

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

    private static <T> ArrayList<T> copyList(List<T> input) {
        ArrayList<T> result = new ArrayList<>(input);
        Collections.copy(input, result);
        return result;
    }

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

    public void removeEdge(Pair<Character, Character> edge) {
        edgeMap.get(edge.getValue0()).remove(edge.getValue1());
    }

    public void appendEdge(Pair<Character, Character> edge) {
        edgeMap.get(edge.getValue0()).add(edge.getValue1());
    }

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

    public String toString() {
        StringBuilder result = new StringBuilder();
        HashMap<Character, ArrayList<Character>> edgeMapCopy = copyEdgeMap();

        char current = start;
        result.append(start);

        // Fixme: This only works if the traverse is in a valid ordering.
        // Otherwise it throws an out of bounds exception. Need to think of a better
        // way to handle this. Possibly with an unsafeToString and a safe toString with a wrap.
        try {
            for (int i = 1; i < this.length - 1; i++) {
                char next = edgeMapCopy.get(current).remove(0);
                result.append(next);
                current = next;
            }
        } catch (IndexOutOfBoundsException e) {
            return super.toString();
        }

        result.append(end);
        return result.toString();
    }

    public Set<Character> alphabet() {
        return this.edgeMap.keySet();
    }

    public Traverse deepCopy() {
        return new Traverse(this.start, this.end, this.length, copyEdgeMap());
    }

    public ArrayList<Character> getEdgeList(char startEdge) {
        return this.edgeMap.get(startEdge);
    }

    void shuffleEdgeLists() {
        for (ArrayList<Character> connections : edgeMap.values()) {
            Collections.shuffle(connections);
        }
    }
}