package info.gehrels.voting.web;

import info.gehrels.voting.web.applicationState.CastBallotsState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
public final class DeleteBallotController {
	private final CastBallotsState castBallotsState;

	public DeleteBallotController(CastBallotsState castBallotsState) {

		this.castBallotsState = castBallotsState;
	}
	@RequestMapping(value = "/deleteBallot", method = {POST, PUT})
	public String deleteBallotById(@RequestParam long ballotId) {
		castBallotsState.deleteById(ballotId);

		return "redirect:/";
	}
}
