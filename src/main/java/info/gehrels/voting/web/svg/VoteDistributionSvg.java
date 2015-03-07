package info.gehrels.voting.web.svg;

import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.singleTransferableVote.VoteDistribution;
import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import java.util.ArrayList;
import java.util.List;

public final class VoteDistributionSvg {
    private final List<VotesForCandidate> voteDistribution = new ArrayList<>();

    public VoteDistributionSvg(VoteDistribution<GenderedCandidate> voteDistribution, List<GenderedCandidate> electableCandidates, BigFraction quorum) {
        for (GenderedCandidate electableCandidate : electableCandidates) {
            BigFraction numberOfVotes = voteDistribution.votesByCandidate.get(electableCandidate);
            this.voteDistribution.add(new VotesForCandidate(numberOfVotes, quorum));
        }

        this.voteDistribution.add(new VotesForCandidate(voteDistribution.noVotes, quorum));
    }

    public BigFraction getMaxNumberOfLocalVotes() {
        BigFraction result = BigFraction.ZERO;
        for (VotesForCandidate votesForCandidate : voteDistribution) {
            if (result.compareTo(votesForCandidate.getNumberOfVotes()) < 0) {
                result = votesForCandidate.getNumberOfVotes();
            }
        }

        return result;
    }

    public VoteDistributionSvg setGlobalMaxNumberOfVotes(BigFraction globalMaxNumberOfVotes) {
        for (VotesForCandidate votesForCandidate : voteDistribution) {
            votesForCandidate.setGlobalMaxNumberOfVotes(globalMaxNumberOfVotes);
        }
        return this;
    }

    public double getHeight() {
        return voteDistribution.get(0).getHeight();
    }

    public Element build(SVGDocument document, double baseX, double baseY) {
        Element svg = document.createElement("svg");
        svg.setAttribute("x", String.valueOf(baseX));
        svg.setAttribute("y", String.valueOf(baseY));
        double x = 0;
        for (VotesForCandidate votesForCandidate : voteDistribution) {
            svg.appendChild(votesForCandidate.build(document, x, baseY));
            x += votesForCandidate.getWidth() * 1.25;
        }

        return svg;
    }
}
