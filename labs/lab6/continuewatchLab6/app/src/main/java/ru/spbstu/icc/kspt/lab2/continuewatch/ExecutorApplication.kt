package ru.spbstu.icc.kspt.lab2.continuewatch

import android.app.Application
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ExecutorApplication: Application() {
    val executor: ExecutorService = Executors.newSingleThreadExecutor()

    fun shutdownAndAwaitTermination() {
        executor.shutdown() // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(60, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow() // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(60, TimeUnit.MILLISECONDS)
                ) System.err.println("Pool did not terminate")
            }
        } catch (ie: InterruptedException) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow()
            // Preserve interrupt status
            Thread.currentThread().interrupt()
        }
    }
}