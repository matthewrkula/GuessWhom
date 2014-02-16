package com.mattkula.guesswhom.data.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by matt on 2/9/14.
 */
public class Game implements Serializable{

    public Game(String id, String opponent_id, String last_question, String question, String response, String whose_turn, String creator_answer, String opponent_answer, Answer[] answers) {
        this.id = id;
        this.opponent_id = opponent_id;
        this.lastquestion = last_question;
        this.question = question;
        this.response = response;
        this.whose_turn = whose_turn;
        this.creator_answer = creator_answer;
        this.opponent_answer = opponent_answer;
        this.answers = answers;
    }

    @Expose
    public String id;
    @Expose
    public String opponent_id;
    @Expose
    public String lastquestion;
    @Expose
    public String question;
    @Expose
    public String response;
    @Expose
    public String whose_turn;
    @Expose
    public String creator_answer;
    @Expose
    public String opponent_answer;
    @Expose
    public Answer[] answers;
}
