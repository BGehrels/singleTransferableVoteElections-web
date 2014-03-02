package info.gehrels.voting.web.initialization;

import info.gehrels.voting.web.AdministrateBallotLayoutController;
import info.gehrels.voting.web.ManageElectionCalculationsController;
import info.gehrels.voting.web.CastVoteController;
import info.gehrels.voting.web.DeleteBallotController;
import info.gehrels.voting.web.ElectionCalculationController;
import info.gehrels.voting.web.IndexPageController;
import info.gehrels.voting.web.applicationState.BallotLayoutState;
import info.gehrels.voting.web.applicationState.CastBallotsState;
import info.gehrels.voting.web.applicationState.ElectionCalculationsState;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;
import java.util.Locale;


@Configuration
@EnableWebMvc
public class SpringConfig extends WebMvcConfigurerAdapter {
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
		return new AdministrateBallotLayoutController(ballotLayoutState(), castBallotsState());
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
	ElectionCalculationController showElectionCalculationController() {
		return new ElectionCalculationController(electionCalculationsState());
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
	public ManageElectionCalculationsController calculateElectionResultsController() {
		return new ManageElectionCalculationsController(ballotLayoutState(), castBallotsState(),
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

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/*.js").addResourceLocations("/");
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatter(new Formatter<DateTime>() {

			@Override
			public DateTime parse(String s, Locale locale) {
				return new DateTime(s);
			}

			@Override
			public String print(DateTime dateTime, Locale locale) {
				return dateTime.toString();
			}
		});
	}

	@PostConstruct
	public void registerSpringMVCDispatcherServlet() {
		Dynamic servletRegistrationBuilder = servletContext
			.addServlet("springMvcDispatcherServlet", new DispatcherServlet(applicationContext));
		servletRegistrationBuilder.setLoadOnStartup(1);
		servletRegistrationBuilder.addMapping("/*");
	}
}
