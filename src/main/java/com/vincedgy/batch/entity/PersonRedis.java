package com.vincedgy.batch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;

@RedisHash("Person")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonRedis implements Serializable {

    public enum Gender {
        MALE, FEMALE
    }
    private String id;
    private String extId;
    private String name;
    private Gender gender;
}