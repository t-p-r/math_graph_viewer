package model.exception;

public class UsedLabelException extends GraphException {
    public UsedLabelException() {
        super("Label number has already existed in the graph.");
    }
}
