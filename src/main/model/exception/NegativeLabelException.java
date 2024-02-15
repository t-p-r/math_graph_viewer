package model.exception;

public class NegativeLabelException extends GraphException {
    public NegativeLabelException() {
        super("Label number is negative.");
    }
}
