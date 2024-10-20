package info.gehrels.voting.web.auditLogging;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.AmbiguityResolver.AmbiguityResolverResult;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Candidate;
import info.gehrels.voting.Election;
import info.gehrels.voting.Vote;
import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositionsListener;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import info.gehrels.voting.singleTransferableVote.STVElectionCalculationListener;
import info.gehrels.voting.singleTransferableVote.VoteDistribution;
import info.gehrels.voting.singleTransferableVote.VoteState;
import org.apache.commons.math3.fraction.BigFraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import static java.lang.String.format;

public final class StringAuditLog implements ElectionCalculationWithFemaleExclusivePositionsListener,
	STVElectionCalculationListener<GenderedCandidate> {
	private final StringBuilder builder = new StringBuilder();

	@Override
	public void reducedNotFemaleExclusiveSeats(long numberOfOpenFemaleExclusiveSeats,
											   long numberOfElectedFemaleExclusiveSeats,
											   long numberOfOpenNotFemaleExclusiveSeats,
											   long numberOfElectableNotFemaleExclusiveSeats) {
		formatLine(
			"Es wurden nur %d von %d Frauenplätzen besetzt. Daher können auch nur %d von %d offenen Plätzen gewählt werden.",
			numberOfElectedFemaleExclusiveSeats, numberOfOpenFemaleExclusiveSeats,
				numberOfElectableNotFemaleExclusiveSeats, numberOfOpenNotFemaleExclusiveSeats);
	}

	@Override
	public void candidateNotQualified(GenderedCandidate candidate, NonQualificationReason reason) {
		formatLine("%s kann in diesem Wahlgang nicht antreten, Grund: %s", candidate.name,
		           getReasonAsGermanString(reason));
	}

	@Override
	public void startElectionCalculation(GenderedElection election,
	                                     ImmutableCollection<Ballot<GenderedCandidate>> ballots) {
		formatLine("Starte die Wahlberechnungen für %s.", election);
		formatLine("Abgegebene Stimmen:");
		List<Long> ballotIdsWithoutAVote = new ArrayList<>();
		long numberOfCastVotes = 0;
		for (Ballot<GenderedCandidate> ballot : ballots) {
			java.util.Optional<Vote<GenderedCandidate>> vote = ballot.getVote(election);
			if (vote.isPresent()) {
				formatLine("Stimmzettel %d: %s", ballot.id, vote.get());
				numberOfCastVotes++;
			} else {
				ballotIdsWithoutAVote.add(ballot.id);
			}
		}
		formatLine("Insgesamt wurden für diese Wahl %d Stimmen abgegeben.", numberOfCastVotes);

		if (!ballotIdsWithoutAVote.isEmpty()) {
			formatLine("Keine Stimmabgabe auf den Stimmzetteln %s.", ballotIdsWithoutAVote);
		}
	}

	@Override
	public void startFemaleExclusiveElectionRun() {
		formatLine("Starte die Berechnung der Frauenplätze.");
	}

	@Override
	public void startNotFemaleExclusiveElectionRun() {
		formatLine("Starte die Berechnung der offenen Plätze.");
	}

	@Override
	public void numberOfElectedPositions(long numberOfElectedCandidates, long numberOfSeatsToElect) {
		if (numberOfElectedCandidates < numberOfSeatsToElect) {
			formatLine("Es sind erst %d von %d Plätzen gewählt.", numberOfElectedCandidates, numberOfSeatsToElect);
		} else {
			formatLine("Alle %d Plätze sind gewählt.", numberOfSeatsToElect);
		}
	}

	@Override
	public void electedCandidates(ImmutableSet<GenderedCandidate> electedCandidates) {
		if (!electedCandidates.isEmpty()) {
			formatLine("======================================");
			formatLine("Gewählt sind:");
			for (GenderedCandidate electedCandidate : electedCandidates) {
				formatLine("\t%s", electedCandidate.name);
			}
		}
	}

	@Override
	public void redistributingExcessiveFractionOfVoteWeight(GenderedCandidate winner,
	                                                        BigFraction excessiveFractionOfVoteWeight) {
		formatLine("Es werden %f%% des Stimmgewichts von %s weiterverteilt.",
		           excessiveFractionOfVoteWeight.percentageValue(), winner.getName());
	}

	@Override
	public void delegatingToExternalAmbiguityResolution(ImmutableSet<GenderedCandidate> bestCandidates) {
		formatLine("Mehrere stimmengleiche Kandidierende: %s. Delegiere an externes Auswahlverfahren.", bestCandidates);
	}

	/*
	 * § 19 Abs. 4 WahlO-GJ:
	 * Sofern Zufallsauswahlen gemäß § 18 Nr. 7, 8 erforderlich sind, entscheidet das von der Tagungsleitung zu ziehende
	 * Los; die Ziehung und die Eingabe des Ergebnisses in den Computer müssen mitgliederöffentlich erfolgen.
	 */
	@Override
	public void externallyResolvedAmbiguity(AmbiguityResolverResult<GenderedCandidate> ambiguityResolverResult) {
		formatLine("externes Auswahlverfahren ergab: %s. (%s)", ambiguityResolverResult.chosenCandidate.name,
		           ambiguityResolverResult.auditLog);
	}

	/*
	 * § 19 Abs. 3 S. 2 ff WahlO-GJ:
	 * Dieses Protokoll muss mindestens enthalten:
	 * [...]
	 * 2. Die Wahl von KandidatInnen gemäß § 18 Nr. 5
	 * [...]
	 * 4. Die Anzahl der Stimmen von KandidatInnen zum Zeitpunkt ihrer Wahl oder ihres Ausscheidens
	 * [...]
	 */
	@Override
	public void candidateIsElected(GenderedCandidate winner, BigFraction numberOfVotes, BigFraction quorum) {
		formatLine("%s hat mit %f Stimmen das Quorum von %f Stimmen erreicht und ist gewählt.", winner.name,
		           numberOfVotes.doubleValue(), quorum.doubleValue());
	}

	@Override
	public void nobodyReachedTheQuorumYet(BigFraction quorum) {
		formatLine("Niemand von den verbleibenden Kandidierenden hat das Quorum von %f Stimmen erreicht:",
		           quorum.doubleValue());
	}

	@Override
	public void noCandidatesAreLeft() {
		formatLine("Es gibt keine hoffnungsvollen Kandidierenden mehr. Der Wahlgang wird daher beendet.");
	}

	/*
	 * § 19 Abs. 3 S. 2 ff WahlO-GJ:
	 * Dieses Protokoll muss mindestens enthalten:
	 * 1. Das Quorum gemäß § 18 Nr. 2
	 * [...]
	 */
	@Override
	public void quorumHasBeenCalculated(long numberOfValidBallots, long numberOfSeats, BigFraction quorum) {
		formatLine("Das Quorum liegt bei %f (%d Sitze, %d gültige Stimmen).", quorum.doubleValue(), numberOfSeats,
		           numberOfValidBallots);
	}

	@Override
	public void calculationStarted(Election<GenderedCandidate> election,
	                               VoteDistribution<GenderedCandidate> voteDistribution) {
		formatLine("Ausgangsstimmverteilung:");
		dumpVoteDistribution(voteDistribution);
	}

	@Override
	public void voteWeightRedistributionCompleted(ImmutableCollection<VoteState<GenderedCandidate>> originalVoteStates,
	                                              ImmutableCollection<VoteState<GenderedCandidate>> newVoteStates,
	                                              VoteDistribution<GenderedCandidate> voteDistribution) {
		for (VoteStatePair newAndOldState : getMatchingPairs(originalVoteStates, newVoteStates)) {
			VoteState<GenderedCandidate> oldState = newAndOldState.oldState;
			VoteState<GenderedCandidate> newState = newAndOldState.newState;
			boolean voteWeightChanged = !oldState.getVoteWeight().equals(newState.getVoteWeight());
			boolean preferredCandidateChanged = !oldState.getPreferredCandidate()
				.equals(newState.getPreferredCandidate());
			long ballotId = oldState.getBallotId();
			if (preferredCandidateChanged && voteWeightChanged) {
				formatLine(
					"Das Stimmgewicht von Stimmzettel %d verringert sich von %f%% auf %f%% und wird von %s auf %s übertragen.",
					ballotId, oldState.getVoteWeight().percentageValue(),
					newState.getVoteWeight().percentageValue(), oldState.getPreferredCandidate().get().getName(),
					getNameOrNo(newState.getPreferredCandidate()));
			} else if (preferredCandidateChanged) {
				formatLine("Stimmzettel %d überträgt sein bestehendes Stimmgewicht (%f%%) von %s auf %s", ballotId,
				           oldState.getVoteWeight().percentageValue(), oldState.getPreferredCandidate().get().getName(),
				           getNameOrNo(newState.getPreferredCandidate())
				);
			}
		}


		formatLine("Neue Stimmverteilung:");

		dumpVoteDistribution(voteDistribution);

	}

	private String getNameOrNo(Optional<GenderedCandidate> candidate) {
		return candidate.isPresent() ? candidate.get().name : "Nein";
	}

	private Iterable<VoteStatePair> getMatchingPairs(
		ImmutableCollection<VoteState<GenderedCandidate>> originalVoteStates,
		ImmutableCollection<VoteState<GenderedCandidate>> newVoteStates) {
		List<VoteStatePair> matchingPairs = new ArrayList<>();

		for (VoteState<GenderedCandidate> originalVoteState : originalVoteStates) {
			for (VoteState<GenderedCandidate> newVoteState : newVoteStates) {
				if (originalVoteState.getBallotId() == newVoteState.getBallotId()) {
					matchingPairs.add(new VoteStatePair(originalVoteState, newVoteState));
					break;
				}
			}
		}

		return matchingPairs;
	}

	/*
	 * § 19 Abs. 3 S. 2 ff WahlO-GJ:
	 * Dieses Protokoll muss mindestens enthalten:
	 * [...]
	 * 3. Das Ausscheiden von KandidatInnen gemäß § 18 Nr. 8
	 * 4. Die Anzahl der Stimmen von KandidatInnen zum Zeitpunkt ihrer Wahl oder ihres Ausscheidens
	 * [...]
	 */
	@Override
	public void candidateDropped(VoteDistribution<GenderedCandidate> voteDistributionBeforeStriking,
	                             GenderedCandidate candidate) {
		formatLine("%s hat mit %f Stimmen das schlechteste Ergebnis und scheidet aus.", candidate.name,
		           voteDistributionBeforeStriking.votesByCandidate.get(candidate).doubleValue());
	}

	private <CANDIDATE_TYPE extends Candidate> void dumpVoteDistribution(
		VoteDistribution<CANDIDATE_TYPE> voteDistribution) {
		for (Entry<CANDIDATE_TYPE, BigFraction> votesForCandidate : voteDistribution.votesByCandidate.entrySet()) {
			formatLine("\t%s: %f Stimmen", votesForCandidate.getKey().name,
			           votesForCandidate.getValue().doubleValue());
		}
		formatLine("\tNein: %f Stimmen", voteDistribution.noVotes.doubleValue());
		formatLine("\tUngültig: %f Stimmen", voteDistribution.invalidVotes.doubleValue());
	}

	private static final class VoteStatePair {
		public final VoteState<GenderedCandidate> oldState;
		public final VoteState<GenderedCandidate> newState;

		VoteStatePair(VoteState<GenderedCandidate> oldState, VoteState<GenderedCandidate> newState) {
			this.oldState = oldState;
			this.newState = newState;
		}

	}


	private String getReasonAsGermanString(NonQualificationReason reason) {
		switch (reason) {
			case NOT_FEMALE:
				return "Nicht weiblich";
			case ALREADY_ELECTED:
				return "Bereits gewählt";
		}

		throw new IllegalArgumentException("Unbekannter Grund: " + reason);
	}

	private StringBuilder formatLine(String formatString, Object... objects) {
		return builder.append(format(formatString, objects)).append('\n');
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
