package com.fi.uba.ar.tdp2.eukanuber.provider;

import com.fi.uba.ar.tdp2.eukanuber.model.Post;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface PostProvider {
    String API_ROUTE = "/posts";

    @GET(API_ROUTE)
    Call< List<Post> > getPost();
}
