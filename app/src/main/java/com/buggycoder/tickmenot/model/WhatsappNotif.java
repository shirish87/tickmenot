package com.buggycoder.tickmenot.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

@Table(name = "notif", id = BaseColumns._ID)
public class WhatsappNotif extends Model {

    private static final DateFormat df = new SimpleDateFormat("EEE, d MMM, h:mm a");

    @Column(name = "event")
    public String event;

    @Column(name = "sender", index = true)
    public String sender;

    @Column(name = "message")
    public String message;

    @Column(name = "postTime", index = true)
    public long postTime;

    @Column(name = "hashCode", index = true)
    public int hashCode;

    public WhatsappNotif() {
        super();
    }

    public WhatsappNotif(String event, String sender, String message, long postTime) {
        this();

        this.event = event;
        this.sender = sender;
        this.message = message;
        this.postTime = postTime;

        // may come in handy for dup checks
        this.hashCode = hashCode();
    }

    public String getFormattedPostTime() {
        return (postTime > 0) ? df.format(postTime) : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WhatsappNotif that = (WhatsappNotif) o;

        return postTime == that.postTime &&
                event.equals(that.event) &&
                message.equals(that.message) &&
                sender.equals(that.sender);
    }

    @Override
    public int hashCode() {
        if (hashCode != 0) {
            // assuming nothing has altered the public fields :P
            return hashCode;
        }

        int result = super.hashCode();
        result = 31 * result + event.hashCode();
        result = 31 * result + sender.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + (int) (postTime ^ (postTime >>> 32));
        return result;
    }

    public static boolean isDup(WhatsappNotif notif) {
        WhatsappNotif lastNotif = new Select()
                .from(WhatsappNotif.class)
                .where("sender = ?", notif.sender)
                .orderBy("_id DESC")
                .executeSingle();

        return (lastNotif != null && lastNotif.equals(notif));
    }

    public static List<WhatsappNotif> list() {
        List<WhatsappNotif> queryResult = new Select()
                .from(WhatsappNotif.class)
                .orderBy("_id DESC")
                .limit(50)
                .execute();

        Collections.reverse(queryResult);
        return queryResult;
    }
}
