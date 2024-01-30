package com.example.shop.core.store.repository;

import com.example.shop.public_.tables.records.StoreRecord;
import org.jooq.RecordMapper;

public class StoreEntityMapper implements RecordMapper<StoreRecord, StoreEntity> {
    @Override
    public StoreEntity map(StoreRecord storeRecord) {
        return new StoreEntity(
                storeRecord.getStoreId(),
                storeRecord.getName()
        );
    }
}
