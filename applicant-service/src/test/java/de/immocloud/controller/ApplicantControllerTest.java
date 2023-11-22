package de.immocloud.controller;

import de.immocloud.model.*;
import de.immocloud.model.Exceptions.ApplicantNotFoundException;
import de.immocloud.service.ApplicantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Stream;

import static de.immocloud.utils.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicantControllerTest {

    private static ApplicantService applicantService;


    private ApplicantController sut;

    @BeforeEach
    public void setup() {
        applicantService = mock(ApplicantService.class);
        sut = new ApplicantController(applicantService);
    }

    @ParameterizedTest
    @MethodSource("gnerateGetApplicantsTestData")
    <T> void getApplicants(String nameFilterValue, Boolean hideDeclined, HttpStatusCode expectedStatusCode, T applicantServiceResponse) {

        //given
        Object expectedResponseBody;
        ArgumentCaptor<List<Filter>> filtersCaptor = ArgumentCaptor.forClass(List.class);
        if (applicantServiceResponse instanceof Exception e) {
            when(applicantService.getApplicants(anyList())).thenThrow(e);
            expectedResponseBody = e.getMessage();
        } else if (applicantServiceResponse instanceof List<?>) {
            when(applicantService.getApplicants(anyList())).thenReturn((List<Applicant>) applicantServiceResponse);
            expectedResponseBody = applicantServiceResponse;
        } else {
            fail("incompatible Dataset");
            return;
        }

        //when
        ResponseEntity<Object> result = sut.getApplicants(nameFilterValue, hideDeclined);
        verify(applicantService, times(1)).getApplicants(filtersCaptor.capture());

        //then
        assertEquals(expectedStatusCode, result.getStatusCode());
        assertEquals(expectedResponseBody, result.getBody());
        List<Filter> capturedFilters = filtersCaptor.getValue();

        if (!nameFilterValue.isEmpty()){
            assertEquals(1, capturedFilters.stream().filter(filter -> filter.getFilterMethod().equals(FilterMethod.CONTAINS_CASE_I)).toList().size());
            assertEquals(1, capturedFilters.stream().filter(filter -> filter.getKey().equals("name")).toList().size());
            assertEquals(1, capturedFilters.stream().filter(filter -> filter.getValue().equals(nameFilterValue)).toList().size());
        }
        if (hideDeclined){
            assertEquals(1, capturedFilters.stream().filter(filter -> filter.getFilterMethod().equals(FilterMethod.NOT_EQUALS)).toList().size());
            assertEquals(1, capturedFilters.stream().filter(filter -> filter.getKey().equals("status")).toList().size());
            assertEquals(1, capturedFilters.stream().filter(filter -> filter.getValue().equals(Status.DECLINED.toString())).toList().size());
        }
    }

    private static Stream<Arguments> gnerateGetApplicantsTestData() {
        List<Applicant> allApplicantsList = List.of(openApplicant, declinedApplicant, declinedApplicant);

        return Stream.of(
                Arguments.of("", false, HttpStatus.OK, allApplicantsList),
                Arguments.of("Filterpresent", false, HttpStatus.OK, Collections.emptyList()),
                Arguments.of("", true, HttpStatus.OK, Collections.emptyList()),
                Arguments.of("Filterpresent", true, HttpStatus.OK, Collections.emptyList()),
                Arguments.of("", false, HttpStatus.INTERNAL_SERVER_ERROR, new RuntimeException())
        );
    }

    @ParameterizedTest
    @MethodSource("gnerateGetUpdateApplicantTestData")
    <T> void updateApplicant(HttpStatusCode expectedStatusCode, T applicantServiceResponse) {

        //given
        UUID applicantUUID = UUID.randomUUID();
        Status expectedStatus = Status.ACCEPTED;

        Object expectedResponseBody;
        if (applicantServiceResponse instanceof Exception e) {
            when(applicantService.updateApplicant(applicantUUID, expectedStatus)).thenThrow(e);
            expectedResponseBody = e.getMessage();
        } else if (applicantServiceResponse instanceof Applicant) {
            when(applicantService.updateApplicant(applicantUUID, expectedStatus)).thenReturn((Applicant) applicantServiceResponse);
            expectedResponseBody = applicantServiceResponse;
        } else {
            fail("incompatible Dataset");
            return;
        }

        //when
        ResponseEntity<Object> result = sut.updateApplicant(applicantUUID, expectedStatus);

        //then
        assertEquals(expectedStatusCode, result.getStatusCode());
        assertEquals(expectedResponseBody, result.getBody());
    }

    private static Stream<Arguments> gnerateGetUpdateApplicantTestData() {
        return Stream.of(
                Arguments.of(HttpStatus.OK, openApplicant),
                Arguments.of(HttpStatus.OK, declinedApplicant),
                Arguments.of(HttpStatus.OK, acceptedApplicant),
                Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, new RuntimeException()),
                Arguments.of(HttpStatus.BAD_REQUEST, new ApplicantNotFoundException(UUID.randomUUID()))
        );
    }

    @ParameterizedTest
    @MethodSource("gnerateGetAddApplicantTestData")
    <T> void addApplicant(HttpStatusCode expectedStatusCode, T applicantServiceResponse) {
        //given
        ReceivedApplicant receivedApplicant = new ReceivedApplicant(openApplicant.getName());
        Object expectedResponseBody;
        if (applicantServiceResponse instanceof Exception e) {
            when(applicantService.addApplicant(receivedApplicant.getName())).thenThrow(e);
            expectedResponseBody = e.getMessage();
        } else if (applicantServiceResponse instanceof Applicant) {
            when(applicantService.addApplicant(receivedApplicant.getName())).thenReturn((Applicant) applicantServiceResponse);
            expectedResponseBody = applicantServiceResponse;
        } else {
            fail("incompatible Dataset");
            return;
        }

        //when
        ResponseEntity<Object> result = sut.addApplicant(receivedApplicant);

        //then
        assertEquals(expectedStatusCode, result.getStatusCode());
        assertEquals(expectedResponseBody, result.getBody());
    }

    private static Stream<Arguments> gnerateGetAddApplicantTestData() {
        return Stream.of(
                Arguments.of(HttpStatus.CREATED, openApplicant),
                Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, new RuntimeException())
        );
    }

}