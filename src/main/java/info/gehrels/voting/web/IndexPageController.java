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

import info.gehrels.voting.web.applicationState.BallotState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexPageController {

	private final BallotState ballotState;
	private final BallotState ballotLayoutState;

	public IndexPageController(BallotState ballotState, BallotState ballotLayoutState) {
		this.ballotState = ballotState;
		this.ballotLayoutState = ballotLayoutState;
	}

	@RequestMapping(value= "/", method = {RequestMethod.GET, RequestMethod.HEAD})
	protected final ModelAndView doGet() {
		return new ModelAndView("indexPage",
				"indexPageBean",
				new IndexPageBean(
						ballotState.getFirstTryCastBallots().size(),
						ballotState.getSecondTryCastBallots().size(),
						ballotLayoutState.isBallotLayoutPresent()
				)
		);
	}

	public static final class IndexPageBean {

		private final int numberOfCastBallotsFirstTry;
		private final int numberOfCastBallotsSecondTry;
		private final boolean ballotLayoutExists;

		public IndexPageBean(int numberOfCastBallotsFirstTry, int numberOfCastBallotsSecondTry, boolean ballotLayoutExists) {
			this.numberOfCastBallotsFirstTry = numberOfCastBallotsFirstTry;
			this.numberOfCastBallotsSecondTry = numberOfCastBallotsSecondTry;
			this.ballotLayoutExists = ballotLayoutExists;
		}

		public int getNumberOfCastBallotsFirstTry() {
			return numberOfCastBallotsFirstTry;
		}

		public int getNumberOfCastBallotsSecondTry() {
			return numberOfCastBallotsSecondTry;
		}

		public boolean isBallotLayoutExists() {
			return ballotLayoutExists;
		}
	}
}
