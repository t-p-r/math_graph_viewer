package model.exception;

public class MissingLabelException extends GraphException {
    public MissingLabelException() {
        super("No vertex with this label currently exists in the graph.");
    }
}
