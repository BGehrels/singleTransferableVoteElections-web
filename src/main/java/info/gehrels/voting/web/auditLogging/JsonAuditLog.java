package info.gehrels.voting.web.auditLogging;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.AmbiguityResolver.AmbiguityResolverResult;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Election;
import info.gehrels.voting.Vote;
import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositionsListener;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import info.gehrels.voting.singleTransferableVote.STVElectionCalculationListener;
import info.gehrels.voting.singleTransferableVote.VoteDistribution;
import info.gehrels.voting.singleTransferableVote.VoteState;
import org.apache.commons.math3.fraction.BigFraction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map.Entry;

public final class JsonAuditLog  implements ElectionCalculationWithFemaleExclusivePositionsListener,
	STVElectionCalculationListener<GenderedCandidate> {
	private final JSONArray result  = new JSONArray();
	@Override
	public void reducedNonFemaleExclusiveSeats(long numberOfOpenFemaleExclusiveSeats,
	                                           long numberOfElectedFemaleExclusiveSeats,
	                                           long numberOfOpenNonFemaleExclusiveSeats,
	                                           long numberOfElectableNonFemaleExclusiveSeats) {
		JSONObject value = new JSONObject();
		value.put("type", "reducedNonFemaleExclusiveSeats")
			.put("numberOfOpenFemaleExclusiveSeats", numberOfOpenFemaleExclusiveSeats)
			.put("numberOfElectedFemaleExclusiveSeats", numberOfElectedFemaleExclusiveSeats)
			.put("numberOfOpenNonFemaleExclusiveSeats", numberOfOpenNonFemaleExclusiveSeats)
			.put("numberOfElectableNonFemaleExclusiveSeats", numberOfElectableNonFemaleExclusiveSeats);
		result.put(value);
	}

	@Override
	public void candidateNotQualified(GenderedCandidate candidate, NonQualificationReason reason) {
		JSONObject value = new JSONObject();
		value.put("type", "candidateNotQualified")
		    .put("candidate", asJSON(candidate))
			.put("reason", reason.toString());
		result.put(value);
	}

	@Override
	public void startElectionCalculation(GenderedElection election,
	                                     ImmutableCollection<Ballot<GenderedCandidate>> ballots) {
		JSONObject value = new JSONObject();
		value.put("type", "startElectionCalculation")
			.put("election", genderedElectionAsJSON(election))
		    .put("ballots", asJSONObject(ballots));
		result.put(value);
	}

	@Override
	public void startFemaleExclusiveElectionRun() {
		JSONObject value = new JSONObject();
		value.put("type", "startFemaleExclusiveElectionRun");
		result.put(value);
	}

	@Override
	public void startNonFemaleExclusiveElectionRun() {
		JSONObject value = new JSONObject();
		value.put("type", "startNonFemaleExclusiveElectionRun");
		result.put(value);
	}

	@Override
	public void numberOfElectedPositions(long numberOfElectedCandidates, long numberOfSeatsToElect) {
		JSONObject value = new JSONObject();
		value.put("type", "numberOfElectedPositions");
		value.put("numberOfElectedCandidates", numberOfElectedCandidates);
		value.put("numberOfSeatsToElect", numberOfSeatsToElect);
		result.put(value);
	}

	@Override
	public void electedCandidates(ImmutableSet<GenderedCandidate> electedCandidates) {
		JSONObject value = new JSONObject();
		value.put("type", "electedCandidates");
		value.put("electedCandidates", asJSON(electedCandidates));
		result.put(value);
	}

	@Override
	public void candidateDropped(VoteDistribution<GenderedCandidate> voteDistributionBeforeStriking,
	                             GenderedCandidate candidate) {
		JSONObject value = new JSONObject();
		value.put("type", "candidateDropped");
		value.put("voteDistributionBeforeStriking", asJSON(voteDistributionBeforeStriking));
		value.put("candidate", asJSON(candidate));
		result.put(value);
	}

	@Override
	public void voteWeightRedistributionCompleted(ImmutableCollection<VoteState<GenderedCandidate>> originalVoteStates,
	                                              ImmutableCollection<VoteState<GenderedCandidate>> newVoteStates,
	                                              VoteDistribution<GenderedCandidate> voteDistribution) {
		JSONObject value = new JSONObject();
		value.put("type", "voteWeightRedistributionCompleted");
		value.put("originalVoteStates", asJSON(originalVoteStates));
		value.put("newVoteStates", asJSON(newVoteStates));
		value.put("voteDistribution", asJSON(voteDistribution));
		result.put(value);
	}

	@Override
	public void delegatingToExternalAmbiguityResolution(ImmutableSet<GenderedCandidate> bestCandidates) {
		JSONObject value = new JSONObject();
		value.put("type", "delegatingToExternalAmbiguityResolution");
		value.put("bestCandidates", asJSON(bestCandidates));
		result.put(value);
	}

	@Override
	public void externallyResolvedAmbiguity(AmbiguityResolverResult<GenderedCandidate> ambiguityResolverResult) {
		JSONObject value = new JSONObject();
		value.put("type", "externallyResolvedAmbiguity");
		value.put("ambiguityResolverResult", asJSON(ambiguityResolverResult));
		result.put(value);
	}

	@Override
	public void candidateIsElected(GenderedCandidate winner, BigFraction numberOfVotes, BigFraction quorum) {
		JSONObject value = new JSONObject();
		value.put("type", "candidateIsElected");
		value.put("winner", asJSON(winner));
		value.put("numberOfVotes", asJSON(numberOfVotes));
		value.put("quorum", asJSON(quorum));
		result.put(value);
	}

	@Override
	public void nobodyReachedTheQuorumYet(BigFraction quorum) {
		JSONObject value = new JSONObject();
		value.put("type", "nobodyReachedTheQuorumYet");
		value.put("quorum", asJSON(quorum));
		result.put(value);
	}

	@Override
	public void noCandidatesAreLeft() {
		JSONObject value = new JSONObject();
		value.put("type", "noCandidatesAreLeft");
		result.put(value);
	}

	@Override
	public void calculationStarted(Election<GenderedCandidate> election,
	                               VoteDistribution<GenderedCandidate> voteDistribution) {
		JSONObject value = new JSONObject();
		value.put("type", "calculationStarted");
		value.put("election", electionAsJSON(election));
		value.put("voteDistribution", asJSON(voteDistribution));
		result.put(value);
	}

	@Override
	public void quorumHasBeenCalculated(long numberOfValidBallots, long numberOfSeats, BigFraction quorum) {
		JSONObject value = new JSONObject();
		value.put("type", "quorumHasBeenCalculated");
		value.put("numberOfValidBallots", numberOfValidBallots);
		value.put("numberOfSeats", numberOfSeats);
		value.put("quorum", asJSON(quorum));
		result.put(value);
	}

	@Override
	public void redistributingExcessiveFractionOfVoteWeight(GenderedCandidate winner,
	                                                        BigFraction excessiveFractionOfVoteWeight) {
		JSONObject value = new JSONObject();
		value.put("type", "redistributingExcessiveFractionOfVoteWeight");
		value.put("winner", asJSON(winner));
		value.put("excessiveFractionOfVoteWeight", asJSON(excessiveFractionOfVoteWeight));
		result.put(value);
	}

	private JSONObject asJSON(GenderedCandidate candidate) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", candidate.getName());
		jsonObject.put("female", candidate.isFemale());
		return jsonObject;
	}

	private JSONArray asJSONObject(ImmutableCollection<Ballot<GenderedCandidate>> ballots) {
		JSONArray jsonArray = new JSONArray();
		for (Ballot<GenderedCandidate> ballot : ballots) {
			jsonArray.put(asJSON(ballot));
		}

		return jsonArray;
	}

	private JSONObject asJSON(Ballot<GenderedCandidate> ballot) {
		JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", ballot.id);
				jsonObject.put("votesByElections", asJSONArray(ballot.votesByElections));
				return jsonObject;
	}

	private JSONArray asJSONArray(ImmutableMap<Election<GenderedCandidate>, Vote<GenderedCandidate>> votesByElections) {
		JSONArray jsonArray = new JSONArray();
		for (Entry<Election<GenderedCandidate>, Vote<GenderedCandidate>> electionVoteEntry : votesByElections
			.entrySet()) {
			jsonArray.put(asJSON(electionVoteEntry.getKey(), electionVoteEntry.getValue()));
		}
		return jsonArray;
	}

	private JSONObject asJSON(Election<GenderedCandidate> election, Vote<GenderedCandidate> vote) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("election", electionAsJSON(election));
		jsonObject.put("vote", asJSON(vote));
		return jsonObject;
	}

	private JSONObject asJSON(Vote<GenderedCandidate> vote) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("election", electionAsJSON(vote.getElection()));
		jsonObject.put("rankedCandidates", asJSON(vote.getRankedCandidates()));
        jsonObject.put("no", vote.isNo());
        jsonObject.put("valid", vote.isValid());
		return jsonObject;
	}

	private JSONObject electionAsJSON(Election<GenderedCandidate> election) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("officeName", election.getOfficeName());
		jsonObject.put("candidates", asJSON(election.getCandidates()));
		return jsonObject;
	}

	private JSONObject genderedElectionAsJSON(GenderedElection election) {
		JSONObject jsonObject = electionAsJSON(election);
		jsonObject.put("numberOfFemaleExclusivePositions", election.getNumberOfFemaleExclusivePositions());
		jsonObject.put("numberOfNotFemaleExclusivePositions", election.getNumberOfNotFemaleExclusivePositions());
		return jsonObject;
	}

	private JSONArray asJSON(ImmutableSet<GenderedCandidate> candidates) {
		JSONArray jsonArray = new JSONArray();
		for (GenderedCandidate candidate : candidates) {
			jsonArray.put(asJSON(candidate));
		}

		return jsonArray;
	}


	private JSONObject asJSON(VoteDistribution<GenderedCandidate> voteDistributionBeforeStriking) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("votesByCandidate", asJSON(voteDistributionBeforeStriking.votesByCandidate));
		jsonObject.put("invalidVotes", asJSON(voteDistributionBeforeStriking.invalidVotes));
		jsonObject.put("noVotes", asJSON(voteDistributionBeforeStriking.noVotes));
		return jsonObject;
	}

	private JSONObject asJSON(ImmutableMap<GenderedCandidate, BigFraction> votesByCandidates) {
		JSONObject jsonObject = new JSONObject();
		for (Entry<GenderedCandidate, BigFraction> votesForCandidate : votesByCandidates.entrySet()) {
			jsonObject.put(votesForCandidate.getKey().getName(), asJSON(votesForCandidate.getValue()));
		}
		return jsonObject;
	}

	private JSONObject asJSON(BigFraction invalidVotes) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("numerator", invalidVotes.getNumerator().toString());
		jsonObject.put("denominator", invalidVotes.getDenominator().toString());
		jsonObject.put("approximateValue", invalidVotes.doubleValue());
		return jsonObject;
	}

	private JSONArray asJSON(ImmutableCollection<VoteState<GenderedCandidate>> voteStates) {
		JSONArray jsonArray = new JSONArray();
		for (VoteState<GenderedCandidate> voteState : voteStates) {
			jsonArray.put(asJSON(voteState));
		}
		return jsonArray;
	}

	private JSONObject asJSON(VoteState<GenderedCandidate> voteState) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ballotId", voteState.getBallotId());
		jsonObject.put("preferredCandidate", voteState.getPreferredCandidate().map(this::asJSON).orElse(null));
		jsonObject.put("voteWeight", asJSON(voteState.getVoteWeight()));
		jsonObject.put("invalid", voteState.isInvalid());
		jsonObject.put("noVote", voteState.isNoVote());
		return jsonObject;
	}

	private JSONObject asJSON(AmbiguityResolverResult<GenderedCandidate> ambiguityResolverResult) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("auditLog", ambiguityResolverResult.auditLog);
		jsonObject.put("chosenCandidate", asJSON(ambiguityResolverResult.chosenCandidate));
		return jsonObject;
	}

	public JSONArray getResult() {
		return result;
	}
}
