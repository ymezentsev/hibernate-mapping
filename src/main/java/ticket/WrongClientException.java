package ticket;

public class WrongClientException extends Exception{
    @Override
    public String getMessage() {
        return "You entered an incorrect client";
    }
}
