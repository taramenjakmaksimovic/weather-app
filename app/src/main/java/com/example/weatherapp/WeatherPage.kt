package com.example.weatherapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.WeatherModel
import com.example.weatherapp.ui.theme.CardColor2
import com.example.weatherapp.ui.theme.DayColor
import com.example.weatherapp.ui.theme.DayColor2
import com.example.weatherapp.ui.theme.MyBlue
import com.example.weatherapp.ui.theme.MyGray
import com.example.weatherapp.ui.theme.NightColor



@Composable
fun WeatherPage(viewModel: WeatherViewModel) {
    var city by remember {
        mutableStateOf("")
    }

    var backgroundBrush by remember{
        mutableStateOf(Brush.verticalGradient(listOf(DayColor, DayColor)))
    }

    var textColor by remember {
        mutableStateOf(MyBlue)
    }

    val weatherResult = viewModel.weatherResult.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(weatherResult.value, city) {
        backgroundBrush = when {
            city.isEmpty() -> Brush.verticalGradient(listOf(DayColor, DayColor))
            weatherResult.value is NetworkResponse.Success -> {
                val isDaytime =
                    (weatherResult.value as NetworkResponse.Success).data.current.is_day.toInt() == 1
                if (isDaytime) {
                    Brush.verticalGradient(listOf(DayColor, DayColor2 ))
                } else {
                    Brush.verticalGradient(listOf(NightColor, Color.Black))
                }
            }
            else -> Brush.verticalGradient(listOf(DayColor, DayColor))
        }
    }

    LaunchedEffect(weatherResult.value, city) {
        textColor = when {
            city.isEmpty() -> MyBlue
            weatherResult.value is NetworkResponse.Success -> {
                val isDaytime =
                    (weatherResult.value as NetworkResponse.Success).data.current.is_day.toInt() == 1
                if (isDaytime) MyBlue else Color.White
            }

            else -> MyBlue
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundBrush)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = city,
                    onValueChange = {
                        city = it
                    },
                    label = {
                        Text(
                            text = "Search for any location",
                            color = textColor,
                            fontSize = 20.sp)
                    },
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        color = textColor
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        cursorColor = textColor,
                        focusedLabelColor = textColor,
                        unfocusedLabelColor = textColor,
                        focusedBorderColor = textColor,
                        unfocusedBorderColor = textColor
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = {
                    viewModel.getData(city)
                    keyboardController?.hide()
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search for any location",
                        modifier = Modifier.size(40.dp),
                        tint = MyBlue
                    )
                }

            }

            when (val result = weatherResult.value) {
                is NetworkResponse.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = result.message,
                            color = Color.Red,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                    }
                }

                NetworkResponse.Loading -> {
                    CircularProgressIndicator()
                }

                is NetworkResponse.Success -> {
                    WeatherDetails(data = result.data, textColor)
                }

                null -> {

                }
            }

        }
    }
}

@Composable
fun WeatherDetails(data: WeatherModel, textColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location icon",
                modifier = Modifier.size(40.dp),
                tint = MyBlue
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${data.location.name}, ${data.location.country}",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                color=textColor
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text="Feels like: " + data.current.feelslike_c + " °C",
            fontSize = 20.sp,
            color = MyGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text=" ${data.current.temp_c} °C",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color=textColor
        )
        Spacer(modifier = Modifier.height(20.dp))
        AsyncImage(
            modifier = Modifier.size(160.dp),
            model = "https:${data.current.condition.icon}".replace("64x64","128x128"),
            contentDescription = "Condition icon"
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text=data.current.condition.text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MyBlue,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(68.dp))
        Card(
            colors = CardDefaults.cardColors(
                containerColor = CardColor2
            )
        ){
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Humidity", data.current.humidity + " %", R.drawable.drop)
                    WeatherKeyVal("Wind speed", data.current.wind_kph + " km/h", R.drawable.air)

                }
                Row(
                    modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("UV", data.current.uv, R.drawable.sun)
                    WeatherKeyVal("Pressure", data.current.pressure_mb+ " mb", R.drawable.pressure)

                }
                Row(
                    modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Local time", data.location.localtime.split(" ")[1], null)
                    WeatherKeyVal("Local date", data.location.localtime.split(" ")[0]
                        .split("-").reversed().joinToString("-"), null)

                }

            }

        }
    }

}

@Composable
fun WeatherKeyVal(key: String, value: String, icon: Int? = null) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = key,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text=key,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Text(
            text=value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color= MyBlue
        )

    }

}