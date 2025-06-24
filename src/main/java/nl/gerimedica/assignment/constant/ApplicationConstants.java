package nl.gerimedica.assignment.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationConstants {

    public static final String BULK_APPOINTMENT_V1_PATH = "/api/v1/appointments/bulk";

    public static final String GET_APPOINTMENTS_BY_REASON_V1_PATH = "/api/v1/appointments";

    public static final String GET_LATEST_APPOINTMENTS_BY_SSN_V1_PATH = "/api/v1/appointments/latest";
    public static final String DELETE_APPOINTMENTS_V1_PATH = "/api/v1/appointments";

}
