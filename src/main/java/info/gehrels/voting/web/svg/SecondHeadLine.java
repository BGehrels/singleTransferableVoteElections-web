package info.gehrels.voting.web.svg;

import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class SecondHeadLine {
    private final long numberOfSeats;
    private final boolean femaleExclusive;
    private final long numberOfElectablePositions;
    private final long numberOfValidBallots;
    private final BigFraction invalidVotes;
    private final BigFraction quorum;

    public SecondHeadLine(long numberOfSeats, boolean femaleExclusive, long numberOfElectablePositions, long numberOfValidBallots, BigFraction invalidVotes, BigFraction quorum) {
        this.numberOfSeats = numberOfSeats;
        this.femaleExclusive = femaleExclusive;
        this.numberOfElectablePositions = numberOfElectablePositions;
        this.numberOfValidBallots = numberOfValidBallots;
        this.invalidVotes = invalidVotes;
        this.quorum = quorum;
    }


    public Element build(Document document, int baseX, int baseY) {
        String textContent = numberOfSeats + " " + (femaleExclusive ? "Frauenplätze" : "offene Plätze") +
                ", davon sind " + numberOfElectablePositions + " zu besetzen. " +
                "Es gibt " + numberOfValidBallots + " abgegebene gültige Stimmzettel und " +
                invalidVotes + " ungültige Stimmen." +
                (quorum != null ? " Das Quorum liegt daher bei " + Double.toString(quorum.doubleValue()) : "") + ".";
        return new TextElement().withX(baseX).withY((baseY + 20)).withText(textContent).build(document);
    }
}
