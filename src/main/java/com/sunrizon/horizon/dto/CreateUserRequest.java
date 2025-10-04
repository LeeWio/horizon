
package com.sunrizon.horizon.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * CreateUserRequest
 *
 * <p>
 * DTO used to create a new user.
 * This object is received from the client when creating a user.
 * </p>
 */
@Data
public class CreateUserRequest {

  /**
   * Username of the new user.
   */
  // @NotBlank(message = "Username cannot be blank")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String username;

  /**
   * Email address of the new user.
   */
  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Invalid email format")
  private String email;

  /**
   * Password of the new user.
   */
  @NotBlank(message = "Password cannot be blank")
  @Size(min = 6, message = "Password must be at least 6 characters long")
  private String password;

  /**
   * Optional set of role IDs to assign to the new user.
   */
  private Set<String> roleIds;
}
