package nl.gerimedica.assignment.fault;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorType errorType;
    private final String message;

    public BusinessException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
        this.message = message;
    }

    public BusinessException(ErrorType errorType) {
        this(errorType, null);
    }
}
