package info.gehrels.voting.web.svg;

import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import java.math.BigDecimal;

public final class VotesForCandidate {
    public static final BigFraction SEVENTY = new BigFraction(70);
    private final BigFraction numberOfVotes;
    private final BigFraction quorum;
    private BigFraction globalMaxNumberOfVotes;

    VotesForCandidate(BigFraction numberOfVotes, BigFraction quorum) {
        this.numberOfVotes = numberOfVotes;
        this.quorum = quorum;
    }

    public BigFraction getNumberOfVotes() {
        return numberOfVotes != null ? numberOfVotes : BigFraction.ZERO;
    }

    public void setGlobalMaxNumberOfVotes(BigFraction maxNumberOfVotes) {
        this.globalMaxNumberOfVotes = maxNumberOfVotes;
    }

    public double getWidth() {
        return getBarWidth().add(2).doubleValue();
    }

    private BigFraction getBarWidth() {
        return globalMaxNumberOfVotes.multiply(getScale());
    }

    public double getHeight() {
        return getBarHeight().add(2).doubleValue(); // stroke 2 x 1px
    }

    private BigFraction getBarHeight() {
        return getBarWidth().divide(7);
    }

    private BigFraction getScale() {
        return (globalMaxNumberOfVotes.compareTo(SEVENTY) > 0) ? BigFraction.ONE : SEVENTY.divide(globalMaxNumberOfVotes);
    }

    public Element build(SVGDocument document, double baseX, double baseY) {
        Element svg = document.createElement("svg");
        svg.setAttribute("x", String.valueOf(baseX));
        svg.setAttribute("y", String.valueOf(baseY));
        if (numberOfVotes != null) {
            Element numberOfVotesBar = document.createElement("rect");
            numberOfVotesBar.setAttribute("x", "1");
            numberOfVotesBar.setAttribute("y", "1");
            numberOfVotesBar.setAttribute("width", String.valueOf(numberOfVotes.multiply(getScale()).doubleValue()));
            numberOfVotesBar.setAttribute("height", String.valueOf(getBarHeight().doubleValue()));
            numberOfVotesBar.setAttribute("style", "fill:red; stroke:none");
            svg.appendChild(numberOfVotesBar);

            BigFraction quorumWidth = quorum.multiply(getScale());
            Element quorumBorder = document.createElement("rect");
            quorumBorder.setAttribute("x", "1");
            quorumBorder.setAttribute("y", "1");
            quorumBorder.setAttribute("width", String.valueOf(quorumWidth.doubleValue()));
            quorumBorder.setAttribute("height", String.valueOf(getBarHeight().doubleValue()));
            quorumBorder.setAttribute("fill-opacity", "0");
            quorumBorder.setAttribute("stroke", "black");
            svg.appendChild(quorumBorder);

            svg.appendChild(new TextElement(document)
                    .withX(1 + quorumWidth.divide(2).doubleValue())
                    .withY(1 + getBarHeight().doubleValue() - getBarHeight().divide(10).doubleValue() -1)
                    .withText(numberOfVotes.bigDecimalValue(1, BigDecimal.ROUND_HALF_UP) + "/" + quorum.bigDecimalValue(1, BigDecimal.ROUND_HALF_UP))
                    .withFontSize(getBarHeight().multiply(BigFraction.getReducedFraction(8,10)).doubleValue())
                    .withMiddleAnchor()
                    .build());
        }
        return svg;
    }
}
