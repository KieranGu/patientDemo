package main.java.com.example.demo.repository;

import main.java.com.example.demo.model.ClinicalData;
import main.java.com.example.demo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClinicalDataRepository extends JpaRepository<ClinicalData, Long> {
    
    // Find clinical data by patient
    List<ClinicalData> findByPatient(Patient patient);
    
    // Find clinical data by patient ID
    List<ClinicalData> findByPatientId(Long patientId);
    
    // Find clinical data by component name
    List<ClinicalData> findByComponentName(String componentName);
    
    // Find clinical data by component name and patient
    List<ClinicalData> findByComponentNameAndPatient(String componentName, Patient patient);
    
    // Find clinical data by component name and patient ID
    List<ClinicalData> findByComponentNameAndPatientId(String componentName, Long patientId);
    
    // Find clinical data by date range
    List<ClinicalData> findByMeasuredDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find clinical data by patient and date range
    List<ClinicalData> findByPatientAndMeasuredDateTimeBetween(Patient patient, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find clinical data by patient ID and date range
    List<ClinicalData> findByPatientIdAndMeasuredDateTimeBetween(Long patientId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Custom query to find latest clinical data for each component by patient
    @Query("SELECT c FROM ClinicalData c WHERE c.patient = :patient AND c.measuredDateTime = " +
           "(SELECT MAX(c2.measuredDateTime) FROM ClinicalData c2 WHERE c2.patient = :patient AND c2.componentName = c.componentName)")
    List<ClinicalData> findLatestClinicalDataByPatient(@Param("patient") Patient patient);
    
    // Custom query to find latest clinical data for a specific component by patient ID
    @Query("SELECT c FROM ClinicalData c WHERE c.patient.id = :patientId AND c.componentName = :componentName " +
           "ORDER BY c.measuredDateTime DESC LIMIT 1")
    ClinicalData findLatestByPatientIdAndComponentName(@Param("patientId") Long patientId, @Param("componentName") String componentName);
    
    // Count clinical data entries by patient
    long countByPatient(Patient patient);
    
    // Count clinical data entries by patient ID
    long countByPatientId(Long patientId);
    
    // Count clinical data entries by component name
    long countByComponentName(String componentName);
    
    // Custom query to get distinct component names
    @Query("SELECT DISTINCT c.componentName FROM ClinicalData c")
    List<String> findDistinctComponentNames();
    
    // Custom query to get distinct component names for a patient
    @Query("SELECT DISTINCT c.componentName FROM ClinicalData c WHERE c.patient.id = :patientId")
    List<String> findDistinctComponentNamesByPatientId(@Param("patientId") Long patientId);
}
