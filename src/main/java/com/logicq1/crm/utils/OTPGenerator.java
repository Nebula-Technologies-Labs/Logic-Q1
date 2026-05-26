package com.logicq1.crm.utils;

import java.util.Random;

public class OTPGenerator {
    public static Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
