package com.travelquest.travelquest.database_handler;

public class API {

    private static final String ROOT_URL = "https://travelquest.000webhostapp.com/v1/API.php?apicall=";

    public static final String URL_CREATE_USER = ROOT_URL + "createuser";
    public static final String URL_USER_EXISTS = ROOT_URL + "userexists";
    public static final String URL_READ_USERS = ROOT_URL + "getusers";
    public static final String URL_DELETE_HERO = ROOT_URL + "deletehero&id=";

    public static final String URL_CREATE_USER_PREFERENCES = ROOT_URL + "createuserpreferences";
    public static final String URL_UPDATE_USER_PREFERENCES = ROOT_URL + "updateuserpreferences";
    public static final String URL_UPDATE_USER_INFORMATION = ROOT_URL + "updateuserinformation";

    public static final String URL_GET_POIS = ROOT_URL + "getpois";
    public static final String URL_GET_USER_POIS = ROOT_URL + "getuserpois";
    public static final String URL_ADD_USER_POI = ROOT_URL + "adduserpoi";
    public static final String POST = "post";
    public static final String GET = "get";



}