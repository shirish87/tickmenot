package com.buggycoder.tickmenot.notif;

import android.app.Notification;
import android.content.res.Resources;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.activeandroid.query.Select;
import com.buggycoder.tickmenot.R;
import com.buggycoder.tickmenot.lib.Tuple;
import com.buggycoder.tickmenot.model.WhatsappNotif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhatsappNotifParser extends NotifParser<WhatsappNotif> {

    private final String mGroupNotifTitle;
    private final String mSenderSeparator;

    public WhatsappNotifParser(Resources res) {
        super(res.getString(R.string.notif_package));

        mGroupNotifTitle = res.getString(R.string.group_notif_title);
        mSenderSeparator = res.getString(R.string.sender_separator);
    }

    @Override
    public Tuple<String, List<WhatsappNotif>> parse(StatusBarNotification sbn) throws UnsupportedNotifException {
        dumpStatusBarNotification(sbn);

        if (!isValid(sbn)) {
            throw new UnsupportedNotifException("Not a WhatsApp notification");
        }

        List<WhatsappNotif> notifs = new ArrayList<>();
        Notification notification = sbn.getNotification();

        long postTime = sbn.getPostTime();
        String event = notification.tickerText.toString();
        Bundle extras = notification.extras;

        String title = extras.getString(NotificationProps.TITLE, "");
        String message = extras.getString(NotificationProps.TEXT, "");
        String summaryText = extras.getString(NotificationProps.SUMMARY_TEXT, "");
        String sender = "";

        if (extras.containsKey(NotificationProps.TEXT) &&
                !extras.containsKey(NotificationProps.TEXT_LINES)) {
            // 1 new message - either from a contact (android.title=contact_name)
            // or a group (android.title=group_name)

            // "Message from Shirish Airtel 3g @ things"
            if (event.endsWith(" @ " + title)) {
                // group message
                Tuple<String, String> senderMsg = splitSenderMessage(message);
                if (senderMsg.isNotNull()) {
                    sender = senderMsg._1;
                    message = senderMsg._2;
                }
            } else if (!mGroupNotifTitle.equalsIgnoreCase(title)) {
                // contact message
                sender = title;
            }

            if (!sender.isEmpty() && !message.isEmpty()) {
                notifs.add(new WhatsappNotif(event, sender, message, postTime));
                return new Tuple<>(summaryText, notifs);
            }
        }

        // notif is a summary of multiple conversations
        Map<String, List<String>> senderMessages = parseLines(notification.extras);

        for (Map.Entry<String, List<String>> senderMsgs : senderMessages.entrySet()) {
            WhatsappNotif lastMsgFromSender = new Select()
                    .from(WhatsappNotif.class)
                    .where("sender = ?", senderMsgs.getKey())
                    .orderBy("_id DESC")
                    .executeSingle();

            if (lastMsgFromSender == null) {
                for (String msg : senderMsgs.getValue()) {
                    notifs.add(new WhatsappNotif(event, senderMsgs.getKey(), msg, postTime));
                }
            } else {
                // merging local db list with list of android.textLines in incoming notif
                // using strcmp here :(

                List<String> msgs = senderMsgs.getValue();
                int i;

                for (i = msgs.size() - 1; i >= 0; i--) {
                    if (msgs.get(i).equals(lastMsgFromSender.message)) {
                        // found the marker
                        break;
                    }
                }

                for (int k = 0; k < i; k++) {
                    // check intermediates
                    // TODO: This could cause dups or inconsistency

                    boolean exists = new Select().from(WhatsappNotif.class)
                            .where("sender = ? AND message = ?", senderMsgs.getKey(), msgs.get(k))
                            .exists();

                    if (!exists) {
                        notifs.add(new WhatsappNotif(event, senderMsgs.getKey(), msgs.get(k), postTime));
                    }
                }

                for (int j = i + 1; j < msgs.size(); j++) {
                    // merge the rest
                    notifs.add(new WhatsappNotif(event, senderMsgs.getKey(), msgs.get(j), postTime));
                }
            }
        }

        return new Tuple<>(summaryText, notifs);
    }


    private Tuple<String, String> splitSenderMessage(String message) {
        Pattern p = Pattern.compile("^(.*?)" + mSenderSeparator + "\\s*(.*)", Pattern.DOTALL);
        Matcher m = p.matcher(message);

        if (m.find() && m.groupCount() >= 2) {
            return new Tuple<>(m.group(1), m.group(2));
        }

        return new Tuple<>(null, null);
    }

    private Map<String, List<String>> parseLines(final Bundle extras) {
        Map<String, List<String>> senderMessages = new HashMap<>();
        CharSequence[] textLines = extras.getCharSequenceArray(NotificationProps.TEXT_LINES);

        if (textLines != null && textLines.length > 0) {

            for (CharSequence textLine : textLines) {

                String sender, message;
                Tuple<String, String> senderMsg = splitSenderMessage(textLine.toString());

                if (senderMsg.isNotNull()) {
                    sender = senderMsg._1;
                    message = senderMsg._2;
                } else {
                    sender = extras.getString(NotificationProps.TITLE, mGroupNotifTitle);
                    message = textLine.toString();
                }

                if (!TextUtils.isEmpty(sender)) {
                    if (!senderMessages.containsKey(sender)) {
                        senderMessages.put(sender, new ArrayList<String>());
                    }

                    if (!TextUtils.isEmpty(message)) {
                        senderMessages.get(sender).add(message);
                    }
                }
            }
        }

        return senderMessages;
    }
}
