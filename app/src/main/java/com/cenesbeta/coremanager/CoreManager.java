package com.cenesbeta.coremanager;

import com.cenesbeta.Manager.AlertManager;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.Impl.AlertManagerImpl;
import com.cenesbeta.Manager.DeviceManager;
import com.cenesbeta.Manager.Impl.ApiManagerImpl;
import com.cenesbeta.Manager.Impl.DeviceManagerImpl;
import com.cenesbeta.Manager.Impl.InternetManagerImpl;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.Impl.ValidatioManagerImpl;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.Manager.ValidationManager;
import com.cenesbeta.api.CenesCommonAPI;
import com.cenesbeta.api.GatheringAPI;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.CenesCommonAPIManager;
import com.cenesbeta.backendManager.FriendApiManager;
import com.cenesbeta.backendManager.GatheringApiManager;
import com.cenesbeta.backendManager.HomeScreenApiManager;
import com.cenesbeta.backendManager.LocationApiManager;
import com.cenesbeta.backendManager.MeTimeApiManager;
import com.cenesbeta.backendManager.NotificationAPiManager;
import com.cenesbeta.backendManager.UserApiManager;
import com.cenesbeta.database.impl.AlarmManagerImpl;
import com.cenesbeta.database.impl.UserManagerImpl;
import com.cenesbeta.database.manager.AlarmManager;
import com.cenesbeta.database.manager.UserManager;

/**
 * Created by puneet on 11/8/17.
 */

public class CoreManager {
    CenesApplication cenesApplication = null;

    public UserApiManager userAppiManager = null;
    public AlarmManager alarmManager = null;
    public InternetManager internetManager = null;
    public ValidationManager validatioManager=null;
    public AlertManager alertManager = null;
    public UrlManager urlManager = null;
    public DeviceManager deviceManager = null;
    public ApiManager apiManager = null;
    public UserManager userManager = null;
    public HomeScreenApiManager homeScreenApiManager = null;
    public LocationApiManager locationApiManager = null;
    public GatheringApiManager gatheringApiManager = null;
    public NotificationAPiManager notificationAPiManager = null;
    public MeTimeApiManager meTimeApiManager = null;
    public CenesCommonAPIManager cenesCommonAPIManager = null;
    public FriendApiManager friendAPIManager = null;

    public CoreManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        this.userManager = new UserManagerImpl(cenesApplication);
        this.alarmManager = new AlarmManagerImpl(cenesApplication);
        this.internetManager = new InternetManagerImpl(cenesApplication);
        this.validatioManager = new ValidatioManagerImpl(cenesApplication);
        this.alertManager = new AlertManagerImpl(cenesApplication);
        this.urlManager = new UrlManagerImpl(cenesApplication);
        this.deviceManager = new DeviceManagerImpl(cenesApplication);
        this.apiManager = new ApiManagerImpl(cenesApplication);
        this.userAppiManager = new UserApiManager(cenesApplication);
        this.homeScreenApiManager = new HomeScreenApiManager(cenesApplication);
        this.locationApiManager = new LocationApiManager(cenesApplication);
        this.gatheringApiManager = new GatheringApiManager(cenesApplication);
        this.notificationAPiManager = new NotificationAPiManager(cenesApplication);
        this.meTimeApiManager = new MeTimeApiManager(cenesApplication);
        this.cenesCommonAPIManager = new CenesCommonAPIManager(cenesApplication);
        this.friendAPIManager = new FriendApiManager(cenesApplication);
    }

    public UserManager getUserManager(){
        return this.userManager;
    }
    public AlarmManager getAlarmManager(){
        return this.alarmManager;
    }

    public InternetManager getInternetManager(){
        return this.internetManager;
    }

    public ValidationManager getValidatioManager(){
        return this.validatioManager;
    }

    public AlertManager getAlertManager(){
        return this.alertManager;
    }

    public UrlManager getUrlManager(){
        return this.urlManager;
    }

    public DeviceManager getDeviceManager(){
        return this.deviceManager;
    }

    public ApiManager getApiManager(){
        return this.apiManager;
    }

    public UserApiManager getUserAppiManager() {
        return this.userAppiManager;
    }

    public HomeScreenApiManager getHomeScreenApiManager() {
        return  this.homeScreenApiManager;
    }

    public LocationApiManager getLocationApiManager() {
        return this.locationApiManager;
    }

    public GatheringApiManager getGatheringApiManager() {
        return this.gatheringApiManager;
    }

    public NotificationAPiManager getNotificationAPiManager() {
        return this.notificationAPiManager;
    }

    public MeTimeApiManager getMeTimeApiManager() {
        return this.meTimeApiManager;
    }

    public CenesCommonAPIManager getCenesCommonAPIManager() {
        return this.cenesCommonAPIManager;
    }

    public FriendApiManager getFriendAPIManager() {return this.friendAPIManager; }
}
