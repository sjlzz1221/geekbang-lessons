package org.geektimes.context;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * @author: cola
 */
public class ServletComponentInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        // 增加 ServletContextListener
        ctx.addListener(ServletContextComponentInitializer.class);
    }
}
