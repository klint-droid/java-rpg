package inventory;
public class EmptyInventoryException extends Exception {
    private static final long serialVersionUID = 1L;

    public EmptyInventoryException(String message) {
        super(message);
    }
}

