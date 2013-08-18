package info.gehrels.voting.web;

import info.gehrels.voting.genderedElections.GenderedElection;

import java.util.ArrayList;
import java.util.List;

public class BallotLayoutBuilderBean {
	private List<GenderedElectionBuilderBean> elections = new ArrayList<>();

	public BallotLayoutBuilderBean() {
	}

	public List<GenderedElectionBuilderBean> getElections() {
		return elections;
	}

	public void setElection(List<GenderedElectionBuilderBean> elections) {
		this.elections = elections;
	}

	public BallotLayout createBallotLayout() {
		BallotLayout ballotLayout = new BallotLayout();
		for (GenderedElectionBuilderBean genderedElectionBuilderBean : getElections()) {
			GenderedElection genderedElection = genderedElectionBuilderBean.build();
			ballotLayout.addElection(genderedElection);
		}

		return ballotLayout;
	}
}
