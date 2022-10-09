package br.com.alura.forum.config.security;import br.com.alura.forum.repository.UsuarioRepository;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;import org.springframework.http.HttpMethod;import org.springframework.security.authentication.AuthenticationManager;import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;import org.springframework.security.config.annotation.web.builders.HttpSecurity;import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;import org.springframework.security.config.http.SessionCreationPolicy;import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.security.web.SecurityFilterChain;import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;@EnableWebSecurity@Configurationpublic class SecurityConfigurations {    @Autowired    private AutenticacaoService autenticacaoService;    @Autowired    private TokenAPIService tokenAPIService;    @Autowired    private UsuarioRepository usuarioRepository;    /**     * Authentication configurations anotado com @Bean para ser injetado no controller de autenticação,     * pois o Spring Boot não deixa injetar diretamente.     *     * @param authenticationConfiguration     * @return     * @throws Exception     */    @Bean    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {        return authenticationConfiguration.getAuthenticationManager();    }    /**     * Autentica o usuário.<br><br>     * Equivalente ao método configure(AuthenticationManagerBuilder auth).     *     * @param auth     */    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {        // qual a service tem a lógica de autenticação junto com o algoritmo que faz o encoding da senha        auth.userDetailsService(autenticacaoService).passwordEncoder(passwordEncoder());    }    @Bean    public PasswordEncoder passwordEncoder() {        return new BCryptPasswordEncoder();    }    /**     * Authorization configuration.<br><br>     * the resource /* means, for example, /{id}     *     * @param http     * @return     * @throws Exception     */    @Bean    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {        http            .authorizeRequests()            .antMatchers(HttpMethod.POST, "/auth").permitAll()            .antMatchers(HttpMethod.GET, "/topicos").permitAll()            .antMatchers(HttpMethod.GET, "/topicos/*").permitAll()            .antMatchers(HttpMethod.GET, "/h2-console").permitAll()            .antMatchers(HttpMethod.GET, "/h2-console/**").permitAll()            //.antMatchers(HttpMethod.GET, "/actuator/**").permitAll() // remover em produção                .antMatchers("/swagger-ui.html").permitAll()            .anyRequest().authenticated()            // informa que não vamos mais fazer autenticação via sessão e sim via token/stateless            .and().csrf().disable()            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)            // executar o AutenticacaoViaTokeFilter antes de autenticar o usuário            .and().addFilterBefore(new AutenticacaoViaTokenFilter(tokenAPIService, usuarioRepository), UsernamePasswordAuthenticationFilter.class);        return http.build();    }    /**     * Static configurations.     *     * @return     */    @Bean    public WebSecurityCustomizer webSecurityCustomizer() {        return (web) -> web.ignoring().antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**", "/swagger-resources/**");    }//    public static void main(String[] args) {//        System.out.println(new BCryptPasswordEncoder().encode("123456"));//    }}