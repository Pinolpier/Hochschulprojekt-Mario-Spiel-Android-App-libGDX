package de.hhn.aib.swlab.wise1920.group01.exercise2.controller;

import android.util.Log;

public class SettingsFunction {
    public void changeRadius(String s) {
        //TODO chnage radius with use of the string s
        Log.e("cngRadius", s);
    }

    public void changeInterval(String s) {
        //TODO change interval with use of the string s
        Log.e("cngInterval", s);
    }

    public void changePOI(Boolean b) {
        //TODO change showPoi with use of boolean b
        Log.e("cngPOI", "" + b);
    }

    public void changeWeatherdata(Boolean b) {
        //TODO change showWeatherdata with use of boolean b
        Log.e("cngWatherdata", "" + b);
    }

    public void changeLocationHistory(Boolean b) {
        //TODO change showLocationhistory with use of boolean b
        Log.e("cngLocationhistory", "" + b);
    }

    public void changeLocationHistoryTimeframe(String s) {
        Log.e("cngLocHistorytimeframe", s);
    }

    public void changePassword(String s) {
        //TODO change password with use of string s
        Log.e("changePassword", s);
    }
}
