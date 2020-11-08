package com.github.millefoglie.exception;

public class GameException extends RuntimeException {
    public GameException(String message) {
        super(message);
    }

    public static GameException couldNotTransform() {
        return new GameException("Could not perform transformation");
    }
}
