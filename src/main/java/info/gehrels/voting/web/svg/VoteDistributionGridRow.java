package info.gehrels.voting.web.svg;

public abstract class VoteDistributionGridRow {
    protected static final double TOTAL_WIDTH = 800.0;

    protected final int numberOfElectableCandidates;
    protected double perCandidateWidth;

    protected VoteDistributionGridRow(int numberOfElectableCandidates) {
        this.numberOfElectableCandidates = numberOfElectableCandidates;
    }

    public void initializeSizing(double baseX, double baseY) {
        perCandidateWidth = TOTAL_WIDTH / (numberOfElectableCandidates + 1);
    }

    abstract double getHeight();
}
