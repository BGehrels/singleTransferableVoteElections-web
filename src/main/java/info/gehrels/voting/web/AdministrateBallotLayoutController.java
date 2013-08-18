package info.gehrels.voting.web;

import info.gehrels.voting.genderedElections.GenderedElection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
public class AdministrateBallotLayoutController {

	private final BallotLayoutState ballotLayoutState;

	public AdministrateBallotLayoutController(BallotLayoutState ballotLayoutState) {
		this.ballotLayoutState = ballotLayoutState;
	}

	@RequestMapping(value = "/administrateBallotLayout", method = {PUT, POST})
	public ModelAndView addNewElection(@ModelAttribute AdministrateBallotLayoutForm form) {
		BallotLayout ballotLayout = new BallotLayout();
		for (GenderedElectionBuilderBean genderedElectionBuilderBean : form.getElections()) {
			GenderedElection genderedElection = genderedElectionBuilderBean.build();
			ballotLayout.addElection(genderedElection);
		}

		this.ballotLayoutState.ballotLayout = ballotLayout;
		return new ModelAndView("administrateBallotLayout", "ballotLayout", ballotLayout);
	}


	@RequestMapping(value = "/administrateBallotLayout", method = {GET, HEAD})
	public ModelAndView showCurrentBallotLayout(@RequestParam int numberOfElections) {
		return new ModelAndView("administrateBallotLayout", "numberOfElections", numberOfElections);
	}

}
