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
import com.example.fgonzalez.domain.executor.ThreadExecutor;
import com.example.fgonzalez.domain.interactor.base.UseCase;
import com.example.fgonzalez.domain.repository.PostRepository;

import org.junit.Assert;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Testing GetCommentSize UseCase.
 *
 * We only make sure the business logic of the UseCase is correct. We don't mind on objects outside our scope,
 * which is the Domain layer, thus testing a Repository or a Network request belongs to the Data layer.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetCommentSizeTest {

    private GetCommentSize getCommentSizeUseCase;

    @Mock private ThreadExecutor mockThreadExecutor;
    @Mock private PostRepository mockPostRepository;

    @Before
    public void setUp() {
        // GetCommentSize is the class we want to test
        getCommentSizeUseCase = new GetCommentSize(mockPostRepository, mockThreadExecutor, mockThreadExecutor);
    }

    /**
     * Checks whether GetCommentSize UseCase is working as expected and the behaviour of the code have not changed.
     */
    @Test
    public void testGetCommentSizeObservableCase() {
        // Preconditions
        List<Comment> commentList = Collections.singletonList(mock(Comment.class));
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        TestScheduler testScheduler = new TestScheduler();
        given(mockThreadExecutor.getScheduler()).willReturn(testScheduler);
        given(mockPostRepository.getComments(anyLong())).willReturn(Single.just(commentList));

        // Attaches the subscriber and executes the Observable.
        Subscription subscription = getCommentSizeUseCase.execute(testSubscriber, mock(GetCommentSize.Params.class));
        testScheduler.triggerActions();
        subscription.unsubscribe();

        // Checks whether these methods from inner objects are called.
        verify(mockPostRepository).getComments(anyLong());
        verify(mockThreadExecutor, times(2)).getScheduler();

        // Ensures there aren't new changes on the Repository calls and UseCase structure
        verifyNoMoreInteractions(mockPostRepository, mockThreadExecutor);

        // Ensures Observer is emitting only the mocked element we passed through before
        testSubscriber.assertValueCount(1);
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValue(1);

        // Ensures the Subscriber is not subscribed anymore
        testSubscriber.assertUnsubscribed();
    }

    /**
     * Checks it carry on an Exception if GetCommentSize.Params is not provided through the {@link rx.Subscriber#onError(Throwable)} method.
     */
    @Test
    public void testGetCommentSizeWithNoParamsThrowsException() {
        // Preconditions
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        TestScheduler testScheduler = new TestScheduler();
        given(mockThreadExecutor.getScheduler()).willReturn(testScheduler);

        // Attaches the subscriber and executes the Observable.
        Subscription subscription = getCommentSizeUseCase.execute(testSubscriber);
        testScheduler.triggerActions();
        subscription.unsubscribe();

        // Checks whether these methods from inner objects are NEVER called.
        verify(mockPostRepository, never()).getComments(anyLong());
        verify(mockThreadExecutor, times(2)).getScheduler();

        // Ensures there aren't new changes on the Repository calls and UseCase structure
        verifyNoMoreInteractions(mockPostRepository, mockThreadExecutor);

        // Ensures Observer is emitting NullPointerException in the onError method
        testSubscriber.assertError(UseCase.NullParameterException.class);
        testSubscriber.assertNotCompleted();
        testSubscriber.assertNoValues();

        List<Throwable> throwableList = testSubscriber.getOnErrorEvents();
        Assert.assertTrue(throwableList.size() > 0);
        //noinspection ThrowableResultOfMethodCallIgnored
        Assert.assertEquals(String.format(UseCase.NULL_PARAMETER, GetCommentSize.Params.class.getSimpleName()),
                throwableList.get(0).getMessage());

        // Ensures the Subscriber is not subscribed anymore
        testSubscriber.assertUnsubscribed();
    }
}
