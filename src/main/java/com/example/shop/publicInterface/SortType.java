package com.example.shop.publicInterface;

import com.example.shop.core.product.repository.ProductCommonEntity;

import java.util.Comparator;

public enum SortType {
    DEFAULT {
        @Override
        public Comparator<ProductCommonEntity> getComparator() {
            return Comparator.comparing(ProductCommonEntity::name);
        }
    },
    TOTAL_ORDER_DESC {
        @Override
        public Comparator<ProductCommonEntity> getComparator() {
            return Comparator.comparing(ProductCommonEntity::orderQuantity)
                    .reversed();
        }
    },
    PRICE_ASC {
        @Override
        public Comparator<ProductCommonEntity> getComparator() {
            return Comparator.comparing(ProductCommonEntity::price);
        }
    },
    PRICE_DESC {
        @Override
        public Comparator<ProductCommonEntity> getComparator() {
            return Comparator.comparing(ProductCommonEntity::price)
                    .reversed();
        }
    },
    ;
    public abstract Comparator<ProductCommonEntity> getComparator();
}
