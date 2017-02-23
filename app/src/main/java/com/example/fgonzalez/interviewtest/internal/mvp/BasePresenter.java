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

import android.support.annotation.NonNull;

import com.example.fgonzalez.domain.interactor.base.UseCase;
import com.example.fgonzalez.interviewtest.internal.mvp.contract.Presentable;
import com.example.fgonzalez.interviewtest.internal.mvp.contract.Viewable;

public class BasePresenter<T extends Viewable> implements Presentable<T> {

    private T viewable;

    protected void attachLoading(UseCase... useCases) {
        for (UseCase useCase : useCases) {
            useCase.onSubscribe(() -> getView().showLoading());
            useCase.onTerminate(() -> getView().hideLoading());
        }
    }

    @Override
    public void onStart() {
        // No-op by default
    }

    @Override
    public void onViewCreated() {
        // No-op by default
    }

    @Override
    public void onResume() {
        // No-op by default
    }

    @Override
    public void onPause() {
        // No-op by default
    }

    @Override
    public void onStop() {
        // No-op by default
    }

    @Override
    public void attachView(@NonNull T viewable) {
        this.viewable = viewable;
    }

    @Override
    public void detachView() {
        this.viewable = null;
    }

    @Override
    public T getView() {
        return viewable;
    }
}
