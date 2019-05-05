enum UserStatus {
  USER_VALIDATED, // User has been cleared to use the app.
  USER_REJECTED, // User has been banned from using the app.
  PENDING // User hasn't been validated yet. Different roles require different validation levels.
}

export default UserStatus;
