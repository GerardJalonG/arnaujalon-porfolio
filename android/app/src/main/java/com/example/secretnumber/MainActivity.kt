package com.example.secretnumber

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.secretnumber.ui.theme.SecretnumberTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecretnumberTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Saludo(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    )
                }
            }
        }
    }
}


@Composable
fun Saludo(modifier: Modifier = Modifier) {

    var numeroSecreto by remember { mutableIntStateOf(kotlin.random.Random.nextInt(1, 11)) }

    var numero by rememberSaveable { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("¡Adivina el numero aleatorio!")
        TextField(
            value = numero,
            onValueChange = { nuevo ->
                numero = nuevo.filter { it.isDigit() }
            },
            label = { Text("Tu intento") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
        Button(onClick = {
            if (numero.isNotEmpty()) {
                var num = numero.toIntOrNull()
                mensaje = when {
                    num == null -> "Introduce un número"
                    num == numeroSecreto -> {
                        numeroSecreto = kotlin.random.Random.nextInt(1, 11)
                        numero = ""
                        "🎉 ¡Acertaste! Nuevo número generado."
                    }
                    num < numeroSecreto -> "🔼 Demasiado bajo"
                    else -> "🔽 Demasiado alto"
                }
            } else {
                mensaje = "Escribe un número primero"
            }
        }) {
            Text("Comprobar")
        }
        Text(text = mensaje)

    }
}