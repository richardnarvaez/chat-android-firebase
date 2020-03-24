/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package ec.richardnarvaez.chatf.chat.adapters;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;

class FirebaseArray implements ChildEventListener {
    public interface OnChangedListener {
        enum EventType { Added, Changed, Removed, Moved }
        void onChanged(EventType type, int index, int oldIndex);
        void onCancelled(DatabaseError databaseError);
    }

    private Query mQuery;
    private OnChangedListener mListener;
    private ArrayList<DataSnapshot> mSnapshots;

    public FirebaseArray(Query ref) {
        mQuery = ref;
        mSnapshots = new ArrayList<DataSnapshot>();
        mQuery.addChildEventListener(this);
    }

    public void cleanup() {
        mQuery.removeEventListener(this);
    }

    //tamaño de la lista de snapshots
    public int getCount() {
        return mSnapshots.size();

    }

    public DataSnapshot getItem(int index) {
        return mSnapshots.get(index);
    }

    public String getKey(int index) {return getItem(index).getKey();}




    private int getIndexForKey(String key) {

        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        int index = 0;

        if (previousChildKey != null) {
            index = getIndexForKey(previousChildKey) + 1;
        }

        mSnapshots.add(index, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.Added, index);
    }

    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.set(index, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.Changed, index);
    }

    public void onChildRemoved(DataSnapshot snapshot) {
        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(index);
        notifyChangedListeners(OnChangedListener.EventType.Removed, index);
    }

    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        int oldIndex = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(oldIndex);
        int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
        mSnapshots.add(newIndex, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.Moved, newIndex, oldIndex);
    }

    public void onCancelled(DatabaseError databaseError) {
        notifyCancelledListeners(databaseError);
    }
    // End of ChildEventListener methods

    public void setOnChangedListener(OnChangedListener listener) {
        mListener = listener;
    }

    protected void notifyChangedListeners(OnChangedListener.EventType type, int index) {
        notifyChangedListeners(type, index, -1);
    }

    protected void notifyChangedListeners(OnChangedListener.EventType type, int index, int oldIndex) {
        if (mListener != null) {
            mListener.onChanged(type, index, oldIndex);
        }
    }

    protected void notifyCancelledListeners(DatabaseError databaseError) {
        if (mListener != null) {
            mListener.onCancelled(databaseError);
        }
    }
}