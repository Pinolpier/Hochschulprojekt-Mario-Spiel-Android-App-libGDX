package de.hhn.aib.swlab.wise1920.group01.exercise2.controller;

import android.util.Log;

public class SettingsFunction {
    public void changeRadius(String s) {
        //TODO chnage radius with use of the string s
        Log.wtf("cngRadius", s);
    }

    public void changeInterval(String s) {
        //TODO change interval with use of the string s
        Log.wtf("cngInterval", s);
    }

    public void changePOI(Boolean b) {
        //TODO change showPoi with use of boolean b
        Log.wtf("cngPOI", "" + b);
    }

    public void changeWeatherdata(Boolean b) {
        //TODO change showWeatherdata with use of boolean b
        Log.wtf("cngWatherdata", "" + b);
    }

    public void changeLocationHistory(Boolean b) {
        //TODO change showLocationhistory with use of boolean b
        Log.wtf("cngLocationhistory", "" + b);
    }

    public void changeLocationHistoryTimeframe(String s) {
        //TODO change locHistoryTimeframe with use of string s
        Log.wtf("cngLocHistorytimeframe", s);
    }

    public void changePassword(String s) {
        //TODO change password with use of string s
        Log.wtf("changePassword", s);
    }
}
