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
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
public final class AdministrateBallotLayoutController {
	private final BallotLayoutState ballotLayoutState;
	private final CastBallotsState castBallotsState;

	public AdministrateBallotLayoutController(BallotLayoutState ballotLayoutState, CastBallotsState castBallotsState) {
		this.ballotLayoutState = ballotLayoutState;
		this.castBallotsState = castBallotsState;
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

	@RequestMapping(value = "/administrateBallotLayout", method = {PUT, POST}, params = {"deleteElection"})
	public ModelAndView deleteElection(@RequestParam("deleteElection") int electionIndex, @Valid BallotLayoutBuilderBean form, BindingResult bindingResult) {
		form.getElections().remove(electionIndex);
		return createModelAndView(form);
	}

	@RequestMapping(value = "/administrateBallotLayout", method = {PUT, POST}, params = {"deleteCandidate"})
	public ModelAndView deleteOffice(@RequestParam String deleteCandidate, @Valid BallotLayoutBuilderBean form, BindingResult bindingResult) {
		String[] splited = deleteCandidate.split("_");
		form.getElections().get(Integer.parseInt(splited[0])).deleteCandidate(Integer.parseInt(splited[1]));
		return createModelAndView(form);
	}

	@RequestMapping(value = "/administrateBallotLayout", method = {PUT, POST})
	public ModelAndView saveBallotLayout(@Valid BallotLayoutBuilderBean form, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return createModelAndView(form);
		}

		ballotLayoutState.ballotLayout = form.createBallotLayout();
		castBallotsState.reset();
		return new ModelAndView("redirect:/");
	}

	private ModelAndView createModelAndView(BallotLayoutBuilderBean form) {
		return new ModelAndView("administrateBallotLayout", new ModelMap("ballotLayoutBuilderBean", form));
	}

}
