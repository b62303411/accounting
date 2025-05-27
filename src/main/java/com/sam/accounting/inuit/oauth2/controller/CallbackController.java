package com.sam.accounting.inuit.oauth2.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.data.UserInfoResponse;
import com.intuit.oauth2.exception.OAuthException;
import com.intuit.oauth2.exception.OpenIdException;
import com.sam.accounting.inuit.oauth2.client.OAuth2PlatformClientFactory;

import jakarta.servlet.http.HttpSession;
import com.auth0.jwt.*;
import com.auth0.jwt.interfaces.DecodedJWT;
/**
 * @author dderose
 *
 */
@Controller
@RequestMapping("/inuit")
public class CallbackController {
    
	@Autowired
	OAuth2PlatformClientFactory factory;

    private static final Logger logger = Logger.getLogger(CallbackController.class.getName());
    public static void decodeAccessToken(String accessToken) {
        // Decode the access token
//    	com.auth0.jwt.JWTSigner
        DecodedJWT jwt = JWT.decode(accessToken);

        // Get the scopes from the "scope" claim
        String scope = jwt.getClaim("scope").asString();
        
        System.out.println("Granted Scopes: " + scope);
    }
    /**
     *  This is the redirect handler you configure in your app on developer.intuit.com
     *  The Authorization code has a short lifetime.
     *  Hence Unless a user action is quick and mandatory, proceed to exchange the Authorization Code for
     *  BearerToken
     *      
     * @param auth_code
     * @param state
     * @param realmId
     * @param session
     * @return
     */
    @RequestMapping("/oauth2redirect")
    public String callBackFromOAuth(@RequestParam("code") String authCode, @RequestParam("state") String state, @RequestParam(value = "realmId", required = false) String realmId, HttpSession session) {   
        logger.info("inside oauth2redirect of sample"  );
        try {
	        String csrfToken = (String) session.getAttribute("csrfToken");
	        if(null==csrfToken) 
	        {
	        	System.err.println();
	        	return "";
	        }
	        if (csrfToken.equals(state)) {
	            session.setAttribute("realmId", realmId);
	            session.setAttribute("auth_code", authCode);
	
	            OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();
	            String redirectUri = factory.getPropertyValue("OAuth2AppRedirectUri");
	         
	            // Check the scopes included in the access token
	           
	            //logger.info("Granted Scopes: " + grantedScopes);
	            logger.info("inside oauth2redirect of sample -- redirectUri " + redirectUri  );
	            BearerTokenResponse bearerTokenResponse = client.retrieveBearerTokens(authCode, redirectUri);
	           // String grantedScopes = bearerTokenResponse.getScope();
	            session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
	            //decodeAccessToken( bearerTokenResponse.getAccessToken());
	            session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());
	    
	            /*
	                Update your Data store here with user's AccessToken and RefreshToken along with the realmId
	    
	                However, in case of OpenIdConnect, when you request OpenIdScopes during authorization,
	                you will also receive IDToken from Intuit.
	                You first need to validate that the IDToken actually came from Intuit.
	             */
	    
	            if (StringUtils.isNotBlank(bearerTokenResponse.getIdToken())) {
	               try {
						if(client.validateIDToken(bearerTokenResponse.getIdToken())) {
						       logger.info("IdToken is Valid");
						       //get user info
						       saveUserInfo(bearerTokenResponse.getAccessToken(), session, client);
						   }
					} catch (OpenIdException e) {
						logger.log(Level.SEVERE,"Exception validating id token ", e);
					   logger.log(Level.SEVERE,"intuit_tid: " + e.getIntuit_tid());
					   logger.log(Level.SEVERE,"More info: " + e.getResponseContent());
					}
	            }
	            
	            return "connected";
	        }
	        logger.info("csrf token mismatch " );
        } catch (OAuthException e) {
        	logger.log(Level.SEVERE,"Exception in callback handler ", e);
		} 
        return null;
    }

    
    private void saveUserInfo(String accessToken, HttpSession session, OAuth2PlatformClient client) {
        //Ideally you would fetch the realmId and the accessToken from the data store based on the user account here.
 
        try {
        	UserInfoResponse response = client.getUserInfo(accessToken); 

        	session.setAttribute("sub", response.getSub());
            session.setAttribute("givenName", response.getGivenName());
            session.setAttribute("email", response.getEmail());
            
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE,"Exception while retrieving user info ", ex);
        }
    }

}
