package info.gehrels.voting.web;

import info.gehrels.voting.Ballot;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;


@Controller
public final class CastVoteController {
	private final BallotLayoutState ballotLayoutState;
	private final CastBallotsState castBallotsState;

	public CastVoteController(BallotLayoutState ballotLayoutState, CastBallotsState castBallotsState) {
		this.ballotLayoutState = ballotLayoutState;
		this.castBallotsState = castBallotsState;
	}

	@RequestMapping(value = "/castVote", method = {POST, PUT})
	public ModelAndView doCastVote(@Valid BallotBuilder ballotBuilder, BindingResult bindingResult, BallotInputTry firstOrSecondTry) {
		if (bindingResult.hasErrors()) {
			return createModelAndView(ballotBuilder, firstOrSecondTry);
		}

		getCastBallotsState(firstOrSecondTry).add(ballotBuilder.createBallotFromForm(ballotLayoutState.ballotLayout));

		return new ModelAndView("redirect:/castVote?firstOrSecondTry="+ firstOrSecondTry);
	}

	private Collection<Ballot<GenderedCandidate>> getCastBallotsState(BallotInputTry firstOrSecondTry) {
		if (firstOrSecondTry == BallotInputTry.FIRST) {
			return castBallotsState.firstTryCastBallots;
		} else if (firstOrSecondTry == BallotInputTry.SECOND) {
			return castBallotsState.secondTryCastBallots;
		} else {
			throw new IllegalStateException("Unknown enum value " + firstOrSecondTry);
		}
	}

	@RequestMapping(value = "/castVote", method = {HEAD, GET})
	public ModelAndView doGet(@RequestParam BallotInputTry firstOrSecondTry) {
		if (ballotLayoutState.ballotLayout == null) {
			return new ModelAndView("redirect:/");
		}

		return createModelAndView(new BallotBuilder(), firstOrSecondTry);
	}

	private ModelAndView createModelAndView(BallotBuilder ballotBuilder, @RequestParam BallotInputTry firstOrSecondTry) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("ballotLayout", ballotLayoutState.ballotLayout);
		modelMap.addAttribute("ballotBuilder", ballotBuilder);
		modelMap.addAttribute("firstOrSecondTry", firstOrSecondTry);
		return new ModelAndView("castVote", modelMap);
	}

}
