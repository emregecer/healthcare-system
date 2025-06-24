package nl.gerimedica.assignment.mapper;

import nl.gerimedica.assignment.dto.AppointmentRequestDto;
import nl.gerimedica.assignment.dto.AppointmentResponseDto;
import nl.gerimedica.assignment.entity.Appointment;
import nl.gerimedica.assignment.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AppointmentResponseMapper {

    AppointmentResponseMapper INSTANCE = Mappers.getMapper(AppointmentResponseMapper.class);

    @Mapping(source = "patient.name", target = "patientName")
    AppointmentResponseDto toDto(Appointment appointment);

    List<AppointmentResponseDto> toDtoList(List<Appointment> appointments);

}

