package com.nckueat.foodsmap.component.emailValidation;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import com.nckueat.foodsmap.exception.EmailValidateTooManyRetry;
import com.nckueat.foodsmap.exception.TooFrequentResends;
import com.nckueat.foodsmap.exception.WrongValidateCode;
import com.nckueat.foodsmap.properties.MailProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class ValidateData {
    private final String email;
    private final String code;
    private final String identifyCode;
    private final long time;
    private final Timer timer;
    @Builder.Default
    private int countDown = 8;
}

@Component
@EnableConfigurationProperties(MailProperties.class)
public class EmailValidation {
    private final HashMap<String, ValidateData> emailMap;
    private final String from;
    private final boolean enable;

    @Autowired
    private JavaMailSender javaMailSender;

    public EmailValidation(MailProperties mailProperties) {
        this.emailMap = new HashMap<>();
        this.from = mailProperties.getFrom();
        this.enable = mailProperties.isEnabled();
    }

    private void removeRecord(String email) {
        ValidateData validateData = emailMap.get(email);
        if (validateData != null) {
            validateData.getTimer().cancel();
            emailMap.remove(email);
        }
    }

    private void createRecord(@NonNull ValidateData validateData) {
        final String email = validateData.getEmail();
        final Timer timer = validateData.getTimer();
        ValidateData oldData = emailMap.get(email);
        if (oldData != null) {
            this.removeRecord(email);
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (emailMap.containsKey(email)) {
                    emailMap.remove(email);
                }
                timer.cancel();
            }
        };
        timer.schedule(task, 15 * 60 * 1000);

        emailMap.put(email, validateData);
    }

    public String sendValidateEmail(@NonNull String email) throws TooFrequentResends {
        return sendValidateEmail(email, false);
    }

    public String sendValidateEmail(@NonNull String email, boolean reSend)
            throws TooFrequentResends {
        ValidateData validateData = emailMap.get(email);

        if (validateData != null) {
            long delta = System.currentTimeMillis() - validateData.getTime();
            if (!reSend && delta < 10 * 60 * 1000) {
                return validateData.getIdentifyCode();
            }

            if (delta < 60 * 1000) {
                throw new TooFrequentResends(email);
            }

            this.removeRecord(email);
        }

        String code = String.valueOf((int) (Math.random() * 9000 + 1000));
        String identifyCode = String.format("0x%05x", (int) (Math.random() * 0xFFFFF));
        Timer timer = new Timer();

        validateData = ValidateData.builder().email(email).code(code).identifyCode(identifyCode)
                .time(System.currentTimeMillis()).timer(timer).build();
        this.createRecord(validateData);

        if (this.enable) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(this.from);
            message.setTo(email);
            message.setSubject("驗證碼");
            message.setText(
                    String.format("您的驗證碼為: %s 請在 15 分鐘內進行驗證。\n請勿將此驗證碼告訴他人。\n\nEmail 識別碼: %s", code,
                            identifyCode));

            javaMailSender.send(message);
        } else {
            System.out.println(String.format("Email: %s, Code: %s", email, code));
        }

        return identifyCode;
    }

    public void preCheck(String email, String code, String identifyCode)
            throws WrongValidateCode, EmailValidateTooManyRetry {
        ValidateData validateData = emailMap.get(email);
        if (validateData == null) {
            throw new WrongValidateCode(email);
        }

        int countDown = validateData.getCountDown();
        validateData.setCountDown(validateData.getCountDown() - 1);
        if (countDown <= 0) {
            this.removeRecord(email);
            throw new EmailValidateTooManyRetry(email);
        }

        if (!validateData.getCode().equals(code)
                || !validateData.getIdentifyCode().equals(identifyCode)) {

            if (countDown == 1) {
                this.removeRecord(email);
            }
            throw new WrongValidateCode(email);
        }
    }

    public void validateEmail(String email, String code, String identifyCode)
            throws WrongValidateCode, EmailValidateTooManyRetry {
        this.preCheck(email, code, identifyCode);

        this.removeRecord(email);
    }
}
