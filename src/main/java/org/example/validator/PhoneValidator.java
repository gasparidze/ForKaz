package org.example.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhoneValidator {
    private static final String PHONE_PATTERN = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
    private static final PhoneValidator INSTANCE = new PhoneValidator();

    public static PhoneValidator getInstance() {
        return INSTANCE;
    }
    public boolean isValid(String phone){
        return phone.matches(PHONE_PATTERN);
    }
}
