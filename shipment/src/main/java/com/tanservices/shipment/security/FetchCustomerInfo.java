package com.tanservices.shipment.security;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class FetchCustomerInfo {

    private Claims claims;

    public void setClaims(Claims claims) {
        this.claims = claims;
    }

    public String getCustomerName() {
        return (String) this.claims.get("name");
    }

    public String getCustomerEmail() {
        return (String) this.claims.get("email");
    }

    public String getCustomerAddress() {
        return (String) this.claims.get("address");
    }
}
