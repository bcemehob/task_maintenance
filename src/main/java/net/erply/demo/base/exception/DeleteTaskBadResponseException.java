package net.erply.demo.base.exception;

public class DeleteTaskBadResponseException extends TaskException {

    private static final long serialVersionUID = -7521032833411145440L;

    public DeleteTaskBadResponseException(String code) {
        super(code);
    }
}
