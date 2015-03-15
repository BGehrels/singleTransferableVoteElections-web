package info.gehrels.voting.web.svg;

import com.google.common.base.Optional;
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
import java.util.*;

public final class ElectionCalculationSvgDocumentBuilder {
    private final long numberOfSeats;
    private final List<VoteDistributionSvg> voteDistributions = new ArrayList<>();
    private final boolean femaleExclusive;
    private final List<GenderedCandidate> electableCandidates;
    private final FirstHeadline firstHeadline;
    private final Collection<VoteFlow> voteFlows = new ArrayList<>();

    private long numberOfElectablePostitions;
    private long numberOfValidBallots;
    private BigFraction quorum;
    private SecondHeadLine secondHeadLine;
    private int indexOfVoteDistributions = 0;

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
        root.appendChild(firstHeadline.build(document, 0, 0));
        root.appendChild(secondHeadLine.build(document, 0, 25));

        BigFraction maxNumberOfVotes = BigFraction.ZERO;
        for (VoteDistributionSvg voteDistribution : voteDistributions) {
            if (maxNumberOfVotes.compareTo(voteDistribution.getMaxNumberOfLocalVotes()) < 0) {
                maxNumberOfVotes = voteDistribution.getMaxNumberOfLocalVotes();
            }
        }

        double y = 60;
        for (VoteDistributionSvg voteDistribution : voteDistributions) {
            voteDistribution.setGlobalMaxNumberOfVotes(maxNumberOfVotes);

            voteDistribution.initializeSizing(5, y);
            voteDistribution.initializeVoteFlowSizing();

            y += Math.max(
                    voteDistribution.getHeight(),
                    voteDistribution.getMaxOutgoingVoteFlowWidth() * 3
            );
        }

        for (VoteFlow voteFlow : voteFlows) {
            root.appendChild(voteFlow.build(document));
        }

        for (VoteDistributionSvg voteDistribution : voteDistributions) {
            root.appendChild(voteDistribution.build(document));
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
        voteDistributions.add(new VoteDistributionSvg(voteDistribution,electableCandidates, quorum, indexOfVoteDistributions++));

        Collection<VoteFlow> calculatedVoteFlows = calculateVoteFlows(originalVoteStates, newVoteStates);
        for (VoteFlow voteFlow : calculatedVoteFlows) {
            voteFlows.add(voteFlow);
            voteDistributions.get(voteDistributions.size()-2).registerOutgoingFlow(voteFlow);
            voteDistributions.get(voteDistributions.size()-1).registerIncomingFlow(voteFlow);
        }
    }

    private Collection<VoteFlow> calculateVoteFlows(ImmutableCollection<VoteState<GenderedCandidate>> originalVoteStates, ImmutableCollection<VoteState<GenderedCandidate>> newVoteStates) {
        Map<GenderedCandidate, Map<Optional<GenderedCandidate>, VoteFlow>> calculatedVoteFlows = new LinkedHashMap<>();
        for (VoteState<GenderedCandidate> originalVoteState : originalVoteStates) {
            VoteState<GenderedCandidate> newVoteState = getById(newVoteStates, originalVoteState.getBallotId());
            if (!sameState(originalVoteState, newVoteState)) {
                VoteFlow voteFlow = getOrCreateVoteFlow(
                        calculatedVoteFlows,
                        originalVoteState.getPreferredCandidate().get(),
                        newVoteState.getPreferredCandidate());
                voteFlow.addVoteWeight(newVoteState.getVoteWeight());
            }
        }
        return flatten(calculatedVoteFlows);
    }

    private Collection<VoteFlow> flatten(Map<GenderedCandidate, Map<Optional<GenderedCandidate>, VoteFlow>> calculatedVoteFlows) {
        Collection<Map<Optional<GenderedCandidate>, VoteFlow>> values = calculatedVoteFlows.values();
        Collection<VoteFlow> result = new ArrayList<>();
        for (Map<Optional<GenderedCandidate>, VoteFlow> value : values) {
            result.addAll(value.values());
        }
        return result;
    }

    private VoteFlow getOrCreateVoteFlow(Map<GenderedCandidate, Map<Optional<GenderedCandidate>, VoteFlow>> voteFlows, GenderedCandidate oldPreferredCandidate, Optional<GenderedCandidate> newPreferredCandidate) {
        Map<Optional<GenderedCandidate>, VoteFlow> genderedCandidateVoteFlowMap = voteFlows.get(oldPreferredCandidate);
        if (genderedCandidateVoteFlowMap == null) {
            genderedCandidateVoteFlowMap = new LinkedHashMap<>();
            voteFlows.put(oldPreferredCandidate, genderedCandidateVoteFlowMap);
        }

        VoteFlow voteFlow = genderedCandidateVoteFlowMap.get(newPreferredCandidate);
        if (voteFlow == null) {
            voteFlow = new VoteFlow(oldPreferredCandidate, newPreferredCandidate);
            genderedCandidateVoteFlowMap.put(newPreferredCandidate, voteFlow);
        }
        return voteFlow;
    }

    private boolean sameState(VoteState<GenderedCandidate> originalVoteState, VoteState<GenderedCandidate> newVoteState) {
        return (originalVoteState.isInvalid() == newVoteState.isInvalid()) &&
        (originalVoteState.isNoVote() == newVoteState.isNoVote()) &&
        originalVoteState.getPreferredCandidate().equals(newVoteState.getPreferredCandidate());
    }

    private VoteState<GenderedCandidate> getById(ImmutableCollection<VoteState<GenderedCandidate>> newVoteStates, long ballotId) {
        for (VoteState<GenderedCandidate> newVoteState : newVoteStates) {
            if (newVoteState.getBallotId() == ballotId) {
                return newVoteState;
            }
        }

        throw new IllegalArgumentException("Ballot No. " + ballotId + " does not exist");
    }

    public void markCandidateElected(GenderedCandidate winner, BigFraction numberOfVotes, BigFraction quorum) {

    }

    public void setQuorum(long numberOfValidBallots, long numberOfSeats, BigFraction quorum) {
        this.numberOfValidBallots = numberOfValidBallots;
        this.numberOfElectablePostitions = numberOfSeats;
        this.quorum = quorum;
    }

    public void initialVoteDistribution(VoteDistribution<GenderedCandidate> voteDistribution) {
        voteDistributions.add(new VoteDistributionSvg(voteDistribution, electableCandidates, quorum, indexOfVoteDistributions++));
        secondHeadLine = new SecondHeadLine(numberOfSeats, femaleExclusive, numberOfElectablePostitions, numberOfValidBallots, voteDistribution.invalidVotes, quorum);
    }

    public void candidateNotQualified(GenderedCandidate candidate) {
        electableCandidates.remove(candidate);
    }

}
