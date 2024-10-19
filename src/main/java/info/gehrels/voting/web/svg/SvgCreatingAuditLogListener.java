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
    private ElectionCalculationSvgDocumentBuilder femaleExclusiveRun;
    private ElectionCalculationSvgDocumentBuilder nonFemaleExclusiveRun;

    boolean weAreStillInFemaleExclusiveRun = true;

    @Override
    public void startElectionCalculation(GenderedElection election, ImmutableCollection<Ballot<GenderedCandidate>> ballots) {
        femaleExclusiveRun = new ElectionCalculationSvgDocumentBuilder(election, true);
        nonFemaleExclusiveRun = new ElectionCalculationSvgDocumentBuilder(election, false);
    }

    @Override
    public void startFemaleExclusiveElectionRun() {
        femaleExclusiveRun.started();
    }

    @Override
    public void candidateNotQualified(GenderedCandidate candidate, NonQualificationReason reason) {
        if (weAreStillInFemaleExclusiveRun) {
            femaleExclusiveRun.candidateNotQualified(candidate);
        } else {
            nonFemaleExclusiveRun.candidateNotQualified(candidate);
        }
    }

    @Override
    public void reducedNotFemaleExclusiveSeats(long numberOfOpenFemaleExclusiveSeats, long numberOfElectedFemaleExclusiveSeats, long numberOfOpenNotFemaleExclusiveSeats, long numberOfElectableNotFemaleExclusiveSeats) {
    }

    @Override
    public void startNotFemaleExclusiveElectionRun() {
        weAreStillInFemaleExclusiveRun = false;
        nonFemaleExclusiveRun.started();
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
        getBuilder().voteWeightRedistributionCompleted(originalVoteStates, newVoteStates, voteDistribution);
    }

    @Override
    public void delegatingToExternalAmbiguityResolution(ImmutableSet<GenderedCandidate> bestCandidates) {
    }

    @Override
    public void externallyResolvedAmbiguity(AmbiguityResolverResult<GenderedCandidate> ambiguityResolverResult) {
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
        getBuilder().initialVoteDistribution(voteDistribution);
    }

    @Override
    public void quorumHasBeenCalculated(long numberOfValidBallots, long numberOfSeats, BigFraction quorum) {
        getBuilder().setQuorum(numberOfValidBallots, numberOfSeats, quorum);
    }

    @Override
    public void redistributingExcessiveFractionOfVoteWeight(GenderedCandidate winner, BigFraction excessiveFractionOfVoteWeight) {

    }

    public String getFemaleExclusiveRunSvg() {
        return femaleExclusiveRun.build();
    }

    public String getNotFemaleExclusiveRunSvg() {
        return nonFemaleExclusiveRun.build();
    }

    private ElectionCalculationSvgDocumentBuilder getBuilder() {
        if (weAreStillInFemaleExclusiveRun) {
            return femaleExclusiveRun;
        } else {
            return nonFemaleExclusiveRun;
        }
    }
}
