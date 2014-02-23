package info.gehrels.voting.web;

import com.google.common.collect.ImmutableCollection;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.web.BallotIterableDiffCalculator.BallotIterableDiff;
import info.gehrels.voting.web.applicationState.BallotLayoutState;
import info.gehrels.voting.web.applicationState.CastBallotsState;
import info.gehrels.voting.web.applicationState.ElectionCalculationsState;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static info.gehrels.voting.web.BallotIterableDiffCalculator.calculateDiff;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public final class CalculateElectionResultsController {
	private final BallotLayoutState ballotLayoutState;
	private final CastBallotsState castBallotsState;
	private final ElectionCalculationsState electionCalculationsState;


	public CalculateElectionResultsController(BallotLayoutState ballotLayoutState, CastBallotsState castBallotsState,
	                                          ElectionCalculationsState electionCalculationsState) {
		this.ballotLayoutState = ballotLayoutState;
		this.castBallotsState = castBallotsState;
		this.electionCalculationsState = electionCalculationsState;
	}

	@RequestMapping(value = "/startElectionCalculation", method = {POST})
	public ModelAndView startElectionCalculation() {
		ImmutableCollection<Ballot<GenderedCandidate>> firstTryCastBallots = castBallotsState.getFirstTryCastBallots();
		ImmutableCollection<Ballot<GenderedCandidate>> secondTryCastBallots = castBallotsState
			.getSecondTryCastBallots();
		BallotIterableDiff ballotIterableDiff = calculateDiff(firstTryCastBallots, secondTryCastBallots);
		if (ballotIterableDiff.isDifferent()) {
			return new ModelAndView("handleDifferingBallotCollections", "ballotIterableDiff", ballotIterableDiff);
		}

		AsyncElectionCalculation electionCalculation = new AsyncElectionCalculation(
			ballotLayoutState.ballotLayout.getElections(),
			firstTryCastBallots);
		electionCalculationsState.addElectionCalculation(electionCalculation);
		new Thread(electionCalculation).start();

		return new ModelAndView("redirect:/listElectionCalculations");
	}

	@RequestMapping(value = "/listElectionCalculations", method = {GET})
	public ModelAndView listElectionCalculations() {
		return new ModelAndView("listElectionCalculations", "electionCalculations",
		                        electionCalculationsState.getHistoryOfElectionCalculations());
	}

	@RequestMapping(value = "/showElectionCalculation", method = {GET})
	public ModelAndView showElectionCalculation(@RequestParam DateTime dateTimeTheCalculationStarted) {
		return new ModelAndView("showElectionCalculation", "electionCalculation",
		                        electionCalculationsState.getHistoryOfElectionCalculations()
			                        .get(dateTimeTheCalculationStarted).getSnapshot());
	}
}
