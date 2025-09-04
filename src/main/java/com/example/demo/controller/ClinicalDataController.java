package main.java.com.example.demo.controller;

import main.java.com.example.demo.model.ClinicalData;
import main.java.com.example.demo.model.Patient;
import main.java.com.example.demo.repository.ClinicalDataRepository;
import main.java.com.example.demo.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clinical-data")
@CrossOrigin(origins = "*")
public class ClinicalDataController {

    @Autowired
    private ClinicalDataRepository clinicalDataRepository;

    @Autowired
    private PatientRepository patientRepository;

    // GET all clinical data
    @GetMapping
    public ResponseEntity<List<ClinicalData>> getAllClinicalData() {
        try {
            List<ClinicalData> clinicalData = clinicalDataRepository.findAll();
            if (clinicalData.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(clinicalData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET clinical data by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClinicalData> getClinicalDataById(@PathVariable("id") Long id) {
        Optional<ClinicalData> clinicalData = clinicalDataRepository.findById(id);
        
        if (clinicalData.isPresent()) {
            return new ResponseEntity<>(clinicalData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET clinical data by patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ClinicalData>> getClinicalDataByPatientId(@PathVariable("patientId") Long patientId) {
        try {
            List<ClinicalData> clinicalData = clinicalDataRepository.findByPatientId(patientId);
            if (clinicalData.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(clinicalData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET clinical data by component name
    @GetMapping("/component/{componentName}")
    public ResponseEntity<List<ClinicalData>> getClinicalDataByComponentName(@PathVariable("componentName") String componentName) {
        try {
            List<ClinicalData> clinicalData = clinicalDataRepository.findByComponentName(componentName);
            if (clinicalData.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(clinicalData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET clinical data by component name and patient ID
    @GetMapping("/patient/{patientId}/component/{componentName}")
    public ResponseEntity<List<ClinicalData>> getClinicalDataByPatientIdAndComponentName(
            @PathVariable("patientId") Long patientId,
            @PathVariable("componentName") String componentName) {
        try {
            List<ClinicalData> clinicalData = clinicalDataRepository.findByComponentNameAndPatientId(componentName, patientId);
            if (clinicalData.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(clinicalData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET clinical data by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<ClinicalData>> getClinicalDataByDateRange(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(startDateStr, formatter);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr, formatter);
            
            List<ClinicalData> clinicalData = clinicalDataRepository.findByMeasuredDateTimeBetween(startDate, endDate);
            if (clinicalData.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(clinicalData, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET latest clinical data by patient ID
    @GetMapping("/patient/{patientId}/latest")
    public ResponseEntity<List<ClinicalData>> getLatestClinicalDataByPatientId(@PathVariable("patientId") Long patientId) {
        try {
            Optional<Patient> patient = patientRepository.findById(patientId);
            if (!patient.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            List<ClinicalData> clinicalData = clinicalDataRepository.findLatestClinicalDataByPatient(patient.get());
            if (clinicalData.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(clinicalData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET latest clinical data by patient ID and component name
    @GetMapping("/patient/{patientId}/component/{componentName}/latest")
    public ResponseEntity<ClinicalData> getLatestClinicalDataByPatientIdAndComponentName(
            @PathVariable("patientId") Long patientId,
            @PathVariable("componentName") String componentName) {
        try {
            ClinicalData clinicalData = clinicalDataRepository.findLatestByPatientIdAndComponentName(patientId, componentName);
            if (clinicalData == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(clinicalData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST create new clinical data
    @PostMapping
    public ResponseEntity<ClinicalData> createClinicalData(@RequestBody ClinicalData clinicalData) {
        try {
            // Validate that patient exists
            if (clinicalData.getPatient() != null && clinicalData.getPatient().getId() != null) {
                Optional<Patient> patient = patientRepository.findById(clinicalData.getPatient().getId());
                if (!patient.isPresent()) {
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
                clinicalData.setPatient(patient.get());
            }
            
            // Set current timestamp if not provided
            if (clinicalData.getMeasuredDateTime() == null) {
                clinicalData.setMeasuredDateTime(LocalDateTime.now());
            }
            
            ClinicalData savedClinicalData = clinicalDataRepository.save(clinicalData);
            return new ResponseEntity<>(savedClinicalData, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT update clinical data
    @PutMapping("/{id}")
    public ResponseEntity<ClinicalData> updateClinicalData(@PathVariable("id") Long id, @RequestBody ClinicalData clinicalData) {
        Optional<ClinicalData> clinicalDataOptional = clinicalDataRepository.findById(id);
        
        if (clinicalDataOptional.isPresent()) {
            ClinicalData existingClinicalData = clinicalDataOptional.get();
            existingClinicalData.setComponentName(clinicalData.getComponentName());
            existingClinicalData.setComponentValue(clinicalData.getComponentValue());
            existingClinicalData.setMeasuredDateTime(clinicalData.getMeasuredDateTime());
            
            // Update patient if provided
            if (clinicalData.getPatient() != null && clinicalData.getPatient().getId() != null) {
                Optional<Patient> patient = patientRepository.findById(clinicalData.getPatient().getId());
                if (patient.isPresent()) {
                    existingClinicalData.setPatient(patient.get());
                }
            }
            
            return new ResponseEntity<>(clinicalDataRepository.save(existingClinicalData), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE clinical data by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteClinicalData(@PathVariable("id") Long id) {
        try {
            clinicalDataRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE all clinical data
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteAllClinicalData() {
        try {
            clinicalDataRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET distinct component names
    @GetMapping("/components")
    public ResponseEntity<List<String>> getDistinctComponentNames() {
        try {
            List<String> componentNames = clinicalDataRepository.findDistinctComponentNames();
            if (componentNames.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(componentNames, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET distinct component names by patient ID
    @GetMapping("/patient/{patientId}/components")
    public ResponseEntity<List<String>> getDistinctComponentNamesByPatientId(@PathVariable("patientId") Long patientId) {
        try {
            List<String> componentNames = clinicalDataRepository.findDistinctComponentNamesByPatientId(patientId);
            if (componentNames.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(componentNames, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET count of clinical data by patient ID
    @GetMapping("/patient/{patientId}/count")
    public ResponseEntity<Long> countClinicalDataByPatientId(@PathVariable("patientId") Long patientId) {
        try {
            long count = clinicalDataRepository.countByPatientId(patientId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
