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

import info.gehrels.voting.web.applicationState.BallotLayout;
import  javax.validation.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class BallotLayoutBuilderBean {
	@Valid
	@NotEmpty
	private List<GenderedElectionBuilderBean> elections = new ArrayList<>();

	public BallotLayoutBuilderBean() {
		elections.add(new GenderedElectionBuilderBean());
	}

	public List<GenderedElectionBuilderBean> getElections() {
		return elections;
	}

	public void setElections(List<GenderedElectionBuilderBean> elections) {
		this.elections = elections;
	}

	public void addNewElection() {
		elections.add(new GenderedElectionBuilderBean());
	}

	public void deleteElection(int electionIndex) {
		// Keep at least one Election
		if (elections.size() > 1) {
			elections.remove(electionIndex);
		}
	}

	public BallotLayout createBallotLayout() {
		return new BallotLayout(
				elections.stream()
						.map(GenderedElectionBuilderBean::build)
						.collect(toList())
		);
	}
}
