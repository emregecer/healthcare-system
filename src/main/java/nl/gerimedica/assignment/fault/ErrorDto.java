package nl.gerimedica.assignment.fault;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class ErrorDto {

    private String code;
    private String message;

}
