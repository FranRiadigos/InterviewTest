package com.example.fgonzalez.interviewtest.feature.post;

import com.example.fgonzalez.domain.Post;
import com.example.fgonzalez.interviewtest.internal.mvp.contract.Presentable;
import com.example.fgonzalez.interviewtest.internal.mvp.contract.Viewable;

import java.util.List;

public interface PostsView<T extends Presentable> extends Viewable<T> {

    void render(List<Post> postList);

}
