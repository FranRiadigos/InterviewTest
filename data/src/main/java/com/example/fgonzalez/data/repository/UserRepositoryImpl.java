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

package com.example.fgonzalez.data.repository;

import com.example.fgonzalez.data.service.UserService;
import com.example.fgonzalez.data.service.adapter.RestFactory;
import com.example.fgonzalez.domain.User;
import com.example.fgonzalez.domain.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Single;

/**
 * User Repository implementation which should discriminate between cloud and database requests.
 */
@Singleton
public class UserRepositoryImpl implements UserRepository {
    private final RestFactory api;
    private final String apiUrl;

    @Inject
    UserRepositoryImpl(RestFactory api, @Named("ApiUrl") String apiUrl) {
        this.api = api;
        this.apiUrl = apiUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Single<User> get(final long id) {
        this.api.setBaseUrl(apiUrl); // Ability to change the API Url at any time.
        return this.api.create(UserService.class).getUser(id);
    }

}
