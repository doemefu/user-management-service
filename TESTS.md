## **TESTS.md**

### **Overview**
This document describes the tests implemented for the `UserController` and `UserService` classes. Each test is designed to validate a specific functionality of the application, ensuring robust behavior and maintainability.
Most of the tests are brought to you by ChatGPT.

---

### **Controller Tests**

#### **1. `testCreateUser`**
- **What it tests**: Verifies that the `/users` endpoint correctly creates a user and returns a `201 Created` response.
- **How it works**:
    - Mocks the `UserService.createUser()` method to return a predefined `User`.
    - Sends a POST request with a `CreateUserRequest` as JSON.
    - Asserts that the response contains the expected `username` and `email`.

---

#### **2. `testGetUserById`**
- **What it tests**: Ensures the `/users/{id}` endpoint fetches a user by their ID and returns a `200 OK` response.
- **How it works**:
    - Mocks the `UserService.getUserById()` method to return a predefined `User`.
    - Sends a GET request to `/users/1`.
    - Asserts that the response contains the expected user details (`username`, `email`).

---

#### **3. `testGetAllUsers`**
- **What it tests**: Verifies that the `/users` endpoint fetches all users and returns a list in a `200 OK` response.
- **How it works**:
    - Mocks the `UserService.getAllUsers()` method to return a list of predefined `User` objects.
    - Sends a GET request to `/users`.
    - Asserts that the response contains an array with the expected user details.

---

#### **4. `testUpdateUser`**
- **What it tests**: Verifies that the `/users/{id}` endpoint updates a user and returns a `200 OK` response with the updated data.
- **How it works**:
    - Mocks the `UserService.updateUser()` method to return a predefined updated `User`.
    - Sends a PUT request with a `UserDto` as JSON to `/users/1`.
    - Asserts that the response contains the updated `username` and `email`.

---

#### **5. `testDeleteUser`**
- **What it tests**: Ensures the `/users/{id}` endpoint deletes a user and returns a `204 No Content` response.
- **How it works**:
    - Mocks the `UserService.deleteUser()` method to perform no operation (`doNothing()`).
    - Sends a DELETE request to `/users/1`.
    - Asserts that the response status is `204 No Content`.

---

#### **6. `testResetPassword`**
- **What it tests**: Verifies that the `/users/{id}/reset-password` endpoint resets a user's password and returns a `200 OK` response.
- **How it works**:
    - Mocks the `UserService.resetPassword()` method to perform no operation (`doNothing()`).
    - Sends a POST request with the new password as JSON to `/users/1/reset-password`.
    - Asserts that the response status is `200 OK`.

---

### **Service Tests**

#### **1. `testCreateUser`**
- **What it tests**: Validates that a new user can be created successfully when the username and email are unique.
- **How it works**:
    - Mocks `UserRepository.existsByUsername()` and `UserRepository.existsByEmail()` to return `false`.
    - Mocks `PasswordEncoder.encode()` to simulate password hashing.
    - Asserts that the `User` returned from `UserService.createUser()` contains the expected values and is saved via `UserRepository.save()`.

---

#### **2. `testCreateUser_UsernameAlreadyExists`**
- **What it tests**: Ensures that attempting to create a user with an existing username throws an `IllegalArgumentException`.
- **How it works**:
    - Mocks `UserRepository.existsByUsername()` to return `true`.
    - Asserts that `UserService.createUser()` throws an exception with the appropriate message.

---

#### **3. `testCreateUser_EmailAlreadyExists`**
- **What it tests**: Ensures that attempting to create a user with an existing email throws an `IllegalArgumentException`.
- **How it works**:
    - Mocks `UserRepository.existsByUsername()` to return `false`.
    - Mocks `UserRepository.existsByEmail()` to return `true`.
    - Asserts that `UserService.createUser()` throws an exception with the appropriate message.

---

#### **4. `testGetUserById_UserExists`**
- **What it tests**: Validates that a user is successfully retrieved by their ID.
- **How it works**:
    - Mocks `UserRepository.findById()` to return an `Optional<User>`.
    - Asserts that `UserService.getUserById()` returns the expected `User`.

---

#### **5. `testGetUserById_UserNotFound`**
- **What it tests**: Ensures that trying to fetch a user by a non-existent ID throws a `UserNotFoundException`.
- **How it works**:
    - Mocks `UserRepository.findById()` to return `Optional.empty()`.
    - Asserts that `UserService.getUserById()` throws the appropriate exception.

---

#### **6. `testUpdateUser`**
- **What it tests**: Validates that a user's details are updated correctly.
- **How it works**:
    - Mocks `UserRepository.findById()` to return the existing user.
    - Mocks `UserRepository.save()` to return the updated user.
    - Asserts that the updated user details match the input DTO.

---

#### **7. `testResetPassword`**
- **What it tests**: Ensures that a user's password is reset correctly and saved to the database.
- **How it works**:
    - Mocks `UserRepository.findById()` to return the existing user.
    - Mocks `PasswordEncoder.encode()` to simulate hashing.
    - Asserts that the updated password hash is saved to the database.

---

#### **8. `testDeleteUser`**
- **What it tests**: Validates that a user is deleted successfully.
- **How it works**:
    - Mocks `UserRepository.findById()` to return the existing user.
    - Mocks `UserRepository.delete()` to perform no operation.
    - Asserts that the user is deleted without any exceptions.

---

### **How These Tests Work Together**

- **Controller Tests**:
    - Focus on validating HTTP endpoints, request/response structure, and interaction with the service layer.
    - Use `MockMvc` for simulating requests and verifying responses.
    - Use `Mockito` to mock the `UserService` and test controller logic in isolation.

- **Service Tests**:
    - Validate business logic, such as unique user validation, password hashing, and entity updates.
    - Use `Mockito` to mock `UserRepository` and `PasswordEncoder` for focused unit testing.

---

### **Maintenance Tips**
1. **Test Naming**: Use descriptive test names that explain the scenario being tested.
2. **Add New Tests**: For new functionality, follow the structure above (e.g., split controller and service tests).
3. **Mock Interactions**: Always mock external dependencies (e.g., `UserRepository`, `PasswordEncoder`) in service tests.
4. **Document Behavior**: Update this file whenever new tests are added or existing ones are modified.

---