package com.example.fgonzalez.interviewtest.feature.post.detail;

import com.example.fgonzalez.interviewtest.internal.mvp.contract.Presentable;
import com.example.fgonzalez.interviewtest.internal.mvp.contract.Viewable;

public interface PostDetailsView<T extends Presentable> extends Viewable<T> {

    void render(int commentSize);

}
