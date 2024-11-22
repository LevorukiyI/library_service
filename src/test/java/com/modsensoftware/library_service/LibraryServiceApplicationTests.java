package com.modsensoftware.library_service;

import com.modsensoftware.library_service.annotations.ContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ContainerTest
class LibraryServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
