package com.security.authentication.config;
import com.security.authentication.security.jwt.AuthEntryPointJwt;
import com.security.authentication.security.jwt.AuthTokenFilter;
import com.security.authentication.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    AuthEntryPointJwt authEntryPointJwt;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
            httpSecurity.csrf(csrf -> csrf.disable())
                    .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt))
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(
                            auth -> auth
                                    .requestMatchers("/api/auth/**").permitAll()
                                    .requestMatchers("/api/admin/**").hasAnyAuthority("admin")
                                    .requestMatchers("/api/user/**").hasAnyAuthority("admin","user")
                                    .anyRequest()
                                    .authenticated()
                    );
            httpSecurity.authenticationProvider(authenticationProvider());
            httpSecurity.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

            return httpSecurity.build();

    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
//        httpSecurity
//                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt))
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(authorizeRequests ->
//                authorizeRequests
//                        .requestMatchers("/**").permitAll().anyRequest().authenticated()
//
//        ).csrf(AbstractHttpConfigurer::disable)
//                .oauth2Login(httpSecurityOAuth2LoginConfigurer -> httpSecurityOAuth2LoginConfigurer.successHandler((request, response, authentication) -> {
//                    LoginStatusResponse loginStatusResponse = loginUser();
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    String jsonString = objectMapper.writeValueAsString(loginStatusResponse);
//                    String urlEncodedJson = URLEncoder.encode(jsonString, StandardCharsets.UTF_8);
//                    if(loginStatusResponse.getStatusCode() == 200) {
//                        response.sendRedirect("localhost:3000/successhanlder" + urlEncodedJson);
//                    }else{
//                        response.sendRedirect("https://google.com");
//                    }
//                }));
//        return httpSecurity.build();
//    }
//
//    public LoginStatusResponse loginUser(){
//        LoginStatusResponse loginStatusResponse = new LoginStatusResponse();
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
//            String upn =  oauthToken.getPrincipal().getAttribute("upn");
//            if (upn == null) {
//                upn =  oauthToken.getPrincipal().getAttribute("preferred_username");
//            }
//            String email = upn;
//            User userModelResponse = (User) this.userDetailsService.loadUserByUsername(email);
//            if (userModelResponse != null) {
//                String emailToken = userModelResponse.getUsername();
//                List<PermissionDTO> permissionDTOList = roleService.getPermissions(userModelResponse.getRoleId());
//
//                if (!userModelResponse.isStatus()) {
//                    loginStatusResponse.setStatusMessage("User has been disabled");
//                    loginStatusResponse.setSuccess(false);
//                    loginStatusResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
//                    return loginStatusResponse;
//                }
//    }
}
