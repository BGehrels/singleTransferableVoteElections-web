package info.gehrels.voting.web.svg;

import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.singleTransferableVote.VoteDistribution;
import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class VoteDistributionSvg {
    private static final double TOTAL_WIDTH = 800.0;
    private final Map<Optional<GenderedCandidate>, VotesForCandidate> voteDistribution = new LinkedHashMap<>();
    private final int numberOfElectableCandidates;
    private final TextElement caption;

    public VoteDistributionSvg(VoteDistribution<GenderedCandidate> voteDistribution, List<GenderedCandidate> electableCandidates, BigFraction quorum, int indexOfVoteDistribution) {
        this.numberOfElectableCandidates = electableCandidates.size();
        for (GenderedCandidate electableCandidate : electableCandidates) {
            BigFraction numberOfVotes = voteDistribution.votesByCandidate.get(electableCandidate);
            this.voteDistribution.put(Optional.of(electableCandidate), new VotesForCandidate(numberOfVotes, quorum));
        }

        this.voteDistribution.put(Optional.<GenderedCandidate>empty(), new VotesForCandidate(voteDistribution.noVotes, quorum));
        this.caption = new TextElement().withText((indexOfVoteDistribution + 1) + ". Wahlgang");

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

    public void initializeSizing(double baseX, double baseY){
        double totalAmountOfSpacing = 0.05 * TOTAL_WIDTH;
        double spacingWidth = totalAmountOfSpacing / numberOfElectableCandidates;
        double perCandidateWidth = TOTAL_WIDTH / (numberOfElectableCandidates + 1); // +1 for the caption


        caption.withX(baseX).withY(baseY + voteDistribution.values().iterator().next().getHeight());
        baseX += perCandidateWidth;

        for (VotesForCandidate votesForCandidate : voteDistribution.values()) {
            votesForCandidate.initializeSizing(baseX, baseY, perCandidateWidth - spacingWidth);
            baseX += perCandidateWidth;
        }


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

    public Element build(SVGDocument document) {
        Element svg = document.createElement("g");
        svg.appendChild(caption.build(document));
        for (VotesForCandidate votesForCandidate : voteDistribution.values()) {
            svg.appendChild(votesForCandidate.build(document));
        }

        return svg;
    }

    public void registerOutgoingFlow(VoteFlow voteFlow) {
        voteDistribution.get(Optional.of(voteFlow.getOldPreferredCandidate())).registerOutgoingFlow(voteFlow);
    }

    public void registerIncomingFlow(VoteFlow voteFlow) {
        voteDistribution.get(voteFlow.getNewPreferredCandidate()).registerIncomingFlow(voteFlow);
    }

    public void initializeVoteFlowSizing() {
        voteDistribution.values().forEach(VotesForCandidate::initializeVoteFlowSizing);
    }
}
