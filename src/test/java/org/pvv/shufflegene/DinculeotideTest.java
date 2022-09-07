package org.pvv.shufflegene;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class DinculeotideTest {

    /**
     * Count how many occurences there are of each dinucleotide (2-mer) in the input
     *
     * @param input string of nucleotides
     * @return HashMap with a count for each set of 2 consecutive nucleotides in the input
     */
    private static HashMap<String, Integer> countDinucleotides(String input) {
        HashMap<String, Integer> counter = new HashMap<>();
        for (int i = 0; i < input.length() - 1; i++) {
            String dinucleotide = input.substring(i, i + 1);
            int count = counter.getOrDefault(dinucleotide, 0);
            counter.put(dinucleotide, ++count);
        }
        return counter;
    }

    @Test
    public void testValidSequence() {
        Assertions.assertTrue(DinucleotideShuffle.validSequence("AAAACCCCGGGGTTTT"));
        Assertions.assertFalse(DinucleotideShuffle.validSequence("UAAACCCCGGGGTTTT"));
        // Empty string or one-character string is invalid
        Assertions.assertFalse(DinucleotideShuffle.validSequence(""));
        Assertions.assertFalse(DinucleotideShuffle.validSequence("A"));
        // Two-character strings are valid.
        Assertions.assertTrue(DinucleotideShuffle.validSequence("AA"));
    }

    @Test
    public void testShuffle() throws InvalidInputException, IllegalStateException {
        String input = "ACAGGATTCAGATTAGCCCGGAAATTTAAC";
        for (int i = 0; i < 10; i++) {
            String output = DinucleotideShuffle.shuffleSequence(input);
            Assertions.assertEquals(output.charAt(0), 'A');
            Assertions.assertEquals(output.charAt(input.length() - 1), input.charAt(input.length() - 1));
            Assertions.assertEquals(output.length(), input.length());
            Assertions.assertEquals(countDinucleotides(input), countDinucleotides(output));
            Assertions.assertNotEquals(output, input);
        }
    }

    @Test
    public void shuffleShortSequence() throws InvalidInputException, IllegalStateException {
        String input = "AC";
        String output = DinucleotideShuffle.shuffleSequence(input);
        //There's only one way to preserve the dinucleotide frequency, which is to return the same sequence.
        Assertions.assertEquals(output, input);
    }
}
