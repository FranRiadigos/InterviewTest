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

import com.example.fgonzalez.domain.Post;
import com.example.fgonzalez.domain.executor.ThreadExecutor;
import com.example.fgonzalez.domain.interactor.base.UseCase;
import com.example.fgonzalez.domain.repository.PostRepository;
import com.example.fgonzalez.domain.repository.UserRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

/**
 * Use case that retrieves a List of {@link Post}s.
 */
public class GetPostList extends UseCase<List<Post>, Void> {

    private static final int AVATAR_SIZE = 50;
    private static final String AVATAR_URL = "https://api.adorable.io/avatars/";

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ThreadExecutor concurrentExecutor;
    private final int availableProcessors;

    private final AtomicInteger batch = new AtomicInteger(0);

    @Inject
    public GetPostList(PostRepository postRepository, UserRepository userRepository,
                       @Named("SubscriberThread") ThreadExecutor subscriberThread,
                       @Named("ObserverThread") ThreadExecutor observerThread,
                       @Named("ConcurrentExecutor") ThreadExecutor concurrentExecutor,
                       @Named("AvailableProcessors") int threads) {
        super(subscriberThread, observerThread);
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.concurrentExecutor = concurrentExecutor;
        this.availableProcessors = threads;
    }

    /**
     * We make a call to the Post endpoint, and once we get the list of all posts,
     * we make another request in parallel (in a different Thread) to retrieve each related Users.
     *
     * This is an example on how you can chain multiple requests, and also how to deal with
     * concurrency calls with RxJava in order to improve the performance.
     *
     * By default, if we avoid making the requests in parallel, it'll take around 1 minute to finish.
     * By applying the concurrency technique, the whole chained request takes up to 8 seconds to finish,
     * which is more than 85% of a really significant performance improvement.
     *
     * @param aVoid Void
     * @return Observable of Posts
     */
    @Override
    public Observable<List<Post>> provideObservable(Void aVoid) {
        long time = System.currentTimeMillis(); // For Console output purposes
        return postRepository.getAll()
                .toObservable()
                .flatMapIterable(postList ->  postList)
                .groupBy(i -> batch.getAndIncrement() % this.availableProcessors)
                .flatMap(g -> g.observeOn(concurrentExecutor.getScheduler())
                    .flatMap(post -> userRepository.get(post.userId)
                        .map(user -> {
                            // Just see what happens in the Console output
                            System.out.println(
                                "Group["+g.getKey()+"] Post " + post.id
                                        + " retrieved User \"" + user.email
                                        + "\" by using Thread: " + Thread.currentThread().getName());
                            // Updates each post with its user
                            user.avatar = AVATAR_URL + AVATAR_SIZE + "/" + user.email + ".png";
                            post.user = user;
                            return post;
                        }).toObservable()))
                .toSortedList((post1, post2) -> post1.id < post2.id ? -1 : (post1.id == post2.id ? 0 : 1))
                .doOnNext(postList ->
                        System.out.println("Total time: " + (System.currentTimeMillis() - time) / 1000 + " sec"));
    }

}