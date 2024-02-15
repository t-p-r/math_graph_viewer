package model.exception;

import java.io.IOException;

public class GraphFileCorruptedException extends IOException {
    public GraphFileCorruptedException() {
        super("This graph file is possibly corrupted.");
    }
}
