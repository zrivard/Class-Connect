package champagne86.com.classconnect;

import android.support.annotation.NonNull;
import android.util.Log;


import com.google.firebase.auth.FirebaseAuth;


import java.io.SyncFailedException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Calendar;

public class Classroom {

    private String name;
    private boolean isActive;
    private String startDate;
    private String endDate;
    private Map<String, String[]> dayTimeActive;


    public Classroom() {


    }

    public Classroom(String classname) {
        name = classname;
        isActive = false;

    }

    public void setActiveDay(String dayOfWeek, String startTime, String endTime) {
        String[] times = {startTime, endTime};
        dayTimeActive.put(dayOfWeek, times);
        setEndDate();
        setStartDate();
    }


    public void setStartDate() {
        startDate = "01/09/2018";

    }

    public void setEndDate() {
        endDate = "07/12/2018";
    }

    /*
    This method adds an existing time slot to a day of the week in which the class is active.
    Returns false if class is not in session on dayOfWeek
 */
    public boolean addBlock(String dayOfWeek, String startTime, String endTime) {

        if (dayTimeActive.containsKey(dayOfWeek)) {
            String[] oldTimes = dayTimeActive.get(dayOfWeek);
            String[] newTimes = new String[oldTimes.length + 2];
            for (int i = 0; i < newTimes.length; i++) {

                if (i < oldTimes.length) {
                    newTimes[i] = oldTimes[i];
                } else if (i == oldTimes.length) {
                    newTimes[i] = startTime;
                } else if (i == newTimes.length - 1) {
                    newTimes[i] = endTime;
                }
            }
            return true;
        } else return false;
    }

    public boolean isActive() {
        //DatabaseReference mDatabase;

        Calendar calendar = Calendar.getInstance();
        String daysArray[] = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String dayName = daysArray[dayOfWeek + 1];
        isActive = false;
//
//        if (dayTimeActive.containsKey(dayName)) {
//            SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
//            String curTime = sdf.format(new Date());
//            int curTimeInt = Integer.parseInt(curTime);
//
//            String[] activeTimes = dayTimeActive.get(dayName);
//
//            for (int i = 0; i < activeTimes.length - 1; i += 2) {
//                int startTime = Integer.parseInt(activeTimes[i]);
//                int endTime = Integer.parseInt(activeTimes[i + 1]);
//
//                if (curTimeInt >= startTime && curTimeInt <= endTime) {
//                    isActive = true;
//                    break;
//                }
//            }
//
//        }



        return isActive;

    }

    public String getName() {
        return this.name;
    }


}
