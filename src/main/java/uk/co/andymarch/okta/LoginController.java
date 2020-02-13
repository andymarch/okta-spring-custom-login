package uk.co.andymarch.okta;

import com.okta.authn.sdk.AuthenticationException;
import com.okta.authn.sdk.AuthenticationStateHandlerAdapter;
import com.okta.authn.sdk.client.AuthenticationClient;
import com.okta.authn.sdk.client.AuthenticationClients;
import com.okta.authn.sdk.resource.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.concurrent.CountDownLatch;

@Controller
public class LoginController {

    @Autowired
    private org.springframework.core.env.Environment env;

    @GetMapping("/customLogin")
    public String displayLoginPage(Model model) {
        model.addAttribute("credentials", new CredentialsModel());
        return "customLogin";
    }

    @PostMapping("/customLogin")
    public RedirectView performOktaLogin(@ModelAttribute CredentialsModel creds, RedirectAttributes attributes) {
        AuthenticationClient client = AuthenticationClients.builder()
                .setOrgUrl(env.getProperty("okta.domain"))
                .build();
        try {
            CountDownLatch loginLatch = new CountDownLatch(1);
            LatchedAuthenticationStateHandler handler = new LatchedAuthenticationStateHandler(loginLatch);
            client.authenticate(creds.getUser(), creds.getPword(), "", handler);
            loginLatch.await ();
            if(handler.authenticated) {
                attributes.addFlashAttribute("sessionToken", handler.sessionToken);
                return new RedirectView("/login/oauth2/okta");
            } else{
                return new RedirectView("/customLogin?error=Authentication Failed");
            }
        } catch (AuthenticationException | InterruptedException e) {
            e.printStackTrace();
            return new RedirectView("/customLogin?error=Authentication Failed");
        }
    }

    public class LatchedAuthenticationStateHandler extends AuthenticationStateHandlerAdapter {

        private CountDownLatch loginLatch;
        private boolean authenticated = false;
        private String sessionToken;

        public LatchedAuthenticationStateHandler(CountDownLatch loginLatch) {
            this.loginLatch = loginLatch;
        }

        @Override
        public void handleUnknown(AuthenticationResponse unknownResponse) {
            loginLatch.countDown ();
        }

        @Override
        public void handleSuccess(AuthenticationResponse successResponse) {
            loginLatch.countDown ();
            sessionToken = successResponse.getSessionToken();
            authenticated = true;
        }

        @Override
        public void handlePasswordExpired(AuthenticationResponse passwordExpired) {
            //TODO redirect to "/login/change-password"
            loginLatch.countDown ();
        }

        //TODO handle other states
    }
}