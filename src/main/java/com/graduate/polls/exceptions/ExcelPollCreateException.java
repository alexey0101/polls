package com.graduate.polls.exceptions;

import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Executable;

public class ExcelPollCreateException extends MethodArgumentNotValidException {
    public ExcelPollCreateException(MethodParameter parameter, BindingResult bindingResult) {
        super(parameter, bindingResult);
    }

    public ExcelPollCreateException(Executable executable, BindingResult bindingResult) {
        super(executable, bindingResult);
    }
}
