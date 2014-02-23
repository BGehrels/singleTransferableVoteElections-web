package info.gehrels.voting.web;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.AmbiguityResolver;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.NotMoreThanTheAllowedNumberOfCandidatesCanReachItQuorum;
import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositions;
import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositions.Result;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import info.gehrels.voting.genderedElections.StringBuilderBackedElectionCalculationWithFemaleExclusivePositionsListener;
import info.gehrels.voting.singleTransferableVote.STVElectionCalculationFactory;
import info.gehrels.voting.singleTransferableVote.StringBuilderBackedSTVElectionCalculationListener;
import org.joda.time.DateTime;

import java.util.List;

import static org.apache.commons.math3.fraction.BigFraction.ONE;

public final class AsyncElectionCalculation implements Runnable {
	private final List<GenderedElection> elections;
	private final ImmutableCollection<Ballot<GenderedCandidate>> ballots;
	private final ElectionCalculationWithFemaleExclusivePositions electionCalculation;
	private final StringBuilder auditLogBuilder;
	private final ImmutableList.Builder<ElectionCalculationResultBean> resultModelBuilder = ImmutableList.builder();
	private final DateTime startDateTime = DateTime.now();

	private ImmutableList<ElectionCalculationResultBean> result;
	private ElectionCalculationState state = ElectionCalculationState.NOT_YET_STARTED;

	public AsyncElectionCalculation(List<GenderedElection> elections,
	                                ImmutableCollection<Ballot<GenderedCandidate>> ballots) {
		this.elections = elections;
		this.ballots = ballots;
		this.auditLogBuilder = new StringBuilder();
		this.electionCalculation = createGenderedElectionCalculation();
	}

	@Override
	public void run() {
		if (state != ElectionCalculationState.NOT_YET_STARTED) {
			throw new IllegalStateException("Election calculations may only be started once. Current State: " + state);
		}

		state = ElectionCalculationState.RUNNING;
		for (GenderedElection election : elections) {
			reset(auditLogBuilder);
			Result electionResult = electionCalculation
				.calculateElectionResult(election, ImmutableList.copyOf(ballots));
			String auditLog = auditLogBuilder.toString();
			resultModelBuilder.add(new ElectionCalculationResultBean(election, electionResult, auditLog));
			this.result = resultModelBuilder.build();
		}

		state = ElectionCalculationState.FINISHED;
	}

	private ElectionCalculationWithFemaleExclusivePositions createGenderedElectionCalculation() {
		return new ElectionCalculationWithFemaleExclusivePositions(
			new STVElectionCalculationFactory<>(
				createQuorumCalculation(),
				new StringBuilderBackedSTVElectionCalculationListener<GenderedCandidate>(auditLogBuilder),
				new TakeFirstOneAmbiguityResolver()),
			new StringBuilderBackedElectionCalculationWithFemaleExclusivePositionsListener(auditLogBuilder));
	}

	private void reset(StringBuilder stringBuilder) {
		stringBuilder.setLength(0);
	}

	public synchronized Snapshot getSnapshot() {
		return new Snapshot(startDateTime, state, result);
	}

	private NotMoreThanTheAllowedNumberOfCandidatesCanReachItQuorum createQuorumCalculation() {
		// § 18 Satz 2 Nr. 2 WahlO-GJ:
		// Berechne das Quorum: q = [(gültige Stimmen) / (zu vergebende Sitze + 1)] +1. Hat der so berechnete Wert des
		// Quorums mehr als sieben Nachkommastellen, so wird das Quorum auf sieben Nachkommastellen aufgerundet, d.h.
		// die überzähligen	Nachkommastellen werden abgeschnitten und der Wert des Quorums wird	um die	kleinste
		// positive Zahl, die mit sieben Nachkommastellen darstellbar ist, erhöht.
		// TODO: SÄ zur Streichung der Rundung bei der Quorenberechnung
		return new NotMoreThanTheAllowedNumberOfCandidatesCanReachItQuorum(ONE);
	}

	public DateTime getStartDateTime() {
		return startDateTime;
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
	private static final class TakeFirstOneAmbiguityResolver implements AmbiguityResolver<GenderedCandidate> {
		@Override
		public AmbiguityResolverResult<GenderedCandidate> chooseOneOfMany(
			ImmutableSet<GenderedCandidate> bestCandidates) {
			return new AmbiguityResolverResult<>(bestCandidates.iterator().next(), "Einfach den ersten ausgewählt");
		}
	}

	public static final class Snapshot {
		private final DateTime startDateTime;
		private final ImmutableList<ElectionCalculationResultBean> resultsOfFinishedCalculations;
		private final ElectionCalculationState state;

		public Snapshot(DateTime startDateTime, ElectionCalculationState state, ImmutableList<ElectionCalculationResultBean> result) {
			this.startDateTime = startDateTime;
			this.state = state;
			this.resultsOfFinishedCalculations = result;
		}

		public DateTime getStartDateTime() {
			return startDateTime;
		}

		public ImmutableList<ElectionCalculationResultBean> getResultsOfFinishedCalculations() {
			return resultsOfFinishedCalculations;
		}

		public ElectionCalculationState getState() {
			return state;
		}
	}

}
