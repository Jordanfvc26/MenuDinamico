package com.example.menudinamico

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    lateinit var drawerLayout: DrawerLayout
    var  toolbar: Toolbar? = null
    lateinit var navView: NavigationView
    lateinit var cabeceraDinamica: View
    var rolParaMenu = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this);
        var menuDinamico = navView.menu

        //Para obtener los datos enviados del Login
        val bundle = intent.extras
        val usuarioPasado = bundle?.getString("usuario")

        //Consumiendo las APIS según los usuarios
        val queue = Volley.newRequestQueue(this)
        val stringRequest = JsonObjectRequest(
            Request.Method.GET, "https://usuariosmenuandroid-default-rtdb.firebaseio.com/data.json", null,
            { response ->
                val listaUsersJSON = JSONArray(response.getString("usuarios"));
                for(i in 0 until listaUsersJSON.length()){
                    val infoUser: JSONObject = listaUsersJSON.getJSONObject(i);
                    if(usuarioPasado == infoUser.getString("usuario")){

                        if(infoUser.getString("rol") == "profesor"){
                            //Titulo del toolbar
                            toolbar = findViewById<Toolbar>(R.id.toolbar);
                            toolbar!!.title="Profesores SGA"
                            setSupportActionBar(toolbar);

                            //Icono barras para ver el menú
                            getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.iconmenu)
                            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

                            rolParaMenu = "menu-profesor"
                        }
                        else{
                            //Titulo del toolbar
                            toolbar = findViewById<Toolbar>(R.id.toolbar);
                            toolbar!!.title="Estudiantes SGA"
                            setSupportActionBar(toolbar);

                            //Icono barras para ver el menú
                            getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.iconmenu)
                            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
                            rolParaMenu = "menu-estudiante"
                        }
                        cabeceraDinamica = navView.getHeaderView(0)

                        //Mostrando la imagen de perfil del usuario
                        var imgPerfil = cabeceraDinamica.findViewById<CircleImageView>(R.id.imgUsuarioPerfil)
                        Picasso.get().load(infoUser.getString("imgperfil")).into(imgPerfil)

                        //Mostrando el nombre de usuario y el rol
                        val nomUsuarioPerfil = cabeceraDinamica.findViewById<TextView>(R.id.nomUsuarioPerfil);
                        nomUsuarioPerfil.text = (usuarioPasado + " - " + infoUser.getString("rol")).toString()

                        //Mostrando el email del usuario
                        val correoUsuarioPerfil = cabeceraDinamica.findViewById<TextView>(R.id.correoUsuarioPerfil);
                        correoUsuarioPerfil.text = (infoUser.getString("email")).toString()

                        //Consumiendo la API de opciones del menu
                        val queue2 = Volley.newRequestQueue(this)
                        val stringRequest2 = JsonObjectRequest(
                            Request.Method.GET, "https://opcionesmenuandroid-default-rtdb.firebaseio.com/data.json", null,
                            { response ->
                                var temp = response.getJSONArray(rolParaMenu)
                                var opciones = temp.getJSONObject(0)
                                for(i in 0 until opciones.length()){
                                    var opc = opciones.getString("opc"+(i+1))
                                    menuDinamico.add(opc)
                                    if(rolParaMenu == "menu-estudiante"){
                                        menuDinamico.getItem(i).setIcon(R.drawable.iconmenu)
                                    }
                                    else{
                                        menuDinamico.getItem(i).setIcon(R.drawable.iconcheck)
                                    }
                                }
                            },
                            {
                                Toast.makeText(applicationContext, "Error en consumo de API menu", Toast.LENGTH_SHORT).show()
                            })
                        queue2.add(stringRequest2)
                    }
                    else{
                        Toast.makeText(applicationContext, "No se pudo cargar la información", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            {
                Toast.makeText(applicationContext, "Error en consumo de API usuarios", Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*when (item.itemId) {
            android.R.id.home -> {
                drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
                drawerLayout?.openDrawer(GravityCompat.START)
                return true
            }
        }*/

        when(item.itemId) {

            else -> {
                drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
                drawerLayout?.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        /*when(item.itemId){
            R.id.menu_seccion_1 -> Toast.makeText(this, "Mis Alumnos", Toast.LENGTH_SHORT).show()
            R.id.menu_seccion_2 -> Toast.makeText(this, "Mis clases", Toast.LENGTH_SHORT).show()
            R.id.menu_seccion_3 -> Toast.makeText(this, "Nueva Horarios", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)*/

        when(rolParaMenu){
            "menu-estudiante"->{
                when(item.itemId){
                    else -> supportFragmentManager.beginTransaction().replace(R.id.content_frame, Fragment1()).commit()
                }
            }
            "menu-profesor"->{
                when(item.itemId){
                    else -> supportFragmentManager.beginTransaction().replace(R.id.content_frame, Fragment2()).commit()
                }
            }
            else -> supportFragmentManager.beginTransaction().replace(R.id.content_frame, Fragment3()).commit()
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}