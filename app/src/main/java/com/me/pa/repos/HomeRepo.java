package com.me.pa.repos;


public class HomeRepo {
    private static HomeRepo instance;

    private HomeRepo() {
    }

    public static HomeRepo getInstance() {
        if (instance == null) {
            instance = new HomeRepo();
        }
        return instance;
    }
}
