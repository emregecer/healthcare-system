package nl.gerimedica.assignment.repository;

import jakarta.transaction.Transactional;
import nl.gerimedica.assignment.entity.Appointment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.patient.ssn = :ssn ORDER BY a.dateTime DESC")
    List<Appointment> findLatestByPatientSsn(@Param("ssn") String ssn, Pageable pageable);

    List<Appointment> findByReasonIgnoreCaseContaining(String reasonKeyword);

    @Transactional
    @Modifying
    @Query("DELETE FROM Appointment a WHERE a.patient.ssn = :ssn")
    void deleteByPatientSsn(@Param("ssn") String ssn);

}
