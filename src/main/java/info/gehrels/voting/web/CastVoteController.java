package info.gehrels.voting.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

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
	public ModelAndView doCastVote(@Valid BallotBuilder ballotBuilder, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return createModelAndView(ballotBuilder);
		}

		castBallotsState.castBallotsById.put(ballotBuilder.getBallotId(), ballotBuilder.createBallotFromForm(ballotLayoutState.ballotLayout));

		return new ModelAndView("redirect:/castVote");
	}

	@RequestMapping(value = "/castVote", method = {HEAD, GET})
	public ModelAndView doGet() {
		if (ballotLayoutState.ballotLayout == null) {
			return new ModelAndView("redirect:/");
		}

		return createModelAndView(new BallotBuilder());
	}

	private ModelAndView createModelAndView(BallotBuilder ballotBuilder) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("ballotLayout", ballotLayoutState.ballotLayout);
		modelMap.addAttribute("ballotBuilder", ballotBuilder);
		return new ModelAndView("castVote", modelMap);
	}

}
