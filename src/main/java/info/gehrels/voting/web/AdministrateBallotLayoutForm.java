package info.gehrels.voting.web;

import java.util.ArrayList;
import java.util.List;

public class AdministrateBallotLayoutForm {
	private List<GenderedElectionBuilderBean> elections = new ArrayList<>();

	public AdministrateBallotLayoutForm() {
	}

	public List<GenderedElectionBuilderBean> getElections() {
		return elections;
	}

	public void setElection(List<GenderedElectionBuilderBean> elections) {
		this.elections = elections;
	}
}
