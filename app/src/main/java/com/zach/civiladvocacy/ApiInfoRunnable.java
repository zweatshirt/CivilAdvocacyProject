package com.zach.civiladvocacy;

public class ApiInfoRunnable implements Runnable {
    // Query Format: https://www.googleapis.com/civicinfo/v2/representatives?key=Your-API-Key&address=address
    private final String key = "AIzaSyA1WurKkx_fI9GRiA1NrM1swPK2AU_u7uk";
    private final MainActivity main;
    private final String location;
    public ApiInfoRunnable(MainActivity main, String location) {
        this.main = main;
        this.location = location;
    }

    @Override
    public void run() {

    }
}
