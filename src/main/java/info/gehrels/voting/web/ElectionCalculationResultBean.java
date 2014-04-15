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

import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositions.Result;
import info.gehrels.voting.genderedElections.GenderedElection;

public class ElectionCalculationResultBean {
	private final GenderedElection election;
	private final Result electionResult;
	private final String auditLog;

	public ElectionCalculationResultBean(GenderedElection election, Result electionResult, String auditLog) {
		this.election = election;
		this.electionResult = electionResult;
		this.auditLog = auditLog;
	}

	public GenderedElection getElection() {
		return election;
	}

	public Result getElectionResult() {
		return electionResult;
	}

	public String getAuditLog() {
		return auditLog;
	}
}
