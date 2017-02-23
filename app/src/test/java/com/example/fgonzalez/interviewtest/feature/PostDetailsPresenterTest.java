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
import com.example.fgonzalez.domain.interactor.GetCommentSize;
import com.example.fgonzalez.domain.repository.PostRepository;
import com.example.fgonzalez.domain.repository.UserRepository;
import com.example.fgonzalez.interviewtest.feature.post.detail.PostDetailsPresenter;
import com.example.fgonzalez.interviewtest.feature.post.detail.PostDetailsView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;
import rx.SingleSubscriber;
import rx.schedulers.TestScheduler;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Testing PostDetailsPresenter.
 *
 * As a unit test, we only focus on testing the behaviour of the Presenter is working as expected.
 */
@RunWith(MockitoJUnitRunner.class)
public class PostDetailsPresenterTest extends BaseUnitTestCase {

    @Mock private ThreadExecutor mockThreadExecutor;
    @Mock private PostRepository mockPostRepository;
    @Mock private UserRepository mockUserRepository;
    @Mock private Post post;

    private GetCommentSize          model;
    private PostDetailsView         view;
    private PostDetailsPresenter    presenter;

    @Before
    public void setup() throws Exception {
        super.setUp();
        view = mock(PostDetailsView.class);
        model = spy(new GetCommentSize(mockPostRepository, mockThreadExecutor, mockThreadExecutor));

        // PostDetailsPresenter is the class we want to test
        presenter = new PostDetailsPresenter(model);
        presenter.setPost(post);
        presenter.attachView(view);

        //noinspection unchecked
        doCallRealMethod().when(model).execute(any(SingleSubscriber.class), any(GetCommentSize.Params.class));
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
        Observable<Integer> result = Observable.just(1);
        TestScheduler testScheduler = new TestScheduler();
        given(mockThreadExecutor.getScheduler()).willReturn(testScheduler);
        doReturn(result).when(model).provideObservable(any(GetCommentSize.Params.class));

        // Simulates the onViewCreated method is called from the BaseFragment class
        presenter.onViewCreated();
        testScheduler.triggerActions();

        // Checks model and view methods are fired respectively
        //noinspection unchecked
        verify(model).execute(any(SingleSubscriber.class), any(GetCommentSize.Params.class));
        verify(model).asObservable(any(GetCommentSize.Params.class));
        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view).render(anyInt());
    }
}
