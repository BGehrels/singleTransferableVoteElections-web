package info.gehrels.voting.web;

import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Ballot.ElectionCandidatePreference;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;


@Controller
public class CastVoteController {
	private final BallotLayoutState ballotLayoutState;
	private final CastBallotsState castBallotsState;

	public CastVoteController(BallotLayoutState ballotLayoutState, CastBallotsState castBallotsState) {
		this.ballotLayoutState = ballotLayoutState;
		this.castBallotsState = castBallotsState;
	}

	@RequestMapping(value = {"/castVote"}, method = {POST, PUT})
	public String doCastVote(@ModelAttribute CastVoteForm castVoteForm) {
		castBallotsState.castBallotsById.put(castVoteForm.getBallotId(), new Ballot<>(
			ImmutableSet.of(
				new ElectionCandidatePreference<>(ballotLayoutState.ballotLayout.getElections().get(0),
				                                  ImmutableSet.<GenderedCandidate>of()))));

		return "redirect:/castVote";
	}

	@RequestMapping("/castVote")
	public ModelAndView doGet() {
		if (ballotLayoutState.ballotLayout == null) {
			return new ModelAndView("redirect:/");
		}

		return new ModelAndView("castVote", "ballotLayout", ballotLayoutState.ballotLayout);
	}

}
