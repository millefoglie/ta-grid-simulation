package com.github.millefoglie.gridsim.entity;

import com.github.millefoglie.gridsim.component.Component;

public interface Entity {
    int getId();
    EntityType getType();
    <T extends Component> void setComponent(Class<T> componentClass, T component);
    <T extends Component> T getComponent(Class<T> componentClass);
}
