package info.gehrels.voting.web;

import info.gehrels.voting.genderedElections.GenderedElection;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
		elections.add(new GenderedElectionBuilderBean(0));
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
