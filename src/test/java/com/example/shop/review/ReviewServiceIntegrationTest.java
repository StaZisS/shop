package com.example.shop.review;

import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.order.repository.DeliveryStatus;
import com.example.shop.core.order.repository.OrderEntity;
import com.example.shop.core.order.repository.ProductInOrderEntity;
import com.example.shop.core.product.repository.ProductCommonEntity;
import com.example.shop.core.review.repository.ReviewEntity;
import com.example.shop.core.review.repository.ReviewRepository;
import com.example.shop.core.review.service.ReviewService;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.review.ReviewCreateDto;
import com.example.shop.public_interface.review.ReviewDeleteDto;
import com.example.shop.public_interface.review.ReviewUpdateDto;
import com.github.dockerjava.api.exception.NotFoundException;
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
import java.util.List;
import java.util.UUID;

import static com.example.shop.public_.tables.Client.CLIENT;
import static com.example.shop.public_.tables.Product.PRODUCT;
import static com.example.shop.public_.tables.Store.STORE;
import static com.example.shop.public_.tables.Orders.ORDERS;
import static com.example.shop.public_.tables.ProductInOrder.PRODUCT_IN_ORDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@SpringJUnitConfig(classes = {ReviewIntegrationTestConfiguration.class})
@ActiveProfiles("test")
public class ReviewServiceIntegrationTest {
    private static final String CHANGELOG_FILE_PATH = "db/changelog/db.changelog-master.yaml";
    private static final UUID STORE_ID = UUID.randomUUID();

    @Container
    private static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("Shop")
            .withUsername("postgres")
            .withPassword("veryStrongPassword");

    private static DSLContext dslContext;

    @Autowired private ReviewService reviewService;
    @Autowired private ReviewRepository reviewRepository;

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
    public void addReview() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp@mail.ru";
        var productCode = "1";
        var client = formatClient(clientId, clientEmail);
        var product = formatProduct(productCode);
        addClientInDatabase(client);
        addProductInDatabase(product);

        createOrderWithProducts(List.of(productCode), clientId, DeliveryStatus.DELIVERED);

        var reviewCreateDto = formatReviewCreateDto(clientId, productCode);
        reviewService.createReview(reviewCreateDto);
    }

    @Test
    public void addTwoReviewOnOneProduct() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp1@mail.ru";
        var productCode = "2";
        var client = formatClient(clientId, clientEmail);
        var product = formatProduct(productCode);
        addClientInDatabase(client);
        addProductInDatabase(product);

        createOrderWithProducts(List.of(productCode), clientId, DeliveryStatus.DELIVERED);

        var reviewCreateDto = formatReviewCreateDto(clientId, productCode);
        reviewService.createReview(reviewCreateDto);

        assertThrows(ExceptionInApplication.class, () -> reviewService.createReview(reviewCreateDto));
    }

    @Test
    public void addReviewNotDeliveredProduct() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp2@mail.ru";
        var productCode = "3";
        var client = formatClient(clientId, clientEmail);
        var product = formatProduct(productCode);
        addClientInDatabase(client);
        addProductInDatabase(product);

        createOrderWithProducts(List.of(productCode), clientId, DeliveryStatus.APPLY);

        var reviewCreateDto = formatReviewCreateDto(clientId, productCode);
        assertThrows(ExceptionInApplication.class, () -> reviewService.createReview(reviewCreateDto));
    }

    @Test
    public void updateReview() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp3@mail.ru";
        var productCode = "4";
        var client = formatClient(clientId, clientEmail);
        var product = formatProduct(productCode);
        addClientInDatabase(client);
        addProductInDatabase(product);

        createOrderWithProducts(List.of(productCode), clientId, DeliveryStatus.DELIVERED);

        var reviewCreateDto = formatReviewCreateDto(clientId, productCode);
        var reviewId = reviewService.createReview(reviewCreateDto);

        var newRating = 5;
        var newBody = "{}";

        var reviewUpdateDto = new ReviewUpdateDto(
                reviewId,
                clientId,
                newRating,
                newBody
        );
        reviewService.updateReview(reviewUpdateDto);

        var review = reviewRepository.getReviewById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        assertEquals(newRating, review.rating());
        assertEquals(newBody, review.body());
        assertNotNull(review.modifiedTime());
    }

    @Test
    public void updateNotExistReview() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp4@mail.ru";
        var productCode = "5";
        var client = formatClient(clientId, clientEmail);
        var product = formatProduct(productCode);
        addClientInDatabase(client);
        addProductInDatabase(product);

        createOrderWithProducts(List.of(productCode), clientId, DeliveryStatus.DELIVERED);

        var newRating = 5;
        var newBody = "{}";

        var reviewUpdateDto = new ReviewUpdateDto(
                UUID.randomUUID(),
                clientId,
                newRating,
                newBody
        );
        assertThrows(ExceptionInApplication.class, () -> reviewService.updateReview(reviewUpdateDto));
    }

    @Test
    public void deleteReview() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp5@mail.ru";
        var productCode = "6";
        var client = formatClient(clientId, clientEmail);
        var product = formatProduct(productCode);
        addClientInDatabase(client);
        addProductInDatabase(product);

        createOrderWithProducts(List.of(productCode), clientId, DeliveryStatus.DELIVERED);

        var reviewCreateDto = formatReviewCreateDto(clientId, productCode);
        var reviewId = reviewService.createReview(reviewCreateDto);

        var reviewDeleteDto = new ReviewDeleteDto(reviewId, clientId);
        reviewService.deleteReview(reviewDeleteDto);

        var review = reviewRepository.getReviewById(reviewId);
        assertTrue(review.isEmpty());
    }

    @Test
    public void deleteNotExistReview() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp6@mail.ru";
        var productCode = "7";
        var client = formatClient(clientId, clientEmail);
        var product = formatProduct(productCode);
        addClientInDatabase(client);
        addProductInDatabase(product);

        createOrderWithProducts(List.of(productCode), clientId, DeliveryStatus.DELIVERED);

        var reviewDeleteDto = new ReviewDeleteDto(UUID.randomUUID(), clientId);
        assertThrows(ExceptionInApplication.class, () -> reviewService.deleteReview(reviewDeleteDto));
    }

    private ReviewCreateDto formatReviewCreateDto(UUID clientId, String productCode) {
        return new ReviewCreateDto(
                clientId,
                productCode,
                1,
                "{}"
        );
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

    private OrderEntity formatOrder(UUID clientId, DeliveryStatus status) {
        return new OrderEntity(
                UUID.randomUUID(),
                clientId,
                "",
                "",
                BigDecimal.valueOf(0),
                status,
                OffsetDateTime.now(),
                ""
        );
    }

    private ProductInOrderEntity formatProductInOrder(UUID orderId, String productCode) {
        return new ProductInOrderEntity(
                orderId,
                productCode,
                1
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

    private static void addOrderInDatabase(OrderEntity entity) {
        dslContext.insertInto(ORDERS)
                .set(ORDERS.ORDER_ID, entity.orderId())
                .set(ORDERS.CLIENT_ID, entity.clientId())
                .set(ORDERS.ADDRESS_DELIVERY_CODE, entity.addressDeliveryCode())
                .set(ORDERS.ADDRESS_DELIVERY, entity.addressDelivery())
                .set(ORDERS.TOTAL_PRICE, entity.totalPrice())
                .set(ORDERS.STATUS, entity.status().name())
                .set(ORDERS.CREATION_DATE, entity.creationDate())
                .set(ORDERS.TRACK_NUMBER, entity.trackNumber())
                .execute();
    }

    private static void addProductInOrderInDatabase(ProductInOrderEntity entity) {
        dslContext.insertInto(PRODUCT_IN_ORDER)
                .set(PRODUCT_IN_ORDER.ORDER_ID, entity.orderId())
                .set(PRODUCT_IN_ORDER.PRODUCT_CODE, entity.productCode())
                .set(PRODUCT_IN_ORDER.COUNT_PRODUCT, entity.countProduct())
                .execute();
    }

    private void createOrderWithProducts(List<String> productsCode, UUID clientId, DeliveryStatus status) {
        var order = formatOrder(clientId, status);
        var productsInOrder = productsCode.stream()
                .map(product -> formatProductInOrder(order.orderId(), product))
                .toList();

        addOrderInDatabase(order);
        for(var product : productsInOrder) {
            addProductInOrderInDatabase(product);
        }
    }
}
