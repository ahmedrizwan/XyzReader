package example.com.xyzreader.retrofit;

import java.util.List;

import example.com.xyzreader.model.Item;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by ahmedrizwan on 13/12/2015.
 */
public interface ItemService {
    @GET("data.json")
    Observable<List<Item>> getItems();
}
