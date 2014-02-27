package info.gehrels.voting.web;

import info.gehrels.voting.AmbiguityResolver.AmbiguityResolverResult;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.web.AsyncElectionCalculation.Snapshot;
import info.gehrels.voting.web.applicationState.ElectionCalculationsState;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public final class ElectionCalculationController {
	private final ElectionCalculationsState electionCalculationsState;

	public ElectionCalculationController(ElectionCalculationsState electionCalculationsState) {
		this.electionCalculationsState = electionCalculationsState;
	}

	@RequestMapping(value = "/showElectionCalculation", method = {GET})
	public ModelAndView showElectionCalculation(@RequestParam DateTime dateTimeTheCalculationStarted) {
		Snapshot snapshot = electionCalculationsState.getHistoryOfElectionCalculations()
			.get(dateTimeTheCalculationStarted).getSnapshot();

		return createModelAndView(snapshot, new AmbiguityResolverResultBuilderBean());
	}

	@RequestMapping(value = "/resolveAmbiguity", method = {POST})
	public ModelAndView resolveAmbiguity(@RequestParam DateTime dateTimeTheCalculationStarted,
	                                     @Valid AmbiguityResolverResultBuilderBean ambiguityResolverResultBuilder,
	                                     BindingResult bindingResult) {
		AsyncElectionCalculation asyncElectionCalculation = electionCalculationsState.getHistoryOfElectionCalculations()
			.get(dateTimeTheCalculationStarted);
		Snapshot snapshot = asyncElectionCalculation.getSnapshot();

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
			asyncElectionCalculation.setAmbiguityResulutionResult(build);
			RedirectView redirectView = new RedirectView("/showElectionCalculation");
			redirectView.setExposeModelAttributes(true);
			RedirectAttributesModelMap redirectAttributesModelMap = new RedirectAttributesModelMap();
			redirectAttributesModelMap.put("dateTimeTheCalculationStarted", dateTimeTheCalculationStarted);
			return new ModelAndView(redirectView, redirectAttributesModelMap);
		}
	}


	private ModelAndView createModelAndView(Snapshot snapshot,
	                                        AmbiguityResolverResultBuilderBean ambiguityResolverResultBuilder) {
		ModelAndView modelAndView = new ModelAndView("showElectionCalculation");
		modelAndView.addObject("electionCalculation", snapshot);
		modelAndView.addObject("ambiguityResolverResultBuilder", ambiguityResolverResultBuilder);
		return modelAndView;
	}

}
