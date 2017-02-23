package com.example.fgonzalez.domain;

import java.io.Serializable;

public class Post implements Serializable {

    public long userId;
    public User user;
    public long id;
    public String title;
    public String body;

}
