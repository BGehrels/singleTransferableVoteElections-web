package info.gehrels.voting.web.svg;

import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.singleTransferableVote.VoteDistribution;
import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import java.util.List;

public final class VoteDistributionSvg {
    private final VoteDistribution<GenderedCandidate> voteDistribution;
    private final BigFraction quorum;

    public VoteDistributionSvg(VoteDistribution<GenderedCandidate> voteDistribution, BigFraction quorum) {
        this.voteDistribution = voteDistribution;
        this.quorum = quorum;
    }

    public Element build(SVGDocument document, double baseX, double baseY, List<GenderedCandidate> electableCandidates) {
        Element svg = document.createElement("svg");
        svg.setAttribute("x", String.valueOf(baseX));
        svg.setAttribute("y", String.valueOf(baseY));
        int i = 0;
        for (GenderedCandidate electableCandidate : electableCandidates) {
            BigFraction numberOfVotes = voteDistribution.votesByCandidate.get(electableCandidate);
            svg.appendChild(new VotesForCandidate(numberOfVotes, quorum).build(document, (quorum.doubleValue() * 20 * 2 * i), baseY));
            i++;
        }

        svg.appendChild(new VotesForCandidate(voteDistribution.noVotes, quorum).build(document, (quorum.doubleValue() * 20 * 2 * i), baseY));

        return svg;
    }
}
