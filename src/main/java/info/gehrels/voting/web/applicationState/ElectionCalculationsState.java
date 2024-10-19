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
package info.gehrels.voting.web.applicationState;

import com.google.common.collect.ImmutableMap;
import info.gehrels.voting.web.resultCalculation.AsyncElectionCalculation;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public final class ElectionCalculationsState {
	private final Map<Instant, AsyncElectionCalculation> historyOfElectionCalculations = new HashMap<>();

	public void addElectionCalculation(AsyncElectionCalculation asyncElectionCalculation) {
		historyOfElectionCalculations.put(asyncElectionCalculation.getStartDateTime(), asyncElectionCalculation);
	}

	public ImmutableMap<Instant, AsyncElectionCalculation> getHistoryOfElectionCalculations() {
		return ImmutableMap.copyOf(historyOfElectionCalculations);
	}
}
