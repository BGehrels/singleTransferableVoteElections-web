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
import info.gehrels.voting.web.applicationState.BallotLayoutState;
import info.gehrels.voting.web.applicationState.CastBallotsState;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public final class EditBallotLayoutController {
	private final BallotLayoutState ballotLayoutState;
	private final CastBallotsState castBallotsState;

	public EditBallotLayoutController(BallotLayoutState ballotLayoutState, CastBallotsState castBallotsState) {
		this.ballotLayoutState = ballotLayoutState;
		this.castBallotsState = castBallotsState;
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
		if (ballotLayoutState.getBallotLayout() == null) {
			return new ModelAndView("redirect:/");
		}

		if (StringUtils.isEmpty(newOfficeName)) {
			return new ModelAndView("editBallotLayout", ImmutableMap.of("ballotLayout", ballotLayoutState.getBallotLayout(), "error", "Der Name des Amtes darf nicht leer sein"));
		}

		ballotLayoutState.changeOfficeName(oldOfficeName, newOfficeName);

		return new ModelAndView("editBallotLayout", "ballotLayout", ballotLayoutState.getBallotLayout());
	}

}
