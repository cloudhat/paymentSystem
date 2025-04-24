package com.paymentsystemex.utils;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("h2-test")
@SpringBootTest
public class JpaH2TestBase {

    @Autowired
    private DatabaseCleanup databaseCleanup;


    @BeforeEach
    public void setUp() {
        databaseCleanup.execute();
    }
}
