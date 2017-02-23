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

package com.example.fgonzalez.interviewtest.internal.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fgonzalez.interviewtest.internal.mvp.contract.Presentable;
import com.example.fgonzalez.interviewtest.internal.mvp.contract.Viewable;
import com.hannesdorfmann.fragmentargs.FragmentArgs;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<T extends Presentable> extends Fragment implements Viewable<T> {

    private Unbinder unbinder;
    protected T presenter;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(@StringRes int resource) {
        setTitle(getString(resource));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(@NonNull CharSequence msg) {
        getActivity().setTitle(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
        getPresenter().onStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
        setRetainInstance(true);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        //noinspection unchecked
        getPresenter().attachView(this);
        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().onViewCreated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView() {
        getPresenter().detachView();
        unbinder.unbind();
        super.onDestroyView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        getPresenter().onStop();
        super.onStop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        presenter = null;
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayError(String message) {
        View rootContent = ButterKnife.findById(getActivity(), android.R.id.content);
        Snackbar.make(rootContent, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayError(int messageId) {
        displayError(getString(messageId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showLoading() {
        // no-op by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideLoading() {
        // no-op by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getPresenter() {
        return presenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void injectPresenter(T presenter) {
        this.presenter = presenter;
    }

    protected abstract int getLayoutId();
}
