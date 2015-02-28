package info.gehrels.voting.web.svg;

import org.junit.Test;

public class ElectionCalculationSvgDocumentBuilderTest {
    @Test
    public void buildsWithoutAnException() {
        new ElectionCalculationSvgDocumentBuilder(true).build();
    }


}