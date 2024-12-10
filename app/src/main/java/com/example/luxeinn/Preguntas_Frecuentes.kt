package com.example.luxeinn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Preguntas_Frecuentes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preguntas_frecuentes)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val categorias = listOf(
            Categoria(
                "Registro y Perfil de Usuario",
                listOf(
                    Pregunta("¿Cómo puedo registrarme en la aplicación?", "Los usuarios pueden registrarse en la aplicación mediante su correo electrónico, siguiendo un proceso de autenticación que garantiza la seguridad de su información personal."),
                    Pregunta("¿Cómo puedo actualizar mi información personal en la aplicación?", "Los usuarios pueden actualizar su información personal, como nombre, dirección y preferencias, accediendo a la sección de 'Perfil' en la aplicación y editando los campos correspondientes."),
                    Pregunta("¿Puedo guardar mis métodos de pago en la aplicación?", "Sí, la aplicación permite a los usuarios guardar sus métodos de pago para facilitar futuras reservas, garantizando que la información esté protegida.")
                )
            ),
            Categoria(
                "Métodos de Pago",
                listOf(
                    Pregunta("¿Qué métodos de pago acepta la aplicación para las reservas?", "La aplicación acepta diversas formas de pago, incluyendo tarjetas de crédito y débito, garantizando un proceso de pago seguro."),
                    Pregunta("¿Qué debo hacer si veo un cargo doble en mi cuenta?", "Si observas un cargo doble en tu cuenta, te recomendamos que contactes al soporte al cliente a través de la aplicación lo antes posible. Ellos investigarán el problema y te ayudarán a resolverlo."),
                    Pregunta("¿La aplicación permite dividir el pago entre varias tarjetas?", "No, la aplicación no permite dividir el pago entre varias tarjetas. Debes utilizar una sola tarjeta para completar la transacción."),
                    Pregunta("¿Qué debo hacer si mi pago es rechazado?", "Si tu pago es rechazado, verifica que la información de la tarjeta sea correcta y que haya fondos suficientes. Si el problema persiste, contacta a tu banco o al soporte al cliente de la aplicación para obtener más información.")
                )
            ),
            Categoria(
                "Gestión de Reservas",
                listOf(
                    Pregunta("¿Puedo modificar o cancelar mis reservas a través de la aplicación?", "Sí, los usuarios pueden gestionar sus reservas, incluyendo modificaciones y cancelaciones, directamente desde la aplicación de manera sencilla."),
                    Pregunta("¿Qué debo hacer si necesito cambiar mis fechas de reserva?", "Los usuarios pueden modificar sus fechas de reserva directamente desde la aplicación, siguiendo un proceso sencillo para ajustar su estadía según sea necesario."),
                    Pregunta("¿La aplicación permite realizar reservas de última hora?", "Sí, la aplicación permite realizar reservas de última hora, siempre y cuando haya disponibilidad en los hoteles seleccionados."),
                    Pregunta("¿Qué hago si no recibo la confirmación de mi reserva?", "Si no recibes la confirmación de tu reserva, verifica tu carpeta de spam en el correo electrónico. Si aún no la encuentras, contacta al soporte al cliente a través de la aplicación.")
                )
            ),
            Categoria(
                "Soporte y Problemas",
                listOf(
                    Pregunta("¿La aplicación ofrece soporte al cliente?", "Sí, la aplicación incluye un sistema de mensajería automática que permite a los usuarios enviar preguntas y recibir respuestas sobre temas comunes, además de acceso a atención al cliente en horas de operación."),
                    Pregunta("¿Qué hago si tengo problemas con mi reserva?", "Si enfrentas problemas con tu reserva, puedes contactar al soporte al cliente a través de la aplicación para recibir asistencia y resolver cualquier inconveniente."),
                    Pregunta("¿Qué debo hacer si tengo una queja sobre un hotel?", "Si tienes una queja sobre un hotel, puedes reportarla a través de la aplicación, donde el equipo de atención al cliente se encargará de investigar y resolver el problema.")
                )
            )
        )

        val adapter = CategoriaAdapter(categorias)
        recyclerView.adapter = adapter
    }
}
