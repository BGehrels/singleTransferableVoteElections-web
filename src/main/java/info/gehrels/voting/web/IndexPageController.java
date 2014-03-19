package info.gehrels.voting.web;

import info.gehrels.voting.web.applicationState.CastBallotsState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexPageController {

	private final CastBallotsState castBallotsState;

	public IndexPageController(CastBallotsState castBallotsState) {
		this.castBallotsState = castBallotsState;
	}

	@RequestMapping(value= "/", method = {RequestMethod.GET, RequestMethod.HEAD})
	protected final ModelAndView doGet() {
		return new ModelAndView("indexPage", "numberOfCastBallots", new NumberOfCastBallotsBean(castBallotsState.getFirstTryCastBallots().size(),castBallotsState.getSecondTryCastBallots().size()));
	}

	public static final class NumberOfCastBallotsBean {

		private final int firstTry;
		private final int secondTry;

		public NumberOfCastBallotsBean(int firstTry, int secondTry) {

			this.firstTry = firstTry;
			this.secondTry = secondTry;
		}

		public int getFirstTry() {
			return firstTry;
		}

		public int getSecondTry() {
			return secondTry;
		}
	}
}
