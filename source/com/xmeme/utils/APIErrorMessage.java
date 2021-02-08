package com.xmeme.utils;

public class APIErrorMessage {

    public static final String MISSING_KEY = "This Missing Key is {0}";
    public static final String IMPROPER_KEY = "This Improper Key sent from client is {0}";
    public enum ERRORCODE {
        URL_NOT_PRESENT(404, "URL is Mandatory"),
        IMPROPER_URL(404, "Improper Value for the URL"),
        CAPTION_NOT_PRESENT(404, "Caption is Mandatory"),
        IMPROPER_CAPTION(404, "Improper Value for the Caption"),
        MEME_ALREADY_EXIST(409, "Meme already exist; Kindly upload different Meme"),
        INTERNAL_ERROR(500, "Internal Error. will be back in few mins"),
        ;
        private final String responseMessage;
        private final int responseCode;


        ERRORCODE(int errorCode, String errorMessage) {
            this.responseMessage = errorMessage;
            this.responseCode = errorCode;
        }

        public int getResponseCode(){
            return this.responseCode;
        }

        public String getResponseMessage(){
            return this.responseMessage;
        }
    }
}
