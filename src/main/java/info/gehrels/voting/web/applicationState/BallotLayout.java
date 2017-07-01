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

import com.google.common.collect.ImmutableList;
import info.gehrels.voting.genderedElections.GenderedElection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BallotLayout {
	private List<GenderedElection> elections = new ArrayList<>();

	public void addElection(GenderedElection election) {
		elections.add(election);
	}

	public List<GenderedElection> getElections() {
		return ImmutableList.copyOf(elections);
	}

	public void replaceElection(String officeName, Function<GenderedElection, GenderedElection> replacementFactory) {
		for (int i = 0; i < elections.size(); i++) {
			GenderedElection oldElection = elections.get(i);
			if (oldElection.getOfficeName().equals(officeName)) {
				GenderedElection newElection = replacementFactory.apply(oldElection);
				elections.set(i, newElection);
			}
		}
	}

	public void setElections(List<GenderedElection> elections) {
		this.elections = elections;
	}
}
