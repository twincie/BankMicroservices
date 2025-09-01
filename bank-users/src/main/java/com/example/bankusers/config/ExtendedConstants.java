package com.example.bankusers.config;


import lombok.Getter;

public class ExtendedConstants {

    @Getter
    public enum ResponseCode {

        SUCCESS("00", "Success"),
        FAILED("01", "Failed"),
        NO_PERMISSION("02", "Not authorized"),
        NOT_FOUND("03", "Data Not Found"),
        WEAK_PASSWORD("04", "Weak password"),
        EMAIL_EXISTS("05", "Email already exists"),
        USERNAME_EXISTS("06", "Username already exists"),
        WRONG_PASSWORD("07", "Wrong password"),
        USERNAME_NOT_FOUND("08", "Username not found"),
        INVALID_TOKEN("09", "Invalid token");


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
