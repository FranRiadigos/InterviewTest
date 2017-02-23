package com.example.fgonzalez.interviewtest;

import android.support.test.espresso.idling.CountingIdlingResource;

import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;

// Based on https://github.com/ReactiveX/RxAndroid/issues/149
public class IdlingResourceScheduler extends Scheduler {
    private CountingIdlingResource countingIdlingResource;

    private Scheduler scheduler;

    public IdlingResourceScheduler(Scheduler scheduler, String resourceName) {
        this.scheduler = scheduler;
        countingIdlingResource = new CountingIdlingResource(resourceName, true);
    }

    @Override
    public Worker createWorker() {
        return new IdlingWorker(scheduler.createWorker());
    }

    public CountingIdlingResource countingIdlingResource() {
        return countingIdlingResource;
    }

    private class IdlingWorker extends Worker {

        private Worker worker;
        private boolean recursive;

        public IdlingWorker(Worker worker) {
            this.worker = worker;
        }

        @Override
        public Subscription schedule(Action0 action) {
            return recursive ?
                   worker.schedule(action) :
                   worker.schedule(decorateAction(action));
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            return recursive ?
                   worker.schedule(action, delayTime, unit) :
                   worker.schedule(decorateAction(action), delayTime, unit);
        }

        @Override
        public Subscription schedulePeriodically(Action0 action, long initialDelay, long period, TimeUnit unit) {
            recursive = true;
            return worker.schedulePeriodically(decorateAction(action), initialDelay, period, unit);
        }

        @Override
        public void unsubscribe() {
            worker.unsubscribe();
        }

        @Override
        public boolean isUnsubscribed() {
            return worker.isUnsubscribed();
        }

        private Action0 decorateAction(Action0 action) {
            return () -> {
                countingIdlingResource.increment();
                try {
                    action.call();
                } finally {
                    countingIdlingResource.decrement();
                }
            };
        }
    }
}
