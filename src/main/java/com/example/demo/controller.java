package com.example.demo;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class controller {

    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestParam("limit") int limit) {
        StringBuilder url = new StringBuilder("https://jsonmock.hackerrank.com/api/article_users?page=");

        var restTemplate = new RestTemplate();

        var parameterizedTypeReference = new ParameterizedTypeReference<Response<User>>(){};
        List<User> responseList = new ArrayList<>();
        var response = restTemplate.exchange(url.toString()+1, HttpMethod.GET, null, parameterizedTypeReference).getBody();


        if (response != null) {
            responseList.addAll(response.getData());
            if (response.getTotal_pages() > 1) {
                for (int i = response.getPage() + 1; i <= response.getTotal_pages(); i++) {
                    url.append(i);
                    var extra = restTemplate.exchange(url.toString() + i, HttpMethod.GET, null, parameterizedTypeReference).getBody();
                    if (extra != null) {
                        responseList.addAll(extra.getData());
                    }
                }
            }
        }

        var finalResult = responseList.stream().filter(user -> user.getSubmission_count() > limit).map(User::getUsername).collect(Collectors.toList());

        return new ResponseEntity<>(finalResult, HttpStatus.OK);

    }
}
