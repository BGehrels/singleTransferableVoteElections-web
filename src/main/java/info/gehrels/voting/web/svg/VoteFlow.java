package info.gehrels.voting.web.svg;

import com.google.common.base.Optional;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class VoteFlow {
    private final GenderedCandidate oldPreferredCandidate;
    private final Optional<GenderedCandidate> newPreferredCandidate;
    private BigFraction voteWeight = BigFraction.ZERO;
    private double width;
    private double sourceX;
    private double sourceY;
    private double targetX;
    private double targetY;

    public VoteFlow(GenderedCandidate oldPreferredCandidate, Optional<GenderedCandidate> newPreferredCandidate) {
        this.oldPreferredCandidate = oldPreferredCandidate;
        this.newPreferredCandidate = newPreferredCandidate;
    }

    public VoteFlow addVoteWeight(BigFraction voteWeightToAdd) {
        voteWeight = voteWeight.add(voteWeightToAdd);
        return this;
    }

    public GenderedCandidate getOldPreferredCandidate() {
        return oldPreferredCandidate;
    }

    public java.util.Optional<GenderedCandidate> getNewPreferredCandidate() {
        return java.util.Optional.ofNullable(newPreferredCandidate.orNull());
    }

    public BigFraction getVoteWeight() {
        return voteWeight;
    }

    public void setSourceCoordinates(double leftFlowEnd, double rightFlowEnd, double y) {
        width = rightFlowEnd-leftFlowEnd;
        sourceX = leftFlowEnd + (width / 2);
        sourceY = y;
    }

    public void setTargetCoordinates(double leftFlowEnd, double rightFlowEnd, double y) {
        width = rightFlowEnd-leftFlowEnd;
        targetX = leftFlowEnd + (width / 2);
        targetY = y;
    }

    public Element build(Document svgDocument) {
        Element path = svgDocument.createElement("path");
        path.setAttribute("d",
                "M" + sourceX + "," + sourceY +
                " C" + sourceX + "," + targetY +
                "  " + targetX + "," + sourceY +
                "  " + targetX + "," + targetY);
                path.setAttribute("style", "stroke:#660000; fill:none; stroke-width: " + width);
        return path;
    }

    public double getWidth() {
        return width;
    }
}
