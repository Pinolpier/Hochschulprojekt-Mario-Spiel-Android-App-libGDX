package com.mygdx.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import server.BackendCommunicator;
import server.GameMessage;
import server.MessageListener;

public class AndroidLauncher extends AndroidApplication implements BackendCommunicator, MessageListener {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGLSurfaceView20API18=true;
		config.useGyroscope=true;
		config.useCompass=true;
        MarioBros game = new MarioBros();
        initialize(game, config);
    }

    @Override
    public void sendMessage(GameMessage message) {

    }

    @Override
    public void onMessageReceived(String message) {

    }
}