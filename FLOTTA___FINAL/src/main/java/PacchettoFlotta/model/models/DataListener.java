package PacchettoFlotta.model.models;

//classe che genera callback quando vi sono modifiche da notificare

public interface DataListener<T> {

    public void onDataChanged(SmartObject<T> resource, T updatedValue);
}