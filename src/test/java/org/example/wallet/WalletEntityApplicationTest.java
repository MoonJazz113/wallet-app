package org.example.wallet;

import org.example.wallet.dto.WalletBalanceResponse;
import org.example.wallet.dto.entity.WalletEntity;
import org.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WalletEntityApplicationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
            .withDatabaseName("mydb")
            .withUsername("postgress")
            .withPassword("1234");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.enabled", () -> true);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WalletRepository walletRepository;

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll(); // чистим таблицу перед каждым тестом
    }

    @Test
    void getBalance_existingWalletEntity_returns200() {
        UUID id = UUID.randomUUID();
        walletRepository.save(new WalletEntity(id, BigDecimal.valueOf(500)));

        ResponseEntity<WalletBalanceResponse> resp = restTemplate.getForEntity("/api/v1/wallets/" + id, WalletBalanceResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().balance()).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    void deposit_updatesBalance() {
        UUID id = UUID.randomUUID();
        walletRepository.save(new WalletEntity(id, BigDecimal.valueOf(0)));

        Map<String, Object> req = new HashMap<>();
        req.put("walletId", id.toString());
        req.put("operationType", "DEPOSIT");
        req.put("amount", 1000);

        ResponseEntity<Void> postResp = restTemplate.postForEntity("/api/v1/wallets", req, Void.class);
        assertThat(postResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<WalletBalanceResponse> getResp = restTemplate.getForEntity("/api/v1/wallets/" + id, WalletBalanceResponse.class);
        assertThat(getResp.getBody().balance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void withdraw_insufficientFunds_returns403() {
        UUID id = UUID.randomUUID();
        walletRepository.save(new WalletEntity(id, BigDecimal.valueOf(100)));

        Map<String, Object> req = new HashMap<>();
        req.put("walletId", id.toString());
        req.put("operationType", "WITHDRAW");
        req.put("amount", 500);

        ResponseEntity<Map> resp = restTemplate.postForEntity("/api/v1/wallets", req, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getBalance_WalletNotFound_returns404() {
        UUID id = UUID.randomUUID();
        UUID falseId = UUID.randomUUID();
        walletRepository.save(new WalletEntity(id, BigDecimal.valueOf(0)));
        ResponseEntity<WalletBalanceResponse> resp = restTemplate.getForEntity("/api/v1/wallets/" + falseId, WalletBalanceResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void concurrentDeposits_shouldNotLoseMoney() throws InterruptedException {
        UUID id = UUID.randomUUID();
        walletRepository.save(new WalletEntity(id, BigDecimal.ZERO));

        int threads = 20;
        int depositsPerThread = 50;
        BigDecimal singleAmount = BigDecimal.valueOf(1);

        ExecutorService es = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        List<Integer> serverErrors = Collections.synchronizedList(new ArrayList<>());

        Runnable worker = () -> {
            try {
                startLatch.await();
                for (int i = 0; i < depositsPerThread; i++) {
                    Map<String, Object> req = new HashMap<>();
                    req.put("walletId", id.toString());
                    req.put("operationType", "DEPOSIT");
                    req.put("amount", 1);
                    ResponseEntity<Void> r = restTemplate.postForEntity("/api/v1/wallets", req, Void.class);
                    if (r.getStatusCode().is5xxServerError()) {
                        serverErrors.add(r.getStatusCodeValue());
                    }
                }
            } catch (Exception e) {
                serverErrors.add(500);
            } finally {
                doneLatch.countDown();
            }
        };
        for (int i = 0; i < threads; i++) es.submit(worker);
        startLatch.countDown();
        doneLatch.await(60, TimeUnit.SECONDS);
        es.shutdown();

        assertThat(serverErrors.isEmpty());

        BigDecimal expected = BigDecimal.valueOf((long) threads * depositsPerThread).multiply(singleAmount);

        ResponseEntity<WalletBalanceResponse> resp = restTemplate.getForEntity("/api/v1/wallets/" + id, WalletBalanceResponse.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().balance()).isEqualByComparingTo(expected);
    }

}
