package com.sneider.diycode.utils;

public class Constant {

    public static final String KEYSTORE_KEY_ALIAS = "DiyCode";

    public static final String OAUTH_URL = "https://www.diycode.cc/oauth/token";

    public static final String BASE_URL = "https://www.diycode.cc/api/v3/";

    public static final String UPLOAD_URL = BASE_URL + "photos.json";

    public static final String GRANT_TYPE_PASSWORD = "password";

    public static final int PAGE_SIZE = 20;

    public static final String ORDER = "recent"; // "recent", ("likes", "replies")
}
