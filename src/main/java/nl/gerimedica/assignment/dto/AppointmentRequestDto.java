package nl.gerimedica.assignment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// todo: Create message properties for validation messages
@Builder
@Getter
public class AppointmentRequestDto {

    @NotBlank(message = "Reason for appointment cannot be blank")
    private String reason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotBlank(message = "Date and time of appointment cannot be blank")
    private LocalDateTime dateTime;

}
