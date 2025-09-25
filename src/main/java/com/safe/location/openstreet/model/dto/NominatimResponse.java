package com.safe.location.openstreet.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimResponse {
    private NominatimAddress address;

    // Getter y setter

    public NominatimAddress getAddress() {
        return address;
    }

    public void setAddress(NominatimAddress address) {
        this.address = address;
    }
}