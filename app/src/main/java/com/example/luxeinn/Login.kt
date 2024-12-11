package com.example.luxeinn

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates

class Login : AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var contra: EditText
    private lateinit var ingresar: Button
    private lateinit var limpiar: Button
    private lateinit var iniciar: Button
    private lateinit var switchNuevaCuenta: Switch

    private var correo by Delegates.notNull<String>()
    private var passwd by Delegates.notNull<String>()

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        usuario = findViewById(R.id.editTextText)
        contra = findViewById(R.id.editTextTextPassword)
        ingresar = findViewById(R.id.ingresarButton)
        limpiar = findViewById(R.id.buttonLimpiar)
        iniciar = findViewById(R.id.buttonGoogle)
        switchNuevaCuenta = findViewById(R.id.switchNuevaCuenta)

        ingresar.isEnabled = false

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        ingresar.setOnClickListener { validarFormulario() }
        limpiar.setOnClickListener { borrarValores() }
        iniciar.setOnClickListener { signInWithGoogle() }
        usuario.doOnTextChanged { _, _, _, _ -> existeDominio() }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.blue, theme)
        }

    }

    private fun validarFormulario() {
        correo = usuario.text.toString()
        passwd = contra.text.toString()

        if (switchNuevaCuenta.isChecked) {
            registrarNuevaCuenta()
        } else {
            validarCorreo()
        }
    }

    private fun validarCorreo() {
        correo = usuario.text.toString()
        passwd = contra.text.toString()

        mAuth.signInWithEmailAndPassword(correo, passwd)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null) {
                        if (user.uid == "FGwTO1DVMubg09T7GxjzCFpm8cV2") {
                            // UID del admin detectado, redirigir a RegistroHotelActivity
                            lanzarRegistroHotel()
                        } else {
                            // Usuario normal, lanzar MainActivity
                            lanzarFormulario()
                        }
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Datos incorrectos"
                    Toast.makeText(baseContext, errorMessage, Toast.LENGTH_LONG).show()
                    Log.e("LoginError", "Error al iniciar sesión: ${task.exception}")
                }
            }
        borrarValores()
    }

    private fun lanzarRegistroHotel() {
        val registroHotelIntent = Intent(this, RegistroHotelActivity::class.java)
        startActivity(registroHotelIntent)
    }


    private fun registrarNuevaCuenta() {
        if (correo.isNotEmpty() && passwd.isNotEmpty()) {
            mAuth.createUserWithEmailAndPassword(correo, passwd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Cuenta registrada exitosamente", Toast.LENGTH_SHORT).show()
                        lanzarFormulario()
                    } else {
                        val errorMessage = task.exception?.message ?: "Error al registrar la cuenta"
                        Toast.makeText(baseContext, errorMessage, Toast.LENGTH_LONG).show()
                        Log.e("RegisterError", "Error al registrar la cuenta: ${task.exception}")
                    }
                }
        } else {
            Toast.makeText(this, "Por favor complete ambos campos", Toast.LENGTH_SHORT).show()
        }
        borrarValores()
    }

    private fun lanzarFormulario() {
        val formulario = Intent(this, MainActivity::class.java)
        startActivity(formulario)
    }

    private fun borrarValores() {
        usuario.text.clear()
        contra.text.clear()
        usuario.requestFocus()
    }

    private fun existeDominio() {
        correo = usuario.text.toString()
        val existe = correo.indexOf("@")

        if (existe != 1) {
            ingresar.isEnabled = true
        } else {
            ingresar.isEnabled = false
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("GoogleSignIn", "Cuenta seleccionada: ${account.email}")
                lanzarFormulario()
            } catch (e: ApiException) {
                Log.e("GoogleSignInError", "Error al seleccionar la cuenta de Google: ${e.statusCode} - ${e.message}")
                lanzarFormulario()
            }
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}