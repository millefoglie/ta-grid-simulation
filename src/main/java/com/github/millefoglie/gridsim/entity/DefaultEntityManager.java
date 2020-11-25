package com.github.millefoglie.gridsim.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultEntityManager implements EntityManager {
    private final AtomicInteger idSequence = new AtomicInteger(0);
    private final Map<EntityType, List<Entity>> entityRegistry = new HashMap<>();

    @Override
    public Entity createEntity(EntityType type) {
        Entity entity = new DefaultEntity(idSequence.getAndIncrement(), type);
        List<Entity> entitiesOfType = entityRegistry.computeIfAbsent(type, t -> new CopyOnWriteArrayList<>());

        entitiesOfType.add(entity);
        return entity;
    }

    @Override
    public Collection<Entity> findAllByType(EntityType type) {
        return entityRegistry.getOrDefault(type, Collections.emptyList());
    }
}
