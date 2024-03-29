package com.siteparser.MyOwnSiteParser.service.security;

import com.siteparser.MyOwnSiteParser.domain.User;
import com.siteparser.MyOwnSiteParser.service.jpa.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserValidateService {

    @Autowired
    private UserService userService;

    public enum ValidateResult {
        ok,
        invalidEmail,
        shortPassword,
        userExists,
        invalidPassword;

        public String getStringRepresentation() {
            switch (this) {
                case ok:
                    return "OK";
                case invalidEmail:
                    return "Некоректный email.";
                case shortPassword:
                    return "Короткий пароль! Пароль должен быть больше 5-ти символов.";
                case userExists:
                    return "Пользователь с таким email уже существует.";
                case invalidPassword:
                    return "Неправильный пароль.";
                default:
                    return "";
            }
        }
    }

    public ValidateResult validate(User user) {
        if (userService.getByEmail(user.getEmail()) != null) return ValidateResult.userExists;
        if (user.getPassword().length() < 5) return ValidateResult.shortPassword;
        if (!isValidEmailAddress(user.getEmail())) return ValidateResult.invalidEmail;
        return ValidateResult.ok;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public ValidateResult validatePassword(User user, String oldPassword, String newPassword){
        if (newPassword.length() < 5) return ValidateResult.shortPassword;
        // сравнение паролей
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) return ValidateResult.invalidPassword;
        return ValidateResult.ok;
    }
}
