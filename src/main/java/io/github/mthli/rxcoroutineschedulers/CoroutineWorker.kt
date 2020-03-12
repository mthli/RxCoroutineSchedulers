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

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.EmptyDisposable
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * Refer to [io.reactivex.internal.schedulers.NewThreadWorker] implementation.
 */
internal class CoroutineWorker(
    private val dispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope
) : Scheduler.Worker() {

    private val supervisorJob = SupervisorJob(scope.coroutineContext[Job])

    @Volatile
    private var isDisposed = false

    override fun isDisposed(): Boolean = isDisposed

    override fun dispose() {
        if (!isDisposed) {
            isDisposed = true
            supervisorJob.cancel()
        }
    }

    override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
        if (isDisposed || supervisorJob.isCancelled) {
            return EmptyDisposable.INSTANCE
        }

        val decoratedRun = RxJavaPlugins.onSchedule(run)
        val job = scope.launch(supervisorJob) {
            withContext(dispatcher) {
                if (delay > 0L) delay(unit.toMillis(delay))
                decoratedRun.run()
            }
        }

        return JobDisposable(job)
    }
}
