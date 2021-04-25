package net.erply.demo.base.exception;

import lombok.Getter;

@Getter
public class TaskException extends RuntimeException {
    private static final long serialVersionUID = -5515807722109543867L;
    private String code;
    public TaskException(String code) {
        this.code = code;
    }
}
