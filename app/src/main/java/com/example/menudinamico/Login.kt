package com.example.menudinamico

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class Login : AppCompatActivity() {


    lateinit var usuario: EditText
    lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun ingresar(view: View){
        val bundle = Bundle();
        val intent = Intent(this, MainActivity::class.java);

        //Obtenes el usuario y contraseña que se ingresa en la pantalla de login
        usuario = findViewById(R.id.txtUsuario)
        password = findViewById(R.id.txtPassword)

        //Ahora consumimos la API
        val queue = Volley.newRequestQueue(this)
        val stringRequest = JsonObjectRequest(
            Request.Method.GET, "https://usuariosmenuandroid-default-rtdb.firebaseio.com/data.json", null,
            { response ->
                val listaUsersJSON = JSONArray(response.getString("usuarios"));
                for(i in 0 until listaUsersJSON.length()){
                    val infoUser: JSONObject = listaUsersJSON.getJSONObject(i);
                    if(usuario.text.toString() == infoUser.getString("usuario") && password.text.toString() == infoUser.getString("password")){
                        bundle.putString("usuario", usuario.text.toString())
                        intent.putExtras(bundle);
                        startActivity(intent);
                        Toast.makeText(applicationContext, "Usuario logueado correctamente", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(applicationContext, "Usuario incorrecto o no registrado", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            {
                Toast.makeText(applicationContext, "No se pudo consumir la API", Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)
    }
}