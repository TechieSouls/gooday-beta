package com.cenes.coremanager;

import com.cenes.Manager.AlertManager;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.Impl.AlertManagerImpl;
import com.cenes.Manager.DeviceManager;
import com.cenes.Manager.Impl.ApiManagerImpl;
import com.cenes.Manager.Impl.DeviceManagerImpl;
import com.cenes.Manager.Impl.InternetManagerImpl;
import com.cenes.Manager.Impl.UrlManagerImpl;
import com.cenes.Manager.Impl.ValidatioManagerImpl;
import com.cenes.Manager.InternetManager;
import com.cenes.Manager.UrlManager;
import com.cenes.Manager.ValidationManager;
import com.cenes.api.CenesCommonAPI;
import com.cenes.api.GatheringAPI;
import com.cenes.application.CenesApplication;
import com.cenes.backendManager.CenesCommonAPIManager;
import com.cenes.backendManager.GatheringApiManager;
import com.cenes.backendManager.HomeScreenApiManager;
import com.cenes.backendManager.LocationApiManager;
import com.cenes.backendManager.MeTimeApiManager;
import com.cenes.backendManager.NotificationAPiManager;
import com.cenes.backendManager.UserApiManager;
import com.cenes.database.impl.AlarmManagerImpl;
import com.cenes.database.impl.UserManagerImpl;
import com.cenes.database.manager.AlarmManager;
import com.cenes.database.manager.UserManager;

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
}
