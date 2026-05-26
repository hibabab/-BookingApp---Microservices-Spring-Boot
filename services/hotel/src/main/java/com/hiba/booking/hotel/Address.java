package com.hiba.booking.hotel;

import jakarta.persistence.Embeddable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Embeddable
public class Address {
    private String street;
    private String zipCode;
    private String city;
}
