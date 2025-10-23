package dev.wework.pet.user.signup.configure.generate;

import dev.wework.pet.user.signup.configure.validation.Validation;
import dev.wework.pet.user.signup.exception.ValidationFaliureSSN;

public class Convention {

    public static String ConvertSSN(String SSN) {

        String front = SSN.substring(0, 6);
        String back = SSN.substring(6);
        String Backssn = back+"******";

        if(!Validation.isValidFrontSSN(front)){
            throw new ValidationFaliureSSN();
        };

        return front + "-" + Backssn;
    }
}
