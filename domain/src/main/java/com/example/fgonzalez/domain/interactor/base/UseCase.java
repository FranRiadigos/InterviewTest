/*******************************************************************************
 * Copyright (c) 2017 Francisco Gonzalez-Armijo Riádigos
 * Based on Fernando Cejas Open Source Project
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

package com.example.fgonzalez.domain.interactor.base;

import com.example.fgonzalez.domain.executor.ThreadExecutor;

import rx.Observable;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 */
public abstract class UseCase<T, Params> {

  private final ThreadExecutor subscriberThread;
  private final ThreadExecutor observerThread;
  private       Action0        onSubscribe;
  private       Action0        onTerminate;

  public final static String NULL_PARAMETER = "%s is null";

  protected UseCase(ThreadExecutor subscriberThread,
                    ThreadExecutor observerThread) {
    this.subscriberThread = subscriberThread;
    this.observerThread = observerThread;
  }

  public static class NullParameterException extends RuntimeException {

    public NullParameterException(Class<?> clazz) {
      super(String.format(NULL_PARAMETER, clazz.getSimpleName()));
    }

  }

  /**
   * Builds the provided {@link Observable} and performs some transformation on it.
   *
   * @return The Observable with any transformation applied.
   */
  private Observable.Transformer<T, T> buildUseCaseObservable() {
    return observable -> {
      Observable<T> observableBuilder = observable
              .subscribeOn(subscriberThread.getScheduler())
              .observeOn(observerThread.getScheduler());
      if (onSubscribe != null) {
        observableBuilder = observableBuilder.doOnSubscribe(onSubscribe);
      }
      if (onTerminate != null) {
        observableBuilder = observableBuilder
                .doOnNext(t -> onTerminate.call())
                .doOnError(throwable -> onTerminate.call());
      }
      return observableBuilder;
    };
  }

  /**
   * Implement this method in your custom UseCase in order to provide the final {@link Observable}.
   *
   * @param params The Params.
   * @return The provided Observable.
   */
  public abstract Observable<T> provideObservable(Params params);

  /**
   * Allows you to apply and Action to the Observable when it subscribes.
   *
   * @param action Action to be applied when the Observable subscribes.
   */
  final public void onSubscribe(final Action0 action) {
    this.onSubscribe = action;
  }

  /**
   * Allows you to apply and Action to the Observable when it terminates.
   *
   * This action will be fired always no mather whether an Exception happens.
   *
   * @param action Action to be applied when the Observable terminates.
   */
  final public void onTerminate(final Action0 action) {
    this.onTerminate = action;
  }

  /**
   * Exposes the provided {@link Observable}.
   *
   * This is a handful method that allow you, for instance, to chain more Observables or apply a map.
   *
   * @param params The Params.
   * @return The provided Observable.
   */
  public Observable<T> asObservable(Params params) {
    Observable<T> ob = provideObservable(params);
    if (ob == null) {
      return Observable.error(new NullPointerException(
              String.format("provideObservable() method of %s class returned null",
                      this.getClass().getSimpleName())));
    }
    return ob.compose(buildUseCaseObservable());
  }

  /**
   * Exposes the provided {@link Observable} without parameters.
   *
   * This is a handful method that allow you, for instance, to chain more Observables or apply a map.
   *
   * @return The provided Observable.
   */
  public Observable<T> asObservable() {
    return asObservable(null);
  }

  /**
   * Allows you to subscribe directly to the provided {@link rx.Observable}.
   *
   * @param subscriber The subscriber object.
   * @param params The Params.
   * @return The subscription made by the Observable.
   */
  public Subscription execute(Subscriber<T> subscriber, Params params) {
    return asObservable(params).subscribe(subscriber);
  }

  /**
   * Allows you to subscribe directly to the provided {@link rx.Observable} without parameters.
   *
   * @param subscriber The subscriber object.
   * @return The subscription made by the Observable.
   */
  public Subscription execute(Subscriber<T> subscriber) {
    return execute(subscriber, null);
  }

  /**
   * Allows you to subscribe directly to the provided {@link rx.Single}.
   *
   * @param singleSubscriber The subscriber object.
   * @param params The Params.
   * @return The subscription made by the Single.
   */
  public Subscription execute(SingleSubscriber<T> singleSubscriber, Params params) {
    return asObservable(params).toSingle().subscribe(singleSubscriber);
  }

  /**
   * Allows you to subscribe directly to the provided {@link rx.Single} without parameters.
   *
   * @param singleSubscriber The subscriber object.
   * @return The subscription made by the Single.
   */
  public Subscription execute(SingleSubscriber<T> singleSubscriber) {
    return execute(singleSubscriber, null);
  }

}
