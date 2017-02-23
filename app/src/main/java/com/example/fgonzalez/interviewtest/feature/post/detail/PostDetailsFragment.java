package com.example.fgonzalez.interviewtest.feature.post.detail;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.fgonzalez.domain.Post;
import com.example.fgonzalez.interviewtest.AndroidApplication;
import com.example.fgonzalez.interviewtest.DispatcherActivity;
import com.example.fgonzalez.interviewtest.R;
import com.example.fgonzalez.interviewtest.internal.di.components.DaggerFragmentComponent;
import com.example.fgonzalez.interviewtest.internal.di.components.FragmentComponent;
import com.example.fgonzalez.interviewtest.internal.di.modules.FragmentModule;
import com.example.fgonzalez.interviewtest.internal.mvp.BaseFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import javax.inject.Inject;

import butterknife.BindView;

@FragmentWithArgs
public class PostDetailsFragment extends BaseFragment<PostDetailsPresenter> implements PostDetailsView<PostDetailsPresenter> {

    @BindView(R.id.avatar)
    SimpleDraweeView avatar;
    @BindView(R.id.txt_title)
    TextView title;
    @BindView(R.id.txt_body)
    TextView body;
    @BindView(R.id.txt_username)
    TextView username;
    @BindView(R.id.txt_comments)
    TextView comments;

    @Arg
    Post post;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInjector();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(post.title);
        initViews();
    }

    private void initInjector() {
        FragmentComponent component = DaggerFragmentComponent.builder()
                .applicationComponent(AndroidApplication.getComponent(getContext()))
                .fragmentModule(new FragmentModule(this)) // Support for future providers
                .build();

        component.inject(this);
    }

    private void initViews() {
        Uri uri = Uri.parse(post.user.avatar);
        avatar.setImageURI(uri);
        title.setText(post.title);
        body.setText(post.body);
        username.setText(post.user.name);
        comments.setText("--");
    }

    @Override
    public void render(int commentSize) {
        comments.setText(String.valueOf(commentSize));
    }

    @Inject
    @Override
    public void injectPresenter(PostDetailsPresenter presenter) {
        presenter.setPost(post);
        super.injectPresenter(presenter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.post_details_fragment;
    }

    @Override
    public void showLoading() {
        ((DispatcherActivity)getActivity()).showProgressIndeterminate(true);
    }

    @Override
    public void hideLoading() {
        ((DispatcherActivity)getActivity()).showProgressIndeterminate(false);
    }
}
