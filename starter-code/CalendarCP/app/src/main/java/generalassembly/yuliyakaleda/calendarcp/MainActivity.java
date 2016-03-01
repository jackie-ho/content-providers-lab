package generalassembly.yuliyakaleda.calendarcp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ga.contentproviders";
    private EditText title;
    private EditText description;
    private EditText location;
    private Button getEvents;
    private Button addEvent;
    private Button updateEvent;
    private Button deleteEvent;
    private ListView lv;

    private long eventId;

    private String[] calendarColumns = {CalendarContract.Events._ID, CalendarContract.Events.TITLE};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        location = (EditText) findViewById(R.id.location);
        getEvents = (Button) findViewById(R.id.get_events);
        addEvent = (Button) findViewById(R.id.add_event);
        deleteEvent = (Button) findViewById(R.id.delete_event);
        updateEvent = (Button) findViewById(R.id.update_event);
        lv = (ListView) findViewById(R.id.lv);

        addEvent.setOnClickListener(this);
        getEvents.setOnClickListener(this);
        deleteEvent.setOnClickListener(this);
        updateEvent.setOnClickListener(this);
        fetchCalendars();
    }

    
    public void fetchCalendars() {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] columns = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT
        };

        Cursor cursor = getContentResolver().query(
                uri,
                columns,
                CalendarContract.Calendars.ACCOUNT_NAME + " = ?",
                //TODO: insert your email address that will be associated with the calendar

                new String[]{"your.email@gmail.com"},

                null
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID));
            String accountName = cursor.getString(1);
            String displayName = cursor.getString(2);
            String owner = cursor.getString(3);
            Log.d("ContentProvider", "ID: " + id +
                            ", account: " + accountName +
                            ", displayName: " + displayName +
                            ", owner: " + owner
            );
        }
    }

    public void insertEventInCalendar(String title, String description, String location) {


        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        startTime.set(2016, 3, 1, 9, 0);
        long startMillis = startTime.getTimeInMillis();
        endTime.set(2016, 3, 1, 10, 2);
        long endMillis = endTime.getTimeInMillis();        

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, location);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        eventId = Long.parseLong(uri.getLastPathSegment());
    }


    public void update(String newEvent) {
        //TODO: Using the number eventID from the method insertEventInCalendar(), update the event
        // that was added in that method

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        Uri updateUri = null;

        values.put(CalendarContract.Events.TITLE, "Kickboxing");
        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = getContentResolver().update(updateUri, values, null, null);
        Log.i("UPDATE", "Rows updated: " + rows);


    }

    //This method should return all the events from your calendar from February 29th till March 4th
    // in the year 2016.
    public void fetchEvents() {
        
        // event instances
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2016, 2, 29, 8, 0);
        long startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2016, 3, 4, 8, 0);
        long endMillis = endTime.getTimeInMillis();

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);


        Cursor cursor = null;
        ContentResolver cr = getContentResolver();

        cursor = cr.query(builder.build(), calendarColumns, null, null, "DESC");


        ListAdapter listAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_expandable_list_item_2,
                cursor,
                new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE},
                new int[]{android.R.id.text1, android.R.id.text2},
                0
        );

        lv.setAdapter(listAdapter);
    }



    public void delete() {
        //TODO: Using the number eventID from the method insertEventInCalendar(), delete the event
        // that was added in that method

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = getContentResolver().delete(deleteUri, null, null);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_event:
                String titleString = title.getText().toString();
                String descriptionString = description.getText().toString();
                String locationString = location.getText().toString();
                insertEventInCalendar(titleString, descriptionString, locationString);
                break;
            case R.id.delete_event:
                delete();
                break;
            case R.id.update_event:

                update(title.getText().toString());
                break;
            case R.id.get_events:
                fetchEvents();
                break;
            default:
                break;
        }
    }
}


