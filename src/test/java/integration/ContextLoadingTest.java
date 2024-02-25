package integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.letunov.Main;
import org.letunov.dao.impl.SubjectDaoImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

@SpringJUnitWebConfig(classes = {Main.class})
public class ContextLoadingTest implements ApplicationContextAware
{
    private ApplicationContext context;

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
