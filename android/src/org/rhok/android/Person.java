package org.rhok.android;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Person
{
    public final int id;
    public final String name;
    public final long age;
    public final double weight;
    public final String imagePath;
    public final long date;
    public final String capturerName;
    public final double latitude;
    public final double longitude;
    public final int refObjID;
    public final float refObjStartX;
    public final float refObjStartY;
    public final float refObjEndX;
    public final float refObjEndY;
    public final float personStartX;
    public final float personStartY;
    public final float personEndX;
    public final float personEndY;

    public Person(int id, String name, long age, double weight,
            String imagePath, long date, String capturerName, double latitude,
            double longitude, int refObjID, float refObjStartX,
            float refObjStartY, float refObjEndX, float refObjEndY,
            float personStartX, float personStartY, float personEndX,
            float personEndY)
    {
        this.id = id;
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.imagePath = imagePath;
        this.date = date;
        this.capturerName = capturerName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.refObjID = refObjID;
        this.refObjStartX = refObjStartX;
        this.refObjStartY = refObjStartY;
        this.refObjEndX = refObjEndX;
        this.refObjEndY = refObjEndY;
        this.personStartX = personStartX;
        this.personStartY = personStartY;
        this.personEndX = personEndX;
        this.personEndY = personEndY;
    }

    public ArrayList<NameValuePair> toNameValuePairList()
    {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("id", String.valueOf(id)));
        pairs.add(new BasicNameValuePair("name", name));
        pairs.add(new BasicNameValuePair("age", String.valueOf(age)));
        pairs.add(new BasicNameValuePair("weight", String.valueOf(weight)));
        pairs.add(new BasicNameValuePair("image_path", imagePath));
        pairs.add(new BasicNameValuePair("capture_date", String.valueOf(date)));
        pairs.add(new BasicNameValuePair("capturer_name", capturerName));
        pairs.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
        pairs.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));
        pairs.add(new BasicNameValuePair("ref_obj_id", String.valueOf(refObjID)));
        pairs.add(new BasicNameValuePair("ref_obj_start_x", String.valueOf(refObjStartX)));
        pairs.add(new BasicNameValuePair("ref_obj_start_y", String.valueOf(refObjStartY)));
        pairs.add(new BasicNameValuePair("ref_obj_end_x", String.valueOf(refObjEndX)));
        pairs.add(new BasicNameValuePair("ref_obj_end_y", String.valueOf(refObjEndY)));
        pairs.add(new BasicNameValuePair("person_start_x", String.valueOf(personStartX)));
        pairs.add(new BasicNameValuePair("person_start_y", String.valueOf(personStartY)));
        pairs.add(new BasicNameValuePair("person_end_x", String.valueOf(personEndX)));
        pairs.add(new BasicNameValuePair("person_end_y", String.valueOf(personEndY)));       
        return pairs;
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double xDelta = x2 - x1;
        double yDelta = y2 - y1;

        return Math.sqrt(xDelta * xDelta + yDelta * yDelta);
    }

    public static double height(RefObj refObj, float refObjStartX,
            float refObjStartY, float refObjEndX, float refObjEndY,
            float personStartX, float personStartY, float personEndX,
            float personEndY)
    {

        double refLenPx = distance(refObjStartX, refObjStartY, refObjEndX,
                refObjEndY);
        double personLenPx = distance(personStartX, personStartY, personEndX,
                personEndY);
        double ratioCmPx = refObj.length / refLenPx;
        double personLenCm = personLenPx * ratioCmPx;

        return personLenCm;
    }

    @Override
    public String toString()
    {
        return "Person [capturerName=" + capturerName + ", date=" + date
                + ", id=" + id + ", imagePath=" + imagePath + ", latitude="
                + latitude + ", longitude=" + longitude + ", name=" + name
                + ", personEndX=" + personEndX + ", personEndY=" + personEndY
                + ", personStartX=" + personStartX + ", personStartY="
                + personStartY + ", refObjEndX=" + refObjEndX + ", refObjEndY="
                + refObjEndY + ", refObjID=" + refObjID + ", refObjStartX="
                + refObjStartX + ", refObjStartY=" + refObjStartY + ", weight="
                + weight + "]";
    }
}
