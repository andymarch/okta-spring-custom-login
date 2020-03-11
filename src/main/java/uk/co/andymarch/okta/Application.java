package uk.co.andymarch.okta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/")
    public ModelAndView displayLandingPage(@AuthenticationPrincipal OidcUser principal) {
        ModelAndView mav = new ModelAndView("landing");
        if(principal != null) {
            mav.addObject("user",(DefaultOidcUser)principal);
        }
        return mav;
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
