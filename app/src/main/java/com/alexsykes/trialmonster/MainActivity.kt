package com.alexsykes.trialmonster

import android.app.Application
import android.app.DownloadManager
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexsykes.trialmonster.ui.theme.ScoreMonsterKTheme
import com.android.volley.Response
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class MainActivity : ComponentActivity() {
    private var requestQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestQueue = Volley.newRequestQueue(this)
        jsonParse()
        setContent {
            ScoreMonsterKTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val owner = LocalViewModelStoreOwner.current

                    owner?.let {
                        val viewModel: MainViewModel = viewModel(
                            it,
                            "MainViewModel",
                            MainViewModelFactory(
                                LocalContext.current.applicationContext
                                        as Application )
                        )
                        ScreenSetup(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenSetup(viewModel: MainViewModel) {

    val allTrials by viewModel.allTrials.observeAsState(listOf())
    val searchResults by viewModel.searchResults.observeAsState(listOf())
    MainScreen(
        allTrials = allTrials,
        searchResults = searchResults,
        viewModel = viewModel
    )
}

@Composable
fun MainScreen(
    allTrials: List<Trial>,
    searchResults: List<Trial>,
    viewModel: MainViewModel) {

    var trialName by remember { mutableStateOf("") }
    var trialClub by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }

    val onTrialTextChange = { text: String ->
        trialName = text
    }

    val onTrialClubChange = { text: String ->
        trialClub = text
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        CustomTextField(
            title = "Trial",
            textState = trialName,
            onTextChange = onTrialTextChange,
            keyboardType = KeyboardType.Text
        )

        CustomTextField(
            title = "Club",
            textState = trialClub,
            onTextChange = onTrialClubChange,
            keyboardType = KeyboardType.Text
        )


        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(onClick = {
                if (trialClub.isNotEmpty() && trialName.isNotEmpty()) {
                    viewModel.insertTrial(
                        Trial(trialName, trialClub)
                    )
                }
            }) {
                Text("Add")
            }

            Button(onClick = {
                searching = true
                viewModel.findTrial(trialName)
            }) {
                Text("Search")
            }

            Button(onClick = {
                searching = false
                viewModel.deleteTrial(trialName)
            }) {
                Text("Delete")
            }

            Button(onClick = {
                searching = false
                trialName = ""
                trialClub = ""
            }) {
                Text("Clear")
            }
        }

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            val list = if (searching) searchResults else allTrials

            item {
                TitleRow( head2 = "Trial", head3 = "Club")
            }

            items(list) {
                    trial ->
//                TrialRow(id = 2, name = trialName, trialClub = club)
                TrialRow( name = trial.trialName, club = trial.club)
            }
        }
    }
}

@Composable
fun TitleRow(  head2: String, head3: String) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(head2, color = Color.White,
            modifier = Modifier
                .weight(0.2f))
        Text(head3, color = Color.White,
            modifier = Modifier
                .weight(0.2f))
    }
}

@Composable
fun TrialRow( name: String, club: String)  {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(name, modifier = Modifier.weight(0.2f))
        Text(club, modifier = Modifier.weight(0.2f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    title: String,
    textState: String,
    onTextChange: (String) -> Unit,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = textState,
        onValueChange = { onTextChange(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        singleLine = true,
        label = { Text(title)},
        modifier = Modifier.padding(10.dp),
        textStyle = TextStyle(fontWeight = FontWeight.Bold,
            fontSize = 16.sp )
    )
}

private fun jsonParse() {
    val  requestQueue: RequestQueue = RequestQueue(this)
    val  url: String = "https://android.trialmonster.uk/getAndroidPastTrials.php"
    val request = JsonObjectRequest(Request.Method.GET, url, null, {
            response ->try {
        val jsonArray = response.getJSONArray("employees")
        for (i in 0 until jsonArray.length()) {
            val employee = jsonArray.getJSONObject(i)
            val firstName = employee.getString("firstname")
            val age = employee.getInt("age")
            val mail = employee.getString("mail")
        }
    } catch (e: JSONException) {
        e.printStackTrace()
    }
    }, { error -> error.printStackTrace() })
    requestQueue?.add(request)
}


class MainViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}