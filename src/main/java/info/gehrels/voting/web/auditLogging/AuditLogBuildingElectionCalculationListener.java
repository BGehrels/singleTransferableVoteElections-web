/*
 * Copyright © 2014 Benjamin Gehrels
 *
 * This file is part of The Single Transferable Vote Elections Library.
 *
 * The Single Transferable Vote Elections Web Interface is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * The Single Transferable Vote Elections Web Interface is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with The Single Transferable Vote
 * Elections Web Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package info.gehrels.voting.web.auditLogging;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.AmbiguityResolver.AmbiguityResolverResult;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Candidate;
import info.gehrels.voting.Election;
import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositionsListener;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import info.gehrels.voting.singleTransferableVote.STVElectionCalculationListener;
import info.gehrels.voting.singleTransferableVote.VoteDistribution;
import info.gehrels.voting.singleTransferableVote.VoteState;
import info.gehrels.voting.web.auditLogging.AuditLog.Entry;
import org.apache.commons.math3.fraction.BigFraction;

public final class AuditLogBuildingElectionCalculationListener
	implements STVElectionCalculationListener<GenderedCandidate>,
	ElectionCalculationWithFemaleExclusivePositionsListener {
	private ImmutableList.Builder<Entry> entries = ImmutableList.builder();

	// TODO: In zwei interfaces spalten? Eigenes Interface fürs replay?
	// TODO: Per "close" immutable machen?

	@Override
	public void numberOfElectedPositions(final long numberOfElectedCandidates, final long numberOfSeatsToElect) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener
					.numberOfElectedPositions(numberOfElectedCandidates, numberOfSeatsToElect);
			}
		});
	}

	@Override
	public void electedCandidates(final ImmutableSet<GenderedCandidate> electedCandidates) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener.electedCandidates(electedCandidates);
			}
		});
	}

	@Override
	public void redistributingExcessiveFractionOfVoteWeight(final Candidate winner,
	                                                        final BigFraction excessiveFractionOfVoteWeight) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener
					.redistributingExcessiveFractionOfVoteWeight(winner, excessiveFractionOfVoteWeight);
			}
		});
	}

	@Override
	public void delegatingToExternalAmbiguityResolution(final ImmutableSet<GenderedCandidate> bestCandidates) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener.delegatingToExternalAmbiguityResolution(bestCandidates);
			}
		});
	}

	@Override
	public void externalyResolvedAmbiguity(final AmbiguityResolverResult<GenderedCandidate> ambiguityResolverResult) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener.externalyResolvedAmbiguity(ambiguityResolverResult);
			}
		});
	}

	@Override
	public void candidateIsElected(final GenderedCandidate winner, final BigFraction numberOfVotes, final BigFraction quorum) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener.candidateIsElected(winner, numberOfVotes, quorum);
			}
		});
	}

	@Override
	public void nobodyReachedTheQuorumYet(final BigFraction quorum) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener.nobodyReachedTheQuorumYet(quorum);
			}
		});
	}

	@Override
	public void noCandidatesAreLeft() {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener.noCandidatesAreLeft();
			}
		});
	}

	@Override
	public void quorumHasBeenCalculated(final long numberOfValidBallots, final long numberOfSeats, final BigFraction quorum) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener.quorumHasBeenCalculated(numberOfValidBallots, numberOfSeats, quorum);
			}
		});
	}

	@Override
	public void calculationStarted(final Election<GenderedCandidate> election, final VoteDistribution<GenderedCandidate> voteDistribution) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener.calculationStarted(election, voteDistribution);
			}
		});
	}

	@Override
	public void voteWeightRedistributionCompleted(final ImmutableCollection<VoteState<GenderedCandidate>> originalVoteStates,
	                                              final ImmutableCollection<VoteState<GenderedCandidate>> newVoteStates,
	                                              final VoteDistribution<GenderedCandidate> voteDistribution) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener
					.voteWeightRedistributionCompleted(originalVoteStates, newVoteStates, voteDistribution);
			}
		});
	}

	@Override
	public void candidateDropped(final VoteDistribution<GenderedCandidate> voteDistributionBeforeStriking, final GenderedCandidate candidate) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener.candidateDropped(voteDistributionBeforeStriking, candidate);
			}
		});
	}

	@Override
	public void reducedNonFemaleExclusiveSeats(final long numberOfOpenFemaleExclusiveSeats,
	                                           final long numberOfElectedFemaleExclusiveSeats,
	                                           final long numberOfOpenNonFemaleExclusiveSeats,
	                                           final long numberOfElectableNonFemaleExclusiveSeats) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(T stvElectionCalculationListener) {
				stvElectionCalculationListener.reducedNonFemaleExclusiveSeats(numberOfOpenFemaleExclusiveSeats,
				                                                              numberOfElectedFemaleExclusiveSeats,
				                                                              numberOfOpenNonFemaleExclusiveSeats,
				                                                              numberOfElectableNonFemaleExclusiveSeats);
			}
		});
	}

	@Override
	public void candidateNotQualified(final GenderedCandidate candidate, final NonQualificationReason reason) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(
				T listener) {
				listener.candidateNotQualified(candidate, reason);
			}
		});
	}

	@Override
	public void startElectionCalculation(final GenderedElection election,
	                                     final ImmutableCollection<Ballot<GenderedCandidate>> ballots) {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(
				T listener) {
				listener.startElectionCalculation(election, ballots);
			}
		});
	}

	@Override
	public void startFemaleExclusiveElectionRun() {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(
				T listener) {
				listener.startFemaleExclusiveElectionRun();
			}
		});
	}

	@Override
	public void startNonFemaleExclusiveElectionRun() {
		entries.add(new Entry() {
			@Override
			public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(
				T listener) {
				listener.startNonFemaleExclusiveElectionRun();
			}
		});
	}

	public AuditLog buildAndReset() {
		Builder<Entry> currentEntries = entries;
		entries = ImmutableList.builder();
		return new AuditLog(currentEntries.build());
	}

	public AuditLog build() {
		return new AuditLog(entries.build());
	}

}
