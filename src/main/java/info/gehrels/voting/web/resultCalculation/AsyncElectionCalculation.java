/*
 * Copyright © 2014 Benjamin Gehrels
 *
 * This file is part of The Single Transferable Vote Elections Web Interface.
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
package info.gehrels.voting.web.resultCalculation;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.AmbiguityResolver;
import info.gehrels.voting.AmbiguityResolver.AmbiguityResolverResult;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.NotMoreThanTheAllowedNumberOfCandidatesCanReachItQuorum;
import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositions;
import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositions.Result;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import info.gehrels.voting.singleTransferableVote.STVElectionCalculationFactory;
import info.gehrels.voting.web.auditLogging.AuditLog;
import info.gehrels.voting.web.auditLogging.AuditLogBuildingElectionCalculationListener;
import info.gehrels.voting.web.auditLogging.StringAuditLog;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.math3.fraction.BigFraction.ONE;

public final class AsyncElectionCalculation implements Runnable {
	private final List<GenderedElection> elections;
	private final ImmutableCollection<Ballot<GenderedCandidate>> ballots;
	private final ElectionCalculationWithFemaleExclusivePositions electionCalculation;
	private final AuditLogBuildingElectionCalculationListener auditLogBuilder = new AuditLogBuildingElectionCalculationListener();
	private final ImmutableList.Builder<ElectionCalculationResultBean> resultModelBuilder = ImmutableList.builder();
	private final DateTime startDateTime = DateTime.now();

	private ImmutableList<ElectionCalculationResultBean> result;
	private ElectionCalculationState state = ElectionCalculationState.NOT_YET_STARTED;
	private Optional<AmbiguityResolverResult<GenderedCandidate>> ambiguityResolutionResult;
	private Optional<AmbiguityResolutionTask> ambiguityResolutionTask;

	public AsyncElectionCalculation(List<GenderedElection> elections,
	                                ImmutableCollection<Ballot<GenderedCandidate>> ballots) {
		this.elections = elections;
		this.ballots = ballots;
		this.electionCalculation = createGenderedElectionCalculation();
	}

	@Override
	public void run() {
		ElectionCalculationState currentState = getState();
		if (currentState != ElectionCalculationState.NOT_YET_STARTED) {
			throw new IllegalStateException(
				"Election calculations may only be started once. Current State: " + currentState);
		}

		setState(ElectionCalculationState.RUNNING);
		for (GenderedElection election : elections) {
			Result electionResult = electionCalculation
				.calculateElectionResult(election, ImmutableList.copyOf(ballots));
			AuditLog auditLog = auditLogBuilder.buildAndReset();
			resultModelBuilder.add(new ElectionCalculationResultBean(startDateTime, election, electionResult, auditLog));
			setResult(resultModelBuilder.build());
		}

		setState(ElectionCalculationState.FINISHED);
	}

	private synchronized void setResult(ImmutableList<ElectionCalculationResultBean> result) {
		this.result = result;
	}

	private synchronized ElectionCalculationState getState() {
		return state;
	}

	private synchronized void setState(ElectionCalculationState state) {
		this.state = state;
		notifyAll();
	}

	public synchronized void setAmbiguityResolutionResult(AmbiguityResolverResult<GenderedCandidate> ambiguityResolverResult) {
		this.ambiguityResolutionResult = Optional.ofNullable(ambiguityResolverResult);
		if (ambiguityResolutionResult.isPresent()) {
			setState(ElectionCalculationState.AMBIGUITY_RESOLVED);
		}
	}

	private ElectionCalculationWithFemaleExclusivePositions createGenderedElectionCalculation() {
		return new ElectionCalculationWithFemaleExclusivePositions(
			new STVElectionCalculationFactory<>(
				createQuorumCalculation(),
				auditLogBuilder,
				new BlockUntilFetchedAsyncHttpUserInputAmbiguityResolver(this)),
			auditLogBuilder);
	}

	public synchronized Snapshot getSnapshot() {
		return new Snapshot(startDateTime, state, result, ambiguityResolutionTask);
	}

	private NotMoreThanTheAllowedNumberOfCandidatesCanReachItQuorum createQuorumCalculation() {
		return new NotMoreThanTheAllowedNumberOfCandidatesCanReachItQuorum(ONE);
	}

	public DateTime getStartDateTime() {
		return startDateTime;
	}

	public synchronized Optional<AmbiguityResolverResult<GenderedCandidate>> getAmbiguityResolutionResult() {
		return ambiguityResolutionResult;
	}

	public synchronized Optional<AmbiguityResolutionTask> getAmbiguityResolutionTask() {
		return ambiguityResolutionTask;
	}

	// TODO: Verfahren nach Satzung implementieren
	// § 18 Satz 2 Nr. 7 sub (II) WahlO-GJ:
	// Haben mehrere KandidatInnen einen Überschuss, so wird zunächst der größte Überschuss übertragen. Haben zwei oder
	// mehr KandidatInnen einen gleich großen Überschuss, so wird der Überschuss jener / jenes dieser KandidatInnen
	// zuerst übertragen, die / der die meisten Stimmen hatte, als sich die	Stimmenzahl der	betreffenden KandidatInnen
	// zuletzt unterschied; hatten zwei oder mehr dieser KandidatInnen zu jedem Zeitpunkt jeweils die gleiche
	// Stimmenzahl, so wird durch eine Zufallsauswahl entschieden, welcher Überschuss als erstes übertragen wird.
	// TODO: Eventuell eine synchrone Übertragung bei Gleichstand in die Satzung schreiben?
	// TODO: Auch für die Stimmgewichtsübertragung bei Patt beim Streichen von Kandidierenden einfacheres Verfahren finden:
	// § 18 Satz 2 Nr. 8 sub (i) WahlO-GJ
	// Falls zwei oder mehr KandidatInnen gleichermaßen die wenigsten Stimmen haben, so wird jeneR dieser KandidatInnen
	// aus dem Rennen genommen, die / der die wenigsten Stimmen hatte, als sich die Stimmenzahl der betreffenden
	// KandidatInnen zuletzt unterschied; hatten zwei oder mehr dieser KandidatInnen zu jedem Zeitpunkt jeweils die
	// gleiche Stimmenzahl, so wird durch eine Zufallsauswahl entschieden, welcheR dieser KandidatInnen aus dem Rennen
	// ausscheidet
		/*
		 * § 19 Abs. 4 WahlO-GJ:
		 * Sofern Zufallsauswahlen gemäß § 18 Nr. 7, 8 erforderlich sind, entscheidet das von der Tagungsleitung zu ziehende
		 * Los; die Ziehung und die Eingabe des Ergebnisses in den Computer müssen mitgliederöffentlich erfolgen.
		 */
	private static final class BlockUntilFetchedAsyncHttpUserInputAmbiguityResolver implements AmbiguityResolver<GenderedCandidate> {
		private final AsyncElectionCalculation asyncElectionCalculation;

		private BlockUntilFetchedAsyncHttpUserInputAmbiguityResolver(AsyncElectionCalculation asyncElectionCalculation) {
			this.asyncElectionCalculation = asyncElectionCalculation;
		}

		@Override
		public AmbiguityResolverResult<GenderedCandidate> chooseOneOfMany(
			ImmutableSet<GenderedCandidate> bestCandidates) {

			synchronized (asyncElectionCalculation) {
				asyncElectionCalculation.setAmbiguityResulutionTask(new AmbiguityResolutionTask(bestCandidates, asyncElectionCalculation.getCurrentLog()));
				while (asyncElectionCalculation.getState() != ElectionCalculationState.AMBIGUITY_RESOLVED) {
					try {
						asyncElectionCalculation.wait();
					} catch (InterruptedException e) {
						// TODO: Ganz schlechte Idee...
					}
				}
			}
			return asyncElectionCalculation.getAmbiguityResolutionResult().get();
		}

	}

	private AuditLog getCurrentLog() {
		return auditLogBuilder.build();
	}

	private synchronized void setAmbiguityResulutionTask(AmbiguityResolutionTask ambiguityResolutionTask1) {
		setState(ElectionCalculationState.MANUAL_AMBIGUITY_RESOLUTION_NECESSARY);
		this.ambiguityResolutionResult = Optional.empty();
		this.ambiguityResolutionTask = Optional.of(ambiguityResolutionTask1);
	}

	public static final class AmbiguityResolutionTask {

		private final ImmutableSet<GenderedCandidate> candidatesToChooseFrom;
		private final String currentLog;

		public AmbiguityResolutionTask(ImmutableSet<GenderedCandidate> candidatesToChooseFrom, AuditLog currentLog) {
			this.candidatesToChooseFrom = candidatesToChooseFrom;
			this.currentLog = convertToString(currentLog);
		}

		public ImmutableSet<GenderedCandidate> getCandidatesToChooseFrom() {
			return candidatesToChooseFrom;
		}

		public String getCurrentLog() {
			return currentLog;
		}


		private String convertToString(AuditLog auditLog) {
			StringAuditLog stringAuditLog = new StringAuditLog();
			auditLog.replay(stringAuditLog);
			return stringAuditLog.toString();
		}
	}

	public static final class Snapshot {
		private final DateTime startDateTime;
		private final ImmutableList<ElectionCalculationResultBean> resultsOfFinishedCalculations;
		private final Optional<AmbiguityResolutionTask> ambiguityResulutionTask;
		private final ElectionCalculationState state;

		public Snapshot(DateTime startDateTime, ElectionCalculationState state,
		                ImmutableList<ElectionCalculationResultBean> result,
		                Optional<AmbiguityResolutionTask> ambiguityResulutionTask) {
			this.startDateTime = startDateTime;
			this.state = state;
			this.resultsOfFinishedCalculations = result;
			this.ambiguityResulutionTask = ambiguityResulutionTask;
		}

		public DateTime getStartDateTime() {
			return startDateTime;
		}

		public ImmutableList<ElectionCalculationResultBean> getResultsOfFinishedCalculations() {
			return resultsOfFinishedCalculations;
		}

		public Optional<ElectionCalculationResultBean> getResultOfFinishedCalculation(String officeName) {
			for (ElectionCalculationResultBean resultOfFinishedCalculation : resultsOfFinishedCalculations) {
				if (resultOfFinishedCalculation.getElection().getOfficeName().equals(officeName)) {
					return Optional.of(resultOfFinishedCalculation);
				}
			}
			return Optional.empty();
		}

		public ElectionCalculationState getState() {
			return state;
		}

		public AmbiguityResolutionTask getAmbiguityResulutionTask() {
			return ambiguityResulutionTask.orElse(null);
		}
	}

}
