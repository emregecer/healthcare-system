package nl.gerimedica.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.gerimedica.assignment.dto.AppointmentResponseDto;
import nl.gerimedica.assignment.dto.BulkAppointmentRequestDto;
import nl.gerimedica.assignment.entity.Appointment;
import nl.gerimedica.assignment.entity.Patient;
import nl.gerimedica.assignment.fault.BusinessException;
import nl.gerimedica.assignment.fault.ErrorType;
import nl.gerimedica.assignment.logging.UsageTracker;
import nl.gerimedica.assignment.mapper.AppointmentRequestMapper;
import nl.gerimedica.assignment.mapper.AppointmentResponseMapper;
import nl.gerimedica.assignment.repository.AppointmentRepository;
import nl.gerimedica.assignment.repository.PatientRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HospitalService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final UsageTracker usageTracker;

    @Transactional
    public List<AppointmentResponseDto> bulkCreateAppointments(BulkAppointmentRequestDto request) {
        Patient patient = findOrCreatePatient(request.getSsn(), request.getPatientName());

        List<Appointment> appointments = AppointmentRequestMapper.INSTANCE.mapToAppointmentList(
                request.getAppointments(), patient
        );

        List<Appointment> savedAppointments = appointmentRepository.saveAll(appointments);
        savedAppointments.forEach(appt -> log.info("Created appointment for reason: {} [Date: {}] [Patient SSN: {}]",
                appt.getReason(), appt.getDateTime(), appt.getPatient().getSsn())
        );

        usageTracker.record("Bulk create appointments");

        return AppointmentResponseMapper.INSTANCE.toDtoList(savedAppointments);
    }

    public List<AppointmentResponseDto> getAppointmentsByReason(String reasonKeyword) {
        List<AppointmentResponseDto> matchedAppointments = appointmentRepository.findByReasonIgnoreCaseContaining(reasonKeyword).stream()
                .map(AppointmentResponseMapper.INSTANCE::toDto)
                .toList();

        usageTracker.record("Get appointments by reason");

        return matchedAppointments;
    }

    public void deleteAppointmentsBySSN(String ssn) {
        if (patientRepository.findBySsn(ssn).isEmpty()) {
            log.warn("No patient found with SSN {}, nothing to delete", ssn);
            return;
        }

        appointmentRepository.deleteByPatientSsn(ssn);
        log.info("Deleted all appointments for patient with SSN {}", ssn);

        usageTracker.record("Delete appoints by SSN");
    }

    public AppointmentResponseDto findLatestAppointmentBySSN(String ssn) {
        return appointmentRepository.findLatestByPatientSsn(ssn, PageRequest.of(0, 1)).stream()
                .findFirst()
                .map(AppointmentResponseMapper.INSTANCE::toDto)
                .orElseThrow(() -> new BusinessException(ErrorType.APPOINTMENT_NOT_FOUND_FOR_SSN));
    }

    private Patient findOrCreatePatient(String ssn, String patientName) {
        Optional<Patient> optionalPatient = patientRepository.findBySsn(ssn);
        Patient patient;
        if (optionalPatient.isPresent()) {
            patient = optionalPatient.get();
            log.info("Existing patient found, SSN: {}", patient.getSsn());
        } else {
            patient = patientRepository.save(new Patient(patientName, ssn));
            log.info("Created new patient with SSN: {}", ssn);
        }

        return patient;
    }
}
