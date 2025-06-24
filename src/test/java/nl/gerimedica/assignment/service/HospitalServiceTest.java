package nl.gerimedica.assignment.service;

import nl.gerimedica.assignment.dto.AppointmentRequestDto;
import nl.gerimedica.assignment.dto.AppointmentResponseDto;
import nl.gerimedica.assignment.dto.BulkAppointmentRequestDto;
import nl.gerimedica.assignment.entity.Appointment;
import nl.gerimedica.assignment.entity.Patient;
import nl.gerimedica.assignment.fault.BusinessException;
import nl.gerimedica.assignment.fault.ErrorType;
import nl.gerimedica.assignment.logging.UsageTracker;
import nl.gerimedica.assignment.repository.AppointmentRepository;
import nl.gerimedica.assignment.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {

    @Mock
    private AppointmentRepository appointmentRepo;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UsageTracker usageTracker;

    @InjectMocks
    private HospitalService hospitalService;

    @Test
    void bulkCreateAppointments_patientExists_savesAppointments() {
        String ssn = "123456789";
        String patientName = "John Doe";

        Patient patient = new Patient(patientName, ssn);
        patient.setId(1L);

        BulkAppointmentRequestDto request = BulkAppointmentRequestDto.builder()
                .ssn(ssn)
                .patientName(patientName)
                .appointments(List.of(
                        AppointmentRequestDto.builder()
                                .dateTime(LocalDateTime.of(2025, 3, 1, 12, 0))
                                .reason("Checkup")
                                .build(),
                        AppointmentRequestDto.builder()
                                .dateTime(LocalDateTime.of(2025, 3, 15, 14, 0))
                                .reason("Follow-up")
                                .build()
                ))
                .build();

        when(patientRepository.findBySsn(ssn)).thenReturn(Optional.of(patient));

        // Mock saving appointments — assume saveAll returns same list
        ArgumentCaptor<List<Appointment>> captor = ArgumentCaptor.forClass(List.class);
        when(appointmentRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<AppointmentResponseDto> result = hospitalService.bulkCreateAppointments(request);

        verify(patientRepository, never()).save(any());
        verify(patientRepository).findBySsn(ssn);
        verify(appointmentRepo).saveAll(captor.capture());
        verify(usageTracker).record("Bulk create appointments");

        List<Appointment> savedAppointments = captor.getValue();

        assertEquals(2, savedAppointments.size());
        assertEquals(patient, savedAppointments.get(0).getPatient());
        assertEquals(patient, savedAppointments.get(1).getPatient());

        assertEquals(2, result.size());
        assertEquals("Checkup", result.getFirst().getReason());
    }

    /*
    @Test
    void bulkCreateAppointments_patientDoesNotExist_createsPatientAndSavesAppointments() {
        String ssn = "987654321";
        String patientName = "Jane Doe";

        BulkAppointmentRequestDto request = new BulkAppointmentRequestDto();
        request.setSsn(ssn);
        request.setPatientName(patientName);
        request.setAppointments(List.of(
                new AppointmentRequestDto("X-Ray", LocalDateTime.of(2025, 4, 1, 11, 0))
        ));

        when(patientRepo.findBySsn(ssn)).thenReturn(Optional.empty());

        // Simulate saving patient returns patient with ID
        when(patientRepo.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setId(2L);
            return p;
        });

        when(appointmentRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<AppointmentResponseDto> result = hospitalService.bulkCreateAppointments(request);

        verify(patientRepo).findBySsn(ssn);
        verify(patientRepo).save(any(Patient.class));
        verify(appointmentRepo).saveAll(anyList());
        verify(usageTracker).record("Bulk create appointments");

        assertEquals(1, result.size());
        assertEquals("X-Ray", result.get(0).getReason());
    }

    @Test
    void findLatestAppointmentBySSN_found() {
        String ssn = "12345678901";

        Appointment appointment = new Appointment();
        appointment.setReason("Checkup");
        appointment.setDate(LocalDate.of(2025, 3, 1));
        appointment.setPatient(new Patient("John Doe", ssn));

        AppointmentResponseDto dto = AppointmentResponseDto.builder()
                .reason("Checkup")
                .dateTime(LocalDate.of(2025, 3, 1))
                .patientName("John Doe")
                .build();

        when(appointmentRepo.findLatestByPatientSsn(eq(ssn), any(PageRequest.class)))
                .thenReturn(List.of(appointment));

        AppointmentResponseDto result = hospitalService.findLatestAppointmentBySSN(ssn);

        assertNotNull(result);
        assertEquals(dto.getReason(), result.getReason());
        assertEquals(dto.getDateTime(), result.getDateTime());
        assertEquals(dto.getPatientName(), result.getPatientName());
    }

     */

    @Test
    void deleteAppointmentsBySSN_patientNotFound_logsWarningAndReturns() {
        String ssn = "123456789";

        when(patientRepository.findBySsn(ssn)).thenReturn(Optional.empty());

        hospitalService.deleteAppointmentsBySSN(ssn);

        verify(patientRepository, times(1)).findBySsn(ssn);
        verify(appointmentRepo, never()).deleteByPatientSsn(anyString());
        verify(usageTracker, never()).record(anyString());
    }

    @Test
    void deleteAppointmentsBySSN_patientFound_deletesAppointmentsAndRecordsUsage() {
        String ssn = "123456789";

        when(patientRepository.findBySsn(ssn)).thenReturn(Optional.of(mock(Patient.class)));

        hospitalService.deleteAppointmentsBySSN(ssn);

        verify(patientRepository, times(1)).findBySsn(ssn);
        verify(appointmentRepo, times(1)).deleteByPatientSsn(ssn);
        verify(usageTracker, times(1)).record("Delete appoints by SSN");
    }

    @Test
    void findLatestAppointmentBySSN_notFound_throws() {
        String ssn = "12345678901";

        when(appointmentRepo.findLatestByPatientSsn(eq(ssn), any(PageRequest.class)))
                .thenReturn(List.of());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            hospitalService.findLatestAppointmentBySSN(ssn);
        });

        assertEquals(ErrorType.APPOINTMENT_NOT_FOUND_FOR_SSN, ex.getErrorType());
    }
}
