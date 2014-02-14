package com.mattkula.guesswhom.data.models;

import java.io.Serializable;

/**
 * Created by matt on 2/9/14.
 */
public class Game implements Serializable{

    public Game(String id, String opponent_id, String question, String response, String whose_turn, String my_answer, String opponent_answer, Answer[] answers) {
        this.id = id;
        this.opponent_id = opponent_id;
        this.question = question;
        this.response = response;
        this.whose_turn = whose_turn;
        this.my_answer = my_answer;
        this.opponent_answer = opponent_answer;
        this.answers = answers;
    }

    public String id;
    public String opponent_id;
    public String question;
    public String response;
    public String whose_turn;
    public String my_answer;
    public String opponent_answer;

    public Answer[] answers;
}
