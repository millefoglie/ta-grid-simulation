package com.github.millefoglie.exception;

public class FileReadException extends RuntimeException {

    public FileReadException(String message) {
        super(message);
    }

    public static FileReadException couldNotParseScenarioFile() {
        return new FileReadException("Could not parse scenario file");
    }
}
