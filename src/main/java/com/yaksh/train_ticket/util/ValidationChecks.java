package com.yaksh.train_ticket.util;

import org.springframework.stereotype.Component;
import com.yaksh.train_ticket.model.User;
import com.yaksh.train_ticket.repository.UserRepositoryV2;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utility class for various validation checks such as user presence, email format,
 * password strength, and OTP expiration.
 */
@Component
@RequiredArgsConstructor
public class ValidationChecks {

  // Repository for accessing user data
  private final UserRepositoryV2 userRepositoryV2;

  /**
   * Checks if a user is already present in the system with the given username.
   *
   * @param userEmail the userEmail to check
   * @return true if the user exists, false otherwise
   */
  public boolean isUserPresent(String userEmail) {
    // Checking if a user with the given username exists in the database
    Optional<User> userFound = userRepositoryV2.findByUserEmail(userEmail.toLowerCase());
    // Return true if the user exists, otherwise false
    return userFound.isPresent();
  }

  /**
   * Validates whether the given email is in a valid format.
   *
   * @param email the email to validate
   * @return true if the email is valid, false otherwise
   */
  public boolean isValidEmail(String email) {
    // Regular expression for validating email format
    String email_regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    Pattern pattern = Pattern.compile(email_regex);

    // Check if the email is not null and matches the regex pattern
    return email != null && pattern.matcher(email).matches();
  }

  /**
   * Validates whether the given password meets the required strength criteria.
   * The password must have a minimum length of 8 characters and include at least:
   * one uppercase letter, one lowercase letter, one digit, and one special character.
   *
   * @param password the password to validate
   * @return true if the password is strong, false otherwise
   */
  public boolean isValidPassword(String password) {
    int minLength = 8; // Minimum password length requirement

    // Validate the password against the defined criteria
    return password != null
        && password.length() >= minLength
        && password.matches(".*[A-Z].*") // At least one uppercase letter
        && password.matches(".*[a-z].*") // At least one lowercase letter
        && password.matches(".*\\d.*") // At least one digit
        && password.matches(".*[!@#$%^&*()-+].*"); // At least one special character
  }

  /**
   * Checks if the given OTP (One-Time Password) expiration time has already passed.
   *
   * @param userOtpExpiryTime the expiration time of the OTP
   * @return true if the OTP is expired, false otherwise
   */
  public boolean isOtpExpired(LocalDateTime userOtpExpiryTime) {
    // Compare the OTP expiration time with the current time
    if (userOtpExpiryTime.isBefore(LocalDateTime.now())) {
      return true; // OTP is expired
    }
    return false; // OTP is still valid
  }
}