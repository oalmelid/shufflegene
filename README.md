# Package shufflegene

This was made as a short project to refresh my java knowledge.
It contains an implementation of
the [Altschul & Erickson dinucleotide shuffle](https://doi.org/10.1093/oxfordjournals.molbev.a040370).
This algorithm shuffles a sequence of DNA nucleotides (i.e. ACGT) in such a way as to preserve the original frequency of
each dinucleotide (or 2-mer) present in the original input.

Since this was mainly a refresher, I've added a
readme, [javadoc](https://oalmelid.github.io/shufflegene/org/pvv/shufflegene/package-summary.html) and unit tests.
I might add more bells and whistles in the future for no good reason.

## Usage

See the javadoc for details, but the shuffle can be called with e.g.

```java
import org.pvv.shufflegene.DinucleotideShuffle

public class Main {
    public static void main(String[] args) {
        String input = "ACAGGATTCAGATTAGCCCGGAAATTTAAC";
        System.out.println(DinucleotideShuffle.shuffleSequence(input));
    }
}
```