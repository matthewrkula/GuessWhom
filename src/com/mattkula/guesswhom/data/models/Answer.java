package com.mattkula.guesswhom.data.models;

import java.io.Serializable;

/**
 * Created by matt on 2/9/14.
 */
public class Answer implements Serializable{

    public Answer(String name, String fb_id) {
        this.name = name;
        this.fb_id = fb_id;
    }

    public String name;
    public String fb_id;
}
