package info.gehrels.voting.web;

import info.gehrels.voting.genderedElections.GenderedElection;

import java.util.ArrayList;
import java.util.List;

public final class BallotLayoutBuilderBean {
	private List<GenderedElectionBuilderBean> elections = new ArrayList<>();

	public BallotLayoutBuilderBean() {
	}

	public BallotLayoutBuilderBean(
		int numberOfElections, int numberOfCandidatesPerElection) {
		for (int i = 0; i < numberOfElections; i++) {
		elections.add(new GenderedElectionBuilderBean(numberOfCandidatesPerElection));
		}
	}

	public List<GenderedElectionBuilderBean> getElections() {
		return elections;
	}

	public void setElection(List<GenderedElectionBuilderBean> elections) {
		this.elections = elections;
	}

	public BallotLayout createBallotLayout() {
		BallotLayout ballotLayout = new BallotLayout();
		for (GenderedElectionBuilderBean genderedElectionBuilderBean : elections) {
			GenderedElection genderedElection = genderedElectionBuilderBean.build();
			ballotLayout.addElection(genderedElection);
		}

		return ballotLayout;
	}
}
