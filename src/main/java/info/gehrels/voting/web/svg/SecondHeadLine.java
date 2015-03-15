package info.gehrels.voting.web.svg;

import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

public final class SecondHeadLine {
    private final long numberOfSeats;
    private final boolean femaleExclusive;
    private final long numberOfElectablePostitions;
    private final long numberOfValidBallots;
    private final BigFraction invalidVotes;
    private final BigFraction quorum;

    public SecondHeadLine(long numberOfSeats, boolean femaleExclusive, long numberOfElectablePostitions, long numberOfValidBallots, BigFraction invalidVotes, BigFraction quorum) {
        this.numberOfSeats = numberOfSeats;
        this.femaleExclusive = femaleExclusive;
        this.numberOfElectablePostitions = numberOfElectablePostitions;
        this.numberOfValidBallots = numberOfValidBallots;
        this.invalidVotes = invalidVotes;
        this.quorum = quorum;
    }


    public Element build(SVGDocument document, int baseX, int baseY) {
        String textContent = numberOfSeats + " " + (femaleExclusive ? "Frauenplätze" : "offene Plätze") +
                ", davon sind " + numberOfElectablePostitions + " zu besetzen. " +
                "Es gibt " + numberOfValidBallots + " abgegebene gültige Stimmzettel und " +
                invalidVotes + " ungültige Stimmen." +
                (quorum != null ? " Das Quorum liegt daher bei " + Double.toString(quorum.doubleValue()) : "") + ".";
        return new TextElement().withX(baseX).withY((baseY + 20)).withText(textContent).build(document);
    }
}
