package com.example.fgonzalez.domain;

import java.io.Serializable;

public class User implements Serializable {

    public long id;
    public String name;
    public String username;
    public String avatar;
    public String email;
    public Address address;
    public String phone;
    public String website;
    public Company company;

}
