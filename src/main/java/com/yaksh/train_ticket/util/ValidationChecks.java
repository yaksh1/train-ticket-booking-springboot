package com.yaksh.train_ticket.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.repository.UserRepositoryV2;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ValidationChecks {

  private static UserRepositoryV2 userRepositoryV2;
  public static boolean isUserPresent(String userName) {
    // checking if user already exists with the username provided
        Optional<User> userFound = userRepositoryV2.findByUserName(userName.toLowerCase());
        // if user already exists return false
        return userFound.isPresent();
  }

  public boolean isValidEmail(String email) {
    String email_regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    Pattern pattern = Pattern.compile(email_regex);

    // Check if the email is valid
    return email != null && pattern.matcher(email).matches();
  }

  public boolean isValidPassword(String password) {
    int minLength = 8; // minimum length

    return password != null
        && password.length() >= minLength
        && password.matches(".*[A-Z].*") // at least one uppercase letter
        && password.matches(".*[a-z].*") // at least one lowercase letter
        && password.matches(".*\\d.*") // at least one digit
        && password.matches(".*[!@#$%^&*()-+].*"); // at least one special character
  }

  public boolean isOtpExpired(LocalDateTime userOtpExpiryTime) {
    if (userOtpExpiryTime.isBefore(LocalDateTime.now())) {
      return true;
    }
    return false;
  }
}
