package de.immocloud.utils;

import de.immocloud.model.Applicant;
import de.immocloud.model.Status;

import java.util.UUID;

public class TestData {

    public static final Applicant openApplicant = new Applicant(UUID.randomUUID(), "Marco Molfese", Status.OPEN);
    public static final Applicant declinedApplicant = new Applicant(UUID.randomUUID(), "Max Mustermann", Status.DECLINED);
    public static final Applicant acceptedApplicant = new Applicant(UUID.randomUUID(), "Qwert Zuiopü", Status.ACCEPTED);

}
