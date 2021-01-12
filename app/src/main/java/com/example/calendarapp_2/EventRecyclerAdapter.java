package com.example.calendarapp_2;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<Events> arrayList;
    DBOpenHelper dbOpenHelper;
    MainActivity mainActivity;

    public EventRecyclerAdapter(Context context, ArrayList<Events> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_rowlayout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Events events = arrayList.get(position);
        holder.Event.setText(events.getEVENT());
        holder.Description.setText(events.getDESCRIPTION());
        holder.DateTxt.setText(events.getDATE());
        holder.Time.setText(events.getTIME());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCalendarEvent(events.getEVENT(), events.getDATE(), events.getTIME());
                arrayList.remove(position);
                notifyDataSetChanged();

            }
        });

        if (isAarmed(events.getDATE(), events.getEVENT(), events.getTIME())) {
            holder.setAlarm.setImageResource(R.drawable.ic_action_notification_on);
        } else {
            holder.setAlarm.setImageResource(R.drawable.ic_action_notification_off);
        }
        if (isAarmed_2(events.getDATE(), events.getEVENT(), events.getTIME())) {
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
                if (isAarmed(events.getDATE(), events.getEVENT(), events.getTIME())) {
                    holder.setAlarm.setImageResource(R.drawable.ic_action_notification_off);
                    cancelAlarm(getRequestCode(events.getDATE(), events.getEVENT(), events.getTIME()));
                    updateEvent(events.getDATE(), events.getEVENT(), events.getTIME(), "off");
                    notifyDataSetChanged();

                } else {
                    holder.setAlarm.setImageResource(R.drawable.ic_action_notification_on);
                    Calendar alarmCalendar = Calendar.getInstance();
                    alarmCalendar.set(alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinuit);
                    setAlarm(alarmCalendar, events.getEVENT(), events.getTIME(), getRequestCode(events.getDATE(),
                            events.getEVENT(), events.getTIME()));
                    updateEvent(events.getDATE(), events.getEVENT(), events.getTIME(), "on");
                    notifyDataSetChanged();

                }
            }
        });
        holder.setProgress.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (isAarmed_2(events.getDATE(), events.getEVENT(), events.getTIME())) {
                    holder.setProgress.setImageResource(R.drawable.ic_action_progress_off);
                    updateEvent_2(events.getDATE(), events.getEVENT(), events.getTIME(), "off");
                    notifyDataSetChanged();

                } else {
                    holder.setProgress.setImageResource(R.drawable.ic_action_progress_on);
                    updateEvent_2(events.getDATE(), events.getEVENT(), events.getTIME(), "on");
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


            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView DateTxt, Event, Description, Time;
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
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.deleteEvent(event, date, time, database);
        dbOpenHelper.close();

    }

    private boolean isAarmed(String date, String event, String time) {
        boolean alarmed = false;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents(date, event, time, database);
        while (cursor.moveToNext()) {
            String notify = cursor.getString(cursor.getColumnIndex(DBStructure.Notify));
            if (notify.equals("on")) {
                alarmed = true;
            } else {
                alarmed = false;
            }
        }
        cursor.close();
        dbOpenHelper.close();
        return alarmed;
    }

    private boolean isAarmed_2(String date, String event, String time) {
        boolean alarmed_2 = false;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents_2(date, event, time, database);
        while (cursor.moveToNext()) {
            String progress = cursor.getString(cursor.getColumnIndex(DBStructure.Progress));
            if (progress.equals("on")) {
                alarmed_2 = true;
            } else {
                alarmed_2 = false;
            }
        }
        cursor.close();
        dbOpenHelper.close();
        return alarmed_2;
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

    private int getRequestCode(String date, String event, String time) {
        int code = 0;
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        Cursor cursor = dbOpenHelper.ReadIDEvents(date, event, time, database);
        while (cursor.moveToNext()) {
            code = cursor.getInt(cursor.getColumnIndex(DBStructure.ID));
        }
        cursor.close();
        dbOpenHelper.close();

        return code;
    }

    private void updateEvent(String date, String event, String time, String notify) {
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.updateEvent(date, event, time, notify, database);
        dbOpenHelper.close();
    }

    private void updateEvent_2(String date, String event, String time, String progress) {
        dbOpenHelper = new DBOpenHelper(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.updateEvent_2(date, event, time, progress, database);
        dbOpenHelper.close();
    }


}
