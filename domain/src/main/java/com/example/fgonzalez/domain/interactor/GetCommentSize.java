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

package com.example.fgonzalez.domain.interactor;

import com.example.fgonzalez.domain.Comment;
import com.example.fgonzalez.domain.Post;
import com.example.fgonzalez.domain.executor.ThreadExecutor;
import com.example.fgonzalez.domain.interactor.base.UseCase;
import com.example.fgonzalez.domain.repository.PostRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

/**
 * Use case that returns the size of all {@link Comment}s of a specific {@link Post}.
 */
public class GetCommentSize extends UseCase<Integer, GetCommentSize.Params> {

    private final PostRepository postRepository;

    @Inject
    public GetCommentSize(PostRepository postRepository,
                @Named("SubscriberThread") ThreadExecutor subscriberThread,
                @Named("ObserverThread") ThreadExecutor observerThread) {
        super(subscriberThread, observerThread);
        this.postRepository = postRepository;
    }

    /**
     * We leave the repository the responsibility of the request, and once we get the List of items
     * we just return the number of them.
     *
     * @param params The Params.
     * @return Observable of the number of Comments
     */
    @Override
    public Observable<Integer> provideObservable(Params params) {
        if(params == null) return Observable.error(new NullParameterException(Params.class));
        return this.postRepository.getComments(params.postId).toObservable().map(List::size);
    }

    public static class Params {

        private final long postId;

        private Params(long postId) {
            this.postId = postId;
        }

        public static Params with(long postId) {
            return new Params(postId);
        }
    }
}
