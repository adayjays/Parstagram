package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CREATED_AT = "createdAt";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public Date getKeyCreatedAt() {
        return getDate(KEY_CREATED_AT);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setImage(ParseFile file) {
        put(KEY_IMAGE, file);
    }
}
