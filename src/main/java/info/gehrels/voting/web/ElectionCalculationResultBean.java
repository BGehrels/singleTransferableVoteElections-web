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
