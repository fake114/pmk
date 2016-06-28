package com.example.black.pmk.data;

import java.io.Serializable;
import java.util.Date;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;

public class Patient implements Serializable {

    private String givenName, name;
    private AdministrativeGenderEnum genderEnum;
    private Date birthday;

    public Patient () {
        givenName = "";
        name = "";
        genderEnum = AdministrativeGenderEnum.UNKNOWN;
        birthday = new Date();
    }

    public Patient (String givenName, String name, AdministrativeGenderEnum genderEnum, Date birthday) {
        this.givenName = givenName;
        this.name = name;
        this.genderEnum = genderEnum;
        this.birthday = birthday;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AdministrativeGenderEnum getGenderEnum() {
        return genderEnum;
    }

    public void setGenderEnum(AdministrativeGenderEnum genderEnum) {
        this.genderEnum = genderEnum;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
