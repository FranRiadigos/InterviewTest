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
import com.example.fgonzalez.domain.User;
import com.example.fgonzalez.domain.executor.ThreadExecutor;
import com.example.fgonzalez.domain.repository.PostRepository;
import com.example.fgonzalez.domain.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import rx.Single;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Testing GetPostList UseCase.
 *
 * We  make sure the business logic of the UseCase is correct. We don't mind on objects outside our scope,
 * which is the Domain layer, thus testing a Repository or a Network request belongs to the Data layer.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetPostListTest {

    private GetPostList getPostListUseCase;

    @Mock private ThreadExecutor mockThreadExecutor;
    @Mock private PostRepository mockPostRepository;
    @Mock private UserRepository mockUserRepository;

    @Before
    public void setUp() {
        int threads = 5;
        // GetPostList is the class we want to test
        getPostListUseCase = new GetPostList(mockPostRepository, mockUserRepository,
                mockThreadExecutor, mockThreadExecutor, mockThreadExecutor, threads);
    }

    /**
     * Checks whether GetPostList UseCase is working as expected and the behaviour of the code have not changed.
     */
    @Test
    public void testGetPostListObservableCase() {
        // Preconditions
        List<Post> postList = Collections.singletonList(mock(Post.class));
        TestSubscriber<List<Post>> testSubscriber = new TestSubscriber<>();
        TestScheduler testScheduler = new TestScheduler();
        given(mockThreadExecutor.getScheduler()).willReturn(testScheduler);
        given(mockPostRepository.getAll()).willReturn(Single.just(postList));
        given(mockUserRepository.get(anyLong())).willReturn(Single.just(mock(User.class)));

        // Attaches the subscriber and executes the Observable.
        Subscription subscription = getPostListUseCase.execute(testSubscriber);
        testScheduler.triggerActions();
        subscription.unsubscribe();

        // Checks whether these methods from inner objects are called.
        verify(mockPostRepository).getAll();
        verify(mockUserRepository, times(postList.size())).get(anyLong());
        verify(mockThreadExecutor, times(3)).getScheduler();

        // Ensures there aren't new changes on the Repository calls and UseCase structure
        verifyNoMoreInteractions(mockPostRepository, mockUserRepository, mockThreadExecutor);

        // Ensures Observer is emitting only the mocked element we passed through before
        testSubscriber.assertValueCount(1);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValue(postList);

        // Ensures the Subscriber is not subscribed anymore
        testSubscriber.assertUnsubscribed();
    }
}
