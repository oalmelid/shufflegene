package org.pvv.shufflegene;

import org.javatuples.Pair;

import java.util.*;
import java.util.regex.Pattern;


/**
 * Shuffle a nucleotide sequence, retaining its dinucleotide (2-mer) frequencies.
 * This is based on the algorithm by Altschul and Erickson, as described in
 * <a href="https://doi.org/10.1093/oxfordjournals.molbev.a040370">https://doi.org/10.1093/oxfordjournals.molbev.a040370</a>
 * <p>
 * It does the following:
 * <ul>
 * <li> Decomposes the sequence into a graph with nodes at the nucleotides ACGT and the sequence as a series of directed
 * edges
 * <li> Randomly selects a directed path which visits all nodes exactly ones, is connected, and ends at the final letter
 * in the original sequence. Remove this path from the graph
 * <li> Shuffles the ordering of all other edges in the graph on a per-node basis
 * <li> Appends the previously removed path to the end of the vertices leaving each node
 * </ul>
 */
public class DinucleotideShuffle {

    private static final String ALPHABET = "ACGT";
    private final String sequence;
    private final Traverse traverse;

    // ToDo: convert all of this into just a static shuffle method.
    // We end up copying the traverse each time we shuffle, so not time is saved on instantiating and keeping a traverse.

    /**
     * Constructor for a shuffle. Checks that the sequence contains only valid characters and converts to uppercase.
     *
     * @param sequence the nucleotide sequence, most consist only of the letters ACGT, will be converted to uppercase on input.
     */
    public DinucleotideShuffle(String sequence) throws InvalidInputException {
        sequence = sequence.toUpperCase();
        if (!validSequence(sequence)) {
            throw new InvalidInputException(String.format("Input string %s does not conform with alphabet %s\n", sequence, ALPHABET));
        }
        this.sequence = sequence;
        this.traverse = new Traverse(sequence);
    }

    /**
     * Check if a nucleotide sequence only contains the letters ACGT
     *
     * @param sequence the sequence to be verified, must be upper case before this is called
     * @return true if sequence is valid, false otherwise
     */
    public static boolean validSequence(String sequence) {
        //ToDo: should only really accept strings of length at least 2.
        String patternRegex = String.format("^[%s]+$", ALPHABET);
        return Pattern.matches(patternRegex, sequence);
    }

    /**
     * Perform a dinucleotide shuffle.
     *
     * @return a string containing a shuffled sequence with the same dinucleotide frequency as the input.
     */
    public String shuffleSequence() {
        Traverse newTraversal = traverse.deepCopy();
        ArrayList<Pair<Character, Character>> edges = pickEdges();

        for (Pair<Character, Character> edge : edges) {
            newTraversal.removeEdge(edge);
        }

        newTraversal.shuffleEdgeLists();

        for (Pair<Character, Character> edge : edges) {
            newTraversal.appendEdge(edge);
        }

        return newTraversal.toString();
    }

    /**
     * Pick a set of edges such that
     * - All nodes in the graph (ACGT) are visited exactly once
     * - The edges all connect
     * - The edges end at the final character in the original sequence
     *
     * @return a set of Pairs representing directed edges.
     */
    private ArrayList<Pair<Character, Character>> pickEdges() {
        Random rand = new Random();
        ArrayList<Pair<Character, Character>> edges = null;

        while (edges == null || (!isConnectedToEnd(edges))) {
            edges = new ArrayList<>();
            for (char start : traverse.alphabet()) {
                if (start != traverse.end) {
                    ArrayList<Character> jumps = traverse.getEdgeList(start);
                    char end = jumps.get(rand.nextInt(jumps.size()));
                    edges.add(new Pair<>(start, end));
                }
            }
        }
        return edges;
    }

    /**
     * Check if a set of edges connect to the final character in the sequence.
     *
     * @param edges list of edges to be examined.
     * @return true if connected, false otherwise.
     */
    private boolean isConnectedToEnd(List<Pair<Character, Character>> edges) {
        HashMap<Character, Boolean> connected = new HashMap<>();
        Set<Character> alphabet = traverse.alphabet();

        for (char key : alphabet) {
            connected.put(key, false);
        }
        // The end element is connected to itself.
        connected.put(traverse.end, true);

        for (int i = 0; i < edges.size(); i++) {
            for (Pair<Character, Character> vertex : edges) {
                if (connected.get(vertex.getValue1())) {
                    connected.put(vertex.getValue0(), true);
                }
            }
            if (!connected.containsValue(false)) {
                return true;
            }
        }
        return false;
    }
}
