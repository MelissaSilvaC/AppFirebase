package com.example.appfirestore

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appfirestore.ui.theme.AppFirestoreTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppFirestoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(modifier = Modifier.padding(innerPadding), db)
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun App(modifier: Modifier = Modifier, db : FirebaseFirestore) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(Modifier.fillMaxWidth()){
        Row(Modifier.fillMaxWidth().padding(20.dp)) {  }

        Row(Modifier.fillMaxWidth(), Arrangement.Center){
            Text(text = "App Firebase Firestore")
        }

        Row(Modifier.fillMaxWidth().padding(20.dp)) {  }

        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(0.3f)) {
                Text(text = "Nome:")
            }
            Column {
                TextField(
                    value = name,
                    onValueChange = {name = it }
                )
            }
        }

        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(0.3f)) {
                Text(text = "Telefone:")
            }
            Column {
                TextField(
                    value = phone,
                    onValueChange = {phone = it }
                )
            }
        }
        Row(Modifier.fillMaxWidth().padding(20.dp)) {  }

        Row(Modifier.fillMaxWidth(), Arrangement.Center){
            Button(onClick = {
                val client = hashMapOf(
                    "name" to name,
                    "phone" to phone
                )

                db.collection("Clients")
                    .add(client)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        //limpando os campos após os dados serem gravados
                        name = ""
                        phone = ""
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e) }

            }) {
                Text(text = "Cadastrar")
            }
        }

        Row(Modifier.fillMaxWidth().padding(20.dp)) {  }

        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(0.5f)) {
                Text(text = "Nome:")
            }

            Column(Modifier.fillMaxWidth(0.5f)) {
                Text(text = "Telefone:")
            }
        }

        Row(Modifier.fillMaxWidth()) {
            val clients = mutableStateListOf<HashMap<String, String>>()

            //Atualizando a listagem de forma síncrona com snapshots
            db.collection("Clients")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    // Limpando a lista antes de adicionar novos dados
                    clients.clear()

                    // Percorre os documentos e adiciona na lista
                    snapshots?.forEach { document ->
                        val list = hashMapOf(
                            "name" to "${document.data["name"]}",
                            "phone" to "${document.data["phone"]}"
                        )
                        clients.add(list)
                    }
                }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(clients){ client ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(0.5f)) {
                            Text(text = client["name"] ?: "--")
                        }
                        Column(modifier = Modifier.weight(0.5f)) {
                            Text(text = client["phone"] ?: "--")
                        }
                    }
                }
            }
        }
    }
}

