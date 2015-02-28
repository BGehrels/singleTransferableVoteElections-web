package info.gehrels.voting.web.svg;

import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import org.junit.Test;

public class ElectionCalculationSvgDocumentBuilderTest {
    @Test
    public void buildsWithoutAnException() {
        new ElectionCalculationSvgDocumentBuilder(new GenderedElection("An office", 1, 2, ImmutableSet.of(new GenderedCandidate("Test candidate", true))), true).build();
    }


}