package com.github.millefoglie.component;

import com.github.millefoglie.entity.Entity;

public interface ComponentManager {
    <T extends Component> T createComponent(Entity entity, Class<T> componentClass);
}
