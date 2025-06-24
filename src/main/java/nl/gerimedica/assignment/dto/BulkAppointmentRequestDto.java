package nl.gerimedica.assignment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

// TODO: Use message properties for validation messages
// TODO: Configure ResourceBundleMessageSource in Spring configuration
@Builder
@Getter
public class BulkAppointmentRequestDto {

    @NotBlank(message = "Reason for appointment cannot be blank")
    private String patientName;

    @NotBlank(message = "Reason for appointment cannot be blank")
    private String ssn;

    @Valid
    @NotEmpty(message = "At least one appointment is required")
    private List<AppointmentRequestDto> appointments;

}
