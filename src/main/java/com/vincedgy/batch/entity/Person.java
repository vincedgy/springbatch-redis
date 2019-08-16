package com.vincedgy.batch.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Person {
    Long id;
    String firstName;
    String lastName;
    String email;
    String gender;
    String ipAddress;
}
