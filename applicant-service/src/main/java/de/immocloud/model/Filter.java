package de.immocloud.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Filter {
    private String key;
    private String value;
    private FilterMethod filterMethod;
}
