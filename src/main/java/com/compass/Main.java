package com.compass;

import com.compass.service.ApplicationFeaturesService;
import com.compass.service.impl.ApplicationFeaturesServiceImpl;

public class Main {

    public static void main(String[] args) {
        ApplicationFeaturesService app = new ApplicationFeaturesServiceImpl();
        app.start();
    }
}