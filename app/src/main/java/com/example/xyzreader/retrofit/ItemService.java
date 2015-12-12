package com.example.xyzreader.retrofit;

import com.example.xyzreader.model.Item;

import java.util.List;

import retrofit.http.GET;
import rx.Observable;

/**
 * Created by ahmedrizwan on 13/12/2015.
 */
public interface ItemService {
    @GET("data.json")
    Observable<List<Item>> getItems();
}
