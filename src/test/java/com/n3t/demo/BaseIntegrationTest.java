package com.n3t.demo;

import com.n3t.dispatcher.DemoApplication;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = DemoApplication.class)
@AutoConfigureEmbeddedDatabase(provider = DatabaseProvider.DOCKER)
@ActiveProfiles("test")
class BaseIntegrationTest {
}
