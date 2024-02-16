package com.security.common.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {

    @CachePut(cacheNames = {"otp"}, key = "#key")
    public int generateOTP(String key){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        System.out.println(otp);
        return otp;
    }

    @Cacheable(cacheNames = {"otp"}, key = "#key")
    public int getOtp(String key) {
        return 0;
    }

    @CacheEvict(cacheNames = {"otp"}, key = "#key")
    public void clearOTP(String key) {
    }
}
