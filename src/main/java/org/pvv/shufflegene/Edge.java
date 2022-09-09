package org.pvv.shufflegene;

import java.util.Objects;

public class Edge {
    public char start;
    public char end;

    public Edge(char start, char end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return start == edge.start && end == edge.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
