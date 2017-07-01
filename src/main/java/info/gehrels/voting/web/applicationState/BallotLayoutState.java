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

import info.gehrels.voting.genderedElections.GenderedElection;

public class BallotLayoutState {
	private BallotLayout ballotLayout;

	public synchronized void changeOfficeName(String oldOfficeName, String newOfficeName) {
		for (int i = 0; i < ballotLayout.getElections().size(); i++) {
			GenderedElection oldElection = ballotLayout.getElections().get(i);
			if (oldElection.getOfficeName().equals(oldOfficeName)) {
				GenderedElection newElection = oldElection.withOfficeName(newOfficeName);
				ballotLayout.getElections().set(i, newElection);
			}
		}
	}

	public synchronized BallotLayout getBallotLayout() {
		return ballotLayout;
	}

	public synchronized void setBallotLayout(BallotLayout ballotLayout) {
		this.ballotLayout = ballotLayout;
	}

	public synchronized boolean isBallotLayoutPresent() {
		return ballotLayout != null;
	}
}
