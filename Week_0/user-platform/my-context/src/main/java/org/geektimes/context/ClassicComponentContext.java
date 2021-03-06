package org.geektimes.context;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.naming.*;
import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geektimes.function.ThrowableAction;
import org.geektimes.function.ThrowableFunction;
import org.geektimes.controller.Controller;

/**
 * @author: cola
 * Java 传统组件上下文（基于 JNDI 实现）
 */
public class ClassicComponentContext implements ComponentContext {

    public static final String CONTEXT_NAME = ClassicComponentContext.class.getName();

    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

    private static final Logger logger = Logger.getLogger(CONTEXT_NAME);

    private static ServletContext servletContext; // 请注意
    // 假设一个 Tomcat JVM 进程，三个 Web Apps，会不会相互冲突？（不会冲突）
    // static 字段是 JVM 缓存吗？（是 ClassLoader 缓存）

    private Context envContext; // Component Env Context

    private ClassLoader classLoader;

    private Map<String, Object> componentsCache = new LinkedHashMap<>();

    /**
     * @PreDestroy 方法缓存，Key 为标注方法，Value 为方法所属对象
     */
    private Map<Method, Object> preDestroyMethodCache = new LinkedHashMap<>();

    private List<Controller> controllers = new LinkedList<>();

    public List<Controller> getControllers() {
        return controllers;
    }

    /**
     * 获取 ComponentContext
     *
     * @return
     */
    public static ClassicComponentContext getInstance() {
        return (ClassicComponentContext) servletContext.getAttribute(CONTEXT_NAME);
    }

    private static void close(Context context) {
        if (context != null) {
            ThrowableAction.execute(context::close);
        }
    }

    public void init(ServletContext servletContext) throws RuntimeException {
        ClassicComponentContext.servletContext = servletContext;
        servletContext.setAttribute(CONTEXT_NAME, this);
        this.init();
    }

    @Override
    public void init() {
        initClassLoader();
        initEnvContext();
        instantiateComponents();
        initializeComponents();
        initializeControllers();
        registerShutdownHook();
    }

    private void initClassLoader() {
        // 获取当前 ServletContext（WebApp）ClassLoader
        this.classLoader = servletContext.getClassLoader();
    }

    protected void initializeControllers() {
        // 通过 SPI 获取所有 Controller
        for (Controller controller : ServiceLoader.load(Controller.class)) {
            Class<?> componentClass = controller.getClass();
            // 注入阶段 - {@link Resource}
            injectComponents(controller, componentClass);
            // 查询候选方法
            List<Method> candidateMethods = findCandidateMethods(componentClass);
            // 初始阶段 - {@link PostConstruct}
            processPostConstruct(controller, candidateMethods);
            controllers.add(controller);
        }
    }

    /**
     * 初始化组件（支持 Java 标准 Commons Annotation 生命周期）
     * <ol>
     *  <li>注入阶段 - {@link Resource}</li>
     *  <li>初始阶段 - {@link PostConstruct}</li>
     *  <li>销毁阶段 - {@link PreDestroy}</li>
     * </ol>
     */
    protected void initializeComponents() {
        componentsCache.values().forEach(component -> {
            Class<?> componentClass = component.getClass();
            // 注入阶段 - {@link Resource}
            injectComponents(component, componentClass);
            // 查询候选方法
            List<Method> candidateMethods = findCandidateMethods(componentClass);
            // 初始阶段 - {@link PostConstruct}
            processPostConstruct(component, candidateMethods);
            // 本阶段处理 {@link PreDestroy} 方法元数据
            processPreDestroyMetadata(component, candidateMethods);
        });
    }

    private void injectComponents(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getDeclaredFields())
                .filter(field -> {
                    int mods = field.getModifiers();
                    return !Modifier.isStatic(mods) &&
                            field.isAnnotationPresent(Resource.class);
                }).forEach(field -> {
            Resource resource = field.getAnnotation(Resource.class);
            String resourceName = resource.name();
            Object injectedObject = lookupComponent(resourceName);
            field.setAccessible(true);
            try {
                // 注入目标对象
                field.set(component, injectedObject);
            } catch (IllegalAccessException e) {
            }
        });
    }

    /**
     * 获取组件类中的候选方法
     *
     * @param componentClass 组件类
     * @return non-null
     */
    private List<Method> findCandidateMethods(Class<?> componentClass) {
        return Stream.of(componentClass.getMethods())                     // public 方法
                .filter(method ->
                        !Modifier.isStatic(method.getModifiers()) &&      // 非 static
                                method.getParameterCount() == 0)          // 无参数
                .collect(Collectors.toList());
    }

    private void registerShutdownHook() {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            processPreDestroy();

        }));
    }

    /**
     * @param component        组件对象
     * @param candidateMethods 候选方法
     * @see #processPreDestroy()
     */
    private void processPreDestroyMetadata(Object component, List<Method> candidateMethods) {
        candidateMethods.stream()
                .filter(method -> method.isAnnotationPresent(PreDestroy.class)) // 标注 @PreDestroy
                .forEach(method -> {
                    preDestroyMethodCache.put(method, component);
                });
    }

    private void processPreDestroy() {
        for (Method preDestroyMethod : preDestroyMethodCache.keySet()) {
            // 移除集合中的对象，防止重复执行 @PreDestroy 方法
            Object component = preDestroyMethodCache.remove(preDestroyMethod);
            // 执行目标方法
            ThrowableAction.execute(() -> preDestroyMethod.invoke(component));
        }
    }

    private void processPostConstruct(Object component, List<Method> candidateMethods) {
        candidateMethods
                .stream()
                .filter(method -> method.isAnnotationPresent(PostConstruct.class))// 标注 @PostConstruct
                .forEach(method -> {
                    // 执行目标方法
                    ThrowableAction.execute(() -> method.invoke(component));
                });
    }

    private void initEnvContext() throws RuntimeException {
        if (this.envContext != null) {
            return;
        }
        Context context = null;
        try {
            context = new InitialContext();
            this.envContext = (Context) context.lookup(COMPONENT_ENV_CONTEXT_NAME);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            close(context);
        }
    }

    /**
     * 实例化组件
     */
    protected void instantiateComponents() {
        // 遍历获取所有的组件名称
        List<String> componentNames = listAllComponentNames();
        // 通过依赖查找，实例化对象（ Tomcat BeanFactory setter 方法的执行，仅支持简单类型）
        componentNames.forEach(name -> componentsCache.put(name, lookupComponent(name)));
    }

    private List<String> listAllComponentNames() {
        return listComponentNames("/");
    }

    protected List<String> listComponentNames(String name) {
        return executeInContext(context -> {
            NamingEnumeration<NameClassPair> e = executeInContext(context, ctx -> ctx.list(name), true);

            // 目录 - Context
            // 节点 -
            if (e == null) { // 当前 JNDI 名称下没有子节点
                return Collections.emptyList();
            }

            List<String> fullNames = new LinkedList<>();
            while (e.hasMoreElements()) {
                NameClassPair element = e.nextElement();
                String className = element.getClassName();
                Class<?> targetClass = classLoader.loadClass(className);
                if (Context.class.isAssignableFrom(targetClass)) {
                    // 如果当前名称是目录（Context 实现类）的话，递归查找
                    fullNames.addAll(listComponentNames(element.getName()));
                } else {
                    // 否则，当前名称绑定目标类型的话话，添加该名称到集合中
                    String fullName = name.startsWith("/") ?
                            element.getName() : name + "/" + element.getName();
                    fullNames.add(fullName);
                }
            }
            return fullNames;
        });
    }

    protected <C> C lookupComponent(String name) {
        return executeInContext(context -> (C) context.lookup(name));
    }


    @Override
    public void destroy() {

    }

    /**
     * 通过名称进行依赖查找
     *
     * @param name
     * @param <C>
     * @return
     */
    public <C> C getComponent(String name) {
        return (C) componentsCache.get(name);
    }

    @Override
    public List<String> getComponentNames() {
        return null;
    }

    /**
     * 在 Context 中执行，通过指定 ThrowableFunction 返回计算结果
     *
     * @param function ThrowableFunction
     * @param <R>      返回结果类型
     * @return 返回
     * @see ThrowableFunction#apply(Object)
     */
    protected <R> R executeInContext(ThrowableFunction<Context, R> function) {
        return executeInContext(function, false);
    }

    /**
     * 在 Context 中执行，通过指定 ThrowableFunction 返回计算结果
     *
     * @param function         ThrowableFunction
     * @param ignoredException 是否忽略异常
     * @param <R>              返回结果类型
     * @return 返回
     * @see ThrowableFunction#apply(Object)
     */
    protected <R> R executeInContext(ThrowableFunction<Context, R> function, boolean ignoredException) {
        return executeInContext(this.envContext, function, ignoredException);
    }

    private <R> R executeInContext(Context context, ThrowableFunction<Context, R> function,
                                   boolean ignoredException) {
        R result = null;
        try {
            result = ThrowableFunction.execute(context, function);
        } catch (Throwable e) {
            if (ignoredException) {
                logger.warning(e.getMessage());
            } else {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

}
