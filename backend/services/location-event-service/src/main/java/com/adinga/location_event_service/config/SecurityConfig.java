@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health", "/actuator/info",
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**", "/v3/api-docs/swagger-config",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().permitAll()   // 필요시 authenticated()로 바꾸세요
                );
        return http.build();
    }
}