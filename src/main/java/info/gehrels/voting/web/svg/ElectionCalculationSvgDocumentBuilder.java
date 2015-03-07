package info.gehrels.voting.web.svg;

import com.google.common.collect.ImmutableCollection;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import info.gehrels.voting.singleTransferableVote.VoteDistribution;
import info.gehrels.voting.singleTransferableVote.VoteState;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public final class ElectionCalculationSvgDocumentBuilder {
    private final long numberOfSeats;
    private final List<VoteDistributionSvg> voteDistributions = new ArrayList<>();
    private final boolean femaleExclusive;
    private final List<GenderedCandidate> electableCandidates;
    private final FirstHeadline firstHeadline;

    private long numberOfElectablePostitions;
    private long numberOfValidBallots;
    private BigFraction quorum;
    private SecondHeadLine secondHeadLine;

    public ElectionCalculationSvgDocumentBuilder(GenderedElection election, boolean femaleExclusive) {
        this.electableCandidates = new ArrayList<>(election.getCandidates());
        this.femaleExclusive = femaleExclusive;
        if (femaleExclusive) {
            numberOfSeats = election.getNumberOfFemaleExclusivePositions();
        } else {
            numberOfSeats = election.getNumberOfNotFemaleExclusivePositions();
        }
        firstHeadline = new FirstHeadline(election.getOfficeName());
    }

    public String build() {
        DOMImplementation impl = new SVGDOMImplementation();
        SVGDocument document = (SVGDocument) impl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        SVGSVGElement root = document.getRootElement();
        root.appendChild(firstHeadline.build(document, 0, 0));;
        root.appendChild(secondHeadLine.build(document, 0, 25));

        int i = 0;
        for (VoteDistributionSvg voteDistribution : voteDistributions) {
            root.appendChild(voteDistribution.build(document, 0, (60 + (2 * 20 * i)), electableCandidates));
            i++;
        }

        StringWriter writer = new StringWriter();
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(writer));
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
        return writer.toString();
    }


    public void started() {
    }

    public void dropCandidate(GenderedCandidate candidate) {

    }

    public void voteWeightRedistributionCompleted(ImmutableCollection<VoteState<GenderedCandidate>> originalVoteStates, ImmutableCollection<VoteState<GenderedCandidate>> newVoteStates, VoteDistribution<GenderedCandidate> voteDistribution) {
        voteDistributions.add(new VoteDistributionSvg(voteDistribution, quorum));
    }

    public void markCandidateElected(GenderedCandidate winner, BigFraction numberOfVotes, BigFraction quorum) {

    }

    public void setQuorum(long numberOfValidBallots, long numberOfSeats, BigFraction quorum) {
        this.numberOfValidBallots = numberOfValidBallots;
        this.numberOfElectablePostitions = numberOfSeats;
        this.quorum = quorum;
    }

    public void initialVoteDistribution(VoteDistribution<GenderedCandidate> voteDistribution) {
        voteDistributions.add(new VoteDistributionSvg(voteDistribution, quorum));
        secondHeadLine = new SecondHeadLine(numberOfSeats, femaleExclusive, numberOfElectablePostitions, numberOfValidBallots, voteDistribution.invalidVotes, quorum);
    }

    public void candidateNotQualified(GenderedCandidate candidate) {
        electableCandidates.remove(candidate);
    }

}
