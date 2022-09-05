package com.oalmelid.dinucleotide;

import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * Shuffle a nucleotide sequence, retaining its dinucleotide frequencies
 */
public class DinucleotideShuffle {

    private static final String ALPHABET = "ACGT";
    private final String sequence;
    private final Traverse traverse;

    /**
     * Constructor for a sequence. Checks that the sequence contains only valid characters and converts to uppercase.
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
        String patternRegex = String.format("^[%s]+$", ALPHABET);
        return Pattern.matches(patternRegex, sequence);
    }

    public String shuffleSequence() {
        Traverse shuffled = shuffleTraverse();
        return shuffled.toString();
    }


    private ArrayList<Pair<Character, Character>> pickEdges() {
        Random rand = new Random();
        //fixme: I'm having to init this because of the connected = false below.
        ArrayList<Pair<Character, Character>> edges = null;

        while (edges == null || (!isConnected(edges))) {
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

    private boolean isConnected(ArrayList<Pair<Character, Character>> vertices) {
        HashMap<Character, Boolean> connected = new HashMap<>();
        Set<Character> alphabet = traverse.alphabet();

        for (char key : alphabet) {
            connected.put(key, false);
        }
        // The end element is connected to itself.
        connected.put(traverse.end, true);

        for (int i = 0; i < alphabet.size() - 1; i++) {
            for (Pair<Character, Character> vertex : vertices) {
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

    private Traverse shuffleTraverse() {
        Traverse newTraversal = traverse.deepCopy();
        ArrayList<Pair<Character, Character>> edges = pickEdges();

        for (Pair<Character, Character> edge : edges) {
            newTraversal.removeEdge(edge);
        }

        newTraversal.shuffleEdgeLists();

        for (Pair<Character, Character> edge : edges) {
            newTraversal.appendEdge(edge);
        }

        return newTraversal;
    }
}
