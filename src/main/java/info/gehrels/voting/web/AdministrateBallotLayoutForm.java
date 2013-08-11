package info.gehrels.voting.web;

public class AdministrateBallotLayoutForm {
	private GenderedElectionBuilderBean election = new GenderedElectionBuilderBean();

	public AdministrateBallotLayoutForm() {
	}

	public GenderedElectionBuilderBean getElection() {
		return election;
	}

	public void setElection(GenderedElectionBuilderBean election) {
		this.election = election;
	}
}
