package com.downloadmanager;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.security.KeyChainException;
import android.util.Log;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by victoraliaga on 7/18/17.
 */

public class CustomDownloadManager extends ReactContextBaseJavaModule {
    private Callback callback = null;
    private static final int REQUEST_CODE = 112112;
    private final ReactApplicationContext reactContext;
    private DownloadManager downloadManager;
    private static Boolean DOWNLOADING = false;
    private static String MATERIAL_ID = "6756234";

    private long Video_DownloadId;

    public CustomDownloadManager(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "DownloadManagerCustom";
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                sendCallback(true, false, false);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                sendCallback(false, true, false);
            }
        }
    }

    public void onNewIntent(Intent intent) {
    }

    public void sendCallback(Boolean completed, Boolean cancelled, Boolean error) {
        if (callback != null) {
            callback.invoke(completed, cancelled, error);
            callback = null;
        }
    }


    @ReactMethod
    public void download(final Callback callback) {

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        reactContext.registerReceiver(downloadReceiver, filter);

        String videoURL = "http://www.sample-videos.com/video/mp4/360/big_buck_bunny_360p_5mb.mp4";

        Uri video_uri = Uri.parse(videoURL);
        Video_DownloadId = DownloadData(video_uri, "video 1", "56b248f0c1f9939f08587ce2");

        Log.d("asd","initial");
        callback.invoke("");
    }

//    public boolean checkDownloadRunning(){
//        return DOWNLOADING;
//
//
//    }

    private void Check_Download_Status(long DownloadId) {

        DownloadManager.Query MusicDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        MusicDownloadQuery.setFilterById(DownloadId);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(MusicDownloadQuery);
        if(cursor.moveToFirst()){
            DownloadStatus(cursor, DownloadId);
        }
    }

    private void DownloadStatus(Cursor cursor, long DownloadId){

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch(status){
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch(reason){
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch(reason){
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                break;
        }



    }
    public void cancelDownload(){
        downloadManager.remove(Video_DownloadId);
    }

    private long DownloadData (Uri uri, String video_name, String video_id) {


        Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs();
        long downloadReference;

        downloadManager = (DownloadManager) reactContext.getSystemService(DOWNLOAD_SERVICE);
        //downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(video_name);

        //Setting description of request
        request.setDescription("Descargando video...");

        request.setDestinationInExternalFilesDir(reactContext, Environment.DIRECTORY_DOWNLOADS, video_id +".mp4");
        request.setVisibleInDownloadsUi(false);
        request.allowScanningByMediaScanner();

        //Enqueue download and save the referenceId
        downloadReference = downloadManager.enqueue(request);
        DOWNLOADING = true;
        MATERIAL_ID = video_id;
        Log.d("asd","enqueue download");

        return downloadReference;
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("asd","onReceive");
            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);


//                Toast toast = Toast.makeText(ConceptActivity.this,
//                        "Descarga Terminada", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.TOP, 25, 400);
//                toast.show();
            DOWNLOADING = false;

            File clip=new File(Environment.getExternalStorageDirectory() + "/Android/data/com.downloadmanager/files/Download/", MATERIAL_ID + ".mp4");

            if (clip.exists()){
                Log.d("asd","clip exists");
                clip.deleteOnExit();
                DownloadWebPageTask task = new DownloadWebPageTask();
                task.execute(clip);

            }


        }
    };

    private class DownloadWebPageTask extends AsyncTask<File, Void, String> {
        @Override
        protected String doInBackground(File... clip) {
            Log.d("asd","background");
            // we use the OkHttp library from https://github.com/square/okhttp
            clip[0].deleteOnExit();
            byte[] filebytes = readBytesFromFiles(clip[0]);
            //encryptFile(filebytes);
            clip[0].delete();
            return "Download failed";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("asd","post execute");
            //refreshView();
        }
    }


//    public void encryptFile(byte[] filebytes){
//        com.facebook.crypto.keychain.KeyChain keyChain = new SharedPrefsBackedKeyChain(this, CryptoConfig.KEY_256);
//        Crypto crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
//
//        OutputStream fileStream = null;
//        try {
//            fileStream = new BufferedOutputStream(
//                    new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/Android/data/com.platzi.platzi/files/Download/",  MATERIAL_ID + "")));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        // Creates an output stream which encrypts the data as
//        // it is written to it and writes it out to the file.
//        OutputStream outputStream = null;
//        try {
//            outputStream = crypto.getCipherOutputStream(
//                    fileStream,
//                    Entity.create("entity_id"));
//            // Write plaintext to it.
//            outputStream.write(filebytes);
//            outputStream.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (CryptoInitializationException e) {
//            e.printStackTrace();
//        } catch (KeyChainException e) {
//            e.printStackTrace();
//        }
//
//    }

    public byte[] readBytesFromFiles(File video){
        Log.d("asd","read file");
        ByteSource source = Files.asByteSource(video);
        byte[] result = new byte[0];
        try {
            result = source.read();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e){
            Log.d("ANDRO_ASYNC",String.format("catch Out Of Memory error"));

        }
        return result;
    }
}
