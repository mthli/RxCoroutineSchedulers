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

import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Job

internal class JobDisposable(private val job: Job) : Disposable {
    @Volatile
    private var isDisposed = false

    override fun isDisposed(): Boolean = isDisposed

    override fun dispose() {
        if (!isDisposed) {
            isDisposed = true
            job.cancel()
        }
    }
}
