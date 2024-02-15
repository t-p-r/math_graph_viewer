package model.exception;

public class GraphFileCorruptedException extends GraphException {
    public GraphFileCorruptedException() {
        super("This graph file is possibly corrupted.");
    }
}
