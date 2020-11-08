package com.github.millefoglie.grid;

import org.apache.commons.lang3.StringUtils;

public enum Orientation {
    NORTH, EAST, WEST, SOUTH;

    public static Orientation from(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }

        switch (str) {
        case "N":
            return NORTH;
        case "E":
            return EAST;
        case "W":
            return WEST;
        case "S":
            return SOUTH;
        }

        throw new IllegalArgumentException("Unknown orientation: " + str);
    }
}
