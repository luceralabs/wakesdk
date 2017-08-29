/**
 * Created by gregory on 8/28/17.
 */

package com.luceralabs.wakesdk;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.*;
import com.mashape.unirest.http.async.*;

import java.util.StringTokenizer;
import java.util.concurrent.*;
import java.io.*;

public class WakeSDK {
    private static String accessToken;
    private static String refreshToken;

    private static final String baseUrl = "https://wakeuserapi.azurewebsites.net";

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

    private static String GetAccessToken() throws Exception {
        try {
            DecodedJWT decodedJWT = JWT.decode(accessToken);
            if (decodedJWT.getExpiresAt().before(new Date())) {
                /* TODO: Refresh token */
                throw new Exception("Token is expired");
            }
            return accessToken;
        } catch (JWTDecodeException exception) {
            throw new Exception("Provided token was not valid", exception);
        }
    }

    /**
     * Sends a GET request to the Wakē user API
     * @param endpoint to be called
     * @return Future HttpResponse containing JSON data
     */
    private static Future<HttpResponse<JsonNode>> Get(String endpoint) throws Exception {
        return Unirest.post(baseUrl + "/" + endpoint)
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + WakeSDK.GetAccessToken())
                .asJsonAsync(new Callback<JsonNode>() {

                    public void failed(UnirestException e) {
                        System.out.println("The request has failed");
                    }

                    public void completed(HttpResponse<JsonNode> response) {
                        int code = response.getStatus();
                        JsonNode body = response.getBody();
                        InputStream rawBody = response.getRawBody();
                    }

                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }

                });
    }

    /**
     * Sends a POST request to the Wakē user API
     * @param endpoint to be called
     * @param data to be sent
     * @return Future HttpResponse containing JSON data
     */
    private static Future<HttpResponse<JsonNode>> Post(String endpoint, JsonNode data) throws Exception {
        return Unirest.post(baseUrl + "/" + endpoint)
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + WakeSDK.GetAccessToken())
                .body(data)
                .asJsonAsync(new Callback<JsonNode>() {

                    public void failed(UnirestException e) {
                        System.out.println("The request has failed");
                    }

                    public void completed(HttpResponse<JsonNode> response) {
                        int code = response.getStatus();
                        JsonNode body = response.getBody();
                        InputStream rawBody = response.getRawBody();
                    }

                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }

                });
    }
}
