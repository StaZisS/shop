package com.example.shop.core.client.repository;

import com.example.shop.public_.tables.Client;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {
    private final DSLContext create;
    private final ClientEntityMapper clientEntityMapper = new ClientEntityMapper();

    @Override
    public Optional<ClientEntity> createClient(ClientEntity entity) {
        return create.insertInto(Client.CLIENT)
                .set(Client.CLIENT.CLIENT_ID, entity.clientId())
                .set(Client.CLIENT.NAME, entity.name())
                .set(Client.CLIENT.EMAIL, entity.email())
                .set(Client.CLIENT.PASSWORD, entity.password())
                .set(Client.CLIENT.BIRTH_DATE, entity.birthDate())
                .set(Client.CLIENT.GENDER, entity.gender())
                .set(Client.CLIENT.CREATED_DATE, entity.createdDate())
                .returning(Client.CLIENT)
                .fetchOptional()
                .map(clientEntityMapper::map);
    }

    @Override
    public Optional<ClientEntity> getClientByEmail(String email) {
        return create.selectFrom(Client.CLIENT)
                .where(Client.CLIENT.EMAIL.eq(email))
                .fetchOptional()
                .map(clientEntityMapper::map);
    }

    @Override
    public Optional<ClientEntity> getClientByClientId(UUID clientId) {
        return create.selectFrom(Client.CLIENT)
                .where(Client.CLIENT.CLIENT_ID.eq(clientId))
                .fetchOptional()
                .map(clientEntityMapper::map);
    }
}
