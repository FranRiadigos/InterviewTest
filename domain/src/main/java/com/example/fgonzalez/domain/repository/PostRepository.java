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

package com.example.fgonzalez.domain.repository;

import com.example.fgonzalez.domain.Comment;
import com.example.fgonzalez.domain.Post;

import java.util.List;

import rx.Single;

/**
 * Post Repository contract.
 */
public interface PostRepository {

    /**
     * Gets a {@link Single} which will emit a List of {@link Post}s.
     *
     * @return Single List
     */
    Single<List<Post>> getAll();


    /**
     * Gets a {@link Single} which will emit a List of {@link Comment}s.
     *
     * @param postId long
     * @return Single List
     */
    Single<List<Comment>> getComments(final long postId);
}
