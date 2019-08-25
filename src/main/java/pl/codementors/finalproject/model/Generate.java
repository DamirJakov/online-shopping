package pl.codementors.finalproject.model;

import java.util.UUID;

public class Generate {
    public static String Id() {
        return UUID.randomUUID().toString();
    }

}
