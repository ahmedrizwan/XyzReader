package com.example.xyzreader.model;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by ahmedrizwan on 17/12/2015.
 */
public class ItemHelper {
    public static int getItemCount(){
        return (int) getQuery().count();
    }

    public static RealmQuery<Item> getQuery(){
        return RealmQuery.createQuery(Realm.getDefaultInstance(), Item.class);
    }

    public static List<Item> getAllItems() {
        return getQuery().findAll();
    }
}
