package inventory;
public class EmptyInventoryException extends Exception{
    public EmptyInventoryException(String message) {
        super(message);
    }
}
