package org.prog.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

public class RestExecute {

    public ResultDto getUsers (int count) {
        RequestSpecification requestSpecification = RestAssured.given();
        String apiBaseUrl = System.getenv("Api_Base_Url");
        String apiBasePath = System.getenv("Api_Base_Path");
        requestSpecification.baseUri(apiBaseUrl);
        requestSpecification.basePath(apiBasePath);
        requestSpecification.header("Content-Type", "application/json; charset=UTF-8");
        requestSpecification.queryParam("nat", "fi");
        requestSpecification.queryParam("results", count);

        Response response = requestSpecification.get();
        Assert.assertEquals(response.getStatusCode(), 200, "API request failed!");

        ResultDto dto = response.as(ResultDto.class);
        Assert.assertNotNull(dto.getResults(), "Results list in DTO is null");
        return dto;
    }
}
