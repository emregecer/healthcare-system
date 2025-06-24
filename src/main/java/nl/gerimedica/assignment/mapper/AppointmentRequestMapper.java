package nl.gerimedica.assignment.mapper;

import nl.gerimedica.assignment.dto.AppointmentRequestDto;
import nl.gerimedica.assignment.entity.Appointment;
import nl.gerimedica.assignment.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AppointmentRequestMapper {

    AppointmentRequestMapper INSTANCE = Mappers.getMapper(AppointmentRequestMapper.class);

    default List<Appointment> mapToAppointmentList(List<AppointmentRequestDto> appointmentRequestDtos, Patient patient) {
        return appointmentRequestDtos.stream()
                .map(appointmentRequestDto -> mapToAppointment(appointmentRequestDto, patient))
                .toList();
    }

    @Mapping(target = "reason", source = "appointmentRequestDto.reason")
    @Mapping(target = "dateTime", source = "appointmentRequestDto.dateTime")
    @Mapping(target = "patient", source = "patient")
    Appointment mapToAppointment(AppointmentRequestDto appointmentRequestDto, Patient patient);
}
