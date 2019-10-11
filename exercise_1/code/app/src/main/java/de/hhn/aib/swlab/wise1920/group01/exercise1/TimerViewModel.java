//package de.hhn.aib.swlab.wise1920.group01.exercise1;
//
//import android.app.Application;
//import android.content.Context;
//
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//
//import java.util.List;
//
//public class TimerViewModel extends AndroidViewModel {
//
//    private TimerRepository mRepository;
//
//    private LiveData<List<Timer>> mAllTimer;
//
//    public TimerViewModel (Context context) {
//        super(context);
//        mRepository = new TimerRepository(context);
//        mAllTimer = mRepository.getAllTimer();
//    }
//
//    LiveData<List<Timer>> getAllWords() { return mAllTimer; }
//
//    public void insert(Timer timer) { mRepository.insert(timer); }
//}
