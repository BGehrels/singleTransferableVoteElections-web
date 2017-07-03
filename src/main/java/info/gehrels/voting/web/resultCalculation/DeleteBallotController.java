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
package info.gehrels.voting.web.resultCalculation;

import info.gehrels.voting.web.applicationState.BallotState;
import info.gehrels.voting.web.resultCalculation.BallotIterableDiffCalculator.BallotIterableDiff;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
public final class DeleteBallotController {
	private final BallotState ballotState;

	public DeleteBallotController(BallotState ballotState) {
		this.ballotState = ballotState;
	}

	@RequestMapping(value = "/deleteBallots", method = {POST, PUT})
	public String deleteBallotsById(@RequestParam long[] ballotIds) {
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffCalculator
			.calculateDiff(ballotState.getFirstTryCastBallots(), ballotState.getSecondTryCastBallots());
		for (long ballotId : ballotIds) {
			// We check here again to prevent forged requests.
			if (ballotIterableDiff.isBallotDifferentOrDuplicate(ballotId)) {
				ballotState.deleteById(ballotId);
			}
		}

		return "redirect:/";
	}
}
