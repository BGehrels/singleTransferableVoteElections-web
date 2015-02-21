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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedMap.Builder;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.web.BallotIterableDiffCalculator.BallotIterableDiff;
import info.gehrels.voting.web.applicationState.BallotLayoutState;
import info.gehrels.voting.web.applicationState.CastBallotsState;
import info.gehrels.voting.web.applicationState.ElectionCalculationsState;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static info.gehrels.voting.web.BallotIterableDiffCalculator.calculateDiff;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public final class ManageElectionCalculationsController {
	private final BallotLayoutState ballotLayoutState;
	private final CastBallotsState castBallotsState;
	private final ElectionCalculationsState electionCalculationsState;


	public ManageElectionCalculationsController(BallotLayoutState ballotLayoutState, CastBallotsState castBallotsState,
	                                            ElectionCalculationsState electionCalculationsState) {
		this.ballotLayoutState = ballotLayoutState;
		this.castBallotsState = castBallotsState;
		this.electionCalculationsState = electionCalculationsState;
	}

	@RequestMapping(value = "/startElectionCalculation", method = {POST})
	public ModelAndView startElectionCalculation() {
		if (ballotLayoutState.ballotLayout != null) {
			ImmutableCollection<Ballot<GenderedCandidate>> firstTryCastBallots = castBallotsState
				.getFirstTryCastBallots();
			ImmutableCollection<Ballot<GenderedCandidate>> secondTryCastBallots = castBallotsState
				.getSecondTryCastBallots();
			BallotIterableDiff ballotIterableDiff = calculateDiff(firstTryCastBallots, secondTryCastBallots);
			if (ballotIterableDiff.isDifferent()) {
				return new ModelAndView("handleDifferingBallotCollections", "ballotIterableDiff", ballotIterableDiff);
			}

			AsyncElectionCalculation electionCalculation = new AsyncElectionCalculation(
				ballotLayoutState.ballotLayout.getElections(),
				firstTryCastBallots);
			electionCalculationsState.addElectionCalculation(electionCalculation);
			new Thread(electionCalculation).start();
		}
		return new ModelAndView("redirect:/listElectionCalculations");
	}

	@RequestMapping(value = "/listElectionCalculations", method = GET)
	public ModelAndView listElectionCalculations() {
        Builder<ReadableInstant, AsyncElectionCalculation> sortedMapBuilder = ImmutableSortedMap.reverseOrder();
        for (Map.Entry<DateTime, AsyncElectionCalculation> dateTimeAsyncElectionCalculationEntry : electionCalculationsState.getHistoryOfElectionCalculations().entrySet()) {
            sortedMapBuilder.put(dateTimeAsyncElectionCalculationEntry.getKey(), dateTimeAsyncElectionCalculationEntry.getValue());
        }
        return new ModelAndView("listElectionCalculations", "electionCalculations", sortedMapBuilder.build());
	}
}
