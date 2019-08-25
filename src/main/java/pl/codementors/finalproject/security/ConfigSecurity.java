package pl.codementors.finalproject.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.codementors.finalproject.model.UserRole;

@EnableWebSecurity
public class ConfigSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**","/product","/product/", "/user/add","/product/listByNameContains*","/product/list").permitAll()
                .antMatchers("/user/**","/cart/**","/product/**").hasAnyRole(UserRole.USER.name())
                .antMatchers("/admin/**").hasRole(UserRole.ADMIN.name())
                .and().csrf().disable()
                .httpBasic().and()
                .logout()
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .deleteCookies("JSESSIONID")
                    .permitAll().and()
                .formLogin()
                    .loginPage("/login")
//                    .failureForwardUrl("/login")
                    .defaultSuccessUrl("/", false);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, final LocalUserDetailsService localUserDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(localUserDetailsService);
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        auth.authenticationProvider(authProvider);


    }



}
