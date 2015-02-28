package info.gehrels.voting.web.svg;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.AmbiguityResolver.AmbiguityResolverResult;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Election;
import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositionsListener;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import info.gehrels.voting.singleTransferableVote.STVElectionCalculationListener;
import info.gehrels.voting.singleTransferableVote.VoteDistribution;
import info.gehrels.voting.singleTransferableVote.VoteState;
import org.apache.commons.math3.fraction.BigFraction;

public final class SvgCreatingAuditLogListener implements STVElectionCalculationListener<GenderedCandidate>, ElectionCalculationWithFemaleExclusivePositionsListener {
    private GenderedElection election;
    private ElectionCalculationSvgDocumentBuilder femaleExclusiveRun;
    private ElectionCalculationSvgDocumentBuilder nonFemaleExclusiveRun;

    @Override
    public void startElectionCalculation(GenderedElection election, ImmutableCollection<Ballot<GenderedCandidate>> ballots) {
        this.election = election;
    }

    @Override
    public void reducedNonFemaleExclusiveSeats(long numberOfOpenFemaleExclusiveSeats, long numberOfElectedFemaleExclusiveSeats, long numberOfOpenNonFemaleExclusiveSeats, long numberOfElectableNonFemaleExclusiveSeats) {

    }

    @Override
    public void candidateNotQualified(GenderedCandidate candidate, NonQualificationReason reason) {

    }

    @Override
    public void startFemaleExclusiveElectionRun() {
        this.femaleExclusiveRun = new ElectionCalculationSvgDocumentBuilder(true);
    }

    @Override
    public void startNonFemaleExclusiveElectionRun() {
        this.nonFemaleExclusiveRun = new ElectionCalculationSvgDocumentBuilder(false);

    }

    @Override
    public void numberOfElectedPositions(long numberOfElectedCandidates, long numberOfSeatsToElect) {

    }

    @Override
    public void electedCandidates(ImmutableSet<GenderedCandidate> electedCandidates) {

    }

    @Override
    public void candidateDropped(VoteDistribution<GenderedCandidate> voteDistributionBeforeStriking, GenderedCandidate candidate) {

    }

    @Override
    public void voteWeightRedistributionCompleted(ImmutableCollection<VoteState<GenderedCandidate>> originalVoteStates, ImmutableCollection<VoteState<GenderedCandidate>> newVoteStates, VoteDistribution<GenderedCandidate> voteDistribution) {

    }

    @Override
    public void delegatingToExternalAmbiguityResolution(ImmutableSet<GenderedCandidate> bestCandidates) {

    }

    @Override
    public void externalyResolvedAmbiguity(AmbiguityResolverResult<GenderedCandidate> ambiguityResolverResult) {

    }

    @Override
    public void candidateIsElected(GenderedCandidate winner, BigFraction numberOfVotes, BigFraction quorum) {

    }

    @Override
    public void nobodyReachedTheQuorumYet(BigFraction quorum) {

    }

    @Override
    public void noCandidatesAreLeft() {

    }

    @Override
    public void calculationStarted(Election<GenderedCandidate> election, VoteDistribution<GenderedCandidate> voteDistribution) {

    }

    @Override
    public void quorumHasBeenCalculated(long numberOfValidBallots, long numberOfSeats, BigFraction quorum) {

    }

    @Override
    public void redistributingExcessiveFractionOfVoteWeight(GenderedCandidate winner, BigFraction excessiveFractionOfVoteWeight) {

    }

    public String getFemaleExclusiveRunSvg() {
        return femaleExclusiveRun.build();
    }

    public String getNonFemaleExclusiveRunSvg() {
        return nonFemaleExclusiveRun.build();
    }
}
