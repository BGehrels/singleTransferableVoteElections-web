package info.gehrels.voting.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexPageController {

	@RequestMapping(value= "/", method = {RequestMethod.GET, RequestMethod.HEAD})
	protected final String doGet() {
		return "indexPage";
	}

}
