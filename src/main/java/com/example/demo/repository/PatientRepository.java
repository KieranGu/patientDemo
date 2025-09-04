package main.java.com.example.demo.repository;

import main.java.com.example.demo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    // Find patient by first name and last name
    Optional<Patient> findByFirstNameAndLastName(String firstName, String lastName);
    
    // Find patients by first name
    List<Patient> findByFirstName(String firstName);
    
    // Find patients by last name
    List<Patient> findByLastName(String lastName);
    
    // Find patients by age
    List<Patient> findByAge(int age);
    
    // Find patients by age range
    List<Patient> findByAgeBetween(int minAge, int maxAge);
    
    // Custom query to find patients older than specified age
    @Query("SELECT p FROM Patient p WHERE p.age > :age")
    List<Patient> findPatientsOlderThan(@Param("age") int age);
    
    // Custom query to find patients with clinical data
    @Query("SELECT DISTINCT p FROM Patient p JOIN p.clinicalDataList c")
    List<Patient> findPatientsWithClinicalData();
    
    // Count patients by age
    long countByAge(int age);
}
