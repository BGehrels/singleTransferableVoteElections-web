package info.gehrels.voting.web.initialization;

import info.gehrels.voting.web.AdministrateBallotLayoutController;
import info.gehrels.voting.web.BallotLayoutState;
import info.gehrels.voting.web.CalculateElectionResultsController;
import info.gehrels.voting.web.CastBallotsState;
import info.gehrels.voting.web.CastVoteController;
import info.gehrels.voting.web.IndexPageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;

@Configuration
@EnableWebMvc
public class SpringConfig {
	@Autowired
	private ServletContext servletContext;

	@Autowired
	private WebApplicationContext applicationContext;

	@Bean
	public IndexPageController indexPageController() {
		return new IndexPageController();
	}

	@Bean
	public AdministrateBallotLayoutController administrateBallotLayoutController() {
		return new AdministrateBallotLayoutController(ballotLayoutState());
	}

	@Bean
	public CastVoteController castVoteController() {
		return new CastVoteController(ballotLayoutState(), castBallotsState());
	}

	@Bean
	public CastBallotsState castBallotsState() {
		return new CastBallotsState();
	}

	@Bean
	public InternalResourceViewResolver internalResourceViewResolver() {
		InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		internalResourceViewResolver.setViewClass(JstlView.class);
		internalResourceViewResolver.setPrefix("/WEB-INF/jsp/");
		internalResourceViewResolver.setSuffix(".jsp");
		return internalResourceViewResolver;
	}

	@Bean
	public BallotLayoutState ballotLayoutState() {
		return new BallotLayoutState();
	}

	@Bean
	public CalculateElectionResultsController calculateElectionResultsController() {
		return new CalculateElectionResultsController(ballotLayoutState(), castBallotsState());
	}

	@PostConstruct
	public void registerSpringMVCDispatcherServlet() {
		Dynamic servletRegistrationBuilder = servletContext
			.addServlet("springMvcDispatcherServlet", new DispatcherServlet(applicationContext));
		servletRegistrationBuilder.setLoadOnStartup(1);
		servletRegistrationBuilder.addMapping("/*");
	}
}
