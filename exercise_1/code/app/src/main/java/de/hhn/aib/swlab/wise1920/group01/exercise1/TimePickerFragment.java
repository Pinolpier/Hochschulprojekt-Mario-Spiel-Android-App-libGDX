package de.hhn.aib.swlab.wise1920.group01.exercise1;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{
    public interface TimePickerListener
    {
        void onTimeSet(TimePicker timePicker, int hour, int minute);
    }

    TimePickerListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (TimePickerListener) context;
        }
        catch (Exception e)
        {
            throw new ClassCastException(getActivity().toString() + " must implement TimePickerListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(),this,hour,minute, true);
    }

    @Override
    public void onTimeSet(TimePicker timePicker,int i,int il)
    {
        mListener.onTimeSet(timePicker,i,il);
    }
}
