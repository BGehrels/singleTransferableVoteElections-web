package info.gehrels.voting.web;

import org.springframework.web.bind.annotation.RequestMapping;

public class CastVoteController {

	@RequestMapping("/castVote")
	public String doGet() {
		return "castVote";
	}

}
