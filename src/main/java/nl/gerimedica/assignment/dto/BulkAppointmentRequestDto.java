package nl.gerimedica.assignment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BulkAppointmentRequestDto {

    @NotBlank(message = "Reason for appointment cannot be blank")
    private String patientName;

    @NotBlank(message = "Reason for appointment cannot be blank")
    private String ssn;

    private List<AppointmentRequestDto> appointments;

}
