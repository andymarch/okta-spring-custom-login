package uk.co.andymarch.okta;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Customize the request to the Okta's authorize endpoint to include the sessionToken query parameter if one is present
 * in the flash variables. This value will be returned as a result of a successful AuthN request.
 */
public class OktaSessionTokenAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private OAuth2AuthorizationRequestResolver defaultResolver;

    public OktaSessionTokenAuthorizationRequestResolver(
            ClientRegistrationRepository repo, String authorizationRequestBaseUri) {
        defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, authorizationRequestBaseUri);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        if(req != null) {
            req = customizeAuthorizationRequest(req,request);
        }
        return req;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
        if(req != null) {
            req = customizeAuthorizationRequest(req,request);
        }
        return req;
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(
            OAuth2AuthorizationRequest req,HttpServletRequest request) {

        Map<String, Object> extraParams = new HashMap<>();
        extraParams.putAll(req.getAdditionalParameters());

        FlashMap flashMap = new SessionFlashMapManager().retrieveAndUpdate(request, null);
        if (flashMap != null) {
            String token = (String) flashMap.get("sessionToken");
            if (token != null && !token.isEmpty()) {
                extraParams.put("sessionToken", token);
            }
        }

        return OAuth2AuthorizationRequest
                .from(req)
                .additionalParameters(extraParams)
                .build();
    }
}
