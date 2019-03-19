package com.cenes.service;

import android.app.Application;


import com.instabug.bug.BugReporting;
import com.instabug.bug.PromptOption;
import com.instabug.chat.Chats;
import com.instabug.library.Feature;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.instabug.library.invocation.InvocationManager;

public class InstabugService {

    public void initiateInstabug(Application application) {
        new Instabug.Builder(application, "d81ee39bd8a6ea5c21f101ae80daef5a").setInvocationEvents(InstabugInvocationEvent.NONE).build();
    }

    public void invokeBugReporting() {

        try {

            Instabug.show();
            //BugReporting.setAttachmentTypesEnabled(false, true, true, true);
            //BugReporting.setPromptOptionsEnabled(PromptOption.BUG, PromptOption.FEEDBACK);
            //BugReporting.invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
