package com.h2m.alarm.application;

import com.h2m.alarm.model.AlarmClock;
import com.h2m.alarm.presentation.console.AlarmToConsole;
import com.h2m.alarm.presentation.file.AlarmToFile;

public final class Main
{
    public static void main(String[] args)
    {
        AlarmToConsole alarmToConsole = new AlarmToConsole();
        AlarmToFile alarmToFile = new AlarmToFile();
        AlarmClock alarmClock = new AlarmClock();
        alarmClock.addObserver(alarmToConsole, AlarmClock.ALARM_EVENT);
        alarmClock.addObserver(alarmToFile, AlarmClock.ALARM_EVENT);
        new Thread(alarmClock).run();
    }
}