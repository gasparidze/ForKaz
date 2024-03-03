package org.example.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.example.application_service.exception.ValidationException;
import org.example.validator.PhoneValidator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtil {
    private static PhoneValidator phoneValidator = PhoneValidator.getInstance();
    private static EmailValidator emailValidator = EmailValidator.getInstance();
    public static boolean checkPhone(String phone){
        if(phone == null) return true;

        if(!phoneValidator.isValid(phone)){
            throw new ValidationException("Невалидный номер телефона");
        }

        return true;
    }

    public static boolean checkEmail(String email){
        if(email == null) return true;

        if(!emailValidator.isValid(email)){
            throw new ValidationException("Невалидный email");
        }

        return true;
    }
}
