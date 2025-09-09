package com.example.catalist.core.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.catalist.features.leaderboard.LeaderboardScreen
import com.example.catalist.features.profile.ProfileScreen
import com.example.catalist.features.registration.ui.RegistrationScreen
import com.example.catalist.features.registration.domain.UserCheckViewModel
import com.example.catalist.features.ui.details.BreedDetailScreen
import com.example.catalist.features.ui.details.BreedDetailsViewModel
import com.example.catalist.features.ui.gallery.BreedGalleryScreen
import com.example.catalist.features.ui.gallery.BreedGalleryViewModel
import com.example.catalist.features.ui.list.CatsListScreen
import com.example.catalist.features.ui.list.CatsListViewModel
import com.example.catalist.features.ui.viewer.PhotoViewerScreen
import com.example.catalist.features.ui.viewer.PhotoViewerViewModel
import com.example.catalist.features.quiz.QuizScreen
import com.example.catalist.features.quiz.QuizStartScreen
import com.example.catalist.features.quiz.QuizViewModel

private const val REGISTRATION_ROUTE = "registration"
private const val BREEDS_ROUTE = "breeds"
private const val BREED_DETAILS_ROUTE = "breed_details"
private const val GALLERY_ROUTE = "gallery"
private const val PHOTO_VIEWER_ROUTE = "photo_viewer"
private const val BREED_ID_ARG = "breedId"
private const val PHOTO_INDEX_ARG = "photoIndex"
private const val QUIZ_RESULTS_ROUTE = "quiz_results"
private const val QUIZ_START_ROUTE = "quiz_start"
private const val QUIZ_GAME_ROUTE = "quiz_game"

enum class Tabs(val icon: ImageVector, val title: String) {
    Breeds(Icons.Default.Home, "Breeds"),
    Quiz(Icons.Default.QuestionMark, "Quiz"),
    Leaderboard(Icons.Default.Leaderboard, "Leaderboard"),
    Profile(Icons.Default.Person, "Profile")
}

private fun NavController.navigateToBreedDetails(breedId: String) {
    this.navigate(route = "$BREED_DETAILS_ROUTE/$breedId")
}

private fun NavController.navigateToGallery(breedId: String) {
    this.navigate(route = "$GALLERY_ROUTE/$breedId")
}

private fun NavController.navigateToPhotoViewer(breedId: String, photoIndex: Int) {
    this.navigate(route = "$PHOTO_VIEWER_ROUTE/$breedId/$photoIndex")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppMainNavigation() {
    val userCheckViewModel: UserCheckViewModel = hiltViewModel()
    val isLoggedIn = userCheckViewModel.isLoggedIn.collectAsState(initial = false).value

    if (!isLoggedIn) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = REGISTRATION_ROUTE
        ) {
            composable(REGISTRATION_ROUTE) {
                RegistrationScreen(
                    onRegistrationComplete = {
                        // Nakon registracije, korisnik će biti automatski ulogovan
                    }
                )
            }
        }
    } else {
        var selectedTab by rememberSaveable { mutableStateOf(Tabs.Breeds) }

        val breedsNavController = rememberNavController()
        val quizNavController = rememberNavController()
        val leaderboardNavController = rememberNavController()
        val profileNavController = rememberNavController()

        Scaffold(
            bottomBar = {
                NavigationBar {
                    Tabs.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(tab.title) }
                        )
                    }
                }
            }
        ) { padding ->
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(300)
                        ) with slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(300)
                        )
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(300)
                        ) with slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(300)
                        )
                    }
                }
            ) { currentTab ->
                when (currentTab) {
                    Tabs.Breeds -> {
                        BreedsNavigation(
                            navController = breedsNavController,
                            padding = padding
                        )
                    }
                    Tabs.Quiz -> {
                        QuizNavigation(
                            navController = quizNavController,
                            padding = padding
                        )
                    }
                    Tabs.Leaderboard -> {
                        NavHost(
                            navController = leaderboardNavController,
                            startDestination = "leaderboard"
                        ) {
                            composable("leaderboard") {
                                LeaderboardScreen()
                            }
                        }
                    }
                    Tabs.Profile -> {
                        NavHost(
                            navController = profileNavController,
                            startDestination = "profile"
                        ) {
                            composable("profile") {
                                ProfileScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizNavigation(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = QUIZ_START_ROUTE
    ) {
        quizStart(
            route = QUIZ_START_ROUTE,
            navController = navController
        )

        quizGame(
            route = QUIZ_GAME_ROUTE,
            navController = navController
        )

        composable(QUIZ_RESULTS_ROUTE) {
            // TODO: Implement ResultsScreen
        }
    }
}

@Composable
private fun BreedsNavigation(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = BREEDS_ROUTE
    ) {
        breeds(route = BREEDS_ROUTE, navController = navController)

        breedDetails(
            route = "$BREED_DETAILS_ROUTE/{$BREED_ID_ARG}",
            arguments = listOf(
                navArgument(BREED_ID_ARG) {
                    type = NavType.StringType
                    nullable = false
                }
            ),
            navController = navController
        )

        gallery(
            route = "$GALLERY_ROUTE/{$BREED_ID_ARG}",
            arguments = listOf(
                navArgument(BREED_ID_ARG) {
                    type = NavType.StringType
                    nullable = false
                }
            ),
            navController = navController
        )

        photoViewer(
            route = "$PHOTO_VIEWER_ROUTE/{$BREED_ID_ARG}/{$PHOTO_INDEX_ARG}",
            arguments = listOf(
                navArgument(BREED_ID_ARG) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(PHOTO_INDEX_ARG) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            ),
            navController = navController
        )
    }
}

private fun NavGraphBuilder.breeds(
    route: String,
    navController: NavController
) = composable(route = route) {
    val viewModel = hiltViewModel<CatsListViewModel>()
    CatsListScreen(
        viewModel = viewModel,
        onUserClick = { id -> navController.navigateToBreedDetails(id) }
    )
}

private fun NavGraphBuilder.breedDetails(
    route: String,
    arguments: List<NamedNavArgument>,
    navController: NavController
) = composable(
    route = route,
    arguments = arguments
) { backStackEntry ->
    val breedId = checkNotNull(backStackEntry.arguments?.getString(BREED_ID_ARG)) {
        "breedId parameter is required"
    }

    val viewModel = hiltViewModel<BreedDetailsViewModel>()

    BreedDetailScreen(
        viewModel = viewModel,
        onBack = { navController.navigateUp() },
        onGalleryClick = { navController.navigateToGallery(breedId) }
    )
}

private fun NavGraphBuilder.gallery(
    route: String,
    arguments: List<NamedNavArgument>,
    navController: NavController
) = composable(route = route, arguments = arguments) {
    val breedId = checkNotNull(it.arguments?.getString(BREED_ID_ARG)) {
        "breedId parameter is required"
    }
    val viewModel = hiltViewModel<BreedGalleryViewModel>()

    BreedGalleryScreen(
        viewModel = viewModel,
        onPhotoClick = { _, index ->
            navController.navigateToPhotoViewer(breedId, index)
        },
        onBack = { navController.navigateUp() }
    )
}

private fun NavGraphBuilder.photoViewer(
    route: String,
    arguments: List<NamedNavArgument>,
    navController: NavController
) = composable(route = route, arguments = arguments) {
    val viewModel = hiltViewModel<PhotoViewerViewModel>()
    PhotoViewerScreen(
        viewModel = viewModel,
        onBack = { navController.navigateUp() }
    )
}

private fun NavGraphBuilder.quizStart(
    route: String,
    navController: NavController
) = composable(route = route) {
    QuizStartScreen(
        onStartQuiz = { navController.navigate(QUIZ_GAME_ROUTE) }
    )
}

private fun NavGraphBuilder.quizGame(
    route: String,
    navController: NavController
) = composable(route = route) {
    val viewModel = hiltViewModel<QuizViewModel>()
    QuizScreen(
        viewModel = viewModel,
        onFinish = { navController.navigate(QUIZ_RESULTS_ROUTE) {
            popUpTo(QUIZ_START_ROUTE){inclusive = true}
        }},
        onBack = {
            navController.navigate(QUIZ_START_ROUTE) {
                popUpTo(QUIZ_START_ROUTE) { inclusive = true }
            }
        }
    )
}