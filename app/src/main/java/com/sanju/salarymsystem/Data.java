package com.sanju.salarymsystem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Data {
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat timeFormat;
    int pay, otPay;
    int[] cRegularHoursWorked;
    int[] cOtHoursWorked;
    boolean isHoursCalculated;
    private Date start, end;
    private long id, day, inTime, outTime;
    private double payPerHour;
    private double totalPay;

    public Data(long id, long day, long inTime, long outTime, int pay, int otPay) {
        this.id = id;
        this.day = day;
        this.inTime = inTime;
        this.outTime = outTime;
        this.pay = pay;
        this.otPay = otPay;

        dateFormat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);
        timeFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);

        start = new Date(inTime);
        end = new Date(outTime);

        payPerHour = pay * 1.0 / 8.00;
        totalPay = 0;
        isHoursCalculated = false;
    }

    private static int[] getHoursAndMinutes(long minutes) {
        int[] data = new int[]{0, 0};
        if (minutes < -1) {
            return data;
        }
        data[0] = (int) minutes / 60;
        data[1] = (int) minutes % 60;
        return data;
    }

    public static double getSalaryForOt(int minutes, int pay) {
        double salary = 0;
        salary += (minutes / 60) * pay;
        return salary;
    }

    public int getOtPay() {
        return otPay;
    }

    public void setOtPay(int otPay) {
        this.otPay = otPay;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInTime() {
        return inTime;
    }

    public void setInTime(long inTime) {
        this.inTime = inTime;
    }

    public long getOutTime() {
        return outTime;
    }

    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public String getDateForView() {
        Date date = new Date(day);
        String formatted = dateFormat.format(date.getTime());
        return formatted;
    }

    public String getInTimeForView() {
        String formatted = "In Time : " + timeFormat.format(start.getTime());
        return formatted;
    }

    public String getOutTimeForView() {

        String formatted = "Out Time : " + timeFormat.format(end.getTime());
        return formatted;
    }

    public int[][] getRegularAndOtHours() {
        if (!isHoursCalculated) {
            long difference = end.getTime() - start.getTime();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
            cRegularHoursWorked = new int[]{0, 0};
            cOtHoursWorked = new int[]{0, 0};
            if (minutes < 240) {
                cRegularHoursWorked = getHoursAndMinutes(0);
                cOtHoursWorked = getHoursAndMinutes(minutes);
            } else if (minutes == 240) {
                cRegularHoursWorked = getHoursAndMinutes(240);
                cOtHoursWorked = getHoursAndMinutes(0);
            } else if (minutes == 480) {
                cRegularHoursWorked = getHoursAndMinutes(480);
                cOtHoursWorked = getHoursAndMinutes(0);
            } else if (minutes > 480) {
                cRegularHoursWorked = getHoursAndMinutes(480);
                minutes = minutes - 480;
                cOtHoursWorked = getHoursAndMinutes(minutes);
            } else if (minutes > 240 && minutes < 480) {
                cRegularHoursWorked = getHoursAndMinutes(240);
                minutes = minutes - 240;
                cOtHoursWorked = getHoursAndMinutes(minutes);
            }
            isHoursCalculated = true;
        }
        int[][] hours;
        hours = new int[2][];
        hours[0] = new int[2];
        hours[1] = new int[2];
        hours[0] = cRegularHoursWorked;
        hours[1] = cOtHoursWorked;
        return hours;
    }

    public double getTotalPay() {
        getRegularAndOtHours();
        totalPay = totalPay + (cRegularHoursWorked[0] * payPerHour);
        totalPay = totalPay + (cOtHoursWorked[0] * otPay);
        return totalPay;
    }

}
