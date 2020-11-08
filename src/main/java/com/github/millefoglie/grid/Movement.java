package com.github.millefoglie.grid;

public enum Movement {
    LEFT, RIGHT, FORWARD;

    public static Movement from(char ch) {
        switch (ch) {
        case 'L':
            return LEFT;
        case 'R':
            return RIGHT;
        case 'F':
            return FORWARD;
        }

        throw new IllegalArgumentException("Unknown movement: " + ch);
    }
}
