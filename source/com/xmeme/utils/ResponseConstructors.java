package com.xmeme.utils;

import com.xmeme.api.processor.XMemeException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ResponseConstructors {
    private final int httpCode;
    private final JSONObject clientResponseJSON;
    private final JSONArray clientResponseAsArray;

    private ResponseConstructors(ResponseBuilder builder) {
        this.httpCode = builder.httpCode;
        this.clientResponseJSON = builder.clientResponseJSON;
        this.clientResponseAsArray = builder.clientResponseAsArray;
    }

    public Response getResponse() {

        Response.ResponseBuilder responseBuilder =
                Response.ok(IOCommonUtil.isValidJSON(clientResponseJSON) ?
                                clientResponseJSON.toString()
                                : clientResponseAsArray.toString(),
                        MediaType.APPLICATION_JSON)
                        .status(this.httpCode);
        return responseBuilder.build();
    }

    public static Response getExceptionBaseResponse(final APIErrorMessage.ERRORCODE errorcode) {
        ResponseConstructors responseObject = new ResponseConstructors.ResponseBuilder()
                .setResponseCode(errorcode.getResponseCode())
                .setCustomMessage(errorcode.getResponseMessage())
                .build();
        return responseObject.getResponse();
    }

    public static Response getExceptionBaseResponse(final XMemeException customThriveException) {
        APIErrorMessage.ERRORCODE errorcode = customThriveException.getErrorCode();
        return getExceptionBaseResponse(errorcode);
    }

    public static Response updateSuccess() {
        return Response.noContent().build();
    }

    public static Response notFound() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public static class ResponseBuilder {
        private int httpCode = 200;
        private String clientResponseMessage = null;
        private JSONObject clientResponseJSON;
        private JSONArray clientResponseAsArray;


        public ResponseBuilder setClientJSON(JSONArray clientJSON) {
            this.clientResponseAsArray = clientJSON;
            return this;
        }


        public ResponseBuilder setClientJSON(JSONObject clientJSON) {
            this.clientResponseJSON = clientJSON;
            return this;
        }

        public ResponseBuilder setCustomMessage(String messageToClient) {
            this.clientResponseMessage = messageToClient;
            return this;
        }

        public ResponseBuilder setResponseCode(int responseCOde) {
            this.httpCode = responseCOde;
            return this;
        }

        public ResponseConstructors build() {
            this.clientResponseJSON = IOCommonUtil.addJSONKeyValue(this.clientResponseJSON, Constants.MESSAGE, this.clientResponseMessage);
            return new ResponseConstructors(this);
        }
    }

}
