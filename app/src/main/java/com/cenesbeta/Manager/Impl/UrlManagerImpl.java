package com.cenesbeta.Manager.Impl;

import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.application.CenesApplication;

/**
 * Created by puneet on 11/8/17.
 */

public class UrlManagerImpl implements UrlManager {

    CenesApplication cenesApplication;
    //String url = "http://cenes.test2.redblink.net";
    String url = "http://ec2-18-216-7-227.us-east-2.compute.amazonaws.com";

    String localApiUrl=url;
    String devApiUrl=url;//http://cenes.test2.redblink.net";
    String stageApiUrl=url;
    //public static String prodAPIUrl = "http://ec2-18-216-7-227.us-east-2.compute.amazonaws.com";
    //public static String prodAPIUrl = "https://api.cenesgroup.com";


    public static String prodAPIUrl = "https://api2.cenesgroup.com";
    public static String prodImageApiDomain = "https://images.cenesgroup.com";

    public UrlManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    @Override
    public String getApiUrl(String input){
        input = input.trim();
        int end = 0, flag = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ':') {
                flag = flag + 1;
                end = i;
            }
        }
        if (flag == 1) {
            if (input.substring(0, end).equalsIgnoreCase("local")) {
                return localApiUrl;
            }
            if (input.substring(0, end).equalsIgnoreCase("dev")) {
                return devApiUrl;
            }
        }

        return stageApiUrl;
    }
}
