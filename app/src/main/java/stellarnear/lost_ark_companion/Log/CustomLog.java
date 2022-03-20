package stellarnear.lost_ark_companion.Log;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import androidx.core.content.FileProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import stellarnear.lost_ark_companion.BuildConfig;
import stellarnear.lost_ark_companion.Divers.Tools;
import stellarnear.lost_ark_companion.R;

public class CustomLog {
    private static final Set<LogMsg> allLogs = new LinkedHashSet<>();
    private static final SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);

    private static final Tools tools = Tools.getTools();

    private final Class currentLoggedClass;

    public CustomLog(Class<?> clazz) {
        this.currentLoggedClass = clazz;
    }

    public void info(String msg) {
        allLogs.add(new LogMsg(Level.INFO, msg));
        Log.i("INFO", msg);
    }

    public void warn(String msg) {
        allLogs.add(new LogMsg(Level.WARN, msg));
        Log.w("WARN", msg);
    }

    public void warn(String msg, Exception e) {
        allLogs.add(new LogMsg(Level.WARN, msg, e));
        Log.w("WARN", msg, e);
    }

    public void err(String msg, Exception e) {
        allLogs.add(new LogMsg(Level.ERROR, msg, e));
        Log.e("ERROR", msg, e);
    }

    public void err(Context mC, String msg, Exception e) {
        tools.customToast(mC, "Erreur:" + msg);
        allLogs.add(new LogMsg(Level.ERROR, msg, e));
        Log.e("ERROR", msg, e);
    }

    public void fatal(final Activity mA, String msg, Exception e) {
        allLogs.add(new LogMsg(Level.FATAL_ERROR, msg, e));
        Log.e("FATAL_ERROR", msg, e);
        tools.customToast(mA,"A fatal error occured");
    }


    private void sendEmail(Activity mA, String comment) throws Exception {
        String pathReportTemp = Environment.getExternalStorageDirectory().toString()
                + "/REPORT_" + BuildConfig.APPLICATION_ID + ".html";
        File report = new File(pathReportTemp);
        report.createNewFile();
        FileOutputStream fos = new FileOutputStream(pathReportTemp);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (LogMsg msg : allLogs) {
            bw.write(msg.getHtmlString() + "<br>");
            bw.newLine();
        }

        bw.write("<style type='text/css'>.stackTrace { margin-left: 40px; } .cause { margin-left: 80px; }</style>");
        bw.close();

        Uri path = FileProvider.getUriForFile(mA, mA.getPackageName() + ".provider", report);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("vnd.android.cursor.dir/email");
        String[] to = {"jeremie.chatron@free.fr"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);

        emailIntent.putExtra(Intent.EXTRA_STREAM, path);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Report Crash " + BuildConfig.APPLICATION_ID.replace("stellarnear.", "") + " " + formater.format(new Date()));

        if (comment.length() > 0) {
            emailIntent.putExtra(Intent.EXTRA_TEXT, comment);
        }
        Intent chooser = Intent.createChooser(emailIntent, "Envoi du rapport");

        List<ResolveInfo> resInfoList = mA.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            mA.grantUriPermission(packageName, path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        mA.startActivity(chooser);
    }

    public void sendReport(Activity mA) throws Exception {
        sendEmail(mA, "");
    }

    private enum Level {
        INFO,
        WARN,
        ERROR,
        FATAL_ERROR
    }

    private class LogMsg {
        private final String prefix = currentLoggedClass.getName();
        private final Level level;
        private final String timeStamp;
        private final String msg;
        private Exception exception;

        private LogMsg(Level level, String msg) {
            this.level = level;
            this.timeStamp = formater.format(new Date());
            this.msg = msg;
        }

        private LogMsg(Level level, String msg, Exception e) {
            this.level = level;
            this.timeStamp = formater.format(new Date());
            this.msg = msg;
            this.exception = e;
        }

        public String getHtmlString() {
            String lineHtml = "";
            lineHtml += "<font color=#808080>" + prefix + "</font>";
            lineHtml += "<font color=#00b359> (" + timeStamp + ")</font>";

            switch (level) {
                case INFO:
                    lineHtml += "<font color=#4db8ff> [" + level + "]</font>";
                    break;
                case WARN:
                    lineHtml += "<font color=#ffdd99> [" + level + "]</font>";
                    break;
                case ERROR:
                    lineHtml += "<font color=#ff4d4d> [" + level + "]</font>";
                    break;
                case FATAL_ERROR:
                    lineHtml += "<font color=#e60000> <b>[" + level + "]</b></font>";
                    break;
            }
            lineHtml += "<font color=#262626> : " + msg + "</font>";

            if (exception != null) {
                lineHtml += "<p class='stackTrace'>";
                lineHtml += "<font color=#ff4d4d>Error stacktrace : " + exception.getMessage() + "</font>";
                for (StackTraceElement elem : exception.getStackTrace()) {
                    lineHtml += "<br>-" + elem.toString();
                }
                lineHtml += "</p>";
                if (exception.getCause() != null) {
                    lineHtml += "<p class='cause'>";
                    lineHtml += "<font color=#ff4d4d>-Caused by : " + exception.getCause().getMessage() + "</font>";
                    for (StackTraceElement elem : exception.getCause().getStackTrace()) {
                        lineHtml += "<br>--" + elem.toString();
                    }
                    lineHtml += "</p>";
                }
            }
            return lineHtml;
        }
    }
}
