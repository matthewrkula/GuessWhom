package com.mattkula.guesswhom.data.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by matt on 2/9/14.
 */
public class Answer implements Serializable{

    public Answer(String name, String fb_id) {
        this.name = name;
        this.fb_id = fb_id;
    }

    @Expose
    public String name;
    @Expose
    public String fb_id;
}
