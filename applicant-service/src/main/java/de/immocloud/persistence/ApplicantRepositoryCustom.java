package de.immocloud.persistence;

import de.immocloud.model.Applicant;
import de.immocloud.model.Status;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.UUID;

public interface ApplicantRepositoryCustom {
    List<Applicant> findByDynamicCriteria(Criteria criteria);

    Applicant updateStatus(UUID id, Status status);
}
