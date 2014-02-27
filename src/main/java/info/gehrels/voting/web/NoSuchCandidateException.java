package info.gehrels.voting.web;

public class NoSuchCandidateException extends Exception {
	public final String candidateName;

	public NoSuchCandidateException(String candidateName) {
		this.candidateName = candidateName;
	}
}
