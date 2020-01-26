package com.gkiss01.meetdebwebapi.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomRuntimeException extends RuntimeException {
    private ErrorCodes errorCode;
}