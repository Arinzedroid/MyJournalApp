package tech.arinzedroid.myjournal.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.parceler.Parcel;

import java.util.Date;

import tech.arinzedroid.myjournal.utils.RandomString;

@Parcel
@Entity
public class DiaryModel {

    @PrimaryKey
    private String ID = new RandomString().nextString();
    private String DATE = new Date(System.currentTimeMillis()).toString();
    private String TITLE;
    private String DETAIL;

    public String getDate() {
        return DATE;
    }

    public void setDate(String date) {
        this.DATE = date;
    }

    public String getTitle() {
        return TITLE;
    }

    public void setTitle(String title) {
        this.TITLE = title;
    }

    public String getDetail() {
        return DETAIL;
    }

    public void setDetail(String detail) {
        this.DETAIL = detail;
    } //DETAIL

    public DiaryModel(){

    }


    public String getId() {
        return ID;
    }

    public void setId(String id) {
        this.ID = id;
    }
}
