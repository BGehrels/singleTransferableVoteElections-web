package info.gehrels.voting.web.initialization;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContextEvent;

public class MyContextLoaderListener extends ContextLoaderListener {
	public MyContextLoaderListener() {
		super(createRootApplicationContext());
	}

	private static WebApplicationContext createRootApplicationContext() {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(SpringConfig.class);
		return rootContext;
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);

		event.getServletContext().getServletRegistrations().get("jsp").addMapping("/WEB-INF/jsp/*");
	}
}
