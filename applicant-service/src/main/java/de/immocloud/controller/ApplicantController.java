package de.immocloud.controller;

import de.immocloud.model.Exceptions.ApplicantNotFoundException;
import de.immocloud.model.Filter;
import de.immocloud.model.FilterMethod;
import de.immocloud.model.ReceivedApplicant;
import de.immocloud.model.Status;
import de.immocloud.service.ApplicantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/applicants")
public class ApplicantController {

    private final ApplicantService applicantService;

    @Autowired
    public ApplicantController(ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

    @GetMapping(value = "")
    public ResponseEntity<Object> getApplicants(
            @RequestParam(required = false, defaultValue = "") String nameFilterValue,
            @RequestParam(required = false, defaultValue = "") Boolean hideDeclined) {
        try {
            List<Filter> filters = new ArrayList<>();
            if (!nameFilterValue.isEmpty()) {
                filters.add(new Filter("name", nameFilterValue, FilterMethod.CONTAINS_CASE_I));
            }

            if (hideDeclined) {
                filters.add(new Filter("status", Status.DECLINED.toString(), FilterMethod.NOT_EQUALS));
            }
            return new ResponseEntity<>(applicantService.getApplicants(filters), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/getViaPost")
    public ResponseEntity<Object> getApplicantsViaPost(@RequestBody List<Filter> filters) {
        try {
            return new ResponseEntity<>(applicantService.getApplicants(filters), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(value = "")
    public ResponseEntity<Object> updateApplicant(@RequestParam UUID id, @RequestBody Status status) {
        try {
            return new ResponseEntity<>(applicantService.updateApplicant(id, status), HttpStatus.OK);
        } catch (ApplicantNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "")
    public ResponseEntity<Object> addApplicant(@RequestBody ReceivedApplicant receivedApplicant) {
        try {
            return new ResponseEntity<>(applicantService.addApplicant(receivedApplicant.getName()), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
