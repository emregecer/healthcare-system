package nl.gerimedica.assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.gerimedica.assignment.dto.AppointmentRequestDto;
import nl.gerimedica.assignment.dto.AppointmentResponseDto;
import nl.gerimedica.assignment.dto.BulkAppointmentRequestDto;
import nl.gerimedica.assignment.logging.UsageTracker;
import nl.gerimedica.assignment.service.HospitalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static nl.gerimedica.assignment.constant.ApplicationConstants.BULK_APPOINTMENT_V1_PATH;
import static nl.gerimedica.assignment.constant.ApplicationConstants.GET_APPOINTMENTS_BY_REASON_V1_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HospitalService hospitalService;

    @MockitoBean
    private UsageTracker usageTracker;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBulkAppointments_shouldReturnCreatedAppointments() throws Exception {
        BulkAppointmentRequestDto request = BulkAppointmentRequestDto.builder()
                .patientName("John Doe")
                .ssn("12345678901")
                .appointments(List.of(
                        AppointmentRequestDto.builder()
                                .reason("Checkup")
                                .dateTime(LocalDateTime.of(2025, 3, 1, 12, 0))
                                .build()
                ))
                .build();

        AppointmentResponseDto responseDto = AppointmentResponseDto.builder()
                .reason("Checkup")
                .dateTime(LocalDateTime.of(2025, 3, 1, 12, 0))
                .patientName("John Doe")
                .build();

        when(hospitalService.bulkCreateAppointments(any())).thenReturn(List.of(responseDto));

        mockMvc.perform(
                        post(BULK_APPOINTMENT_V1_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reason").value("Checkup"))
                .andExpect(jsonPath("$[0].patientName").value("John Doe"))
                .andExpect(jsonPath("$[0].dateTime").value("2025-03-01T12:00:00"));

        verify(hospitalService, times(1)).bulkCreateAppointments(any());
        verify(usageTracker, times(1)).record("Controller triggered bulk appointments creation");
    }

    @Test
    void getAppointmentsByReason_returnsList() throws Exception {
        List<AppointmentResponseDto> mockList = List.of(
                AppointmentResponseDto.builder()
                        .reason("Checkup")
                        .dateTime(LocalDateTime.of(2025, 3, 1, 12, 0))
                        .patientName("John Doe")
                        .build(),
                AppointmentResponseDto.builder()
                        .reason("Follow-up")
                        .dateTime(LocalDateTime.of(2025, 3, 2, 14, 0))
                        .patientName("John Doe")
                        .build()
        );

        when(hospitalService.getAppointmentsByReason("Checkup")).thenReturn(mockList);

        mockMvc.perform(get(GET_APPOINTMENTS_BY_REASON_V1_PATH)
                        .param("keyword", "Checkup")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockList.size()))
                .andExpect(jsonPath("$[0].reason").value("Checkup"))
                .andExpect(jsonPath("$[1].reason").value("Follow-up"));

        verify(hospitalService).getAppointmentsByReason("Checkup");
        verifyNoInteractions(usageTracker);
    }
}
