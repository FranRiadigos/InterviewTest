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

package com.example.fgonzalez.interviewtest.internal.mvp.contract;

import android.support.annotation.NonNull;

/**
 * Android contract for every MVP Presenter
 */
public interface Presentable<V extends Viewable> {

    /**
     * Every Presentable must implement onStart state
     */
    void onStart();

    /**
     * Every Presentable must implement onViewCreated state
     */
    void onViewCreated();

    /**
     * Every Presentable must implement onResume state
     */
    void onResume();


    /**
     * Every Presentable must implement onPause state
     */
    void onPause();


    /**
     * Every Presentable must implement onStop state
     */
    void onStop();


    /**
     * Every Presentable must attach a Viewable
     *
     * @param viewable Viewable
     */
    void attachView(@NonNull V viewable);


    /**
     * Every Presentable must detach its Viewable
     */
    void detachView();


    /**
     * Every Presentable must be able to access to its attached View
     *
     * @return V Viewable
     */
    V getView();

}
