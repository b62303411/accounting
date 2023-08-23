package com.example.springboot.accounting.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

//BitwardenPasswordRetriever
public class BitwardenPasswordRetriever {
	public static void main(String[] args) {
		String clientId = "user.6670860b-8c9e-4e62-90f0-92a89eb6a043";
		String clientSecret = "Mb9ArxRttHINEY1VWQq0qftrrbbTd7";
		String accessToken = getAccessToken(clientId, clientSecret);
		System.setProperty("javax.net.ssl.trustStore", "path/to/your/truststore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "your-truststore-password");

		if (accessToken != null) {
			String itemsJson = getItems(accessToken);

			if (itemsJson != null) {
				JSONArray items = new JSONArray(itemsJson);

				for (int i = 0; i < items.length(); i++) {
					JSONObject item = items.getJSONObject(i);
					String password = item.getString("loginPassword"); // Adjust field name as needed

					System.out.println("Password: " + password);
				}
			}
		}
	}


    private static String getAccessToken(String clientId, String clientSecret) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("https://bitwarden.yoda.vm/connect/token");
            JSONObject requestBody = new JSONObject();
            requestBody.put("grant_type", "client_credentials");
            requestBody.put("client_id", clientId);
            requestBody.put("client_secret", clientSecret);
            requestBody.put("scope", "api");
            
            StringEntity entity = new StringEntity(requestBody.toString());
            request.setEntity(entity);
            request.setHeader("Content-Type", "application/json");
            
            CloseableHttpResponse response = httpClient.execute(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject responseJson = new JSONObject(responseBody);
            
            if (responseJson.has("access_token")) {
                return responseJson.getString("access_token");
            } else {
                System.err.println("Failed to retrieve access token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
	private static String getItems(String accessToken) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet request = new HttpGet("https://api.bitwarden.com/api/v2/items");
			request.addHeader("Authorization", "Bearer " + accessToken);

			CloseableHttpResponse response = httpClient.execute(request);
			String responseBody = EntityUtils.toString(response.getEntity());

			return responseBody;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
