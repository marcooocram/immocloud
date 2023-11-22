package de.immocloud.persistence;

import de.immocloud.model.Applicant;
import de.immocloud.model.Exceptions.ApplicantNotFoundException;
import de.immocloud.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ApplicantRepositoryImpl implements ApplicantRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ApplicantRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Applicant> findByDynamicCriteria(Criteria criteria) {
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Applicant.class);
    }

    @Override
    public Applicant updateStatus(UUID id, Status status) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(id));
        Update update = new Update().set("status", status);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        Applicant result = mongoTemplate.findAndModify(query, update, options, Applicant.class);
        if (result == null) throw new ApplicantNotFoundException(id);
        return result;
    }
}
