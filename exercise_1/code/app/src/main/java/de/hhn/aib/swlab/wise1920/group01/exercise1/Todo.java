package de.hhn.aib.swlab.wise1920.group01.exercise1;

public class Todo {
    private String description;
    private boolean active;

    public Todo(String description) {
        this.description = description;
        this.active = true;
    }

    public Todo() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
