package com.example.bankwallet.config;


import lombok.Getter;

public class ExtendedConstants {

    @Getter
    public enum ResponseCode {

        SUCCESS("00", "Success"),
        FAILED("01", "Failed"),
        NO_PERMISSION("02", "Not authorized"),
        NOT_FOUND("03", "Data Not Found");


        private final String code;
        private final String description;

        ResponseCode(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
