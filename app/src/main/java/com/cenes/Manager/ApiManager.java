package com.cenes.Manager;

import android.support.v7.app.AppCompatActivity;

import com.cenes.bo.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Created by puneet on 15/8/17.
 */

public interface ApiManager {

    public JSONObject signUpByEmail(User user, AppCompatActivity appCompatActivity);
    public JSONObject logIn(User user, AppCompatActivity appCompatActivity);
    public JSONObject postMultipart(User user, File file, AppCompatActivity appCompatActivity);
    public JSONArray syncFacebookEvents(User user, AppCompatActivity appCompatActivity);
    public JSONArray googleEvents(User user, String queryStr, AppCompatActivity activity);
    public JSONObject getUserEvents(User user, String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject meTime(User user,JSONObject postDataJson,AppCompatActivity appCompatActivity);
    public JSONObject locationSearch(String queryString);
    public JSONObject locationLatLng(String queryString);
    public JSONArray predictiveCalendar(User user,String queryString,AppCompatActivity appCompatActivity);
    public JSONArray searchFriends(User user,String queryString,AppCompatActivity appCompatActivity);
    public JSONObject postMultipartEventImage(User user, File file, AppCompatActivity appCompatActivity);
    public JSONObject createGathering(User user, JSONObject eventObj,AppCompatActivity appCompatActivity);
    public JSONArray syncOutlookEvents(User user,String queryStr,AppCompatActivity activity);
    public JSONArray syncHolidayCalendar(User user,String queryStr,AppCompatActivity activity);
    public JSONObject getUserGatherings(User user,String queryStr,AppCompatActivity activity);
    public JSONObject saveReminder(User user,JSONObject jsonObject,AppCompatActivity activity);
    public JSONObject getReminders(User user,String  queryStr,AppCompatActivity activity);
    public JSONObject getUserHolidays(User user,String  queryStr,AppCompatActivity activity);
    public JSONObject syncDeviceToekn(User user,JSONObject postDataJson,AppCompatActivity appCompatActivity);
    public JSONObject getEventById(User user,Long eventId,AppCompatActivity appCompatActivity);
    public JSONObject getNotificationsByUserId(User user,String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject updateReminderToFinish(User user,String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject updateReminderInvitation(User user,String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject deleteEventById(User user,String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject updateInvitation(User user, String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject getInvitationById(User user, String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject deleteReminderById(User user,String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject addDiary(User user,JSONObject postDataJson,AppCompatActivity appCompatActivity);
    public JSONObject getAllUserDiaries(User user,String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject postDiaryMedia(User user, File file, AppCompatActivity appCompatActivity);
    public JSONObject deleteDiaryById(User user,String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject updateUserInfo(User user,JSONObject postDataJson,AppCompatActivity appCompatActivity);
    public JSONObject getUserCalendarSyncStatus(User user,int userId,AppCompatActivity appCompatActivity);
    public JSONObject getUserMeTimeData(User user,String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject logout(User user,String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject forgotPassword(String api,String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject getNotificationCounts(User user,String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject markNotificationAsReadByUserIdAndNotificatonId(User user, String queryStr,AppCompatActivity appCompatActivity);
    public JSONObject syncDeviceConcats(User user, JSONObject postData,AppCompatActivity appCompatActivity);
    }
