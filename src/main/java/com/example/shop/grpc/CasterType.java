package com.example.shop.grpc;

import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class CasterType {
    public static OffsetDateTime cast(Timestamp time) {
        var instant = Instant.ofEpochSecond(time.getSeconds(), time.getNanos());
        return OffsetDateTime.ofInstant(instant, ZoneId.of( "America/Montreal"));
    }
}
