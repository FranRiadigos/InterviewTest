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

package com.example.fgonzalez.interviewtest.feature;

import com.example.fgonzalez.domain.Post;
import com.example.fgonzalez.domain.executor.ThreadExecutor;
import com.example.fgonzalez.domain.interactor.GetPostList;
import com.example.fgonzalez.domain.repository.PostRepository;
import com.example.fgonzalez.domain.repository.UserRepository;
import com.example.fgonzalez.interviewtest.feature.post.PostsPresenter;
import com.example.fgonzalez.interviewtest.feature.post.PostsView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.SingleSubscriber;
import rx.schedulers.TestScheduler;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Testing PostsPresenter.
 *
 * As a unit test, we only focus on testing the behaviour of the Presenter is working as expected.
 */
@RunWith(MockitoJUnitRunner.class)
public class PostsPresenterTest extends BaseUnitTestCase {

    @Mock private ThreadExecutor mockThreadExecutor;
    @Mock private PostRepository mockPostRepository;
    @Mock private UserRepository mockUserRepository;

    private GetPostList     model;
    private PostsView       view;
    private PostsPresenter  presenter;

    @Before
    public void setup() throws Exception {
        super.setUp();
        view = mock(PostsView.class);
        model = spy(new GetPostList(mockPostRepository, mockUserRepository,
                mockThreadExecutor, mockThreadExecutor, mockThreadExecutor, 1));

        // PostsPresenter is the class we want to test
        presenter = new PostsPresenter(model);
        presenter.attachView(view);

        //noinspection unchecked
        doCallRealMethod().when(model).execute(any(SingleSubscriber.class));
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Checks that either the Model and the View are fired when the Presenter starts its execution.
     */
    @Test
    public void testPresenterOk() {
        // Preconditions
        List<Post> postList = Collections.singletonList(mock(Post.class));
        TestScheduler testScheduler = new TestScheduler();
        given(mockThreadExecutor.getScheduler()).willReturn(testScheduler);
        doReturn(Observable.just(postList)).when(model).provideObservable(any(Void.class));

        // Simulates the onViewCreated method is called from the BaseFragment class
        presenter.onViewCreated();
        testScheduler.triggerActions();

        // Checks model and view methods are fired respectively
        //noinspection unchecked
        verify(model).execute(any(SingleSubscriber.class));
        verify(model).asObservable(any(Void.class));
        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view).render(anyListOf(Post.class));
    }
}
