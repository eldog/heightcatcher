package org.rhok.android;

public class RefObj
{
    public final int id;
    public final String name;
    public final double length;
    public final String imagePath;

    @Override
    public String toString()
    {
        return name + " " + length + "cm";
    }

    public RefObj(int id, String name, double length, String imagePath)
    {
        this.id = id;
        this.name = name;
        this.length = length;
        this.imagePath = imagePath;
    }
}
