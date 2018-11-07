package com.xiongms.libcore.da;

import java.util.Map;

/**
 *
 */
public class Event {

    private int Position;
    private String SourceData;
    private String time;

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        this.Position = position;
    }

    public String getSourceData() {
        return SourceData;
    }

    public void setSourceData(String sourceData) {
        SourceData = sourceData;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
