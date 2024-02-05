package com.example.shop.cart;

import com.example.shop.core.cart.repository.CartRepository;
import com.example.shop.core.cart.service.CartService;
import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.product.repository.ProductCommonEntity;
import com.example.shop.public_interface.cart.CartDto;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import static com.example.shop.public_.tables.Client.CLIENT;
import static com.example.shop.public_.tables.Product.PRODUCT;
import static com.example.shop.public_.tables.Store.STORE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@SpringJUnitConfig(classes = {CartIntegrationTestConfiguration.class})
@ActiveProfiles("test")
public class CartServiceIntegrationTest {
    private static final String CHANGELOG_FILE_PATH = "db/changelog/db.changelog-master.yaml";
    private static final UUID STORE_ID = UUID.randomUUID();

    @Container
    private static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("Shop")
            .withUsername("postgres")
            .withPassword("veryStrongPassword");

    private static DSLContext dslContext;

    @Autowired private CartService cartService;
    @Autowired private CartRepository cartRepository;

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
    public void addProduct() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp@mail.ru";
        var productCode = "1";
        var client = formatClient(clientId, clientEmail);
        var product = formatProduct(productCode);
        addClientInDatabase(client);
        addProductInDatabase(product);

        var cartDto = new CartDto(
                clientId,
                productCode,
                0
        );

        cartService.addProduct(cartDto);

        assertEquals(1, cartRepository.getCountProduct(clientId, productCode));

        cartService.addProduct(cartDto);

        assertEquals(2, cartRepository.getCountProduct(clientId, productCode));
    }

    @Test
    public void addAndDeleteProduct() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp1@mail.ru";
        var productCode = "2";
        var client = formatClient(clientId, clientEmail);
        var product = formatProduct(productCode);
        addClientInDatabase(client);
        addProductInDatabase(product);

        var cartDto = new CartDto(
                clientId,
                productCode,
                0
        );

        cartService.addProduct(cartDto);

        assertEquals(1, cartRepository.getCountProduct(clientId, productCode));

        cartService.deleteProduct(cartDto);

        assertEquals(0, cartRepository.getCountProduct(clientId, productCode));
    }

    @Test
    public void setCountProduct() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp2@mail.ru";
        var productCode = "3";
        var client = formatClient(clientId, clientEmail);
        var product = formatProduct(productCode);
        addClientInDatabase(client);
        addProductInDatabase(product);

        var countProduct = 7;

        var firstCartDto = new CartDto(
                clientId,
                productCode,
                countProduct
        );


        cartService.setCountProduct(firstCartDto);

        assertEquals(countProduct, cartRepository.getCountProduct(firstCartDto.clientId(), firstCartDto.productCode()));

        countProduct = 0;

        var secondCartDto = new CartDto(
                clientId,
                productCode,
                countProduct
        );

        cartService.setCountProduct(secondCartDto);

        assertEquals(countProduct, cartRepository.getCountProduct(secondCartDto.clientId(), secondCartDto.productCode()));
    }

    private ClientEntity formatClient(UUID clientId, String email) {
        return new ClientEntity(
                clientId,
                "Gordey",
                email,
                "12345678",
                OffsetDateTime.now(),
                "MALE",
                OffsetDateTime.now()
        );
    }

    private ProductCommonEntity formatProduct(String productCode) {
        return new ProductCommonEntity(
                productCode,
                STORE_ID,
                Collections.emptyList(),
                "Клавиатура",
                "клавиатура",
                BigDecimal.valueOf(123.45),
                4.6,
                34,
                "{}"
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
        dslContext.insertInto(STORE)
                .set(STORE.STORE_ID, STORE_ID)
                .set(STORE.NAME, "Рога и копыта")
                .execute();
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

    private static void addClientInDatabase(ClientEntity entity) {
        dslContext.insertInto(CLIENT)
                .set(CLIENT.CLIENT_ID, entity.clientId())
                .set(CLIENT.NAME, entity.name())
                .set(CLIENT.EMAIL, entity.email())
                .set(CLIENT.PASSWORD, entity.password())
                .set(CLIENT.BIRTH_DATE, entity.birthDate())
                .set(CLIENT.GENDER, entity.gender())
                .set(CLIENT.CREATED_DATE, entity.createdDate())
                .execute();
    }

}
