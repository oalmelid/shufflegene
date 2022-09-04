package com.oalmelid.dinucleotide;

import org.javatuples.Triplet;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Shuffle a nucleotide sequence, retaining its dinucleotide frequencies
 */
public class DinucleotideShuffle {

    private static final String ALPHABET = "ACGT";
    private final String sequence;

    // start, end and path of original sequence
    private char start;
    private char end;
    private HashMap<Character, ArrayList<Character>> traversal;

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
    }

    /**
     * Check if a nucleotide sequence only contains the letters ACGT
     *
     * @param sequence the sequence to be verified, must be upper case before this is called
     * @return true if sequence is valid, false otherwise
     */
    public static boolean validSequence(String sequence) {

        String patternRegex = String.format("^[%s]+$", ALPHABET);
        System.out.println(patternRegex);
        return Pattern.matches(patternRegex, sequence);
    }

    private static <T> ArrayList<T> copyList(ArrayList<T> input) {
        ArrayList<T> result = new ArrayList<>(input);
        Collections.copy(input, result);
        return result;
    }

    private static void removeEdges(ArrayList<Triplet<Character, Character, Integer>> edges,
                                    HashMap<Character, ArrayList<Character>> traversal) {
        for (Triplet<Character, Character, Integer> edge : edges) {
            traversal.get(edge.getValue0()).remove(edge.getValue1());
        }
    }

    private static void appendEdges(ArrayList<Triplet<Character, Character, Integer>> edges,
                                    HashMap<Character, ArrayList<Character>> traversal) {
        for (Triplet<Character, Character, Integer> edge : edges) {
            traversal.get(edge.getValue0()).add(edge.getValue1());
        }
    }

    private boolean isConnected(ArrayList<Triplet<Character, Character, Integer>> vertices) {
        HashMap<Character, Boolean> connected = new HashMap<>();
        Set<Character> alphabet = this.traversal.keySet();

        for (char key : alphabet) {
            connected.put(key, false);
        }
        // The end element is connected to itself.
        connected.put(this.end, true);

        for (int i = 0; i < alphabet.size() - 1; i++) {
            for (Triplet<Character, Character, Integer> vertex : vertices) {
                if (connected.get(vertex.getValue1())) {
                    connected.put(vertex.getValue0(), true);
                }
            }
            if (!connected.values().contains(false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Construct a map of the sequence to allow for shuffling.
     */
    private void initShuffle() {

        char[] nucleotides = sequence.toCharArray();
        this.start = nucleotides[0];
        this.end = nucleotides[nucleotides.length - 1];

        HashMap<Character, ArrayList<Character>> traversal = new HashMap<>();

        for (int i = 0; i < nucleotides.length - 1; i++) {
            ArrayList<Character> edges = traversal.getOrDefault(nucleotides[i], new ArrayList<>());
            edges.add(nucleotides[i + 1]);
            traversal.put(nucleotides[i], edges);
        }

        this.traversal = traversal;
    }

    public String shuffleSequence() {
        if (traversal == null) {
            initShuffle();
        }
        HashMap<Character, ArrayList<Character>> shuffled = shuffleTraverse();
        return traverseEdges(shuffled);
    }

    private String traverseEdges(HashMap<Character, ArrayList<Character>> traversal) {
        StringBuilder result = new StringBuilder();

        char current = start;
        result.append(start);

        for (int i = 1; i < sequence.length() - 1; i++) {
            char next = traversal.get(current).remove(0);
            result.append(next);
            current = next;
        }

        result.append(end);
        return result.toString();
    }

    private ArrayList<Triplet<Character, Character, Integer>> pickEdges() {
        Random rand = new Random();
        //fixme: I'm having to init this because of the connected = false below.
        ArrayList<Triplet<Character, Character, Integer>> edges = new ArrayList<>();


        boolean connected = false;
        while (!connected) {
            edges = new ArrayList<>();
            for (char start : traversal.keySet()) {
                if (start != this.end) {
                    ArrayList<Character> jumps = traversal.get(start);
                    int index = rand.nextInt(jumps.size());
                    char end = jumps.get(index);
                    edges.add(new Triplet<>(start, end, index));
                }
            }
            connected = isConnected(edges);
        }
        return edges;
    }

    private HashMap<Character, ArrayList<Character>> shuffleTraverse() {
//        Collections.shuffle(result);

        HashMap<Character, ArrayList<Character>> traversal = (HashMap) this.traversal
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> copyList(e.getValue())));

        //fixme: this should be probably be a static function. See after refactoring.
        ArrayList<Triplet<Character, Character, Integer>> edges = pickEdges();
        removeEdges(edges, traversal);
        for (ArrayList<Character> connections : traversal.values()) {
            Collections.shuffle(connections);
        }
        appendEdges(edges, traversal);
        return traversal;
    }
}
