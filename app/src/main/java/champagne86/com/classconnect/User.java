package champagne86.com.classconnect;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class User {
    public FirebaseUser mUser;
    public String uid;
    public String userName;
    public List<Classroom> classList = new ArrayList();

    public User(){

    }

    public User(FirebaseUser user, String name){
        mUser = user;
        userName = name;
        uid = user.getUid();
    }

    public String getUid(){
        return uid;
    }

    public void setUserName(String name){
        userName = name;
    }

    public String getUserName(){
        return userName;
    }

    public List getClassList(){
        return classList;
    }

    public void addClassList(List classlist) {classList = classlist; }

    public boolean addClass(Classroom classroom){
        return classList.add(classroom);
    }

    public int numClasses(){
        return classList.size();
    }
}
