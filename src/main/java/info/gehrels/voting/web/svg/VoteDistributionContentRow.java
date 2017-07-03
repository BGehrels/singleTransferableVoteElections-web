package info.gehrels.voting.web.svg;

import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.singleTransferableVote.VoteDistribution;
import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class VoteDistributionContentRow extends VoteDistributionGridRow {
    private final Map<Optional<GenderedCandidate>, VotesForCandidate> voteDistribution = new LinkedHashMap<>();
    private final TextElement caption;

    public VoteDistributionContentRow(VoteDistribution<GenderedCandidate> voteDistribution, List<GenderedCandidate> electableCandidates, BigFraction quorum, int indexOfVoteDistribution) {
        super(electableCandidates.size());
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

    @Override
    public void initializeSizing(){
        double x = getBaseX();
        caption.withX(x).withY(getBaseY() + voteDistribution.values().iterator().next().getHeight());

        x += getPerCandidateColumnWidth();

        for (VotesForCandidate votesForCandidate : voteDistribution.values()) {
            votesForCandidate.initializeSizing(x, getBaseY(), getPerCandidateUsableWidth());
            caption.withY(votesForCandidate.getTextBaseY()).withFontSize(votesForCandidate.getTextFontSize());
            x += getPerCandidateColumnWidth();
        }
    }

    public VoteDistributionContentRow setGlobalMaxNumberOfVotes(BigFraction globalMaxNumberOfVotes) {
        for (VotesForCandidate votesForCandidate : voteDistribution.values()) {
            votesForCandidate.setGlobalMaxNumberOfVotes(globalMaxNumberOfVotes);
        }
        return this;
    }

    public double getHeight() {
        return voteDistribution.values().iterator().next().getHeight();
    }

    public Element build(Document document) {
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

    public double getMaxOutgoingVoteFlowWidth() {
        double maxOutgoingVoteFlowWidth = 0;
        for (VotesForCandidate votesForCandidate : voteDistribution.values()) {
            maxOutgoingVoteFlowWidth = Math.max(maxOutgoingVoteFlowWidth, votesForCandidate.getMaxOutgoingVoteFlowWidth());
        }
        return maxOutgoingVoteFlowWidth;
    }
}
