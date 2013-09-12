package info.gehrels.voting.web;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Ballot.ElectionCandidatePreference;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
	public String doCastVote(@ModelAttribute CastVoteForm castVoteForm) {
		castBallotsState.castBallotsById.put(castVoteForm.getBallotId(), createBallotFromForm(castVoteForm));

		return "redirect:/castVote";
	}

	@RequestMapping(value = "/castVote", method = {HEAD, GET})
	public ModelAndView doGet() {
		if (ballotLayoutState.ballotLayout == null) {
			return new ModelAndView("redirect:/");
		}

		return new ModelAndView("castVote", "ballotLayout", ballotLayoutState.ballotLayout);
	}

	private Ballot<GenderedCandidate> createBallotFromForm(CastVoteForm castVoteForm) {
		int i = 0;
		Builder<ElectionCandidatePreference<GenderedCandidate>> preferenceSetBuilder = ImmutableSet.builder();
		for (GenderedElection genderedElection : ballotLayoutState.ballotLayout.getElections()) {
			ElectionCandidatePreference<GenderedCandidate> preference = createPreference(genderedElection, castVoteForm
				.getVotesByElectionId().get(i));
			preferenceSetBuilder.add(preference);
			i++;
		}

		return new Ballot<>(castVoteForm.getBallotId(), preferenceSetBuilder.build());
	}

	private ElectionCandidatePreference<GenderedCandidate> createPreference(GenderedElection genderedElection,
	                                                                        String preferenceString) {
		Builder<GenderedCandidate> preferenceBuilder = ImmutableSet.builder();
		for (char c : preferenceString.toUpperCase().toCharArray()) {
			int candidateIndex = c-'A';
			preferenceBuilder.add(Iterables.get(genderedElection.getCandidates(), candidateIndex));
		}
		return new ElectionCandidatePreference<>(genderedElection, preferenceBuilder.build());
	}

}
