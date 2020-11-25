package com.github.millefoglie.gridsim.entity;

import java.util.Collection;

public interface EntityManager {
    Entity createEntity(EntityType type);
    Collection<Entity> findAllByType(EntityType type);
}
