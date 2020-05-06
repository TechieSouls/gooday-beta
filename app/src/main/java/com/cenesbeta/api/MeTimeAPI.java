package com.cenesbeta.api;

/**
 * Created by mandeep on 10/1/19.
 */

public class MeTimeAPI {

    //POST
    public static String post_metimeData = "/api/user/metime";


    //POST
    public static String post_metimePhoto = "/api/recurring/upload";

    //GET
    public static String get_metimeData = "/api/user/getmetimes";
    public static String get_metimeDataV2 = "/api/recurring/byCreatdById";

    //PUT
    public static String put_deleteByRecurringId = "/api/user/metime/deleteByRecurringId";

}
