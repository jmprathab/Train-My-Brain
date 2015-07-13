package dataclasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmprathab on 10/07/15.
 */
public class Question {
    private int q_no;
    private String question, category;
    private List<String> options;


    public Question() {
        this.q_no = 0;
        this.question = "";
        this.options = new ArrayList<>();
        this.category = "";
    }

    public Question(int q_no, String category, String question, List<String> options) {
        this.q_no = q_no;
        this.question = question;
        this.options = options;
        this.category = category;
    }

    public int getQNO() {
        return q_no;
    }

    public void setQNO(int q_no) {
        this.q_no = q_no;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return options.get(0);
    }

    public void setOptionA(String o) {
        options.add(0, o);
    }

    public String getOptionB() {
        return options.get(1);
    }

    public void setOptionB(String o) {
        options.add(1, o);
    }

    public String getOptionC() {
        return options.get(2);
    }

    public void setOptionC(String o) {
        options.add(2, o);
    }

    public String getOptionD() {
        return options.get(3);
    }

    public void setOptionD(String o) {
        options.add(3, o);
    }
}
