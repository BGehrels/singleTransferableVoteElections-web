package info.gehrels.voting.web.svg;

import info.gehrels.voting.genderedElections.GenderedCandidate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

import java.util.ArrayList;
import java.util.List;

public final class VoteDistributionHeadlineRow extends VoteDistributionGridRow {
    public List<TextElement> columnHeaders = new ArrayList<>();

    public VoteDistributionHeadlineRow(List<GenderedCandidate> electableCandidates) {
        super(electableCandidates.size());

        columnHeaders.add(new TextElement().withText("")); // First column is empty
        for (GenderedCandidate electableCandidate : electableCandidates) {
            columnHeaders.add(new TextElement().withText(electableCandidate.getName()).withMiddleAnchor());
        }
        columnHeaders.add(new TextElement().withText("Nein"));
    }

    @Override
    public void initializeSizing(double baseX, double baseY) {
        super.initializeSizing(baseX, baseY);

        for (TextElement columnHeader : columnHeaders) {
            columnHeader.withX(baseX).withY(baseY);
            baseX += perCandidateWidth;
        }
    }

    public Node build(SVGDocument document) {
        Element g = document.createElement("g");
        columnHeaders.stream().map(t -> t.build(document)).forEach(g::appendChild);
        return g;
    }

    public double getHeight() {
        return 20.0;
    }
}
