package info.gehrels.voting.web;

import info.gehrels.voting.web.applicationState.BallotLayoutState;
import info.gehrels.voting.web.applicationState.CastBallotsState;
import info.gehrels.voting.web.applicationState.ElectionCalculationsState;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;


@Configuration
@EnableAutoConfiguration
public class SpringConfig {

    public static void main(String[] args) {
        SpringApplication.run(SpringConfig.class, args);
    }

	@Bean
	public IndexPageController indexPageController() {
		return new IndexPageController(castBallotsState());
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
	public Formatter dateTimeFormatter() {
		return new DateTimeFormatter();
	}

}
