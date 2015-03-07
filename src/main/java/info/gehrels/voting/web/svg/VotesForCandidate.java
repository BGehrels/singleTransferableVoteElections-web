package info.gehrels.voting.web.svg;

import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

class VotesForCandidate {
    private final BigFraction numberOfVotes;
    private final BigFraction quorum;

    VotesForCandidate(BigFraction numberOfVotes, BigFraction quorum) {
        this.numberOfVotes = numberOfVotes;
        this.quorum = quorum;
    }

    public Element build(SVGDocument document, double baseX, double baseY) {
        Element svg = document.createElement("svg");
        svg.setAttribute("x", String.valueOf(baseX));
        svg.setAttribute("y", String.valueOf(baseY));
        if (numberOfVotes != null) {
            Element numberOfVotesBar = document.createElement("rect");
            numberOfVotesBar.setAttribute("x", "1");
            numberOfVotesBar.setAttribute("y", "1");
            numberOfVotesBar.setAttribute("width", String.valueOf(numberOfVotes.doubleValue() * 20));
            numberOfVotesBar.setAttribute("height", String.valueOf(18));
            numberOfVotesBar.setAttribute("style", "fill:red; stroke:none");
            svg.appendChild(numberOfVotesBar);
            Element quorumBorder = document.createElement("rect");
            quorumBorder.setAttribute("x", "1");
            quorumBorder.setAttribute("y", "1");
            quorumBorder.setAttribute("width", String.valueOf(quorum.doubleValue() * 20));
            quorumBorder.setAttribute("height", String.valueOf(18));
            quorumBorder.setAttribute("fill-opacity", "0");
            quorumBorder.setAttribute("stroke", "black");
            svg.appendChild(quorumBorder);
        }
        return svg;

    }
}
