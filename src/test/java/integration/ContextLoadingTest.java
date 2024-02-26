package integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.letunov.Main;
import org.letunov.dao.impl.SubjectDaoImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringJUnitWebConfig(classes = {Main.class})
public class ContextLoadingTest implements ApplicationContextAware
{
    private ApplicationContext context;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry)
    {
        registry.add("datasource.jdbcUrl", postgres::getJdbcUrl);
        registry.add("datasource.user", postgres::getUsername);
        registry.add("datasource.password", postgres::getPassword);
    }

    @Test
    void contextStart()
    {
        SubjectDaoImpl subjectDaoImpl = (SubjectDaoImpl) context.getBean("subjectDaoImpl");
        assertNotNull(subjectDaoImpl);
        assertTrue(true);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        context = applicationContext;
    }
}
