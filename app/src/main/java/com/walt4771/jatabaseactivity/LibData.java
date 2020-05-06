package com.walt4771.jatabaseactivity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LibData {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    public String tableid;
    public String All;
    public String Using;
    public String Remaining;
    public String Usage;
    public String Waiting;
    public String Calling;
    public String Scheduled;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTableid() { return tableid; }
    public void setTableid(String tableid) { this.tableid = tableid; }

    public String getAll() { return All; }
    public void setAll(String all) { All = all; }

    public String getUsing() { return Using; }
    public void setUsing(String using) { Using = using; }

    public String getRemaining() { return Remaining; }
    public void setRemaining(String remaining) { Remaining = remaining; }

    public String getUsage() { return Usage; }
    public void setUsage(String usage) { Usage = usage; }

    public String getWaiting() { return Waiting; }
    public void setWaiting(String waiting) { Waiting = waiting; }

    public String getCalling() { return Calling; }
    public void setCalling(String calling) { Calling = calling; }

    public String getScheduled() { return Scheduled; }
    public void setScheduled(String scheduled) { Scheduled = scheduled; }

    @Override
    public String toString() {
        return "LibData{" +
                "id=" + id +
                ", tableid='" + tableid + '\'' +
                ", All='" + All + '\'' +
                ", Using='" + Using + '\'' +
                ", Remaining='" + Remaining + '\'' +
                ", Usage='" + Usage + '\'' +
                ", Waiting='" + Waiting + '\'' +
                ", Calling='" + Calling + '\'' +
                ", Scheduled='" + Scheduled + '\'' +
                '}';
    }
}
