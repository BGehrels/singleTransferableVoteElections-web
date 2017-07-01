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
package info.gehrels.voting.web;

import com.google.common.collect.ImmutableMap;
import info.gehrels.voting.web.applicationState.BallotLayoutState;
import info.gehrels.voting.web.applicationState.CastBallotsState;
import info.gehrels.voting.web.applicationState.ElectionCalculationsState;
import info.gehrels.voting.web.ballotCasting.CastVoteController;
import info.gehrels.voting.web.ballotLayoutAdministration.CreateBallotLayoutController;
import info.gehrels.voting.web.resultCalculation.DeleteBallotController;
import info.gehrels.voting.web.resultCalculation.ElectionCalculationController;
import info.gehrels.voting.web.resultCalculation.ManageElectionCalculationsController;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

import static javax.servlet.DispatcherType.REQUEST;


@Configuration
@EnableAutoConfiguration
public class SpringConfig implements ServletContextInitializer {
    public static void main(String... args) {
	    SpringApplication springApplication = new SpringApplication(SpringConfig.class);
	    springApplication.setBannerMode(Banner.Mode.OFF);
	    springApplication.setDefaultProperties(
		    ImmutableMap.<String, Object>builder()
			    .put("logging.level.", "WARN")
			    .put("logging.level.ServerStarted", "INFO")
			    .build());
	    springApplication.addListeners(new ServerStartedMessagePrintingListener());
		springApplication.run(args);
    }

	@Bean
	public IndexPageController indexPageController() {
		return new IndexPageController(castBallotsState());
	}

	@Bean
	public CreateBallotLayoutController createBallotLayoutController() {
		return new CreateBallotLayoutController(ballotLayoutState(), castBallotsState());
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

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		servletContext.addFilter("characterEncodingFilter", filter).addMappingForUrlPatterns(EnumSet.of(REQUEST), false, "/*");
	}

}
