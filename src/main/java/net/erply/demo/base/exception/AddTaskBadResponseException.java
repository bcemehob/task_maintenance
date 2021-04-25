package net.erply.demo.base.exception;

public class AddTaskBadResponseException extends TaskException {

    private static final long serialVersionUID = 8344287990096203126L;

    public AddTaskBadResponseException(String code) {
        super(code);
    }
}
