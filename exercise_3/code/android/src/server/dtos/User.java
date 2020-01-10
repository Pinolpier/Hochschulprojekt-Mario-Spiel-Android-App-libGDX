package server.dtos;

/**
 * A representation of a user who plays our game. Not the same as a player in the backend. Consists of only a username and a password!
 */
public class User {
    private String username, password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}