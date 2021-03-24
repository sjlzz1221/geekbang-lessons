package org.geektimes.context.listener;

import org.geektimes.context.ClassicComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 需要配置web.xml
 * <listener>
 *      <listener-class>org.geektimes.projects.user.web.listener.ComponentContextInitializerListener</listener-class>
 * </listener>
 * {@link ClassicComponentContext} 初始化器
 * ContextLoaderListener
 *
 * 不启用
 *
 */
public class ComponentContextInitializerListener implements ServletContextListener {

    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        ClassicComponentContext context = new ClassicComponentContext();
        context.init(servletContext);

        /* jolokia start
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = null;
        // 创建 UserMBean 实例
        User user = new User();
        user.setId(100L);
        try {
            objectName = new ObjectName("jolokia:name=User");
            mBeanServer.registerMBean(new UserManager(user), objectName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        jolokia end */
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
