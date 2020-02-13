package uk.co.andymarch.okta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/")
    public String landing(Principal principal) {
        return "Hey there!";
    }

    @GetMapping("/protected")
    public String email(Principal principal) {
        return "Hey there! Your email address is: " + principal.getName();
        }

    @RequestMapping("/customLogin")
    public String customLogin(Principal principal) {
        return "customLogin.html";
    }

    @Configuration
    static class OktaOAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        ClientRegistrationRepository clientRegistrationRepository;


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    //grant access to our custom login page
                    .antMatchers("/","/customLogin").permitAll()
                    .anyRequest().authenticated()
                        .and().oauth2Client()
                        .and().oauth2Login()
                            //tell OAuth where to send unauth'd users
                            .loginPage("/customLogin")
                            .authorizationEndpoint()
                            //attach a custom requestResolver to attach session tokens
                            .authorizationRequestResolver(
                                    new OktaSessionTokenAuthorizationRequestResolver(
                                            clientRegistrationRepository, "/login/oauth2/"));
        }
    }
}
