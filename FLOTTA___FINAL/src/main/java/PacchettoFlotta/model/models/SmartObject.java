package PacchettoFlotta.model.models;

import java.util.ArrayList;
import java.util.List;

public abstract class SmartObject<T>{

    protected List<DataListener<T>> resourceListenerList;

    public String Type;
    public String Id;

    public SmartObject() {
        this.resourceListenerList = new ArrayList<>();
    }

    public SmartObject(String type, String id) {
        this.Type = type;
        this.Id = id;
        this.resourceListenerList = new ArrayList<>();
    }

    public abstract T UpdateValue();

    //check riguardanti esistenza dei Data Listener

    public void addDataListener(DataListener<T> resourceDataListener){
        if (this.resourceListenerList != null){
            this.resourceListenerList.add(resourceDataListener);
        }
    }

    public void removeDataListener(DataListener<T> resourceDataListener){
        if (this.resourceListenerList != null && this.resourceListenerList.contains(resourceDataListener)){
            this.resourceListenerList.remove(resourceDataListener);
        }
    }

    protected void notifyUpdate(T UpdateValue){
        if (this.resourceListenerList != null && this.resourceListenerList.size() > 0){
            this.resourceListenerList.forEach(resourceDataListener -> {
                if (resourceDataListener != null){
                    resourceDataListener.onDataChanged(this, UpdateValue);
                }
            });
        }/*else{
            logger.error("Error, nothing to notify");
        }*/
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @Override
    public String toString() {
        return "SmartObject{" +
                "Type='" + Type + '\'' +
                ", Id=" + Id +
                '}';
    }
}
