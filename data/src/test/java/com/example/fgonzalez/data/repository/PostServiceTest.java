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

import com.example.fgonzalez.data.MockResponseDispatcher;
import com.example.fgonzalez.data.RxJavaTestRunner;
import com.example.fgonzalez.data.service.PostService;
import com.example.fgonzalez.data.service.adapter.RetrofitFactory;
import com.example.fgonzalez.domain.Comment;
import com.example.fgonzalez.domain.Post;
import com.example.fgonzalez.domain.repository.PostRepository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;

import java.util.List;

import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.HttpException;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Testing Post repository and service.
 *
 * As part of the Data layer, we will test that the behaviour of Repositories and Responses
 * from Service requests are working as expected.
 */
@RunWith(RxJavaTestRunner.class)
public class PostServiceTest extends BaseTestCase {

    @Spy RetrofitFactory retrofitFactory;

    private PostRepository repository;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockResponseDispatcher.reset();
        server.setDispatcher(MockResponseDispatcher.DISPATCHER);
        server.start();
        repository = new PostRepositoryImpl(retrofitFactory, server.url("").url().toString());
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Checks whether the Post repository and service is working as expected and the behaviour of the code have not changed.
     */
    @Test
    public void testRepositoryGotResponseOkForPosts() throws InterruptedException {
        // Preconditions
        TestSubscriber<List<Post>> subscriber = new TestSubscriber<>();

        // Attaches the subscriber and executes the Observable.
        repository.getAll().subscribe(subscriber);
        RecordedRequest request = server.takeRequest();

        // Checks whether these methods from inner objects are called.
        verify(retrofitFactory).setBaseUrl(anyString());
        verify(retrofitFactory).create(PostService.class);
        verify(retrofitFactory).getClient();

        // Ensures there aren't new changes on the Rest adapter structure
        verifyNoMoreInteractions(retrofitFactory);

        // Ensures Observer and Requests are working as expected
        List<List<Post>> events = subscriber.getOnNextEvents();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertCompleted();
        subscriber.assertUnsubscribed();
        Assert.assertEquals(1, server.getRequestCount());
        Assert.assertEquals("/posts", request.getPath());
        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(3, events.get(0).size());
    }

    /**
     * Checks whether the Post repository and service is working as expected and the behaviour of the code have not changed.
     */
    @Test
    public void testRepositoryGotResponseOkForComments() throws InterruptedException {
        // Preconditions
        TestSubscriber<List<Comment>> subscriber = new TestSubscriber<>();

        // Attaches the subscriber and executes the Observable.
        repository.getComments(1).subscribe(subscriber);
        RecordedRequest request = server.takeRequest();

        // Checks whether these methods from inner objects are called.
        verify(retrofitFactory).setBaseUrl(anyString());
        verify(retrofitFactory).create(PostService.class);
        verify(retrofitFactory).getClient();

        // Ensures there aren't new changes on the Rest adapter structure
        verifyNoMoreInteractions(retrofitFactory);

        // Ensures Observer and Requests are working as expected
        List<List<Comment>> events = subscriber.getOnNextEvents();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertCompleted();
        subscriber.assertUnsubscribed();
        Assert.assertEquals(1, server.getRequestCount());
        Assert.assertEquals("/posts/1/comments", request.getPath());
        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(3, events.get(0).size());
    }

    /**
     * Ensures a 500 exception is thrown as an event through the observable stream
     */
    @Test
    public void testRepositoryGot500Response() {
        // Preconditions
        TestSubscriber<List<Post>> subscriber = new TestSubscriber<>();
        MockResponseDispatcher.RETURN_500 = true;

        // Attaches the subscriber and executes the Observable.
        repository.getAll().subscribe(subscriber);

        // We ensure we've got 500 Server Error in the onError method of the Subscriber.
        subscriber.assertError(HttpException.class);
        subscriber.assertNoValues();
        subscriber.assertUnsubscribed();
        List<Throwable> exceptions = subscriber.getOnErrorEvents();
        //noinspection all
        Assert.assertEquals(500, ((HttpException) exceptions.get(0)).code());
    }

    /**
     * Ensures a 404 exception is thrown as an event through the observable stream
     */
    @Test
    public void testRepositoryGot404Response() {
        // Preconditions
        TestSubscriber<List<Comment>> subscriber = new TestSubscriber<>();

        // Attaches the subscriber and executes the Observable.
        repository.getComments(2).subscribe(subscriber);

        // We ensure we've got 404 Not Found in the onError method of the Subscriber.
        subscriber.assertError(HttpException.class);
        subscriber.assertNoValues();
        subscriber.assertUnsubscribed();
        List<Throwable> exceptions = subscriber.getOnErrorEvents();
        //noinspection all
        Assert.assertEquals(404, ((HttpException) exceptions.get(0)).code());
    }

}
