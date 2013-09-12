package info.gehrels.voting.web;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.AmbiguityResolver;
import info.gehrels.voting.NotMoreThanTheAllowedNumberOfCandidatesCanReachItQuorum;
import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositions;
import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositions.Result;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import info.gehrels.voting.genderedElections.StringBuilderBackedElectionCalculationWithFemaleExclusivePositionsListener;
import info.gehrels.voting.singleTransferableVote.STVElectionCalculationFactory;
import info.gehrels.voting.singleTransferableVote.StringBuilderBackedSTVElectionCalculationListener;
import org.apache.commons.math3.fraction.BigFraction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
public final class CalculateElectionResultsController {
	private final BallotLayoutState ballotLayoutState;
	private final CastBallotsState castBallotsState;

	public CalculateElectionResultsController(BallotLayoutState ballotLayoutState, CastBallotsState castBallotsState) {
		this.ballotLayoutState = ballotLayoutState;
		this.castBallotsState = castBallotsState;
	}

	@RequestMapping(value = "/calculateElectionResults", method = {HEAD, GET})
	public ModelAndView doGet() {
		StringBuilder auditLogBuilder = new StringBuilder();
		ElectionCalculationWithFemaleExclusivePositions electionCalculation =
			createGenderedElectionCalculation(auditLogBuilder);

		ImmutableList.Builder<ElectionCalculationResultBean> resultModelBuilder = ImmutableList.builder();

		for (GenderedElection election : ballotLayoutState.ballotLayout.getElections()) {
			reset(auditLogBuilder);
			Result electionResult = electionCalculation
				.calculateElectionResult(election, ImmutableList.copyOf(castBallotsState.castBallotsById.values()));
			String auditLog = auditLogBuilder.toString();
			resultModelBuilder.add(new ElectionCalculationResultBean(election, electionResult, auditLog));
		}


		return new ModelAndView("electionCalculationResults", "resultModel", resultModelBuilder.build());
	}

	private void reset(StringBuilder auditLogBuilder) {
		auditLogBuilder.setLength(0);
	}

	private ElectionCalculationWithFemaleExclusivePositions createGenderedElectionCalculation(
		StringBuilder auditLogBuilder) {
		return new ElectionCalculationWithFemaleExclusivePositions(
			new STVElectionCalculationFactory<>(
				new NotMoreThanTheAllowedNumberOfCandidatesCanReachItQuorum(BigFraction.getReducedFraction(1, 10)),
				new StringBuilderBackedSTVElectionCalculationListener<GenderedCandidate>(auditLogBuilder),
				new TakeFirstOneAmbiguityResolver()),
			new StringBuilderBackedElectionCalculationWithFemaleExclusivePositionsListener(auditLogBuilder));
	}

	// TODO: Verfahren nach Satzung implementieren
	private static final class TakeFirstOneAmbiguityResolver implements AmbiguityResolver<GenderedCandidate> {
		@Override
		public AmbiguityResolverResult<GenderedCandidate> chooseOneOfMany(
			ImmutableSet<GenderedCandidate> bestCandidates) {
			return new AmbiguityResolverResult<>(bestCandidates.iterator().next(), "Einfach den ersten ausgewählt");
		}
	}
}
