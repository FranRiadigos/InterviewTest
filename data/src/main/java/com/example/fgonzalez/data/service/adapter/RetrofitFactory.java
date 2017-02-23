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

package com.example.fgonzalez.data.service.adapter;

import com.google.gson.GsonBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Simple implementation of a RestFactory class based on Retrofit 2
 */
@Singleton
public class RetrofitFactory implements RestFactory {

    private static GsonBuilder gsonBuilder = new GsonBuilder();

    private String apiUrl;

    @Inject
    public RetrofitFactory() {}

    @Override
    public <S> S create(Class<S> service) {
        return createBuilder()
                .baseUrl(this.apiUrl)
                .client(getClient())
                .build()
                .create(service);
    }

    @Override
    public OkHttpClient getClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        return clientBuilder.build();
    }

    private Retrofit.Builder createBuilder() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    @Override
    public void setBaseUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
