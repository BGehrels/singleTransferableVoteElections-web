package info.gehrels.voting.web.initialization;

import info.gehrels.voting.web.AdministrateBallotLayoutController;
import info.gehrels.voting.web.CastVoteController;
import info.gehrels.voting.web.IndexPageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

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
		return new AdministrateBallotLayoutController();
	}

	@Bean
	public CastVoteController castVoteController() {
		return new CastVoteController();
	}

	@Bean
	public SpringTemplateEngine springTemplateEngine() {
		TemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setPrefix("/thymeleaf/");
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(false);

		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		return templateEngine;
	}

	@Bean
	public ThymeleafViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		thymeleafViewResolver.setTemplateEngine(springTemplateEngine());
		thymeleafViewResolver.setCharacterEncoding("UTF-8");
		return thymeleafViewResolver;
	}

	@PostConstruct
	public void registerSpringMVCDispatcherServlet() {
		Dynamic servletRegistrationBuilder = servletContext
			.addServlet("springMvcDispatcherServlet", new DispatcherServlet(applicationContext));
		servletRegistrationBuilder.setLoadOnStartup(1);
		servletRegistrationBuilder.addMapping("/*");
	}
}
