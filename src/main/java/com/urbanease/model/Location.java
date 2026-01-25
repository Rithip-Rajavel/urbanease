package com.urbanease.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    private double latitude;

    private double longitude;

    private String address;

    private String city;

    private String state;

    private String postalCode;
}
