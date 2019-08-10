package com.vincedgy.batch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String ipAddress;
}
