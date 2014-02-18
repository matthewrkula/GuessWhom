package com.mattkula.guesswhom.data.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by matt on 2/9/14.
 */
public class Game implements Serializable, Comparable<Game> {

    public Game(String id, String opponent_id, String last_question, String question, String response, String whose_turn, String creator_answer, String opponent_answer, Answer[] answers, int turn_count) {
        this.id = id;
        this.opponent_id = opponent_id;
        this.lastquestion = last_question;
        this.question = question;
        this.response = response;
        this.whose_turn = whose_turn;
        this.creator_answer = creator_answer;
        this.opponent_answer = opponent_answer;
        this.answers = answers;
        this.turn_count = turn_count;
    }

    @Expose
    public String id;
    @Expose
    public String opponent_id;
    @Expose
    public String opponent_name;
    @Expose
    public String creator_name;
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
    @Expose
    public int turn_count;
    @Expose
    public Date created_at;
    @Expose
    public Date updated_at;

    @Override
    public int compareTo(Game game) {
        return game.updated_at.compareTo(this.updated_at);
    }
}
