package com.gopyyn.salad.rest;

import com.gopyyn.salad.core.SaladCommands;
import kong.unirest.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private int status;
    private String statusText;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    private boolean success;

    public Response(HttpResponse<String> httpResponse) {
        setStatus(httpResponse.getStatus());
        setStatusText(httpResponse.getStatusText());
        setBody(httpResponse.getBody());
        setSuccess(httpResponse.isSuccess());
        httpResponse.getHeaders().all().forEach(h-> headers.put(h.getName(), h.getValue()));
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return '{' +
                "\"status\":" + status +
                ", \"statusText\":\"" + statusText + '\"' +
                ", \"headers\":" + SaladCommands.convertToJsonString(headers) +
                ", \"body\":" + body +
                ", \"success\":" + success +
                '}';
    }
}
