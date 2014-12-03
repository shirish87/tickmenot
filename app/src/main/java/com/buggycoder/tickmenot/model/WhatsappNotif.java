package com.buggycoder.tickmenot.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "notif", id = BaseColumns._ID)
public class WhatsappNotif extends Model {

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
}
