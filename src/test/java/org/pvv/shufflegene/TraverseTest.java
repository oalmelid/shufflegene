package org.pvv.shufflegene;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TraverseTest {

    @Test
    void testUnsafeToStringThrowsIllegalStateException() {
        // A traverse with e.g. a missing edge or similar can throw an IllegalStateException if it's converted to a string.
        Traverse traverse = new Traverse("ACGT");
        Assertions.assertTrue(traverse.removeEdge(new Edge('A', 'C')));
        Assertions.assertThrows(IllegalStateException.class, traverse::unsafeToString);
    }
}
