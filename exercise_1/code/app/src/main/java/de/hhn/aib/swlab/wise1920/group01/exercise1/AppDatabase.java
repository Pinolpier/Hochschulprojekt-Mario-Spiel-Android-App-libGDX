package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * This class is created using the help of the linked Rooms tutorial
 */
@Database(entities = {Timer.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TimerDao timerDao();

    private static volatile AppDatabase INSTANCE;

    /**
     * This method is used to gt a reference on the used database
     *
     * @param context the apps context, must not be {@code @NotNull}
     * @return a reference to the apps database
     */
    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .allowMainThreadQueries().fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}