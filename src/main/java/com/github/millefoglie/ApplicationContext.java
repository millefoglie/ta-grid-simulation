package com.github.millefoglie;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
  Poor-man application context for injecting global dependencies like managers or event buses
 */
@SuppressWarnings("unchecked")
public class ApplicationContext {
    private static final ApplicationContext INSTANCE = new ApplicationContext();
    private final Map<Class<?>, Object> beans = new HashMap<>();
    private ApplicationContext() {}

    public static ApplicationContext getInstance() {
        return INSTANCE;
    }

    public <T> void registerBean(Class<T> clazz, T bean) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(bean);
        beans.put(clazz, bean);
    }

    public <T> T getBean(Class<T> clazz) {
        T bean = (T) beans.get(clazz);

        if ((bean == null) && (clazz != null)) {
            throw new NullPointerException("Bean of type " + clazz + " was not registered");
        }

        return bean;
    }
}
