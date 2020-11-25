package com.github.millefoglie.gridsim.component;

import com.github.millefoglie.gridsim.entity.Entity;

import java.util.Collection;

public interface ComponentManager {
    <T extends Component> T createComponent(Entity entity, Class<T> componentClass);
    <T> Collection<T> findAllByClass(Class<T> componentClass);
}
