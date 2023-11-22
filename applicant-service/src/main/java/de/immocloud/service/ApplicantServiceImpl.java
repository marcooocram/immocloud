package de.immocloud.service;

import de.immocloud.model.Applicant;
import de.immocloud.model.Filter;
import de.immocloud.model.Status;
import de.immocloud.persistence.ApplicantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository applicantRepository;

    @Autowired
    public ApplicantServiceImpl(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    @Override
    public Applicant updateApplicant(UUID id, Status status) {
        return applicantRepository.updateStatus(id, status);
    }

    @Override
    public Applicant addApplicant(String name) {
        return applicantRepository.insert(new Applicant(UUID.randomUUID(), name, Status.OPEN));
    }

    @Override
    public List<Applicant> getApplicants(List<Filter> filters) {
        Criteria criteria = filters
                .stream()
                .map(filter -> switch (filter.getFilterMethod()) {
                    case CONTAINS_CASE_I -> Criteria.where(filter.getKey()).regex(filter.getValue(), "i");
                    case EQUALS -> Criteria.where(filter.getKey()).is(filter.getValue());
                    case NOT_EQUALS -> Criteria.where(filter.getKey()).ne(filter.getValue());
                })
                .reduce(Criteria::andOperator)
                .orElseGet(Criteria::new);

        return applicantRepository.findByDynamicCriteria(criteria);
    }
}
