package server;

public interface LoginProcessedInterface {
    void onSuccess(String auth);

    void onFailure();
}