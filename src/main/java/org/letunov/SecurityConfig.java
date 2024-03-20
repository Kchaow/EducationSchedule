package org.letunov;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
//@EnableMethodSecurity
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
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasAuthority("admin")
                        .requestMatchers(new AntPathRequestMatcher("/schedule/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll())
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutUrl("/exit")
                        .permitAll()
                        .clearAuthentication(true))
                .formLogin((formLogin) -> formLogin
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/schedule")
                        .permitAll())
                .csrf(AbstractHttpConfigurer::disable); //Исправить!
//                        .failureUrl("")
        return http.build();
    }

    @Bean
    AuthenticationManagerBuilder authenticationManagerBuilder(ObjectPostProcessor<Object>
                                                                      objectPostProcessor, DataSource dataSource) throws Exception {
        final String usersByUsernameQuery = "SELECT login as username, password, 'true' as enabled FROM \"user\" WHERE login = ?";
        final String authoritiesByUsernameQuery = "SELECT login as username, name as authority  FROM \"user\" u " +
                "JOIN \"role\" r ON u.role_id = r.id WHERE login = ?";
        AuthenticationManagerBuilder authenticationManagerBuilder = new AuthenticationManagerBuilder(objectPostProcessor);
        authenticationManagerBuilder.jdbcAuthentication().dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
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
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, 10);
    }
}
