package nl.gerimedica.assignment.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nl.gerimedica.assignment.dto.AppointmentResponseDto;
import nl.gerimedica.assignment.dto.BulkAppointmentRequestDto;
import nl.gerimedica.assignment.logging.UsageTracker;
import nl.gerimedica.assignment.service.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static nl.gerimedica.assignment.constant.ApplicationConstants.*;

@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final HospitalService hospitalService;

    private final UsageTracker usageTracker;

    @Operation(summary = "Create Bulk Appointments", description = "Create multiple appointments for a patient in bulk.")
    @PostMapping(BULK_APPOINTMENT_V1_PATH)
    public ResponseEntity<List<AppointmentResponseDto>> createBulkAppointments(@Valid @RequestBody BulkAppointmentRequestDto request) {
        usageTracker.record("Controller triggered bulk appointments creation");
        return new ResponseEntity<>(hospitalService.bulkCreateAppointments(request), HttpStatus.OK);
    }

    @GetMapping(GET_APPOINTMENTS_BY_REASON_V1_PATH)
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByReason(@RequestParam String keyword) {
        return new ResponseEntity<>(hospitalService.getAppointmentsByReason(keyword), HttpStatus.OK);
    }

    @DeleteMapping(DELETE_APPOINTMENTS_V1_PATH)
    @Operation(summary = "Delete Appointments by SSN", description = "Deletes all appointments for a given SSN.")
    public ResponseEntity<String> deleteAppointmentsBySSN(@RequestParam String ssn) {
        hospitalService.deleteAppointmentsBySSN(ssn);
        return new ResponseEntity<>("Deleted all appointments for SSN: " + ssn, HttpStatus.OK);
    }

    @GetMapping(GET_LATEST_APPOINTMENTS_BY_SSN_V1_PATH)
    public ResponseEntity<AppointmentResponseDto> getLatestAppointment(@RequestParam String ssn) {
        return new ResponseEntity<>(hospitalService.findLatestAppointmentBySSN(ssn), HttpStatus.OK);
    }
}
