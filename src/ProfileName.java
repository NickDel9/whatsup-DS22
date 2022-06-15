import java.util.ArrayList;
import java.util.HashMap;

public class ProfileName{

    private final String profileName;
    private final HashMap<String, ArrayList<Value>> userVideoFilesMap = new HashMap<String, ArrayList<Value>>();
    private final HashMap<String, Integer> subscribedConversations = new HashMap<String, Integer>();

    public ProfileName(){
        this.profileName = null;
    }

    public ProfileName(String profileName){
        this.profileName = profileName;
    }

    //setters
    public void setUserVideoFilesMap(String key , ArrayList<Value> valueArrayList){
        this.userVideoFilesMap.put(key ,valueArrayList);
    }

    public void setSubscribedConversations(String key , Integer value){
        this.subscribedConversations.put(key ,value);
    }

    //delete
    public void deleteRowUserVideoFilesMap(String key){
        this.userVideoFilesMap.remove(key);
    }

    public void deleteRowSubscribedConversations(String key){
        this.subscribedConversations.remove(key);
    }

    //getters
    public String getProfileName() {
        return this.profileName;
    }

    public HashMap<String , ArrayList<Value>> getUserVideoFilesMap(){
        return  this.userVideoFilesMap;
    }

    public HashMap<String, Integer> subscribedConversations(){
        return  this.subscribedConversations;
    }

}
