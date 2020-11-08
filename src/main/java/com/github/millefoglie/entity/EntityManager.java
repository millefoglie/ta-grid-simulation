package com.github.millefoglie.entity;

import java.util.Collection;

public interface EntityManager {
    Entity createEntity(EntityType type);
    Collection<Entity> findAllByType(EntityType type);
}
