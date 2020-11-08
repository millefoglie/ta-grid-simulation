package com.github.millefoglie;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class ApplicationContext {
    private static final ApplicationContext INSTANCE = new ApplicationContext();
    private final Map<Class, Object> beans = new HashMap<>();
    private ApplicationContext() {}

    public static ApplicationContext getInstance() {
        return INSTANCE;
    }

    public <T> void registerBean(Class<T> clazz, T bean) {
        beans.put(clazz, bean);
    }

    public <T> T getBean(Class<T> clazz) {
        return (T) beans.get(clazz);
    }
}
