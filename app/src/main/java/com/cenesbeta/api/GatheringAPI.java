package com.cenesbeta.api;

/**
 * Created by mandeep on 5/1/19.
 */

public class GatheringAPI {
    //GET
    public static String get_gatherings = "/api/user/gatherings";  //user_id, status
    public static String get_gathering_data = "/api/event/"; //eventId
    public static String get_gathering_detail = "/api/event/details"; //eventId


    public static String get_update_invitation_api = "/api/event/memberStatusUpdate";
    public static String get_delete_event_api = "/api/event/delete";
    public static String get_predictive_calendar_api = "/api/predictive/calendar/v2";
    public static String get_gathering_by_key_api = "/api/event/invitation/";

    public static String get_place_details_api = "https://maps.googleapis.com/maps/api/place/details/json"; //eventId


    //POST
    public static String post_create_gathering = "/api/event/create"; //Event Data
    public static String post_upload_image = "/api/event/upload"; //File
    public static String post_upload_image_v2 = "/api/event/uploadv2";
    public static String post_event_chat_api = "/api/eventchat/create";
    public static String get_event_chat_api = "/api/eventchat/byEventId";
    public static String post_read_event_chat_status = "/api/eventchat/updateStatus";
}
