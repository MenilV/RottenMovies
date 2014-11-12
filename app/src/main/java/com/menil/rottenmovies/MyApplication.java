package com.menil.rottenmovies;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/*
 * Created by menil on 07.11.2014.
 */
@ReportsCrashes(
        formKey = "",
        mailTo = "menil.vukovic@gmail.com, igor.trncic@atlantbh.com, jasmin@atlantbh.com",
        //resDialogEmailPrompt = 1,
        mode = ReportingInteractionMode.DIALOG,
        customReportContent = {
                ReportField.BUILD,

                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,


                ReportField.APPLICATION_LOG,

                ReportField.USER_COMMENT,


                ReportField.BRAND,


                ReportField.STACK_TRACE

        },
        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resDialogText = R.string.crash_dialog_text,
        //resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resNotifIcon = android.R.drawable.ic_dialog_info,
        resDialogIcon = R.drawable.ic_launcher_small,
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
)
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
      // ACRA.init(this);
        //TODO: uncomment this^
    }
}