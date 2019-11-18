package com.salad.rest;

import com.salad.core.SaladCommands;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.HashMap;
import java.util.Map;

import static com.salad.core.SaladCommands.parseString;
import static com.salad.core.SaladCommands.readDataFromFile;
import static java.util.Optional.ofNullable;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.codec.digest.DigestUtils.md5;

public class RestApi {

    private static final Map<String, String> headers = new HashMap<>(15);

    private RestApi() {
    }

    public static Response post(String url, String body) {
        url = parseString(url);
        String parsedBody = parseString(readDataFromFile(body));
        ofNullable(body).ifPresent(dlr -> headers.put("Content-MD5", getMd5(parsedBody)));

        HttpRequestWithBody post = Unirest.post(url);

        HttpResponse<String> httpResponse = post.headers(headers).body(parsedBody).asString();
        Response response = new Response(httpResponse);
        SaladCommands.set("response", response.toString());

        return response;
    }

    public static Response put(String url, String body) {
        url = parseString(url);
        String parsedBody = parseString(readDataFromFile(body));
        ofNullable(body).ifPresent(dlr -> headers.put("Content-MD5", getMd5(parsedBody)));

        HttpRequestWithBody put = Unirest.put(url);

        HttpResponse<String> httpResponse = put.headers(headers).body(parsedBody).asString();
        Response response = new Response(httpResponse);
        SaladCommands.set("response", response.toString());

        return response;
    }

    public static Response get(String url) {
        url = parseString(url);

        GetRequest get = Unirest.get(url);

        HttpResponse<String> httpResponse = get.headers(headers).asString();
        Response response = new Response(httpResponse);
        SaladCommands.set("response", response.toString());

        return response;
    }

    public static String addHeader(String name, String value) {
        return headers.put(name, value);
    }

    public static String getMd5(String body) {
        return encodeBase64String(md5(body));
    }
}
