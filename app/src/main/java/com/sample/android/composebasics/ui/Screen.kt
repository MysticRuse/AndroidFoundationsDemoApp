package com.sample.android.composebasics.ui

sealed class Screen(val route: String, val title: String) {
    object Home: Screen("home", "Modern Android")
    object BasicsCodeLab: Screen("compose_basic_codelab", "Compose Basics Codelab")
    object ViewModelExperiment: Screen("viewmodel_experiment", "ViewModel Experiment")
    object ComposeExperiments: Screen("compose_experiments", "Compose Experiments")
    object Architecture: Screen("architecture", "Architecture Lab")
    object ComponentsNLifeCycle: Screen("components_n_lifecycle", "Lifecycle & Components")
    object Networking: Screen("networking", "Networking & Persistence")

    object CodingExercises: Screen("coding_exercises", "Coding Exercises")

}