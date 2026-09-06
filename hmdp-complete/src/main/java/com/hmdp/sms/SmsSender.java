package com.hmdp.sms;

/**
 * SMS sending abstraction.
 *
 * Default implementation in this project logs the code (for local development).
 * Replace with a real provider (Aliyun/Tencent/Twilio, etc.) in production.
 */
public interface SmsSender {
    void sendLoginCode(String phone, String code);

    /**
     * 秒杀券通知短信：到货提醒（订阅用户在补货后收到）与活动开始提醒共用。
     *
     * @param phone 接收手机号
     * @param voucherTitle 券标题，提醒文案直接拼进短信
     */
    void sendVoucherArrival(String phone, String voucherTitle);
}
