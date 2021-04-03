package com.example.android.architecture.blueprints.todoapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class StatisticsViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var tasksRepository: FakeRepository

    @Before
    fun setupStatisticsViewModel() {
        // Initialise the repository with no tasks.
        tasksRepository = FakeRepository()

        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadTasks_Loading() {
        // Neste momento o dispatcher está pausado ou seja qualquer função suspensa que tente ser executada vai ficar parada
        mainCoroutineRule.pauseDispatcher()
        // O código que remove o ícone de loading está dentro da coroutine
        statisticsViewModel.refresh()
        // Then progress indicator is shown.
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(true))
        //Libera a ação do dispather para executar as coroutines
        mainCoroutineRule.resumeDispatcher()
        // Then progress indicator is hidden.
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun empty() {
        listOf<Any>()[0];
    }

    @get:Rule
    var thrown : ExpectedException = ExpectedException.none();

    @Test
    fun shouldTestExceptionMessage(){
        val list  = listOf<Any>();
        thrown.expect(IndexOutOfBoundsException::class.java)
        thrown.expectMessage("Empty list doesn't contain element at index 0.")
        list[0]; // execution will never get past this line
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_callErrorToDisplay() {
        // Make the repository return errors.
        tasksRepository.setReturnError(true)
        statisticsViewModel.refresh()

        // Then empty and error are true (which triggers an error message to be shown).
        assertThat(statisticsViewModel.empty.getOrAwaitValue(), `is`(true))
        assertThat(statisticsViewModel.error.getOrAwaitValue(), `is`(true))
    }
}