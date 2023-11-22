package de.immocloud.model.Exceptions;

import java.text.MessageFormat;
import java.util.UUID;

public class ApplicantNotFoundException extends RuntimeException{

    public ApplicantNotFoundException(UUID id){
        super(MessageFormat.format("Could not find applicant with id: {0}",  id.toString()));
    }
}
