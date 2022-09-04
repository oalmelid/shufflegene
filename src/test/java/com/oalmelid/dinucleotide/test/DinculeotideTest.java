package com.oalmelid.dinucleotide.test;

import com.oalmelid.dinucleotide.DinucleotideShuffle;
import com.oalmelid.dinucleotide.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DinculeotideTest {

    @Test
    void testValidSequence() throws InvalidInputException {
        String input = "AAAACCCCGGGGTTTT";
        assertTrue(DinucleotideShuffle.validSequence(input));
    }

    @Test
    void testShuffle() throws InvalidInputException {
        String input = "AAAACCCCGGGGTTTT";
        DinucleotideShuffle shuffle = new DinucleotideShuffle(input);
        for (int i = 0; i < 10; i++) {
            String output = shuffle.shuffleSequence();
            assert (output.charAt(0) == 'A');
            assert (output.charAt(input.length() - 1) == 'T');
            assert (output.length() == input.length());
            assert (output != input);
        }
    }
}
