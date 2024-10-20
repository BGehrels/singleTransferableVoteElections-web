package info.gehrels.voting.web.auditLogging;

import com.google.common.collect.ImmutableList;
import info.gehrels.voting.genderedElections.ElectionCalculationWithFemaleExclusivePositionsListener;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.singleTransferableVote.STVElectionCalculationListener;

public class AuditLog {
	private final ImmutableList<Entry> entries;

	public AuditLog(ImmutableList<Entry> entries) {
		this.entries = entries;
	}

	public <T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener>
		void replay(T listener) {
		for (Entry entry : entries) {
			entry.replay(listener);
		}
	}

	public interface Entry {
		<T extends STVElectionCalculationListener<GenderedCandidate> & ElectionCalculationWithFemaleExclusivePositionsListener> void replay(
			T listener);
	}
}
