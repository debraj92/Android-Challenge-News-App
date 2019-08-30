package news.agoda.com.sample.model;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import news.agoda.com.sample.AppConstants;

/**
 * This is a cache controller. It ensures that all the read and write to the database file happen in a
 * serialized manner. Concurrent read and write may fetch incorrect values and/or corrupt the cache.
 *
 * The cache controller uses a handler thread and posts all requests for read and write on the handler thread's
 * message queue. This ensures the requests are processed in a first come first serve manner, and no two requests
 * are run concurrently.
 *
 * The class uses Singleton design pattern.
 */
class NewsDataBaseController {

    private static final String TAG = AppConstants.APP_TAG + "." + NewsDataBaseController.class.getSimpleName();

    /**
     * The name of the cache file.
     */
    private static final String FILE_NAME = "news_dump.txt";

    /**
     * Path to the files directory of the application.
     */
    private static String sFilesDir;

    /**
     * The mData is a shared object which will hold the final data read from the cache.
     */
    private static String mData;

    /**
     * The read complete lock is to ensure the read call blocks until the data read is completed.
     * Note, a write call, on the other hand, is not a blocking call because the user can view the news
     * while the write happens in the background. However, a read call must be blocking and the user has
     * to wait until the data is read from the cache for viewing the news.
     */
    private static final AtomicBoolean mReadCompleteLock = new AtomicBoolean(true);

    /**
     * The handler thread is to ensure that read and write operations dont happen concurrently
     */
    private HandlerThread mHandlerThread;

    /**
     * Handler for posting read and write requests on the handler thread.
     */
    private Handler mHandler;

    /**
     * Instance of the class (singleton implementation)
     */
    private static NewsDataBaseController sInstance;

    /**
     * Private constructor (singleton implementation)
     * @param filesDirPath    path to the files directory
     */
    private NewsDataBaseController(String filesDirPath) {
        Log.d(TAG,"NewsDB object created");
        sFilesDir = filesDirPath;
        mHandlerThread = new HandlerThread("NewDataBaseThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    /**
     * Get the static instance of this class (singleton implementation)
     * @param filesDirPath    path to the files directory
     * @return    static instance of this class
     */
    static NewsDataBaseController getInstance(@NotNull String filesDirPath) {
        synchronized (NewsDataBaseController.class) {
            if(sInstance == null) {
                sInstance = new NewsDataBaseController(filesDirPath);
            }
        }
        return sInstance;
    }

    /**
     * Stop accepting new requests and release the resources.
     */
    void cleanUp() {
        Log.d(TAG,"Clean up NewsDB object");
        // if the mHandler  is null , it indicates cleanup is already done.
        if(mHandler != null) {
            mHandlerThread.quitSafely();
        }
        mHandlerThread = null;
        mHandler = null;
        sInstance = null;
    }

    /**
     * Write a json string to the DB (file)
     * @param data    json string
     */
    void writeToDB(final String data) {
        Log.d(TAG,"writeToDB");
        if(mHandler != null) {
            /**
             * Post a write request on the handler thread.
             */
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    Log.d(TAG, "Starting write on a new thread");
                    try {
                        File file = new File(sFilesDir +
                                "/" + FILE_NAME);

                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        FileWriter writer = new FileWriter(file);
                        writer.append(data);
                        writer.flush();
                        writer.close();

                    } catch (IOException e) {
                        Log.e(TAG, "Cannot write to file " + e.getMessage());
                    }
                }
            });
        }

    }

    /**
     * Reads the db file and returns the content. This is a blocking call.
     * @return    the database file content (json string)
     */
    @Nullable
    String readFromDB() {
        Log.d(TAG,"readFromDB");
        /**
         * The caller first acquires the read lock, posts a read request on the handler thread and then wait
         * until the handler thread finishes the read request. The handler thread after reading the db file
         * content places the data in the shared variable (mData). It then notifies the caller who is waiting for
         * the read request to be complete. The caller can then have the data from the shared variable mData.
         *
         * Note, we have used the concurrency design pattern - guarded suspension.
         * https://en.wikipedia.org/wiki/Guarded_suspension
         */
        synchronized (mReadCompleteLock) {

            mReadCompleteLock.set(false);
            mData = null;
            // post the read request
            if(mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (mReadCompleteLock) {
                        /**
                         * The handler thread acquires the read lock and starts reading from the file.
                         */
                        Log.d(TAG, "Starting read on a new thread");

                        File file = new File(sFilesDir +
                                "/" + FILE_NAME);

                        int length = (int) file.length();

                        byte[] bytes = new byte[length];

                        FileInputStream in = null;
                        try {
                            in = new FileInputStream(file);
                            in.read(bytes);
                            in.close();
                            // updates the shared variable
                            mData = new String(bytes);
                        } catch (Exception e) {
                            /**
                             * if read fails this function will return null
                             */
                            Log.e(TAG, "Cannot read from db file " + e.getMessage());
                        }
                        // sets read complete to true.
                        mReadCompleteLock.set(true);
                        // notisfies the caller's thread who is waiting for the read complete.
                        mReadCompleteLock.notifyAll();
                    }
                }
            });
        }

            // wait until precondition (read complete) is not satisfied
            while(!mReadCompleteLock.get()) {
                Log.d(TAG,"Will wait until data is read from disc");
                try {
                    // Wait for 3 seconds
                    mReadCompleteLock.wait(3000);
                } catch (InterruptedException e) {
                }
            }
        }

        return mData;

    }
}
