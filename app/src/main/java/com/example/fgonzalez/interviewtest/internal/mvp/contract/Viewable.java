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
import android.support.annotation.StringRes;

/**
 * Android contract for every MVP View
 */
public interface Viewable<T> {

    /**
     * Sets the Title of the Screen
     */
    void setTitle(@StringRes int resource);

    /**
     * Sets the Title of the Screen
     */
    void setTitle(@NonNull CharSequence msg);

    /**
     * Every Viewable must be able to access to its attached Presenter
     *
     * @return Presentable
     */
    T getPresenter();

    /**
     * Every Viewable must be able to inject its Presenter
     *
     * @param presenter Presentable
     */
    void injectPresenter(T presenter);

    /**
     * Every Viewable must have a error message system
     */
    void displayError(String message);

    /**
     * Every Viewable must have a error message system
     */
    void displayError(int messageId);

    /**
     * Every Viewable must implement one show loading feature
     */
    void showLoading();

    /**
     * Every Viewable must implement one hide loading feature
     */
    void hideLoading();

}
