package com.hiba.booking.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class Customer {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String email;
    private int phone;
    private boolean gender; // true = male, false = female (you can adjust as needed)
    private LocalDate birthDate;
    private Address address;

}
