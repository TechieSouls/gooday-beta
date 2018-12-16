package com.cenes.database.manager;

import com.cenes.bo.Reminder;

import java.util.List;

/**
 * Created by mandeep on 25/11/17.
 */


public interface ReminderManager {
    public void addReminder(Reminder reminder);
    public Reminder findReminderByReminderId(Long reminderId);
    public List<Reminder> getAllReminders();
    public void updateReminder(Reminder reminder);
    public void deleteReminderById(Long reminderId);
}
