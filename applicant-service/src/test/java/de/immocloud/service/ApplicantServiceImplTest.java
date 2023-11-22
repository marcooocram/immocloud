package de.immocloud.service;

import de.immocloud.model.Applicant;
import de.immocloud.model.Filter;
import de.immocloud.model.FilterMethod;
import de.immocloud.model.Status;
import de.immocloud.persistence.ApplicantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static de.immocloud.utils.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicantServiceImplTest {

    private ApplicantRepository applicantRepositoryMock;


    private ApplicantServiceImpl sut;

    @BeforeEach
    public void setup() {
        applicantRepositoryMock = mock(ApplicantRepository.class);
        sut = new ApplicantServiceImpl(applicantRepositoryMock);
    }

    @Test
    void updateApplicant() {

        //given
        UUID id = acceptedApplicant.get_id();
        Status status = acceptedApplicant.getStatus();
        when(applicantRepositoryMock.updateStatus(id, status)).thenReturn(acceptedApplicant);

        //when
        Applicant result = sut.updateApplicant(id, status);

        //then
        verify(applicantRepositoryMock, times(1)).updateStatus(id, status);
        assertEquals(acceptedApplicant, result);
    }

    @Test
    void addApplicant() {

        //given
        String name = "new applicant name";
        when(applicantRepositoryMock.insert(any(Applicant.class))).thenReturn(openApplicant);

        //when
        Applicant result = sut.addApplicant(name);

        //then
        verify(applicantRepositoryMock, times(1)).insert((Applicant) argThat(rawApplicant -> {
            Applicant applicant = (Applicant) rawApplicant;
            assertEquals(Status.OPEN, applicant.getStatus());
            assertEquals(name, applicant.getName());
            assertNotNull(applicant.get_id());
            return true;
        }));
        assertEquals(openApplicant, result);
    }

    @ParameterizedTest
    @MethodSource("gnerateGetApplicantsTestData")
    void getApplicants(List<Filter> filters, Criteria expectedCriteria) {

        //given
        List<Applicant> applicantRepositoryMockResponse = List.of(openApplicant, declinedApplicant);
        when(applicantRepositoryMock.findByDynamicCriteria(any(Criteria.class))).thenReturn(applicantRepositoryMockResponse);

        //when
        List<Applicant> result = sut.getApplicants(filters);

        //then
        verify(applicantRepositoryMock, times(1)).findByDynamicCriteria(expectedCriteria);
        assertEquals(applicantRepositoryMockResponse, result);
    }

    private static Stream<Arguments> gnerateGetApplicantsTestData() {
        Filter containsMol = new Filter("name", "mol", FilterMethod.CONTAINS_CASE_I);
        Filter statusOpen = new Filter("status", "OPEN", FilterMethod.EQUALS);
        Filter statusNotDeclined = new Filter("status", "DECLINED", FilterMethod.NOT_EQUALS);

        Criteria containsmolCriteria = Criteria.where(containsMol.getKey()).regex(containsMol.getValue(), "i");
        // need to copy criteria since the andOperator will modify the first criteria
        // so that containsmolCriteria != containsmolCriteria2 after the call containsmolCriteria.andOperator(...)
        Criteria containsmolCriteria2 = Criteria.where(containsMol.getKey()).regex(containsMol.getValue(), "i");
        Criteria statusOpenCriteria = Criteria.where(statusOpen.getKey()).is(statusOpen.getValue());
        Criteria statusNotDeclinedCriteria = Criteria.where(statusNotDeclined.getKey()).ne(statusNotDeclined.getValue());

        return Stream.of(
                Arguments.of(List.of(containsMol), containsmolCriteria),
                Arguments.of(List.of(statusOpen), statusOpenCriteria),
                Arguments.of(List.of(), new Criteria()),
                Arguments.of(List.of(statusNotDeclined), statusNotDeclinedCriteria),
                Arguments.of(List.of(containsMol, statusOpen), containsmolCriteria2.andOperator(statusOpenCriteria))
        );
    }
}