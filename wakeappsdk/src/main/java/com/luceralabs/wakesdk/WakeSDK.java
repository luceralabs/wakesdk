package com.luceralabs.wakesdk;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.*;
import org.apache.http.client.HttpClient;

/**
 * Created by gregory on 8/28/17.
 */

public class WakeSDK {
    private static String accessToken;
    private static String refreshToken;

    /**
     * Sets the access token used during API calls
     * @param accessToken The access token (JWT)
     * @throws Exception
     */
    public static void SetToken(String accessToken) throws Exception {
        if(accessToken.length() == 0) {
            throw new Exception("Access token is not valid");
        }
        try {
            DecodedJWT decodedJWT = JWT.decode(accessToken);
            if(decodedJWT.getExpiresAt().before(new Date())) {
                throw new Exception("Token is expired");
            }
        } catch (JWTDecodeException exception) {
            throw new Exception("Provided token was not valid", exception);
        }
        WakeSDK.accessToken = accessToken;
    }

    /**
     * Sets the access token used during API calls as well as the refresh token
     * @param accessToken The access token (JWT)
     * @param refreshToken The refresh token (JWT)
     */
    public static void SetToken(String accessToken, String refreshToken) throws Exception {
        try{
            WakeSDK.SetToken(refreshToken);
        } catch (Exception ex) {
            throw ex;
        }
        if(accessToken.length() == 0) {
            throw new Exception("Access token is not valid");
        }
        WakeSDK.refreshToken = refreshToken;
    }

    Future<HttpResponse<JsonNode>> future = Unirest.post("http://httpbin.org/post")
            .header("accept", "application/json")
            .field("param1", "value1")
            .field("param2", "value2")
            .asJsonAsync(new Callback<JsonNode>() {

                public void failed(UnirestException e) {
                    System.out.println("The request has failed");
                }

                public void completed(HttpResponse<JsonNode> response) {
                    int code = response.getStatus();
                    Map<String, String> headers = response.getHeaders();
                    JsonNode body = response.getBody();
                    InputStream rawBody = response.getRawBody();
                }

                public void cancelled() {
                    System.out.println("The request has been cancelled");
                }

            });
}
