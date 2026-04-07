package com.shixi;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

/**
 * 测试基类
 */
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/db/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BaseTest {

    @BeforeEach
    void setUp() {
        // 子类可以重写此方法
    }
}
