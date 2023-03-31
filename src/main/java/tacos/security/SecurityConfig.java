package tacos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import tacos.security.user.repository.UserRepository;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableMethodSecurity
@EnableGlobalAuthentication
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        matcher -> matcher
                                .requestMatchers(antMatcher("/design"), antMatcher("/orders/**"))
                                .access(new WebExpressionAuthorizationManager(
                                        "hasRole('USER') or hasAuthority('OAUTH2_USER')"))
                                .requestMatchers(
                                        antMatcher("/login"),
                                        antMatcher("/**"),
                                        toH2Console()
                                )
                                .access(new WebExpressionAuthorizationManager("permitAll"))
                                .anyRequest().authenticated()
                )
                .headers(
                        headers -> headers.frameOptions().disable()
                )
                .csrf().ignoringRequestMatchers(antMatcher("/h2-console/**"), antMatcher("/data-api/**")).and()
                .oauth2Login(
                        oath2 -> oath2
                                .loginPage("/login")
                                .defaultSuccessUrl("/design", true)
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .defaultSuccessUrl("/design", true)
                )
                .logout(
                        logout -> logout.logoutSuccessUrl("/")
                )

                .build();
    }
}
