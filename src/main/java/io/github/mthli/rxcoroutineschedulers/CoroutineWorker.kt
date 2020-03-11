/*
 * Copyright 2020 Matthew Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mthli.rxcoroutineschedulers

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.disposables.EmptyDisposable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * Refer to [io.reactivex.rxjava3.internal.schedulers.NewThreadWorker] implementation.
 */
internal class CoroutineWorker(
    private val dispatcher: CoroutineDispatcher,
    private val scopeRef: WeakReference<CoroutineScope>
) : Scheduler.Worker() {
    @Volatile
    private var isDisposed = false
    private var job: Job? = null

    override fun isDisposed(): Boolean = isDisposed

    override fun dispose() {
        if (!isDisposed) {
            isDisposed = true
            job?.cancel()
        }
    }

    override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        val scope = scopeRef.get()
        if (scope == null || isDisposed || job?.isCancelled == true) { // job may canceled by scope
            return EmptyDisposable.INSTANCE
        }

        // Should decorated outside launch
        val decoratedRun = RxJavaPlugins.onSchedule(run)
        job = scope.launch {
            withContext(dispatcher) {
                if (delay > 0L) delay(unit.toMillis(delay)) // non-blocking
                decoratedRun.run()
            }
        }

        return job?.let { JobDisposable(it) } ?: throw NullPointerException("schedule job but null")
    }
}
