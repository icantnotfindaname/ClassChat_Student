package com.example.classchat.Util;

import android.content.res.Resources;
import android.view.View;

public interface ThemeUIInterface {
    View getView();
    void setTheme(Resources.Theme theme);
}