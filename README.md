# Test Assignment : Grid Simulation

## Description

This app is a basic simulation of a turn-based movement of entities that are
snapped to a grid. Like in classic RTS games, there are entities position on
a grid that move around in a predefined way remaining within the grid boundaries
and avoiding collisions (sharing same position) between each other.

The grid is a rectangular set of points. Each entity is located in one of
the points, has an orientation and can either move one step forward or turn
left or right within one simulation round. This movement scenario is read from
a file and the simulation ends with printing final positions of all the entities.

## Implementation Details

Since the application is basically an imitation of video game mechanics, the
ECS architecture is used to break the code into meaningful and maintainable
modules. In short, entities - are simple containers for components,
that represent different aspects of the game and encapsulate related functionality
(it can be transformation, animation, sound, rendering, etc.) and systems contain
actual business logic related to corresponding components. All the communication
between components and systems of different domains is done with events. And
if during a simulation round there are no events (no more movements are queued)
then the simulation is stopped.

To make things easier, operations between modules are done using corresponding
manager objects and a very simple application context is added to aid managing
dependencies and avoid using a completed DI library for a project of such a
small scale.

Roughly speaking, the application runs this way:
- A game engine is initialized and scenario parser registers new entities and
components
- Each entity has a scheduler component for queueing its moves and
a transformation component for managing its positioning
- In each round the scheduler system polls next movement from the components 
and registers a sequence of movement requests in the event bus
- The transformation system reacts to requested movements, stages new positions
and runs boundary and collision checks. If there is an invalid movement, then
it gets reverted and the check is propagated to any components located at the
original position
- Once the movement queues are emptied, the game gets stopped and final positions
are printed to the system output

As the systems normally traverse big collections of components, parallel streams
are used where possible to utilize all CPU cores during this process.

## Notes

This case was not clear in the specification. If two entities are located at
adjacent points and are facing each other, then forward movement is considered
invalid for both of them, i.e. swapping adjacent position is forbidden in
the given implementation. 

Tests only cover non-trivial functionality, movement processing in particular.

# Run

```bash
mvn package
java -jar target/ta-grid-simulation-1.0-SNAPSHOT-jar-with-dependencies.jar $FILE
```

where $FILE is path to the simulation scenario file.
By default _scenario.txt_ is used if the argument is omitted.
