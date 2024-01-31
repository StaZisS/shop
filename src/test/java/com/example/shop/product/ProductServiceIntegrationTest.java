package com.example.shop.product;

import com.example.shop.core.product.repository.ProductCommonEntity;
import com.example.shop.core.product.repository.ProductRepository;
import com.example.shop.core.product.service.ProductService;
import com.example.shop.public_interface.product.FilterDto;
import com.example.shop.public_interface.product.ProductCommonDto;
import com.example.shop.public_interface.product.SortType;
import com.example.shop.public_interface.product.mapper.CommonProductMapEntityToDto;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static com.example.shop.public_.tables.Client.CLIENT;
import static com.example.shop.public_.tables.Store.STORE;
import static com.example.shop.public_.tables.Product.PRODUCT;

@SpringBootTest
@Testcontainers
@SpringJUnitConfig(classes = {ProductIntegrationTestConfiguration.class})
@ActiveProfiles("test")
public class ProductServiceIntegrationTest {
    private static final String CHANGELOG_FILE_PATH = "db/changelog/db.changelog-master.yaml";

    @Container
    private static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("Shop")
            .withUsername("postgres")
            .withPassword("veryStrongPassword");

    private static DSLContext dslContext;

    @Autowired private ProductService productService;
    @Autowired private ProductRepository productRepository;

    private static final UUID STORE_ID = UUID.randomUUID();

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.password", database::getPassword);
        registry.add("spring.datasource.username", database::getUsername);
    }

    @BeforeAll
    public static void setup() throws SQLException, LiquibaseException {
        migrate();

        dslContext = DSL.using(
                database.getJdbcUrl(),
                database.getUsername(),
                database.getPassword()
        );

        databasePreparation();
    }

    @Test
    public void getProducts() {
        var filterDto = FilterDto.getDefault();
        var products = productService.getProducts(filterDto);

        assertFalse(products.products().isEmpty());
    }

    @Test
    public void getProductsSortedByPriceAsc() {
        var productList = mapListEntityToDto(getProductList());
        var filterDto = getFilterDtoWithSortType(SortType.PRICE_ASC);

        var receivedProductList = productService.getProducts(filterDto);
        var comparator = Comparator.comparing(ProductCommonDto::price);

        assertArrayEquals(
                getSortedProducts(productList, comparator).toArray(),
                receivedProductList.products().toArray()
        );
    }

    @Test
    public void getProductsSortedByPriceDesc() {
        var productList = mapListEntityToDto(getProductList());
        var filterDto = getFilterDtoWithSortType(SortType.PRICE_DESC);

        var receivedProductList = productService.getProducts(filterDto);
        var comparator = Comparator.comparing(ProductCommonDto::price)
                .reversed();

        assertArrayEquals(
                getSortedProducts(productList, comparator).toArray(),
                receivedProductList.products().toArray()
        );
    }

    @Test
    public void getProductsSortedByOrderDesc() {
        var productList = mapListEntityToDto(getProductList());
        var filterDto = getFilterDtoWithSortType(SortType.TOTAL_ORDER_DESC);

        var receivedProductList = productService.getProducts(filterDto);
        var comparator = Comparator.comparing(ProductCommonDto::orderQuantity)
                .reversed();

        assertArrayEquals(
                getSortedProducts(productList, comparator).toArray(),
                receivedProductList.products().toArray()
        );
    }

    private static List<ProductCommonEntity> getProductList() {
        return List.of(
                new ProductCommonEntity(
                        "1",
                        STORE_ID,
                        Collections.emptyList(),
                        "Клавиатура",
                        "клавиатура",
                        BigDecimal.valueOf(123.45),
                        4.6,
                        34,
                        "{}"
                ),
                new ProductCommonEntity(
                        "2",
                        STORE_ID,
                        Collections.emptyList(),
                        "Мышь",
                        "мышь",
                        BigDecimal.valueOf(543.21),
                        3.4,
                        60,
                        "{}"
                )
        );
    }

    private static void migrate() throws SQLException, LiquibaseException {
        var connection = DataSourceBuilder.create()
                .url(database.getJdbcUrl())
                .username(database.getUsername())
                .password(database.getPassword())
                .build()
                .getConnection();

        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

        Liquibase liquibase = new Liquibase(CHANGELOG_FILE_PATH, new ClassLoaderResourceAccessor(), database);

        liquibase.update();
    }

    private static void databasePreparation() {
        dslContext.insertInto(CLIENT)
                .set(CLIENT.CLIENT_ID, UUID.randomUUID())
                .set(CLIENT.NAME, "Gordey")
                .set(CLIENT.EMAIL, "ggwp@mail.ru")
                .set(CLIENT.PASSWORD, "12345678")
                .set(CLIENT.BIRTH_DATE, OffsetDateTime.now())
                .set(CLIENT.GENDER, "MALE")
                .set(CLIENT.CREATED_DATE, OffsetDateTime.now())
                .execute();

        dslContext.insertInto(STORE)
                .set(STORE.STORE_ID, STORE_ID)
                .set(STORE.NAME, "Рога и копыта")
                .execute();

        var listProducts = getProductList();

        for (ProductCommonEntity product : listProducts) {
            addProductInDatabase(product);
        }
    }

    private static void addProductInDatabase(ProductCommonEntity entity) {
        dslContext.insertInto(PRODUCT)
                .set(PRODUCT.CODE, entity.code())
                .set(PRODUCT.STORE_ID, entity.storeId())
                .set(PRODUCT.NAME, entity.name())
                .set(PRODUCT.NORMALIZED_NAME, entity.normalizeName())
                .set(PRODUCT.PRICE, entity.price())
                .set(PRODUCT.RATING, entity.rating())
                .set(PRODUCT.ORDER_QUANTITY, entity.orderQuantity())
                .set(PRODUCT.ADDITIONAL_INFO, JSONB.valueOf(entity.additionalInfo()))
                .execute();
    }

    private List<ProductCommonDto> getSortedProducts(List<ProductCommonDto> products, Comparator<ProductCommonDto> comparator) {
        var sortedProducts = new ArrayList<>(products);
        sortedProducts.sort(comparator);
        return sortedProducts;
    }

    private List<ProductCommonDto> mapListEntityToDto(List<ProductCommonEntity> entityList) {
        return entityList.stream()
                .map(CommonProductMapEntityToDto::map)
                .collect(Collectors.toList());
    }

    private FilterDto getFilterDtoWithSortType(SortType type) {
        return new FilterDto(
                "",
                type,
                new FilterDto.PaginationProperty(getProductList().size(), 1)
        );
    }
}
