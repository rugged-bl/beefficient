package com.beefficient.data;

import com.beefficient.data.entity.Task;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

public interface ApiService {

    @GET("tasks")
    Observable<List<Task>> getTasks();
}