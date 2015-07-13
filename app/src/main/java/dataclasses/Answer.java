package dataclasses;

/**
 * Created by jmprathab on 10/07/15.
 */
public class Answer {
    private int q_no;
    private String answer;

    public Answer(String answer, int q_no) {
        this.answer = answer;
        this.q_no = q_no;
    }

    public Answer() {
        q_no = 0;
        answer = "None";
    }

    public int getQNO() {
        return q_no;
    }

    public void setQNO(int q_no) {
        this.q_no = q_no;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String toJsonString() {
        String converted = "";
        converted = converted + '{' + "\"qno\":" + String.valueOf(this.q_no) + ",\"answer\":" + "\"" + String.valueOf(this.answer) + "\"" + "}";
        return converted;
    }
}
