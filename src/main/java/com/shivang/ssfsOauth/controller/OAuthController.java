package com.shivang.ssfsOauth.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.shivang.ssfsOauth.security.TokenEncryptor;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class OAuthController {

	@Value("${github.client.id}")
	private String clientId;

	@Value("${github.client.secret}")
	private String clientSecret;

	@Value("${github.redirect.uri}")
	private String redirectUri;
	
	@Value("${githuburl}")
	private String githubUrl;
	
	@Value("${user.api}")
	private String userApi;
	
	private static final String KEY_FILE = System.getProperty("user.home") + "/.sfss_key";
	private static final String TOKEN_FILE = System.getProperty("user.home") + "/.sfss_token";

	@GetMapping("/login")
	public void login(HttpServletResponse response) throws IOException {
		String githubOAuthUrl = githubUrl+"authorize" + "?client_id=" + clientId + "&redirect_uri="
				+ redirectUri + "&scope=read:user";
		response.sendRedirect(githubOAuthUrl);
	}

	@GetMapping("login/oauth2/code/github")
	public String callback(@RequestParam String code) throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);
		body.add("code", code);
		body.add("redirect_uri", redirectUri);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(githubUrl+"access_token",
				request, Map.class);

		String accessToken = (String) response.getBody().get("access_token");

		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.setBearerAuth(accessToken);
		HttpEntity<String> authRequest = new HttpEntity<>(authHeaders);

		ResponseEntity<Map> userResponse = restTemplate.exchange(userApi, HttpMethod.GET,
				authRequest, Map.class);

		Map userDetails = userResponse.getBody();
		String username = (String) userDetails.get("login");
		
		//saveTokenLocally(username, accessToken);
		TokenEncryptor.encryptToFile(accessToken, TOKEN_FILE);
		return "Authentication successful for user: " + username;
	}

	/*
	 * private void saveTokenLocally(String username, String token) throws
	 * IOException { Path path = Paths.get(System.getProperty("user.home"),
	 * ".sfss_token"); Files.writeString(path, username + ":" + token,
	 * StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING); }
	 */
	
	
}
