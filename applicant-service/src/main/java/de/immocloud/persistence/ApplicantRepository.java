package de.immocloud.persistence;

import de.immocloud.model.Applicant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ApplicantRepository extends MongoRepository<Applicant, UUID>, ApplicantRepositoryCustom {
}
