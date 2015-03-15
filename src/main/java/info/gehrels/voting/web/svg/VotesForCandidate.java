package info.gehrels.voting.web.svg;

import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

public final class VotesForCandidate {
    private final BigFraction numberOfVotes;
    private final BigFraction quorum;
    private double baseX;
    private double baseY;
    private double width;
    private final Collection<VoteFlow> incomingFlows = new ArrayList<>();
    private final Collection<VoteFlow> outgoingFlows = new ArrayList<>();

    private BigFraction globalMaxNumberOfVotes;

    VotesForCandidate(BigFraction numberOfVotes, BigFraction quorum) {
        this.numberOfVotes = numberOfVotes;
        this.quorum = quorum;
    }

    public void initializeSizing(double baseX, double baseY, double width) {
        this.baseX = baseX;
        this.baseY = baseY;
        this.width = width;
    }

    public BigFraction getNumberOfVotes() {
        return (numberOfVotes != null) ? numberOfVotes : BigFraction.ZERO; // TODO: fühlt sich nach workaround an.
    }

    public void setGlobalMaxNumberOfVotes(BigFraction maxNumberOfVotes) {
        this.globalMaxNumberOfVotes = maxNumberOfVotes;
    }

    public double getHeight() {
        return getBarHeight();
    }

    private double getBarHeight() {
        return width / 7;
    }

    private double getWidthPerVote() {
        return width / globalMaxNumberOfVotes.doubleValue();
    }

    private double getBarWidth(BigFraction numberOfVotes) {
        return numberOfVotes.doubleValue() * getWidthPerVote();
    }

    public void registerOutgoingFlow(VoteFlow voteFlow) {
        outgoingFlows.add(voteFlow);
    }

    public void registerIncomingFlow(VoteFlow voteFlow) {
        incomingFlows.add(voteFlow);
    }

    public void initializeVoteFlowSizing() {
        if (numberOfVotes != null) {
            double upperBarEnd = baseY + getBarHeight()/4;
            double lowerBarEnd = baseY + getBarHeight()  - getBarHeight()/ 4;
            double rightFlowEnd = baseX + getBarWidth(numberOfVotes);

            for (VoteFlow incomingFlow : incomingFlows) {
                double votesBarWidth = getBarWidth(incomingFlow.getVoteWeight());
                double leftFlowEnd = rightFlowEnd - votesBarWidth;
                incomingFlow.setTargetCoordinates(leftFlowEnd, rightFlowEnd, upperBarEnd);
                rightFlowEnd = leftFlowEnd;
            }

            rightFlowEnd = baseX + getBarWidth(numberOfVotes);

            for (VoteFlow outgoingFlow : outgoingFlows) {
                double votesBarWidth = getBarWidth(outgoingFlow.getVoteWeight());
                double leftFlowEnd = rightFlowEnd - votesBarWidth;
                outgoingFlow.setSourceCoordinates(leftFlowEnd, rightFlowEnd, lowerBarEnd);
                rightFlowEnd = leftFlowEnd;
            }
        }
    }

    public Element build(SVGDocument document) {
        Element svg = document.createElement("g");
        if (numberOfVotes != null) {
            Element numberOfVotesBar = document.createElement("rect");
            numberOfVotesBar.setAttribute("x", String.valueOf(baseX));
            numberOfVotesBar.setAttribute("y", String.valueOf(baseY));
            numberOfVotesBar.setAttribute("width", String.valueOf(getBarWidth(numberOfVotes)));
            numberOfVotesBar.setAttribute("height", String.valueOf(getBarHeight()));
            numberOfVotesBar.setAttribute("style", "fill:red; stroke:none");
            svg.appendChild(numberOfVotesBar);

            Element quorumBorder = document.createElement("rect");
            quorumBorder.setAttribute("x", String.valueOf(baseX));
            quorumBorder.setAttribute("y", String.valueOf(baseY));
            quorumBorder.setAttribute("width", String.valueOf(getBarWidth(quorum)));
            quorumBorder.setAttribute("height", String.valueOf(getBarHeight()));
            quorumBorder.setAttribute("fill-opacity", "0");
            quorumBorder.setAttribute("stroke", "black");
            svg.appendChild(quorumBorder);

            svg.appendChild(new TextElement()
                    .withX(baseX + (getBarWidth(quorum) / 2))
                    .withY(getTextBaseY())
                    .withText(numberOfVotes.bigDecimalValue(1, BigDecimal.ROUND_HALF_UP) + "/" + quorum.bigDecimalValue(1, BigDecimal.ROUND_HALF_UP))
                    .withFontSize(getTextFontSize())
                    .withMiddleAnchor()
                    .build(document));
        }
        return svg;
    }

    double getTextFontSize() {
        return getBarHeight() * 0.8;
    }

    double getTextBaseY() {
        return baseY + getBarHeight() - (getBarHeight() / 5);
    }

    public double getMaxOutgoingVoteFlowWidth() {
        double maxOutgoingVoteFlowWidth = 0;
        for (VoteFlow outgoingFlow : outgoingFlows) {
            maxOutgoingVoteFlowWidth = Math.max(
                    maxOutgoingVoteFlowWidth,
                    outgoingFlow.getWidth()
            );
        }

        return maxOutgoingVoteFlowWidth;
    }
}
