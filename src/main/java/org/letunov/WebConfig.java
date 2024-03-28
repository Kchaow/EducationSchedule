package org.letunov;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.letunov.dao.GroupDao;
import org.letunov.dao.ScheduleTemplateDao;
import org.letunov.dao.UserDao;
import org.letunov.util.ClassDtoToClassConverter;
import org.letunov.util.ClassToClassDtoConverter;
import org.letunov.util.CustomPropertyEditorRegistrar;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;


//Задачи для рефакторинга:
//1. Добавить необходимые Converter'ы и Formatter'ы
//2. Сделать нормальную валидацию через JSR
//3. Дать полям более подходящие имена
//4. Пагинацию для потенциально больших списков
//5. Отправка логина и пароля на почту при создании пользователя

@Configuration
@EnableWebMvc
@ComponentScan
@PropertySource("classpath:schedule.properties")
@Slf4j
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware
{
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ScheduleTemplateDao scheduleTemplateDao;

    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry)
    {
        registry.viewResolver(viewResolver());
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver()
    {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine()
    {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.addDialect(new SpringSecurityDialect());
        return templateEngine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver()
    {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/static/**")
                .addResourceLocations("/static/");
    }

    @Bean
    public CustomEditorConfigurer customEditorConfigurer()
    {
        CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();
        customEditorConfigurer.setPropertyEditorRegistrars(
                new CustomPropertyEditorRegistrar[] { customPropertyEditorRegistrar() }
        );
        return customEditorConfigurer;
    }

    @Bean
    public CustomPropertyEditorRegistrar customPropertyEditorRegistrar()
    {
        return new CustomPropertyEditorRegistrar();
    }

    @Override
    public void addFormatters(FormatterRegistry registry)
    {
        registry.addConverter(new ClassToClassDtoConverter());
        registry.addConverter(new ClassDtoToClassConverter());
    }
}
