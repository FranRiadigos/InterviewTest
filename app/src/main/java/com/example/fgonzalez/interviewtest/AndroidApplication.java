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

package com.example.fgonzalez.interviewtest;

import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.example.fgonzalez.interviewtest.internal.di.components.ApplicationComponent;
import com.example.fgonzalez.interviewtest.internal.di.components.DaggerApplicationComponent;
import com.example.fgonzalez.interviewtest.internal.di.modules.ApplicationModule;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;

public class AndroidApplication extends Application {

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    protected ApplicationComponent applicationComponent;

    @Override public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
        initializeInjector();
        Fresco.initialize(this);
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    protected void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }

    public static ApplicationComponent getComponent(Context context) {
        return ((AndroidApplication)context.getApplicationContext()).getApplicationComponent();
    }
}
