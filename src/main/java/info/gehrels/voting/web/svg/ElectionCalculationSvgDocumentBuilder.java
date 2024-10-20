package info.gehrels.voting.web.svg;

import com.google.common.collect.ImmutableCollection;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import info.gehrels.voting.singleTransferableVote.VoteDistribution;
import info.gehrels.voting.singleTransferableVote.VoteState;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.commons.math3.fraction.BigFraction;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.batik.util.SVGConstants.SVG_NAMESPACE_URI;

public final class ElectionCalculationSvgDocumentBuilder {
    private final long numberOfSeats;
    private final List<VoteDistributionContentRow> voteDistributions = new ArrayList<>();
    private final boolean femaleExclusive;
    private final List<GenderedCandidate> electableCandidates;
    private final FirstHeadline firstHeadline;
    private final Collection<VoteFlow> voteFlows = new ArrayList<>();

    private VoteDistributionHeadlineRow voteDistributionHeadlineRow;
    private long numberOfElectablePositions;
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
        DOMImplementation impl = GenericDOMImplementation.getDOMImplementation();
        Document document = impl.createDocument(SVG_NAMESPACE_URI, "svg", null);
        Element root = document.getDocumentElement();
        root.appendChild(firstHeadline.build(document, 0, 0));
        root.appendChild(secondHeadLine.build(document, 0, 25));

        BigFraction maxNumberOfVotes = BigFraction.ZERO;
        for (VoteDistributionContentRow voteDistribution : voteDistributions) {
            if (maxNumberOfVotes.compareTo(voteDistribution.getMaxNumberOfLocalVotes()) < 0) {
                maxNumberOfVotes = voteDistribution.getMaxNumberOfLocalVotes();
            }
        }

        double y = 60;
        voteDistributionHeadlineRow.initializeSizing(5, y);
        y += voteDistributionHeadlineRow.getHeight();

        for (VoteDistributionContentRow voteDistribution : voteDistributions) {
            voteDistribution.setGlobalMaxNumberOfVotes(maxNumberOfVotes);

            voteDistribution.initializeSizing(5, y);
            voteDistribution.initializeVoteFlowSizing();

            y += Math.max(
                    voteDistribution.getPerCandidateUsableWidth(),
                    voteDistribution.getMaxOutgoingVoteFlowWidth() * 3
            );
        }

        for (VoteFlow voteFlow : voteFlows) {
            root.appendChild(voteFlow.build(document));
        }

        root.appendChild(voteDistributionHeadlineRow.build(document));
        for (VoteDistributionContentRow voteDistribution : voteDistributions) {
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

    public void voteWeightRedistributionCompleted(ImmutableCollection<VoteState<GenderedCandidate>> originalVoteStates, ImmutableCollection<VoteState<GenderedCandidate>> newVoteStates, VoteDistribution<GenderedCandidate> voteDistribution) {
        voteDistributions.add(new VoteDistributionContentRow(voteDistribution,electableCandidates, quorum, indexOfVoteDistributions++));

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
        Map<Optional<GenderedCandidate>, VoteFlow> genderedCandidateVoteFlowMap =
                voteFlows.computeIfAbsent(
                        oldPreferredCandidate,
                        k -> new LinkedHashMap<>()
                );

        return genderedCandidateVoteFlowMap.computeIfAbsent(
                newPreferredCandidate,
                c -> new VoteFlow(oldPreferredCandidate, c)
        );
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

    public void setQuorum(long numberOfValidBallots, long numberOfSeats, BigFraction quorum) {
        this.numberOfValidBallots = numberOfValidBallots;
        this.numberOfElectablePositions = numberOfSeats;
        this.quorum = quorum;
    }

    public void initialVoteDistribution(VoteDistribution<GenderedCandidate> voteDistribution) {
        voteDistributions.add(new VoteDistributionContentRow(voteDistribution, electableCandidates, quorum, indexOfVoteDistributions++));
        secondHeadLine = new SecondHeadLine(numberOfSeats, femaleExclusive, numberOfElectablePositions, numberOfValidBallots, voteDistribution.invalidVotes, quorum);
        voteDistributionHeadlineRow = new VoteDistributionHeadlineRow(electableCandidates);
    }

    public void candidateNotQualified(GenderedCandidate candidate) {
        electableCandidates.remove(candidate);
    }

}
