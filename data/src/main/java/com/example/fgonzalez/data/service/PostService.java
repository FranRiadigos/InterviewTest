/*******************************************************************************
 * Copyright (c) 2017 Francisco Gonzalez-Armijo Riádigos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.example.fgonzalez.data.service;

import com.example.fgonzalez.domain.Comment;
import com.example.fgonzalez.domain.Post;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Single;

public interface PostService {

    String LIST_POST_PATH = "posts";
    String POST_COMMENTS_PATH = "posts/{postId}/comments";

    @GET(LIST_POST_PATH)
    Single<List<Post>> getPosts();

    @GET(POST_COMMENTS_PATH)
    Single<List<Comment>> getComments(@Path("postId") long postId);

}
