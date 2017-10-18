package com.across.senzec.datingapp.manager;

/**
 * Created by power hashing on 4/20/2017.
 */

public class App {

    /* all constant declare here*/
    public static class Key {

        public static final int REQUEST_CODE_SIGN_IN = 2003;
        public static final String IS_LOGGED = "is_login";
        public static final String ID_LOGGED = "id_logged";
        public static final String SPLASH_ONE_TIME = "splash";
        public static final String IS_UPDATE = "is_update";
        public static final String LOGIN_TYPE_MEDIA = "login_type_media";
        public static final String REGISTER_USER_TOKEN = "register_user_token";
        public static final String DIVICE_TOKEN_ID = "divice_token_id";

        public static final String INSTA_ACCESS_TOKEN = "insta_access_token";
        public static final String QB_ID = "quickblock_id";
        public static final String TEMP_INSTA_ID_LOGGED = "temp_insta_id_logged";


    }


    // default message
    public static class Message {

        public static final String NO_INTERNET = "<font color='#131313'>Check your <b>Internet</b> connection, and try again</font>";
    }

    // default message
    public static class Title {
        public static final String NO_INTERNET = "<font color='#DC143C'><b>Oh No ! \nConnection Error</b></font>";

    }

}
