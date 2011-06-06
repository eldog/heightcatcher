package org.rhok.android;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HeightCatcherDB
{
    public static final String REFOBJ_ID = "id";
    public static final String REFOBJ_NAME = "name";
    public static final String REFOBJ_LENGTH = "length";
    public static final String REFOBJ_IMAGE_PATH = "image_path";

    public static final String PERSON_ID = "id";
    public static final String PERSON_NAME = "name";
    public static final String PERSON_WEIGHT = "weight";
    public static final String PERSON_IMAGE_PATH = "image_path";
    public static final String PERSON_CAPTURE_DATE = "capture_date";
    public static final String PERSON_CAPTURER_NAME = "capturer_name";
    public static final String PERSON_LATITUDE = "latitude";
    public static final String PERSON_LONGITUDE = "longitude";
    public static final String PERSON_REFOBJ_ID = "refobj_id";
    public static final String PERSON_REFOBJ_START_X = "refobj_start_x";
    public static final String PERSON_REFOBJ_START_Y = "refobj_start_y";
    public static final String PERSON_REFOBJ_END_X = "refobj_end_x";
    public static final String PERSON_REFOBJ_END_Y = "refobj_end_y";
    public static final String PERSON_PERSON_START_X = "person_start_x";
    public static final String PERSON_PERSON_START_Y = "person_start_y";
    public static final String PERSON_PERSON_END_X = "person_end_x";
    public static final String PERSON_PERSON_END_Y = "person_end_y";

    public static final String REFOBJS_TABLE_NAME = "refobjs";
    public static final String PEOPLE_TABLE_NAME = "people";

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "health_catcher.db";
        private static final int DATABASE_VERSION = 5;

        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("CREATE TABLE  " + REFOBJS_TABLE_NAME + "(" + REFOBJ_ID
                    + " INTEGER PRIMARY KEY," + REFOBJ_NAME + " TEXT UNIQUE,"
                    + REFOBJ_LENGTH + " REAL," + REFOBJ_IMAGE_PATH + " TEXT"
                    + ");");

            db.execSQL("CREATE TABLE " + PEOPLE_TABLE_NAME + "(" + PERSON_ID
                    + " INTEGER PRIMARY KEY," + PERSON_NAME
                    + " TEXT, age INTEGER, " + PERSON_WEIGHT + " REAL,"
                    + PERSON_IMAGE_PATH + " TEXT," + PERSON_CAPTURE_DATE
                    + " INTEGER," + PERSON_CAPTURER_NAME + " TEXT,"
                    + PERSON_LATITUDE + " REAL," + PERSON_LONGITUDE + " REAL,"
                    + PERSON_REFOBJ_ID + " INTEGER," + PERSON_REFOBJ_START_X
                    + " REAL," + PERSON_REFOBJ_START_Y + " REAL,"
                    + PERSON_REFOBJ_END_X + " REAL," + PERSON_REFOBJ_END_Y
                    + " REAL," + PERSON_PERSON_START_X + " REAL,"
                    + PERSON_PERSON_START_Y + " REAL," + PERSON_PERSON_END_X
                    + " REAL," + PERSON_PERSON_END_Y + " REAL,"
                    + "FOREIGN KEY(" + PERSON_REFOBJ_ID + ") REFERENCES "
                    + REFOBJS_TABLE_NAME + "(" + REFOBJ_ID + ")" + ");");
            
            // Insert some default objects
            db.execSQL("INSERT INTO refobjs (name, length, image_path) VALUES ('Ruler', "
                    + "'31', 'skip');");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + PEOPLE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + REFOBJS_TABLE_NAME);
            Log.d("HeightCatcher", "I'm upgrading");
            onCreate(db);
        }

    }

    private DatabaseHelper mOpenHelper;

    public HeightCatcherDB(Context context)
    {
        mOpenHelper = new DatabaseHelper(context);
    }

    public void addRefObj(String name, double length, String imagePath)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.execSQL("INSERT INTO refobjs (name, length, image_path) VALUES (\""
                + name + "\", \"" + length + "\", \"" + imagePath + "\");");
        db.close();
    }

    public ArrayList<RefObj> getRefObjs()
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        ArrayList<RefObj> refObj = new ArrayList<RefObj>();
        Cursor c = db.rawQuery("SELECT * FROM refobjs;", null);

        while (c.move(1))
        {
            refObj.add(new RefObj(c.getInt(0), c.getString(1), c.getDouble(2),
                    c.getString(3)));
        }
        db.close();

        return refObj;

    }

    public void addPerson(String name, long age, double weight,
            String imagePath, String capturerName, double latitude,
            double longitude, int refObjID, float refObjStartX, float refObjStartY,
            float refObjEndX, float refObjEndY, float personStartX, float personStartY,
            float personEndX, float personEndY)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db
                .execSQL("INSERT INTO people (name, age, weight, image_path, capture_date, "
                        + "capturer_name, latitude, longitude, refobj_id, refobj_start_x,"
                        + "refobj_start_y, refobj_end_x, refobj_end_y, person_start_x, "
                        + "person_start_y, person_end_x, person_end_y) VALUES ("
                        + "\""
                        + name
                        + "\", "
                        + "\""
                        + age
                        + "\", "
                        + "\""
                        + weight
                        + "\", "
                        + "\""
                        + imagePath
                        + "\", "
                        + "\""
                        + System.currentTimeMillis()
                        + "\", "
                        + "\""
                        + capturerName
                        + "\", "
                        + "\""
                        + latitude
                        + "\", "
                        + "\""
                        + longitude
                        + "\", "
                        + "\""
                        + refObjID
                        + "\", "
                        + "\""
                        + refObjStartX
                        + "\", "
                        + "\""
                        + refObjStartY
                        + "\", "
                        + "\""
                        + refObjEndX
                        + "\", "
                        + "\""
                        + refObjEndY
                        + "\", "
                        + "\""
                        + personStartX
                        + "\", "
                        + "\""
                        + personStartY
                        + "\", "
                        + "\""
                        + personEndX
                        + "\", " + "\"" + personEndY + "\");");
        db.close();
    }

    public ArrayList<Person> getPeople()
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM people;", null);

        ArrayList<Person> people = new ArrayList<Person>();

        while (c.move(1))
        {
            people.add(new Person(c.getInt(0), c.getString(1), c.getInt(2), c
                    .getDouble(3), c.getString(4), c.getLong(5),
                    c.getString(6), c.getDouble(7), c.getDouble(8),
                    c.getInt(9), c.getFloat(10), c.getFloat(11), c.getFloat(12), c
                            .getFloat(13), c.getFloat(14), c.getFloat(15), c
                            .getFloat(16), c.getFloat(17)));
        }
        db.close();
        c.close();

        return people;
    }
    
    public void flush()
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM people;");
        db.close();
    }
}