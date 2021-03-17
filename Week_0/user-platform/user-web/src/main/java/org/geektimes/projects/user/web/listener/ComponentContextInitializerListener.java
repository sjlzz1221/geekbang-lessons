package org.geektimes.projects.user.web.listener;

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.config.DefaultConfigProviderResolver;
import org.geektimes.config.JavaConfig;
import org.geektimes.config.source.PropertiesConfigSource;
import org.geektimes.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.management.UserManager;

import javax.management.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * {@link ComponentContext} 初始化器
 * ContextLoaderListener
 */
public class ComponentContextInitializerListener implements ServletContextListener {

    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        ComponentContext context = new ComponentContext();
        context.init(servletContext);

        ServiceLoader<ConfigProviderResolver> serviceLoader = ServiceLoader.load(ConfigProviderResolver.class);
        Iterator<ConfigProviderResolver> iterator = serviceLoader.iterator();
        Long userId = null;
        if (iterator.hasNext()) {
            userId = iterator.next().getConfig().getValue("user.id", Long.class);
        }

        // jolokia
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = null;
        // 创建 UserMBean 实例
        User user = new User();
        user.setId(userId);
        try {
            objectName = new ObjectName("jolokia:name=User");
            mBeanServer.registerMBean(new UserManager(user), objectName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
