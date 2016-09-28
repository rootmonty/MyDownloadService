package monty.mydownloadservice;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by monty on 13/9/16.
 */
public class Download extends IntentService {

    public static final String URl = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.vogella.android.service.receiver";
    private int result = Activity.RESULT_CANCELED;

    public Download() {
        super("DownloadService");
    }

    //onhandleintent is called by android always asynchronous of the callback methods

    @Override
    protected void onHandleIntent(Intent intent) {
        //Storing the string urlpath in urlpath obtained from intent
        // variable not serialised
        String urlPath = intent.getStringExtra(URl);
        //storing the filename
        String fileName = intent.getStringExtra(FILENAME);
        File output = new File(Environment.getExternalStorageDirectory(),
                fileName);
        if (output.exists()) {
            output.delete();
        }

        //Inputstream opens the stream from the url
        InputStream stream = null;
        //file output stream outputs the stream inputted before onto a file passed in the arguments
        FileOutputStream fos = null;
        try {

            // Try getting the URL in precise form trimmed down
            // First get the URL and create an object to store that
            //Then open http connection on the url
            // then open the reader to read the stream once opened
            // create an external file where you want to copy the stream
            //check the condition whether the reader has not reached the EOF|| end of stream
            // open the file using output stream and start writing the stream to it
            //try catch should be used as there is a lot of possibility of input output stream exception
            URL url = new URL(urlPath);
            stream = url.openConnection().getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            fos = new FileOutputStream(output.getPath());
            int next = -1;
            while ((next = reader.read()) != -1) {
                fos.write(next);
            }
            // successfully finished
            result = Activity.RESULT_OK;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        publishResults(output.getAbsolutePath(), result);
    }

    //method to pass the result received to broadcast receiver
    //sendbroadcast used to broadcast the message to main activity
    private void publishResults(String outputPath, int result) {
        //passing the receiver in intent
        Intent intent = new Intent(NOTIFICATION);
        //putExtra used to store the Bundle value in the first argument string
        intent.putExtra(FILEPATH, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}