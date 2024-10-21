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
package info.gehrels.voting.web.ballotLayoutAdministration;

import com.google.common.collect.ImmutableMap;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.web.applicationState.BallotState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public final class EditBallotLayoutController {
	private final BallotState ballotLayoutState;

	public EditBallotLayoutController(BallotState ballotState) {
		this.ballotLayoutState = ballotState;
	}

	@RequestMapping(value = "/editBallotLayout", method = {GET})
	public ModelAndView showExistingBallotLayout() {
		if (!ballotLayoutState.isBallotLayoutPresent()) {
			return new ModelAndView("redirect:/");
		}

		return new ModelAndView("editBallotLayout", "ballotLayout", ballotLayoutState.getBallotLayout());
	}

	@RequestMapping(value = "/editBallotLayout", method = POST, params = "renameOffice")
	public ModelAndView renameOffice(String oldOfficeName, String newOfficeName) {
		if (!ballotLayoutState.isBallotLayoutPresent()) {
			return new ModelAndView("redirect:/");
		}

		if (newOfficeName == null || newOfficeName.isEmpty()) {
			return new ModelAndView("editBallotLayout", ImmutableMap.of("ballotLayout", ballotLayoutState.getBallotLayout(), "error", "Der Name des Amtes darf nicht leer sein"));
		}

		ballotLayoutState.replaceElectionVersion(oldOfficeName, (e) -> e.withOfficeName(newOfficeName));

		return new ModelAndView("editBallotLayout", "ballotLayout", ballotLayoutState.getBallotLayout());
	}

	@RequestMapping(value = "/editBallotLayout", method = POST, params = "changeNumberOfFemaleExclusivePositions")
	public ModelAndView changeNumberOfFemaleExclusivePositions(String officeName, long newNumberOfFemaleExclusivePositions) {
		if (!ballotLayoutState.isBallotLayoutPresent()) {
			return new ModelAndView("redirect:/");
		}

		if (newNumberOfFemaleExclusivePositions < 0) {
			return new ModelAndView("editBallotLayout", ImmutableMap.of("ballotLayout", ballotLayoutState.getBallotLayout(), "error", "Die Anzahl an Frauenplätzen darf nicht negativ sein."));
		}

		ballotLayoutState.replaceElectionVersion(officeName, (e) -> e.withNumberOfFemaleExclusivePositions(newNumberOfFemaleExclusivePositions));

		return new ModelAndView("editBallotLayout", "ballotLayout", ballotLayoutState.getBallotLayout());
	}

	@RequestMapping(value = "/editBallotLayout", method = POST, params = "changeNumberOfNotFemaleExclusivePositions")
	public ModelAndView changeNumberOfNotFemaleExclusivePositions(String officeName, long newNumberOfNotFemaleExclusivePositions) {
		if (!ballotLayoutState.isBallotLayoutPresent()) {
			return new ModelAndView("redirect:/");
		}

		if (newNumberOfNotFemaleExclusivePositions < 0) {
			return new ModelAndView("editBallotLayout", ImmutableMap.of("ballotLayout", ballotLayoutState.getBallotLayout(), "error", "Die Anzahl an Frauenplätzen darf nicht negativ sein."));
		}

		ballotLayoutState.replaceElectionVersion(officeName, (e) -> e.withNumberOfNotFemaleExclusivePositions(newNumberOfNotFemaleExclusivePositions));

		return new ModelAndView("editBallotLayout", "ballotLayout", ballotLayoutState.getBallotLayout());
	}

	@RequestMapping(value = "/editBallotLayout", method = POST, params = "switchIsFemale")
	public ModelAndView switchIsFemale(String officeName, String candidateName) {
		if (!ballotLayoutState.isBallotLayoutPresent()) {
			return new ModelAndView("redirect:/");
		}

		ballotLayoutState.replaceCandidateVersion(officeName, candidateName, (GenderedCandidate c) -> c.withIsFemale(!c.isFemale()));

		return new ModelAndView("editBallotLayout", "ballotLayout", ballotLayoutState.getBallotLayout());
	}

}
