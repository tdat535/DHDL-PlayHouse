package com.viendong.webbanhang;

import com.viendong.webbanhang.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService; // Trả về instance đã được inject
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:3000");  // Allow frontend origin
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/", "/oauth/**", "/authentication/**", "/error", "/cart", "/cart/**", "/order/**")
                        .permitAll()
                        .requestMatchers("/authentication/recover")  // Cho phép truy cập mà không cần xác thực
                        .permitAll()
                        .requestMatchers("/products/add", "/categories/add", "/brand/add", "/dashboard")
                        .hasAnyAuthority("ADMIN")
                        .requestMatchers("/api/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/authentication/login")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/authentication/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            // Lấy vai trò của người dùng từ Authentication object
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {
                                response.sendRedirect("/dashboard"); // Chuyển hướng đến trang dashboard nếu là admin
                            } else {
                                response.sendRedirect("/homepage"); // Chuyển hướng đến trang homepage nếu là user
                            }
                        })
                        .failureUrl("/authentication/login?error")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("vanlang")
                        .rememberMeCookieName("vanlang")
                        .tokenValiditySeconds(24 * 60 * 60)
                        .userDetailsService(userDetailsService())
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("text/html; charset=UTF-8");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("""
                                <script>
                                    alert("Bạn không có quyền truy cập vào tài nguyên này!");
                                    window.history.back();
                                </script>
                            """);
                        })
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1)
                        .expiredUrl("/authentication/login")
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName("vanlang")
                )
                .build();
    }
}
