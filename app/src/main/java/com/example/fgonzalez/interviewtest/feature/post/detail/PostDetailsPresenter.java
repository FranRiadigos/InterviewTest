package com.example.fgonzalez.interviewtest.feature.post.detail;

import com.example.fgonzalez.domain.Post;
import com.example.fgonzalez.domain.interactor.GetCommentSize;
import com.example.fgonzalez.interviewtest.R;
import com.example.fgonzalez.interviewtest.internal.di.PerFragment;
import com.example.fgonzalez.interviewtest.internal.mvp.BasePresenter;

import javax.inject.Inject;

import rx.SingleSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class PostDetailsPresenter extends BasePresenter<PostDetailsView> {

    private GetCommentSize getCommentSizeUseCase;

    private Post post;

    CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    public PostDetailsPresenter(GetCommentSize getCommentSizeUseCase) {
        this.getCommentSizeUseCase = getCommentSizeUseCase;
        attachLoading(this.getCommentSizeUseCase);
    }

    @Override
    public void onViewCreated() {
        Subscription subscription = getCommentSizeUseCase.execute(new SingleSubscriber<Integer>() {
            @Override
            public void onSuccess(Integer value) {
                getView().render(value);
            }

            @Override
            public void onError(Throwable error) {
                getView().displayError(R.string.common_error);
            }
        }, GetCommentSize.Params.with(post.id));

        compositeSubscription.add(subscription);
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public void onStop() {
        compositeSubscription.clear();
        super.onStop();
    }
}
