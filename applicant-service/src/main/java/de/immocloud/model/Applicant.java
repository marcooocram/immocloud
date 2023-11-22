package de.immocloud.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Document(collection = "applicants-collection")
public final class Applicant implements Comparable<Applicant>{

    @Id
    private UUID _id;
    private String name;
    private Status status;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Applicant applicant = (Applicant) o;
        return Objects.equals(get_id(), applicant.get_id()) && Objects.equals(getName(), applicant.getName()) && getStatus() == applicant.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_id(), getName(), getStatus());
    }

    @Override
    public int compareTo(Applicant otherApplicant) {
        return this.name.compareTo(otherApplicant.name);
    }
}
