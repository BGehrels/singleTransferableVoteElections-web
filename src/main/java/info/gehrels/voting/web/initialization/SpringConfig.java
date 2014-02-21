package info.gehrels.voting.web.initialization;

import info.gehrels.voting.web.AdministrateBallotLayoutController;
import info.gehrels.voting.web.applicationState.BallotLayoutState;
import info.gehrels.voting.web.CalculateElectionResultsController;
import info.gehrels.voting.web.applicationState.CastBallotsState;
import info.gehrels.voting.web.CastVoteController;
import info.gehrels.voting.web.DeleteBallotController;
import info.gehrels.voting.web.IndexPageController;
import info.gehrels.voting.web.applicationState.ElectionCalculationsState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
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
	public DeleteBallotController deleteBallotController() {
		return new DeleteBallotController(castBallotsState());
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
		return new CalculateElectionResultsController(ballotLayoutState(), castBallotsState(),
		                                              electionCalculationsState());
	}

	@Bean
	public ElectionCalculationsState electionCalculationsState() {
		return new ElectionCalculationsState();
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("/info/gehrels/voting/web/messages");
		return messageSource;
	}

	@PostConstruct
	public void registerSpringMVCDispatcherServlet() {
		Dynamic servletRegistrationBuilder = servletContext
			.addServlet("springMvcDispatcherServlet", new DispatcherServlet(applicationContext));
		servletRegistrationBuilder.setLoadOnStartup(1);
		servletRegistrationBuilder.addMapping("/*");
	}
}
