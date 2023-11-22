package de.immocloud.persistence;

import de.immocloud.model.Applicant;
import de.immocloud.model.Exceptions.ApplicantNotFoundException;
import de.immocloud.model.Status;
import de.immocloud.utils.TestMongoConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.immocloud.utils.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestMongoConfiguration.class)
class ApplicantRepositoryImplTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    private ApplicantRepositoryImpl sut;

    @BeforeEach
    public void initDB() {
        sut = new ApplicantRepositoryImpl(mongoTemplate);
        List<Applicant> dataAsList = List.of(declinedApplicant, openApplicant, acceptedApplicant);
        mongoTemplate.insertAll(dataAsList);
        assertEquals(dataAsList.size(), mongoTemplate.findAll(Applicant.class).size());
    }

    @AfterEach
    public void cleanUp() {
        mongoTemplate.dropCollection(Applicant.class);
    }

    @ParameterizedTest
    @MethodSource("gnerateFindByDynamicCriteriaTestData")
    void findByDynamicCriteria( Criteria criteria, List<Applicant> expectedResult) {
        //given

        //when
        List<Applicant> result = sut.findByDynamicCriteria(criteria);

        //then
        assertIterableEquals(expectedResult.stream().sorted().collect(Collectors.toList()), result.stream().sorted().collect(Collectors.toList()));
    }

    private static Stream<Arguments> gnerateFindByDynamicCriteriaTestData() {
        return Stream.of(
                Arguments.of(Criteria.where("name").is(declinedApplicant.getName()), List.of(declinedApplicant)),
                Arguments.of(Criteria.where("name").is(acceptedApplicant.getName()), List.of(acceptedApplicant)),
                Arguments.of(Criteria.where("name").is(openApplicant.getName()), List.of(openApplicant)),
                Arguments.of(new Criteria(), List.of(openApplicant, declinedApplicant, acceptedApplicant)),
                Arguments.of(Criteria.where("status").is(declinedApplicant.getStatus()), List.of(declinedApplicant)),
                Arguments.of(Criteria.where("status").is(acceptedApplicant.getStatus()), List.of(acceptedApplicant)),
                Arguments.of(Criteria.where("status").is(openApplicant.getStatus()), List.of(openApplicant)),
                Arguments.of(Criteria.where("status").ne(declinedApplicant.getStatus()), List.of(openApplicant, acceptedApplicant)),
                Arguments.of(Criteria.where("nonexistingField").is("irrelevantValue"), List.of()),
                Arguments.of(Criteria.where("name").regex("mol", "i"), List.of(openApplicant)),
                Arguments.of(Criteria.where("name").regex("ma", "i"), List.of(openApplicant, declinedApplicant)),
                Arguments.of(Criteria.where("name").regex("ma", "i").andOperator(Criteria.where("status").is(openApplicant.getStatus())), List.of(openApplicant))
        );
    }

    @ParameterizedTest
    @MethodSource("gnerateUpdateStatusTestData")
    void updateStatus(UUID id, Status desiredStatus) {

        //given
        String oldName = mongoTemplate.findById(id, Applicant.class).getName();

        //when
        Applicant result = sut.updateStatus(id, desiredStatus);

        //then
        assertEquals(id, result.get_id() );
        assertEquals(desiredStatus, result.getStatus());
        assertEquals(oldName, result.getName());
    }

    private static Stream<Arguments> gnerateUpdateStatusTestData() {
        return Stream.of(
                Arguments.of(openApplicant.get_id(), Status.ACCEPTED),
                Arguments.of(declinedApplicant.get_id(), Status.ACCEPTED),
                Arguments.of(declinedApplicant.get_id(), Status.DECLINED),
                Arguments.of(acceptedApplicant.get_id(), Status.OPEN)
        );
    }

    @Test
    void updateStatusexceptionTest() {

        //given
        Status desiredStatus = Status.ACCEPTED;
        UUID nonExistingUUID = UUID.randomUUID();

        //when
        Exception exception = assertThrows(ApplicantNotFoundException.class, () -> sut.updateStatus(nonExistingUUID, desiredStatus));

        //then
        assertTrue(exception.getMessage().contains(nonExistingUUID.toString()));
    }

}
