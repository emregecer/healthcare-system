package nl.gerimedica.assignment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class AppointmentResponseDto {

    private String patientName;

    private String reason;

    private LocalDateTime dateTime;

}
