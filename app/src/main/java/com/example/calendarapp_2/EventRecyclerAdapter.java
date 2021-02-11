package com.example.calendarapp_2;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendarapp_2.firebase.FirebaseCallback;
import com.example.calendarapp_2.firebase.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<Events> arrayList;
    MainActivity mainActivity;
    boolean isAdmin;

    public EventRecyclerAdapter(Context context, ArrayList<Events> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if(isAdmin){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.show_events_admin_layout, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.show_events_member_layout, parent, false);
        }

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Events events = arrayList.get(position);
        holder.Event.setText(events.getEVENT());
        holder.Description.setText(events.getDESCRIPTION());
        holder.DateTxt.setText(events.getDATE());
        holder.Time.setText(events.getTIME());

        if(isAdmin){
            holder.Feedback.setText(events.getFEEDBACK());
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAdmin){
                    deleteCalendarEvent(events.getEVENT(), events.getDATE(), events.getTIME());
                    arrayList.remove(position);
                    notifyDataSetChanged();
                }else {
                    String event = events.getEVENT();
                    String date = events.getDATE();
                    String time = events.getTIME();

                    Intent intent = new Intent(mainActivity, Feedback.class);
                    intent.putExtra("event", event);
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);

                    mainActivity.startActivity(intent);

                }
            }
        });

        final String date = events.getDATE();
        final String event = events.getEVENT();
        final String time = events.getTIME();
        final boolean[] alarmed = new boolean[1];
        final boolean[] progressDone = new boolean[1];

        isAlarmed(date, event, time, new FirebaseCallback() {
            @Override
            public void onSuccess(final Events events) {
                String notify = events.getNOTIFY();
                String progress = events.getPROGRESS();

                if (notify.equals("on")) {
                    alarmed[0] = true;
                } else {
                    alarmed[0] = false;
                }

                if (progress.equals("on")) {
                    progressDone[0] = true;
                } else {
                    progressDone[0] = false;
                }

                if (alarmed[0]) {
                    holder.setAlarm.setImageResource(R.drawable.ic_action_notification_on);
                } else {
                    holder.setAlarm.setImageResource(R.drawable.ic_action_notification_off);
                }

                if (progressDone[0]) {
                    holder.setProgress.setImageResource(R.drawable.ic_action_progress_on);
                } else {
                    holder.setProgress.setImageResource(R.drawable.ic_action_progress_off);
                }

                Calendar datecalendar = Calendar.getInstance();
                datecalendar.setTime(ConvertStringToDate(events.getDATE()));
                final int alarmYear = datecalendar.get(Calendar.YEAR);
                final int alarmMonth = datecalendar.get(Calendar.MONTH);
                final int alarmDay = datecalendar.get(Calendar.DAY_OF_MONTH);
                Calendar timecalendar = Calendar.getInstance();
                timecalendar.setTime(ConvertStringToTime(events.getTIME()));
                final int alarmHour = timecalendar.get(Calendar.HOUR_OF_DAY);
                final int alarmMinuit = timecalendar.get(Calendar.MINUTE);

                holder.setAlarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (alarmed[0]) {
                            holder.setAlarm.setImageResource(R.drawable.ic_action_notification_off);

                            String date = events.getDATE();
                            String event = events.getEVENT();
                            String time = events.getTIME();


                            getRequestCode(date, event, time, new FirebaseCallback() {
                                @Override
                                public void onSuccess(Events events) {
                                    int code = Integer.parseInt(events.getID());
                                    cancelAlarm(code);

                                    updateEvent(events.getDATE(), events.getEVENT(), events.getTIME(), "off", new FirebaseFinishListener() {
                                        @Override
                                        public void onFinish() {
                                            notifyDataSetChanged();
                                        }
                                    });

                                }
                            });

                        } else {
                            holder.setAlarm.setImageResource(R.drawable.ic_action_notification_on);

                            final Calendar alarmCalendar = Calendar.getInstance();
                            alarmCalendar.set(alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinuit);

                            String date = events.getDATE();
                            String event = events.getEVENT();
                            String time = events.getTIME();


                            getRequestCode(date, event, time, new FirebaseCallback() {
                                @Override
                                public void onSuccess(Events events) {
                                    int code = Integer.parseInt(events.getID());
                                    setAlarm(alarmCalendar, events.getEVENT(), events.getTIME(), code);
                                    updateEvent(events.getDATE(), events.getEVENT(), events.getTIME(), "on", new FirebaseFinishListener() {
                                        @Override
                                        public void onFinish() {
                                            notifyDataSetChanged();
                                        }
                                    });

                                }
                            });
                        }
                    }
                });

                holder.setProgress.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View v) {
                        if (progressDone[0]) {

                            String date = events.getDATE();
                            String event = events.getEVENT();
                            String time = events.getTIME();

                            holder.setProgress.setImageResource(R.drawable.ic_action_progress_off);
                            updateEvent_2(date, event, time, "off", new FirebaseFinishListener() {
                                @Override
                                public void onFinish() {
                                    notifyDataSetChanged();
                                }
                            });

                        } else {
                            holder.setProgress.setImageResource(R.drawable.ic_action_progress_on);
                            updateEvent_2(date, event, time, "on", new FirebaseFinishListener() {
                                @Override
                                public void onFinish() {
                                    notifyDataSetChanged();
                                    notifyDataSetChanged();

                                    String chanelLabel = "gma7";

                                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, chanelLabel)
                                            .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                                            .setContentTitle(events.getEVENT()) // title for notification
                                            .setContentText(events.getDESCRIPTION()) // message for notification
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setAutoCancel(true); // clear notification after click

                                    Intent intent = new Intent(context, MainActivity.class);
                                    PendingIntent pi = PendingIntent.getActivity
                                            (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    mBuilder.setContentIntent(pi);
                                    NotificationChannel channel = new NotificationChannel(chanelLabel, "Announcement", NotificationManager.IMPORTANCE_DEFAULT);
                                    NotificationManager mNotificationManager = (NotificationManager)
                                            context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    mNotificationManager.createNotificationChannel(channel);
                                    mNotificationManager.notify(0, mBuilder.build());
                                }
                            });
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView DateTxt, Event, Description, Time, Feedback;
        Button delete;
        ImageButton setAlarm;
        ImageButton setProgress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DateTxt = itemView.findViewById(R.id.eventdate);
            Event = itemView.findViewById(R.id.eventname);
            Description = itemView.findViewById(R.id.event_description);
            Time = itemView.findViewById(R.id.eventime);
            delete = itemView.findViewById(R.id.delete);
            setAlarm = itemView.findViewById(R.id.alarmmeBtn);
            setProgress = itemView.findViewById(R.id.progressBtn);
            Feedback = itemView.findViewById(R.id.feedback_text);
        }
    }

    private Date ConvertStringToDate(String eventDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private Date ConvertStringToTime(String eventDate) {
        SimpleDateFormat format = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private void deleteCalendarEvent(String event, String date, String time) {
        FirebaseDatabase mRootNode = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
        FirebaseHelper.DeleteEvent(mDatabaseReference, date, event, time);
    }

    private void isAlarmed(String date, String event, String time, FirebaseCallback callback) {
        FirebaseDatabase mRootNode = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
        FirebaseHelper.ReadIDEvents(mDatabaseReference, date, event, time, callback);
    }


    private void setAlarm(Calendar calendar, String event, String time, int RequestCOde) {
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("event", event);
        intent.putExtra("time", time);
        intent.putExtra("id", RequestCOde);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCOde, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm(int RequestCode) {
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void getRequestCode(String date, String event, String time, FirebaseCallback callback) {
        FirebaseDatabase mRootNode = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
        FirebaseHelper.ReadIDEvents(mDatabaseReference, date, event, time, callback);
    }

    private void updateEvent(String date, String event, String time, String notify, FirebaseCallback.FirebaseFinishListener listener) {

        FirebaseDatabase mRootNode = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
        FirebaseHelper.UpdateEvent(mDatabaseReference, date, event, time, notify, listener);
    }

    private void updateEvent_2(String date, String event, String time, String progress, FirebaseCallback.FirebaseFinishListener listener) {
        FirebaseDatabase mRootNode = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
        FirebaseHelper.UpdateEvent2(mDatabaseReference, date, event, time, progress, listener);
    }

    private void updateEvent_feedback(String date, String event, String time, String feedback, FirebaseCallback.FirebaseFinishListener listener) {
        FirebaseDatabase mRootNode = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
        FirebaseHelper.UpdateEventSaveFeedback(mDatabaseReference, date, event, time, feedback, listener);
    }
}
