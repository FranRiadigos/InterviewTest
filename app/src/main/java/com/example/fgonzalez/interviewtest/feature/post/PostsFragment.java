package com.example.fgonzalez.interviewtest.feature.post;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.fgonzalez.domain.Post;
import com.example.fgonzalez.interviewtest.AndroidApplication;
import com.example.fgonzalez.interviewtest.R;
import com.example.fgonzalez.interviewtest.feature.post.adapter.PostAdapter;
import com.example.fgonzalez.interviewtest.feature.post.detail.PostDetailsFragment;
import com.example.fgonzalez.interviewtest.feature.post.detail.PostDetailsFragmentBuilder;
import com.example.fgonzalez.interviewtest.internal.di.components.DaggerFragmentComponent;
import com.example.fgonzalez.interviewtest.internal.di.components.FragmentComponent;
import com.example.fgonzalez.interviewtest.internal.di.modules.FragmentModule;
import com.example.fgonzalez.interviewtest.internal.mvp.BaseFragment;
import com.example.fgonzalez.interviewtest.util.Navigator;
import com.example.fgonzalez.interviewtest.view.decorator.ItemClickSupport;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class PostsFragment extends BaseFragment<PostsPresenter> implements PostsView<PostsPresenter> {

    @BindView(R.id.post_list)
    RecyclerView rvPosts;
    @BindView(R.id.loading)
    ProgressBar loading;

    @Inject
    Navigator navigator;
    PostAdapter postAdapter;

    public static PostsFragment newInstance() {
        return new PostsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInjector();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(R.string.post_list);
        initList();
    }

    private void initInjector() {
        FragmentComponent component = DaggerFragmentComponent.builder()
                .applicationComponent(AndroidApplication.getComponent(getContext()))
                .fragmentModule(new FragmentModule(this)) // Support for future providers
                .build();

        component.inject(this);
    }

    private void initList() {
        postAdapter = new PostAdapter(getContext());
        rvPosts.setAdapter(postAdapter);

        rvPosts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvPosts.setLayoutManager(llm);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvPosts.addItemDecoration(itemDecoration);

        ItemClickSupport.addTo(rvPosts).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Post post = postAdapter.getItemByPosition(position);

                    PostDetailsFragment fragment = PostDetailsFragmentBuilder.newPostDetailsFragment(post);

                    navigator.replaceFragment(getActivity(), R.id.main_container, fragment);
                }
        );
    }

    @Override
    public void render(List<Post> postList) {
        postAdapter.setPostList(postList);
        postAdapter.notifyItemRangeInserted(0, postList.size());
    }

    @Inject
    @Override
    public void injectPresenter(PostsPresenter presenter) {
        super.injectPresenter(presenter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.post_list_fragment;
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
        ViewCompat.setAlpha(loading, 0);
        ViewCompat.animate(rvPosts).alpha(0);
        ViewCompat.animate(loading).alpha(1);
    }

    @Override
    public void hideLoading() {
        ViewCompat.animate(rvPosts).alpha(1);
        ViewCompat.animate(loading).alpha(0);
    }
}
