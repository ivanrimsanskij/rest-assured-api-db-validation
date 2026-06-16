package org.prog.api;

public class PersonDto {
    private NameDto name;
    private String gender;
    private String nat;

    public NameDto getName() {
        return name;
    }

    public void setName(NameDto name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNat() {
        return nat;
    }

    public void setNat(String nat) {
        this.nat = nat;
    }
}
