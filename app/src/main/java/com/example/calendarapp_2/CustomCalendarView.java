package com.example.calendarapp_2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendarapp_2.firebase.FirebaseCallback;
import com.example.calendarapp_2.firebase.FirebaseHelper;
import com.example.calendarapp_2.firebase.model.EventsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

public class CustomCalendarView extends LinearLayout {
    ImageButton NextButton, PreviousButton;
    TextView CurrentDate;
    GridView gridView;

    Button signOut;

    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormate = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    SimpleDateFormat eventDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    MyGridAdapter myGridAdapter;
    AlertDialog alertDialog;
    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();
    int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinuit;

    MainActivity mMainActivity;
    boolean isAdmin;

    FirebaseDatabase mRootNode;
    DatabaseReference mDatabaseReference;

    public CustomCalendarView(Context context) {
        super(context);
    }

    private void addEvent(AdapterView<?> parent, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        final View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_newevent_layout, null);
        final EditText EventName = addView.findViewById(R.id.eventname);
        final TextView EventsTime = addView.findViewById(R.id.eventtime);
        final TextView EventsDescription = addView.findViewById(R.id.event_description);
        ImageButton SetTime = addView.findViewById(R.id.seteventtime);
        final CheckBox alarmMe = addView.findViewById(R.id.alarmme);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(dates.get(position));
        alarmYear = dateCalendar.get(Calendar.YEAR);
        alarmMonth = dateCalendar.get(Calendar.MONTH);
        alarmDay = dateCalendar.get(Calendar.DAY_OF_MONTH);


        Button AddEvent = addView.findViewById(R.id.addevent);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minuts = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog
                        , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        c.setTimeZone(TimeZone.getDefault());
                        SimpleDateFormat hformate = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                        String event_Time = hformate.format(c.getTime());
                        EventsTime.setText(event_Time);
                        alarmHour = c.get(Calendar.HOUR_OF_DAY);
                        alarmMinuit = c.get(Calendar.MINUTE);


                    }
                }, hours, minuts, false);
                timePickerDialog.show();
            }
        };


        SetTime.setOnClickListener(listener);


        final String date = eventDateFormate.format(dates.get(position));
        final String month = monthFormat.format(dates.get(position));
        final String year = yearFormate.format(dates.get(position));

        AddEvent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final String event = EventName.getText().toString();
                String description = EventsDescription.getText().toString();
                String time = EventsTime.getText().toString();


                if (alarmMe.isChecked()) {

                    mRootNode = FirebaseDatabase.getInstance();
                    mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
                    String id = getRandomId();
                    FirebaseHelper.SaveEvent(mDatabaseReference, id, event, description, time, date, month, year, "off", "on");

                    SetUpCalendar();
                    final Calendar calendar = Calendar.getInstance();
                    calendar.set(alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinuit);

                    getRequestCode(date, EventName.getText().toString(), EventsTime.getText().toString(), new FirebaseCallback() {
                        @Override
                        public void onSuccess(Events events) {
                            int code = Integer.parseInt(events.getID());
                            setAlarm(calendar, EventName.getText().toString(), EventsTime.getText().toString(), code);
                            alertDialog.dismiss();
                        }
                    });


                } else {

                    mRootNode = FirebaseDatabase.getInstance();
                    mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
                    String id = getRandomId();
                    FirebaseHelper.SaveEvent(mDatabaseReference, id, event, description, time, date, month, year, "off", "on");

                    SetUpCalendar();
                    alertDialog.dismiss();
                }


            }
        });

        builder.setView(addView);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private static String getRandomId(){
        Random random = new Random();
        int number = random.nextInt(9999999);
        return String.format("%06d", number);
    }

    public CustomCalendarView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        InitializeLayout();
        SetUpCalendar();

        PreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                SetUpCalendar();
            }
        });

        NextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                SetUpCalendar();
            }
        });

        signOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.startActivity(new Intent(mMainActivity, Login.class));
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isAdmin) {
                    addEvent(parent, position);
                }
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String date = eventDateFormate.format(dates.get(position));
                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout, null);
                RecyclerView recyclerView = showView.findViewById(R.id.EventsRV);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setView(showView);
                alertDialog = builder.create();

                CollectEventByDate(date, showView, recyclerView, alertDialog);
                return true;
            }
        });

    }

    private void getRequestCode(String date, final String event, String time, FirebaseCallback callback) {
        mRootNode = FirebaseDatabase.getInstance();
        mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
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

    private void CollectEventByDate(final String date, final View showView, final RecyclerView recyclerView,
                                    final AlertDialog alertDialog) {
        final ArrayList<Events> arrayList = new ArrayList<>();
        mRootNode = FirebaseDatabase.getInstance();
        mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
        FirebaseHelper.ReadEvents(mDatabaseReference, date, new FirebaseCallback() {
            @Override
            public void onSuccess(Events events) {
                arrayList.add(events);
            }
        }, new FirebaseCallback.FirebaseFinishListener() {
            @Override
            public void onFinish() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(), arrayList);
                        recyclerView.setAdapter(eventRecyclerAdapter);
                        eventRecyclerAdapter.notifyDataSetChanged();
                        eventRecyclerAdapter.mainActivity = mMainActivity;
                        alertDialog.show();
                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                SetUpCalendar();
                            }
                        });
                    }
                });
            }
        });
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, Context context1) {
        super(context, attrs, defStyleAttr);

    }

    private void SaveEvent(String event, String description, String time, String date, String month, String year, String progress, String notify) {

//        dbOpenHelper = new DBOpenHelper(context);
//        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
//        dbOpenHelper.SaveEvent(event, description, time, date, month, year, progress, notify, database);
//        dbOpenHelper.close();
//        Toast.makeText(context, "Event Saved", Toast.LENGTH_SHORT).show();

    }

    private void InitializeLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        NextButton = view.findViewById(R.id.nextBtn);
        PreviousButton = view.findViewById(R.id.previousBtn);
        CurrentDate = view.findViewById(R.id.current_Date);
        gridView = view.findViewById(R.id.gridview);
        signOut = view.findViewById(R.id.logout_button);
    }


    private void SetUpCalendar() {
        String currwntDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currwntDate);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int FirstDayofMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayofMonth);
        CollectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormate.format(calendar.getTime()), monthCalendar);

    }

    private void CollectEventsPerMonth(String Month, String year, final Calendar monthCalendar) {
        eventsList.clear();
        mRootNode = FirebaseDatabase.getInstance();
        mDatabaseReference = mRootNode.getReference(FirebaseHelper.EVENTS_REFERENCE);
        FirebaseHelper.ReadEventsPerMonth(mDatabaseReference, Month, year, new FirebaseCallback() {
            @Override
            public void onSuccess(Events events) {
                eventsList.add(events);
            }

        }, new FirebaseCallback.FirebaseFinishListener() {
            @Override
            public void onFinish() {
                while (dates.size() < MAX_CALENDAR_DAYS) {
                    dates.add(monthCalendar.getTime());
                    monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                myGridAdapter = new MyGridAdapter(context, dates, calendar, eventsList);
                gridView.setAdapter(myGridAdapter);
            }
        });
    }


}
