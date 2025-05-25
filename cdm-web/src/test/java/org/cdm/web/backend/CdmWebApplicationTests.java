package org.cdm.web.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ContextConfiguration(classes = CdmWebApplication.class)
@ImportResource({"application.properties"})

class CdmWebApplicationTests {

    @Test
    void contextLoads() {
    }

}
