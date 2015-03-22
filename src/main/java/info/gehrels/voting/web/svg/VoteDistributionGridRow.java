package info.gehrels.voting.web.svg;

public abstract class VoteDistributionGridRow {
    protected static final double TOTAL_WIDTH = 800.0;

    private final int numberOfElectableCandidates;
    private double perCandidateColumnWidth;
    private double perCandidateUsableWidth;
    private double baseX;
    private double baseY;

    protected VoteDistributionGridRow(int numberOfElectableCandidates) {
        this.numberOfElectableCandidates = numberOfElectableCandidates;
    }

    public final void initializeSizing(double x, double y) {
        this.baseX = x;
        this.baseY = y;
        double totalAmountOfSpacing = 0.05 * TOTAL_WIDTH;
        double spacingWidth = totalAmountOfSpacing / numberOfElectableCandidates;
        perCandidateColumnWidth = TOTAL_WIDTH / (numberOfElectableCandidates + 1);
        perCandidateUsableWidth = perCandidateColumnWidth - spacingWidth;

        initializeSizing();
    }

    protected abstract void initializeSizing();

    protected final double getDefaultCellHeight() {
        return perCandidateUsableWidth / 7;
    }

    protected final double getDefaultFontSize() {
        return getDefaultCellHeight() * 0.8;
    }

    protected final double getBaseX() {
        return baseX;
    }

    protected final double getBaseY() {
        return baseY;
    }

    protected final double getPerCandidateColumnWidth() {
        return perCandidateColumnWidth;
    }

    protected final double getPerCandidateUsableWidth() {
        return perCandidateUsableWidth;
    }
}
