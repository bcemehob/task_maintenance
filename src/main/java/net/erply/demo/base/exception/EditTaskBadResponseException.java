package net.erply.demo.base.exception;

public class EditTaskBadResponseException extends TaskException {

    private static final long serialVersionUID = -4086432485357540609L;

    public EditTaskBadResponseException(String code) {
        super(code);
    }
}
