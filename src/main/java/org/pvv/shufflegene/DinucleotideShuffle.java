package org.pvv.shufflegene;

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

    /**
     * Perform a dinucleotide shuffle.
     *
     * @param sequence the sequence to be shuffled.
     * @return a string containing a shuffled sequence with the same dinucleotide frequency as the input.
     */
    public static String shuffleSequence(String sequence) throws InvalidInputException, IllegalStateException {
        sequence = sequence.toUpperCase();
        if (!validSequence(sequence)) {
            throw new InvalidInputException(String.format("Input string %s does not conform with alphabet %s\n", sequence, ALPHABET));
        }

        Traverse traverse = new Traverse(sequence);
        ArrayList<Edge> edges = pickEdges(traverse);

        for (Edge edge : edges) {
            if (!traverse.removeEdge(edge)) {
                throw new IllegalStateException("Attempted to remove an edge that does not exist. This should never happen");
            }
        }

        traverse.shuffleEdgeLists();

        for (Edge edge : edges) {
            traverse.appendEdge(edge);
        }

        return traverse.unsafeToString();
    }

    /**
     * Check if a nucleotide sequence only contains the letters contained in {@link #ALPHABET} and is at least 2 characters long.
     *
     * @param sequence the sequence to be verified, must be upper case before this is called
     * @return true if sequence is valid, false otherwise
     */
    public static boolean validSequence(String sequence) {
        String patternRegex = String.format("^[%s]{2,}$", ALPHABET);
        return Pattern.matches(patternRegex, sequence);
    }

    /**
     * Pick a set of edges such that
     * - All nodes in the graph (ACGT) are visited exactly once
     * - The edges all connect
     * - The edges end at the final character in the original sequence
     *
     * @return a set of Pairs representing directed edges.
     */
    private static ArrayList<Edge> pickEdges(Traverse traverse) {
        Random rand = new Random();
        ArrayList<Edge> edges = null;

        while (edges == null || (!isConnectedToEnd(edges, traverse))) {
            edges = new ArrayList<>();
            for (char start : traverse.alphabet()) {
                if (start != traverse.end) {
                    ArrayList<Edge> jumps = traverse.getEdgeList(start);
                    edges.add(jumps.get(rand.nextInt(jumps.size())));
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
    private static boolean isConnectedToEnd(List<Edge> edges, Traverse traverse) {
        HashMap<Character, Boolean> connected = new HashMap<>();
        Set<Character> alphabet = traverse.alphabet();

        for (char key : alphabet) {
            connected.put(key, false);
        }
        // The end element is connected to itself.
        connected.put(traverse.end, true);

        // Loop through the edges, and set the start to true if end is true.
        for (int i = 0; i < edges.size(); i++) {
            for (Edge vertex : edges) {
                if (connected.get(vertex.end)) {
                    connected.put(vertex.start, true);
                }
            }
            if (!connected.containsValue(false)) {
                return true;
            }
        }
        return false;
    }
}
