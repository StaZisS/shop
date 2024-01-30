package com.example.shop.core.store.repository;

import com.example.shop.public_.tables.Store;
import com.example.shop.public_.tables.StoreEmployees;
import com.example.shop.public_.tables.records.StoreEmployeesRecord;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import com.example.shop.public_interface.store.EmployeePosition;
import com.example.shop.public_interface.store.StoreCreateDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {
    private final DSLContext create;
    private final StoreEntityMapper storeMapper = new StoreEntityMapper();

    @Override
    public StoreEntity createIfNotExist(StoreCreateDto dto) {
        return create.transactionResult(configuration -> {
            final DSLContext ctx = DSL.using(configuration);

            var isDuplicateName = ctx.selectFrom(Store.STORE)
                    .where(Store.STORE.NAME.eq(dto.storeName()))
                    .fetchOptional()
                    .isPresent();

            if (isDuplicateName) {
                throw new ExceptionInApplication("Магазин с таким названием уже существует", ExceptionType.ALREADY_EXISTS);
            }

            final UUID storeId = UUID.randomUUID();

            var storeEntity = ctx.insertInto(Store.STORE)
                    .set(Store.STORE.STORE_ID, storeId)
                    .set(Store.STORE.NAME, dto.storeName())
                    .returning(Store.STORE)
                    .fetchOptional()
                    .map(storeMapper)
                    .orElseThrow(() -> new ExceptionInApplication("Ошибка при создании магазина", ExceptionType.FATAL));

            var employeeEntity = new EmployeeEntity(
                    dto.clientId(),
                    storeId,
                    Set.of(EmployeePosition.ADMIN)
            );

            addStoreEmployee(employeeEntity, ctx);

            return storeEntity;
        });
    }

    @Override
    public void addStoreEmployee(EmployeeEntity entity) {
        addStoreEmployee(entity, create);
    }

    private void addStoreEmployee(EmployeeEntity entity, DSLContext localCtx) {
        var recordList = entity.positions().stream().map(role ->
                new StoreEmployeesRecord(
                        entity.storeId(),
                        entity.clientId(),
                        role.name()
                )
        ).toList();

        var insertStepN = localCtx.insertInto(StoreEmployees.STORE_EMPLOYEES)
                .set(recordList.getFirst());

        for (var record : recordList.subList(1, recordList.size())) {
            insertStepN = insertStepN.set(record);
        }

        insertStepN.execute();
    }
}
