package org.letunov;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig
{
    private DataSource dataSource;
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    //https://codingtim.github.io/spring-security-6-1-2-requestmatchers/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http.authorizeHttpRequests((authorize) ->
                authorize
//                        .requestMatchers("admin/**").hasAuthority("admin")
//                        .requestMatchers("/").authenticated()
//                        .requestMatchers("schedule/**").permitAll()
                        .requestMatchers(new AntPathRequestMatcher("admin/**")).hasAuthority("admin")
//                        .requestMatchers(new AntPathRequestMatcher("/")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/schedule/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll()
        ).formLogin((formLogin) ->
                formLogin
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginPage("/login")
//                        .loginProcessingUrl("login")
                        .permitAll());
//                        .failureUrl("")
//                        .loginProcessingUrl(""));
        return http.build();
    }

//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        String usersByUsernameQuery = "SELECT login, password, 'true' FROM \"user\" WHERE login = ?";
//        String authoritiesByUsernameQuery = "SELECT login, name FROM \"user\" u " +
//                                        "JOIN \"role\" r ON u.role_id = r.id WHERE login = ?";
//        auth.jdbcAuthentication().dataSource(dataSource)
//                .usersByUsernameQuery(usersByUsernameQuery)
//                .authoritiesByUsernameQuery(authoritiesByUsernameQuery);
//    }

    @Bean
    AuthenticationManagerBuilder authenticationManagerBuilder(ObjectPostProcessor<Object>
                                                                      objectPostProcessor, DataSource dataSource) throws Exception {
        final String usersByUsernameQuery = "SELECT login, password, 'true' FROM \"user\" WHERE login = ?";
        final String authoritiesByUsernameQuery = "SELECT login, name FROM \"user\" u " +
                "JOIN \"role\" r ON u.role_id = r.id WHERE login = ?";
        AuthenticationManagerBuilder authenticationManagerBuilder = new AuthenticationManagerBuilder(objectPostProcessor);
        authenticationManagerBuilder.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery(usersByUsernameQuery)
                .authoritiesByUsernameQuery(authoritiesByUsernameQuery);
        return authenticationManagerBuilder;
    }

    @Bean
    UserDetailsManager users(DataSource dataSource)
    {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
