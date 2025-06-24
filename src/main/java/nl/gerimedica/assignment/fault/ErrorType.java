package nl.gerimedica.assignment.fault;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorType {

    PATIENT_NOT_FOUND("1000"),
    APPOINTMENT_NOT_FOUND_FOR_SSN("1001"),
    GENERIC_ERROR("9999");

    private final String code;

}
