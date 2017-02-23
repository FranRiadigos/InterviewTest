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

package com.example.fgonzalez.data.repository;

import com.example.fgonzalez.data.service.PostService;
import com.example.fgonzalez.data.service.adapter.RestFactory;
import com.example.fgonzalez.domain.Comment;
import com.example.fgonzalez.domain.Post;
import com.example.fgonzalez.domain.repository.PostRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Single;

/**
 * Post Repository implementation which should discriminate between cloud and database requests.
 */
@Singleton
public class PostRepositoryImpl implements PostRepository {
    private final RestFactory api;
    private final String apiUrl;

    @Inject
    PostRepositoryImpl(RestFactory api, @Named("ApiUrl") String apiUrl) {
        this.api = api;
        this.apiUrl = apiUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Single<List<Post>> getAll() {
        this.api.setBaseUrl(apiUrl); // Ability to change the API Url at any time.
        return this.api.create(PostService.class).getPosts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Single<List<Comment>> getComments(final long postId) {
        this.api.setBaseUrl(apiUrl); // Ability to change the API Url at any time.
        return this.api.create(PostService.class).getComments(postId);
    }
}
