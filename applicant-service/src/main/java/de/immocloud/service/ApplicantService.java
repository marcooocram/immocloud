package de.immocloud.service;

import de.immocloud.model.Applicant;
import de.immocloud.model.Filter;
import de.immocloud.model.Status;

import java.util.List;
import java.util.UUID;


public interface ApplicantService {

    Applicant updateApplicant(UUID id, Status status);

    Applicant addApplicant(String name);

    List<Applicant> getApplicants(List<Filter> filters);
}
