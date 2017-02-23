package com.example.fgonzalez.interviewtest.feature.post;

import com.example.fgonzalez.domain.Post;
import com.example.fgonzalez.domain.interactor.GetPostList;
import com.example.fgonzalez.interviewtest.R;
import com.example.fgonzalez.interviewtest.internal.di.PerFragment;
import com.example.fgonzalez.interviewtest.internal.mvp.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.SingleSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class PostsPresenter extends BasePresenter<PostsView> {

    private GetPostList getPostListUseCase;

    CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    public PostsPresenter(GetPostList getPostListUseCase) {
        this.getPostListUseCase = getPostListUseCase;
        attachLoading(this.getPostListUseCase);
    }

    @Override
    public void onViewCreated() {
        Subscription subscription = getPostListUseCase.execute(new SingleSubscriber<List<Post>>() {
            @Override
            public void onSuccess(List<Post> data) {
                getView().render(data);
            }

            @Override
            public void onError(Throwable error) {
                getView().displayError(R.string.common_error);
            }
        });

        compositeSubscription.add(subscription);
    }

    @Override
    public void onStop() {
        compositeSubscription.clear();
        super.onStop();
    }
}
