
package org.novalabs.event.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.AuthorizedUrl;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Spring Boot security configuration file.
 */
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * Paths without security (open to any requestor)
     */
    private static final String[] PUBLIC_PATHS = new String[]{"/", "/**", "/health", "/actuator/**", "/beans", "/liquibase"};

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                    .antMatchers(new String[]{"/admin/**"}).hasAnyRole(new String[]{"ADMIN"})
                    .antMatchers(PUBLIC_PATHS).permitAll()
                    .anyRequest().permitAll()
                .and()
                    .formLogin().loginPage("/login").permitAll()
                .and()
                    .logout().permitAll()
                .and()
                    .exceptionHandling().accessDeniedHandler(this.accessDeniedHandler)
        ;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("password").roles(new String[]{"USER"})
                .and()
                .withUser("admin").password("password").roles(new String[]{"ADMIN"});
    }

}
