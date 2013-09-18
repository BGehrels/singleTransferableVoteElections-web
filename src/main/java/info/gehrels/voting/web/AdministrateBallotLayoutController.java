package info.gehrels.voting.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
public final class AdministrateBallotLayoutController {
	private final BallotLayoutState ballotLayoutState;

	public AdministrateBallotLayoutController(BallotLayoutState ballotLayoutState) {
		this.ballotLayoutState = ballotLayoutState;
	}

	@RequestMapping(value = "/administrateBallotLayout", method = {GET})
	public ModelAndView showNewEmptyBallotLayout() {
		return new ModelAndView("administrateBallotLayout", "ballotLayoutBuilderBean", new BallotLayoutBuilderBean());
	}

	@RequestMapping(value = "/administrateBallotLayout", method = {PUT, POST}, params = {"addNewElection"})
	public ModelAndView addNewElection(@Valid BallotLayoutBuilderBean form, BindingResult bindingResult) {
		form.addNewElection();
		return createModelAndView(form);
	}

	@RequestMapping(value = "/administrateBallotLayout", method = {PUT, POST}, params = {"addNewCandidate"})
	public ModelAndView addNewElection(@RequestParam("addNewCandidate") int electionIndex, @Valid BallotLayoutBuilderBean form, BindingResult bindingResult) {
		form.getElections().get(electionIndex).addNewCandidate();
		return createModelAndView(form);
	}

	@RequestMapping(value = "/administrateBallotLayout", method = {PUT, POST})
	public ModelAndView saveBallotLayout(@Valid BallotLayoutBuilderBean form, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return createModelAndView(form);
		}

		ballotLayoutState.ballotLayout = form.createBallotLayout();
		return new ModelAndView("redirect:/");
	}

	private ModelAndView createModelAndView(BallotLayoutBuilderBean form) {
		return new ModelAndView("administrateBallotLayout", new ModelMap("ballotLayoutBuilderBean", form));
	}

}
