package server;

public interface RegistrationProcessedInterface {
    void onSuccess(String username, String password);

    void onFailure();
}