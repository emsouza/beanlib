package net.sf.beanlib.hibernate5;

import java.sql.Timestamp;
import java.util.Date;

public class Pojo {

    private Date date = new Timestamp(new Date().getTime());

    // reference to the same date instance
    private Date dateRef = date;

    private String text = "whatever";

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDateRef() {
        return dateRef;
    }

    public void setDateRef(Date dateRef) {
        this.dateRef = dateRef;
    }
}
