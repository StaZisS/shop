package com.example.shop.order;

import com.example.shop.core.cart.repository.CartEntity;
import com.example.shop.core.client.repository.ClientEntity;
import com.example.shop.core.order.repository.DeliveryStatus;
import com.example.shop.core.order.repository.OrderRepository;
import com.example.shop.core.order.service.OrderService;
import com.example.shop.core.product.repository.ProductCommonEntity;
import com.example.shop.public_interface.exception.ExceptionInApplication;
import com.example.shop.public_interface.exception.ExceptionType;
import com.example.shop.public_interface.order.CreateOrderDto;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.example.shop.public_.tables.Client.CLIENT;
import static com.example.shop.public_.tables.Product.PRODUCT;
import static com.example.shop.public_.tables.Store.STORE;
import static com.example.shop.public_.tables.Cart.CART;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@SpringJUnitConfig(classes = {OrderIntegrationTestConfiguration.class})
@ActiveProfiles("test")
public class OrderServiceIntagrationTest {
    private static final String CHANGELOG_FILE_PATH = "db/changelog/db.changelog-master.yaml";
    private static final UUID STORE_ID = UUID.randomUUID();
    private static final List<String> PRODUCTS_CODE = List.of(
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10"
    );
    private static final BigDecimal PRICE_PRODUCT = BigDecimal.valueOf(123.45);
    private static final Random RANDOM = new Random();

    @Container
    private static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("Shop")
            .withUsername("postgres")
            .withPassword("veryStrongPassword");

    private static DSLContext dslContext;

    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;

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
    public void createOrder() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp@gmail.com";
        var addressCode = "1";
        var clientEntity = formatClient(clientId, clientEmail);
        addClientInDatabase(clientEntity);

        var randomFourProductsCode = getRandomProductsCode(4);
        var productsInCart = getCartEntityList(randomFourProductsCode, clientId);
        var totalPrice = getTotalPrice(productsInCart);
        for (var productInCart : productsInCart) {
            addProductInCart(productInCart);
        }

        var createOrderDto = new CreateOrderDto(clientId, addressCode, randomFourProductsCode);
        var orderId = orderService.createOrder(createOrderDto);

        var orderEntity = orderRepository.getOrder(orderId)
                .orElseThrow(() -> new NotFoundException("Заказ не найден"));

        assertEquals(DeliveryStatus.PROCESSING, orderEntity.status());
        assertEquals(clientId, orderEntity.clientId());
        assertEquals(totalPrice, orderEntity.totalPrice());

        var productsInOrder = orderRepository.getProductsInOrder(orderId);

        assertEquals(randomFourProductsCode.size(), productsInOrder.size());
    }

    @Test
    public void cancelOrder() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp1@gmail.com";
        var addressCode = "2";
        var clientEntity = formatClient(clientId, clientEmail);
        addClientInDatabase(clientEntity);

        var randomFourProductsCode = getRandomProductsCode(4);
        var productsInCart = getCartEntityList(randomFourProductsCode, clientId);
        for (var productInCart : productsInCart) {
            addProductInCart(productInCart);
        }

        var createOrderDto = new CreateOrderDto(clientId, addressCode, randomFourProductsCode);
        var orderId = orderService.createOrder(createOrderDto);

        orderService.cancelOrder(orderId, clientId);

        var orderEntity = orderRepository.getOrder(orderId)
                .orElseThrow(() -> new NotFoundException("Заказ не найден"));

        assertEquals(DeliveryStatus.CANCELED, orderEntity.status());
    }

    @Test
    public void cancelOrderTwice() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp2@gmail.com";
        var addressCode = "3";
        var clientEntity = formatClient(clientId, clientEmail);
        addClientInDatabase(clientEntity);

        var randomFourProductsCode = getRandomProductsCode(4);
        var productsInCart = getCartEntityList(randomFourProductsCode, clientId);
        for (var productInCart : productsInCart) {
            addProductInCart(productInCart);
        }

        var createOrderDto = new CreateOrderDto(clientId, addressCode, randomFourProductsCode);
        var orderId = orderService.createOrder(createOrderDto);

        orderService.cancelOrder(orderId, clientId);

        var orderEntity = orderRepository.getOrder(orderId)
                .orElseThrow(() -> new NotFoundException("Заказ не найден"));

        assertEquals(DeliveryStatus.CANCELED, orderEntity.status());

        var exception = assertThrows(ExceptionInApplication.class, () -> orderService.cancelOrder(orderId, clientId));
        assertEquals(ExceptionType.INVALID, exception.getType());
    }

    @Test
    public void getOrderDetails() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp3@gmail.com";
        var addressCode = "4";
        var clientEntity = formatClient(clientId, clientEmail);
        addClientInDatabase(clientEntity);

        var randomFourProductsCode = getRandomProductsCode(4);
        var productsInCart = getCartEntityList(randomFourProductsCode, clientId);
        var totalPrice = getTotalPrice(productsInCart);
        for (var productInCart : productsInCart) {
            addProductInCart(productInCart);
        }

        var createOrderDto = new CreateOrderDto(clientId, addressCode, randomFourProductsCode);
        var orderId = orderService.createOrder(createOrderDto);

        var response = orderService.getOrderDetails(orderId, clientId);

        assertEquals(DeliveryStatus.PROCESSING.name(), response.status());
        assertEquals(totalPrice, response.totalPrice());
        assertEquals(randomFourProductsCode.size(), response.products().size());
    }

    @Test
    public void getOrders() {
        var clientId = UUID.randomUUID();
        var clientEmail = "ggwp4@gmail.com";
        var addressCode = "5";
        var clientEntity = formatClient(clientId, clientEmail);
        addClientInDatabase(clientEntity);

        var randomFourProductsCode = getRandomProductsCode(4);
        var productsInCart = getCartEntityList(randomFourProductsCode, clientId);
        for (var productInCart : productsInCart) {
            addProductInCart(productInCart);
        }

        var createOrderDto = new CreateOrderDto(clientId, addressCode, randomFourProductsCode);
        orderService.createOrder(createOrderDto);

        var orders = orderService.getOrders(clientId);

        assertEquals(1, orders.size());

        var randomFiveProductsCode = getRandomProductsCode(5);
        productsInCart = getCartEntityList(randomFiveProductsCode, clientId);
        for (var productInCart : productsInCart) {
            addProductInCart(productInCart);
        }

        createOrderDto = new CreateOrderDto(clientId, addressCode, randomFiveProductsCode);
        orderService.createOrder(createOrderDto);

        orders = orderService.getOrders(clientId);

        assertEquals(2, orders.size());
    }

    private static ClientEntity formatClient(UUID clientId, String email) {
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

    private static ProductCommonEntity formatProduct(String productCode) {
        return new ProductCommonEntity(
                productCode,
                STORE_ID,
                Collections.emptyList(),
                "Клавиатура",
                "клавиатура",
                PRICE_PRODUCT,
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

        for(var productCode : PRODUCTS_CODE) {
            var productEntity = formatProduct(productCode);
            addProductInDatabase(productEntity);
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

    private static void addProductInCart(CartEntity entity) {
        dslContext.insertInto(CART)
                .set(CART.PRODUCT_CODE, entity.productCode())
                .set(CART.CLIENT_ID, entity.clientId())
                .set(CART.COUNT_PRODUCT, entity.countProduct())
                .execute();
    }

    private static List<String> getRandomProductsCode(int countProduct) {
        if (countProduct > PRODUCTS_CODE.size()) {
            throw new RuntimeException("Количество продуктов больше чем есть в базе данных");
        }

        var result = new ArrayList<>(PRODUCTS_CODE);
        var countElements = result.size() - countProduct;

        for (var i = 0; i < countElements; i++) {
            var randomIndex = RANDOM.nextInt(result.size());
            String randomElement = result.get(randomIndex);
            result.remove(randomElement);
        }

        return result;
    }

    private static List<CartEntity> getCartEntityList(List<String> productsCode, UUID clientId) {
        return productsCode.stream()
                .map(productCode -> {
                    var countProduct = RANDOM.nextInt();
                    return new CartEntity(
                            clientId,
                            productCode,
                            countProduct
                    );
                })
                .toList();
    }

    private static BigDecimal getTotalPrice(List<CartEntity> products) {
        return products.stream()
                .map(product -> BigDecimal.valueOf(product.countProduct()).multiply(PRICE_PRODUCT))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
