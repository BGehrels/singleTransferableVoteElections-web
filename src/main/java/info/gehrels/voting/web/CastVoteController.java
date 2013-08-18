package info.gehrels.voting.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;


@Controller
public class CastVoteController {
	private final BallotLayoutState ballotLayoutState;

	public CastVoteController(BallotLayoutState ballotLayoutState) {

		this.ballotLayoutState = ballotLayoutState;
	}

	@RequestMapping(value = {"/castVote"}, method = {POST, PUT})
	public ModelAndView doCastVote() {
		return new ModelAndView("castVote", "ballotLayout", ballotLayoutState.ballotLayout);
	}

	@RequestMapping("/castVote")
	public ModelAndView doGet() {
		return new ModelAndView("castVote", "ballotLayout", ballotLayoutState.ballotLayout);
	}

}
