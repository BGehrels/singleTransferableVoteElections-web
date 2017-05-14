/*
 * Copyright © 2014 Benjamin Gehrels
 *
 * This file is part of The Single Transferable Vote Elections Web Interface.
 *
 * The Single Transferable Vote Elections Web Interface is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * The Single Transferable Vote Elections Web Interface is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with The Single Transferable Vote
 * Elections Web Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package info.gehrels.voting.web.resultCalculation;

import com.google.common.base.Optional;
import info.gehrels.voting.AmbiguityResolver.AmbiguityResolverResult;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.web.applicationState.ElectionCalculationsState;
import info.gehrels.voting.web.auditLogging.JsonAuditLog;
import info.gehrels.voting.web.svg.SvgCreatingAuditLogListener;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public final class ElectionCalculationController {
    private final ElectionCalculationsState electionCalculationsState;

    public ElectionCalculationController(ElectionCalculationsState electionCalculationsState) {
        this.electionCalculationsState = electionCalculationsState;
    }

    @RequestMapping(value = "/showElectionCalculation", method = GET)
    public ModelAndView showElectionCalculation(@RequestParam DateTime dateTimeTheCalculationStarted) {
        AsyncElectionCalculation.Snapshot snapshot = electionCalculationsState.getHistoryOfElectionCalculations()
                .get(dateTimeTheCalculationStarted).getSnapshot();

        return createModelAndView(snapshot, new AmbiguityResolverResultBuilderBean());
    }

    @RequestMapping(value = "/downloadElectionCalculationAsJson", method = GET, produces = "application/json")
    @ResponseBody
    public String downloadElectionCalculationAsJson(@RequestParam DateTime dateTimeTheCalculationStarted, String office) {
        AsyncElectionCalculation.Snapshot snapshot = electionCalculationsState.getHistoryOfElectionCalculations()
                .get(dateTimeTheCalculationStarted).getSnapshot();

        Optional<ElectionCalculationResultBean> resultOfFinishedCalculation = snapshot
                .getResultOfFinishedCalculation(office);
        if (!resultOfFinishedCalculation.isPresent()) {
            throw new IllegalArgumentException("The given Office name does not exist");
        }
        JsonAuditLog jsonAuditLog = new JsonAuditLog();
        resultOfFinishedCalculation.get().getAuditLog().replay(jsonAuditLog);
        return jsonAuditLog.getResult().toString();
    }

    @RequestMapping(value = "/downloadElectionCalculationAsSvg", method = GET, produces = "image/svg+xml")
    @ResponseBody
    public String downloadElectionCalculationAsSvg(@RequestParam DateTime dateTimeTheCalculationStarted, String office, boolean femaleExclusiveRun) {
        AsyncElectionCalculation.Snapshot snapshot = electionCalculationsState.getHistoryOfElectionCalculations()
                .get(dateTimeTheCalculationStarted).getSnapshot();

        Optional<ElectionCalculationResultBean> resultOfFinishedCalculation = snapshot
                .getResultOfFinishedCalculation(office);
        if (!resultOfFinishedCalculation.isPresent()) {
            throw new IllegalArgumentException("The given Office name does not exist");
        }
        SvgCreatingAuditLogListener svgCreatingAuditLogListener = new SvgCreatingAuditLogListener();
        resultOfFinishedCalculation.get().getAuditLog().replay(svgCreatingAuditLogListener);
        String result;
        if (femaleExclusiveRun) {
            result = svgCreatingAuditLogListener.getFemaleExclusiveRunSvg();
        } else {
            result = svgCreatingAuditLogListener.getNonFemaleExclusiveRunSvg();
        }
        return result;
    }

    @RequestMapping(value = "/resolveAmbiguity", method = POST)
    public ModelAndView resolveAmbiguity(@RequestParam DateTime dateTimeTheCalculationStarted,
                                         @Valid AmbiguityResolverResultBuilderBean ambiguityResolverResultBuilder,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes) {
        AsyncElectionCalculation asyncElectionCalculation = electionCalculationsState.getHistoryOfElectionCalculations()
                .get(dateTimeTheCalculationStarted);
        AsyncElectionCalculation.Snapshot snapshot = asyncElectionCalculation.getSnapshot();

        AmbiguityResolverResult<GenderedCandidate> build = null;
        if (!bindingResult.hasErrors()) {
            try {
                build = ambiguityResolverResultBuilder.build(
                        snapshot.getAmbiguityResulutionTask().getCandidatesToChooseFrom());
            } catch (NoSuchCandidateException e) {
                bindingResult.addError(new ObjectError("unknownCandidate.ambiguityResolverResultBuilder.candidateName",
                        "The candidate " + e.candidateName + " is unknown"));
            }
        }

        if (bindingResult.hasErrors()) {
            return createModelAndView(snapshot, ambiguityResolverResultBuilder);
        } else {
            asyncElectionCalculation.setAmbiguityResolutionResult(build);
            redirectAttributes.addAttribute("dateTimeTheCalculationStarted", dateTimeTheCalculationStarted);
            return new ModelAndView("redirect:/showElectionCalculation");
        }
    }


    private ModelAndView createModelAndView(AsyncElectionCalculation.Snapshot snapshot,
                                            AmbiguityResolverResultBuilderBean ambiguityResolverResultBuilder) {
        ModelAndView modelAndView = new ModelAndView("showElectionCalculation");
        modelAndView.addObject("electionCalculation", snapshot);
        modelAndView.addObject("ambiguityResolverResultBuilder", ambiguityResolverResultBuilder);
        return modelAndView;
    }

}
