package info.gehrels.voting.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
public final class AdministrateBallotLayoutController {

	private final BallotLayoutState ballotLayoutState;

	public AdministrateBallotLayoutController(BallotLayoutState ballotLayoutState) {
		this.ballotLayoutState = ballotLayoutState;
	}

	@RequestMapping(value = "/administrateBallotLayout", method = {PUT, POST})
	public String addNewElection(@ModelAttribute BallotLayoutBuilderBean form) {
		ballotLayoutState.ballotLayout = form.createBallotLayout();
		return "redirect:/";
	}


	@RequestMapping(value = "/administrateBallotLayout", method = {GET, HEAD})
	public ModelAndView showCurrentBallotLayout(@RequestParam int numberOfElections, @RequestParam int numberOfCandidatesPerElection) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("numberOfElections", numberOfElections);
		modelMap.addAttribute("numberOfCandidatesPerElection", numberOfCandidatesPerElection);
		return new ModelAndView("administrateBallotLayout", modelMap);
	}

}
