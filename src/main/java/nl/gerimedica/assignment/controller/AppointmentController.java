package nl.gerimedica.assignment.controller;

import lombok.RequiredArgsConstructor;
import nl.gerimedica.assignment.entity.Appointment;
import nl.gerimedica.assignment.logging.UsageTracker;
import nl.gerimedica.assignment.service.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AppointmentController {

    private final HospitalService hospitalService;

    private final UsageTracker usageTracker;

    /**
     * Example: {
     * "reasons": ["Checkup", "Follow-up", "X-Ray"],
     * "dates": ["2025-02-01", "2025-02-15", "2025-03-01"]
     * }
     */
    @PostMapping("/bulk-appointments")
    public ResponseEntity<List<Appointment>> createBulkAppointments(@RequestParam String patientName,
                                                                    @RequestParam String ssn,
                                                                    @RequestBody Map<String, List<String>> payload) {
        List<String> reasons = payload.get("reasons");
        List<String> dates = payload.get("dates");

        usageTracker.record("Controller triggered bulk appointments creation");

        List<Appointment> created = hospitalService.bulkCreateAppointments(patientName, ssn, reasons, dates);
        return new ResponseEntity<>(created, HttpStatus.OK);
    }

    @GetMapping("/appointments-by-reason")
    public ResponseEntity<List<Appointment>> getAppointmentsByReason(@RequestParam String keyword) {
        List<Appointment> found = hospitalService.getAppointmentsByReason(keyword);
        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    @PostMapping("/delete-appointments")
    public ResponseEntity<String> deleteAppointmentsBySSN(@RequestParam String ssn) {
        hospitalService.deleteAppointmentsBySSN(ssn);
        return new ResponseEntity<>("Deleted all appointments for SSN: " + ssn, HttpStatus.OK);
    }

    @GetMapping("/appointments/latest")
    public ResponseEntity<Appointment> getLatestAppointment(@RequestParam String ssn) {
        Appointment latest = hospitalService.findLatestAppointmentBySSN(ssn);
        return new ResponseEntity<>(latest, HttpStatus.OK);
    }
}
