package core;

public abstract class User {

	private int userID;
	private String name;
	private String surname;
	private String email;

	public User() {
	}

	public User(int userID, String name, String surname, String email) {
		setUserID(userID);
		this.name = name;
		this.surname = surname;
		this.email = email;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		if (userID < 0) {
			throw new IllegalArgumentException("User ID cannot be negative.");
		}
		this.userID = userID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void logout() {
	}

}
