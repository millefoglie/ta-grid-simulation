package com.github.millefoglie.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultEntityManager implements EntityManager {
    private final AtomicInteger idSequence = new AtomicInteger(0);
    private final Map<EntityType, List<Entity>> entityRegistry = new HashMap<>();

    @Override
    public Entity createEntity(EntityType type) {
        Entity entity = new DefaultEntity(idSequence.getAndIncrement(), type);
        List<Entity> entitiesOfType = entityRegistry.computeIfAbsent(type, t -> new ArrayList<>());

        entitiesOfType.add(entity);
        return entity;
    }
}
