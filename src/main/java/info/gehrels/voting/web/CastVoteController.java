/*
 * Copyright © 2014 Benjamin Gehrels
 *
 * This file is part of The Single Transferable Vote Elections Web Interface.
 *
 * The Single Transferable Vote Elections Web Interface is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * The Single Transferable Vote Elections Web Interface is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with The Single Transferable Vote
 * Elections Web Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package info.gehrels.voting.web;

import info.gehrels.voting.Ballot;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.web.applicationState.BallotLayoutState;
import info.gehrels.voting.web.applicationState.CastBallotsState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public ModelAndView doCastVote(@Valid BallotBuilder ballotBuilder, BindingResult bindingResult,
	                               BallotInputTry firstOrSecondTry) {
		if (bindingResult.hasErrors()) {
			return createModelAndView(ballotBuilder, firstOrSecondTry);
		}

		ballotBuilder.validate(bindingResult, "ballotBuilder");
		if (bindingResult.hasErrors()) {
			return createModelAndView(ballotBuilder, firstOrSecondTry);
		}

		Ballot<GenderedCandidate> ballotFromForm = ballotBuilder.createBallotFromForm(ballotLayoutState.ballotLayout);
		castBallotsState.add(firstOrSecondTry, ballotFromForm);

		return new ModelAndView(
			"redirect:/castVote?firstOrSecondTry=" + firstOrSecondTry + "&stringInputMode=" + ballotBuilder
				.isStringInputMode());
	}

	@RequestMapping(value = "/castVote", method = {HEAD, GET})
	public ModelAndView doGet(@RequestParam BallotInputTry firstOrSecondTry,
	                          @RequestParam(required = false) boolean stringInputMode) {
		if (ballotLayoutState.ballotLayout == null) {
			return new ModelAndView("redirect:/");
		}

		BallotBuilder ballotBuilder = new BallotBuilder();
		ballotBuilder.setStringInputMode(stringInputMode);
		return createModelAndView(ballotBuilder, firstOrSecondTry);
	}

	private ModelAndView createModelAndView(BallotBuilder ballotBuilder, BallotInputTry firstOrSecondTry) {
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("ballotLayout", ballotLayoutState.ballotLayout);
		modelMap.addAttribute("ballotBuilder", ballotBuilder);
		modelMap.addAttribute("firstOrSecondTry", firstOrSecondTry);
		return new ModelAndView("castVote", modelMap);
	}

}
