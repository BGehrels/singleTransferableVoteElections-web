package info.gehrels.voting.web.svg;

import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.singleTransferableVote.VoteDistribution;
import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import java.util.*;

public final class VoteDistributionSvg {
    private final Map<Optional<GenderedCandidate>, VotesForCandidate> voteDistribution = new HashMap<>();

    public VoteDistributionSvg(VoteDistribution<GenderedCandidate> voteDistribution, List<GenderedCandidate> electableCandidates, BigFraction quorum) {
        for (GenderedCandidate electableCandidate : electableCandidates) {
            BigFraction numberOfVotes = voteDistribution.votesByCandidate.get(electableCandidate);
            this.voteDistribution.put(Optional.of(electableCandidate), new VotesForCandidate(numberOfVotes, quorum));
        }

        this.voteDistribution.put(Optional.<GenderedCandidate>empty(), new VotesForCandidate(voteDistribution.noVotes, quorum));
    }

    public BigFraction getMaxNumberOfLocalVotes() {
        BigFraction result = BigFraction.ZERO;
        for (VotesForCandidate votesForCandidate : voteDistribution.values()) {
            if (result.compareTo(votesForCandidate.getNumberOfVotes()) < 0) {
                result = votesForCandidate.getNumberOfVotes();
            }
        }

        return result;
    }

    public VoteDistributionSvg setGlobalMaxNumberOfVotes(BigFraction globalMaxNumberOfVotes) {
        for (VotesForCandidate votesForCandidate : voteDistribution.values()) {
            votesForCandidate.setGlobalMaxNumberOfVotes(globalMaxNumberOfVotes);
        }
        return this;
    }

    public double getHeight() {
        return voteDistribution.values().iterator().next().getHeight();
    }

    public Element build(SVGDocument document, double baseX, double baseY) {
        Element svg = document.createElement("svg");
        svg.setAttribute("x", String.valueOf(baseX));
        svg.setAttribute("y", String.valueOf(baseY));
        double x = 0;
        for (VotesForCandidate votesForCandidate : voteDistribution.values()) {
            svg.appendChild(votesForCandidate.build(document, x, baseY));
            x += votesForCandidate.getWidth() * 1.25;
        }

        return svg;
    }

    public void registerOutgoingFlow(VoteFlow voteFlow) {
        this.voteDistribution.get(Optional.of(voteFlow.getOldPreferredCandidate())).registerOutgoingFlow(voteFlow);
    }

    public void registerIncomingFlow(VoteFlow voteFlow) {
        this.voteDistribution.get(voteFlow.getNewPreferredCandidate()).registerIncomingFlow(voteFlow);
    }

    public void initializeVoteFlows() {
        for (VotesForCandidate votesForCandidate : voteDistribution.values()) {
            votesForCandidate.initializeVoteFlows(0, 0); // TODO: Fill with real values
        }
    }
}
