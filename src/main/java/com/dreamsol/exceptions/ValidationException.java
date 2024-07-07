package com.dreamsol.exceptions;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ValidationException extends RuntimeException {
    private Set<Set<String>> error = null;

    public ValidationException(Set<Set<String>> msg) {
        error = msg;
    }
}
