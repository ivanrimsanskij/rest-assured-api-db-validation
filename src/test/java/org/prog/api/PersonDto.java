package org.prog.api;

import lombok.Data;

@Data
public class PersonDto {
    private NameDto name;
    private String gender;
    private String nat;
}
