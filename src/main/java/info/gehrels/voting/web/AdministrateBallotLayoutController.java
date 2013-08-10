package info.gehrels.voting.web;

import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdministrateBallotLayoutController {
	@RequestMapping("/administrateBallotLayout")
	public ModelAndView doGet() {
		BallotLayout ballotLayout = new BallotLayout();
		ballotLayout.addElection(new GenderedElection("Bundesschiedsgericht", 2, 1, ImmutableSet.of(new GenderedCandidate("Petra Lustig", true), new GenderedCandidate("John Doe", false), new GenderedCandidate("Willhelm Tell", false), new GenderedCandidate("Mandy Schwalkowiak", true))));
		ballotLayout.addElection(new GenderedElection("Freie Koordination im Bildungsbeirat", 1, 1, ImmutableSet.of(new GenderedCandidate("Aaron A. Aaronson", false), new GenderedCandidate("Maja Biene", true), new GenderedCandidate("Mr. Pink", false), new GenderedCandidate("Maria Musterfrau", true), new GenderedCandidate("Niels Kleiner", false), new GenderedCandidate("Dave B. Rill", false), new GenderedCandidate("Gloria von Weidenbach", true))));
		return new ModelAndView("administrateBallotLayout", "ballotLayout", ballotLayout);
	}
}
