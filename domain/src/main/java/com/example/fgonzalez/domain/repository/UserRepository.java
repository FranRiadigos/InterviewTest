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

package com.example.fgonzalez.domain.repository;

import com.example.fgonzalez.domain.User;

import rx.Single;

/**
 * User Repository contract.
 */
public interface UserRepository {

    /**
     * Gets a {@link Single} which will emit a {@link User}.
     *
     * @param userId long
     * @return Single List
     */
    Single<User> get(final long userId);

}
