package com.example.shop.core.client.repository;

import com.example.shop.public_.tables.records.ClientRecord;
import org.jooq.RecordMapper;

public class ClientEntityMapper implements RecordMapper<ClientRecord, ClientEntity> {
    @Override
    public ClientEntity map(ClientRecord clientRecord) {
        return new ClientEntity(
                clientRecord.getClientId(),
                clientRecord.getName(),
                clientRecord.getEmail(),
                clientRecord.getPassword(),
                clientRecord.getBirthDate(),
                clientRecord.getGender(),
                clientRecord.getCreatedDate()
        );
    }
}
