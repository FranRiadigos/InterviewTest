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
import com.example.fgonzalez.data.service.UserService;
import com.example.fgonzalez.data.service.adapter.RetrofitFactory;
import com.example.fgonzalez.domain.User;
import com.example.fgonzalez.domain.repository.UserRepository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;

import java.util.List;

import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.adapter.rxjava.HttpException;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Testing User repository and service.
 *
 * As part of the Data layer, we will test that the behaviour of Repositories and Responses
 * from Service requests are working as expected.
 */
@RunWith(RxJavaTestRunner.class)
public class UserServiceTest extends BaseTestCase {

    @Spy RetrofitFactory retrofitFactory;

    private UserRepository repository;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockResponseDispatcher.reset();
        server.setDispatcher(MockResponseDispatcher.DISPATCHER);
        server.start();
        repository = new UserRepositoryImpl(retrofitFactory, server.url("").url().toString());
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Checks whether the User repository and service is working as expected and the behaviour of the code have not changed.
     */
    @Test
    public void testRepositoryGotResponseOkForUser() throws InterruptedException {
        // Preconditions
        TestSubscriber<User> subscriber = new TestSubscriber<>();

        // Attaches the subscriber and executes the Observable.
        repository.get(1).subscribe(subscriber);
        RecordedRequest request = server.takeRequest();

        // Checks whether these methods from inner objects are called.
        verify(retrofitFactory).setBaseUrl(anyString());
        verify(retrofitFactory).create(UserService.class);
        verify(retrofitFactory).getClient();

        // Ensures there aren't new changes on the Rest adapter structure
        verifyNoMoreInteractions(retrofitFactory);

        // Ensures Observer and Requests are working as expected
        List<User> events = subscriber.getOnNextEvents();
        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);
        subscriber.assertCompleted();
        subscriber.assertUnsubscribed();
        Assert.assertEquals(1, server.getRequestCount());
        Assert.assertEquals("/users/1", request.getPath());
        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals("Sincere@april.biz", events.get(0).email);
    }

    /**
     * Ensures a 500 exception is thrown as an event through the observable stream
     */
    @Test
    public void testRepositoryGot500Response() {
        // Preconditions
        TestSubscriber<User> subscriber = new TestSubscriber<>();
        MockResponseDispatcher.RETURN_500 = true;

        // Attaches the subscriber and executes the Observable.
        repository.get(1).subscribe(subscriber);

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
        TestSubscriber<User> subscriber = new TestSubscriber<>();

        // Attaches the subscriber and executes the Observable.
        repository.get(2).subscribe(subscriber);

        // We ensure we've got 404 Not Found in the onError method of the Subscriber.
        subscriber.assertError(HttpException.class);
        subscriber.assertNoValues();
        subscriber.assertUnsubscribed();
        List<Throwable> exceptions = subscriber.getOnErrorEvents();
        //noinspection all
        Assert.assertEquals(404, ((HttpException) exceptions.get(0)).code());
    }

}
