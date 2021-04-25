package net.erply.demo.base.api;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorDto {
    private String code;
}
