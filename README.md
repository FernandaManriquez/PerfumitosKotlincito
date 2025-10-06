# 💖 Perfumitos: Una Tiendita Mágica en tu Celular 💖

¡Bienvenida al jardín secreto de **Perfumitos**! 🌸 Esta no es solo una app, es un pequeño rincón del universo donde cada fragancia cuenta una historia y cada clic te acerca a un mundo de aromas inolvidables.

Este proyecto nació de un sueño: crear una tienda online tan especial y personal como tu perfume favorito. Es un espacio construido con mucho cariño, y cada línea de código está pensada para que la experiencia de comprar y vender sea tan placentera como oler una rosa en primavera.

## ✨ ¿Qué hace mágica a esta app? ✨

- **Un Catálogo de Ensueño:** Explora una colección de perfumes cuidadosamente seleccionados, con fotos que te harán sentir que casi puedes olerlos.
- **Un Carrito de Deseos:** Guarda tus fragancias favoritas en tu carrito, ¡listas para cuando decidas darte un capricho!
- **Pago Fácil y Seguro:** Un proceso de compra tan suave como la seda, para que solo te preocupes de disfrutar tu nueva adquisición.
- **Panel de Administración con Amor:** Si eres la jefa de este jardín, tendrás un lugar especial para añadir nuevas fragancias, gestionar los pedidos y ver cómo florece tu negocio.

## 🛠️ Un Vistazo Bajo los Pétalos (La Tecnología) 🛠️

Esta app está construida con las herramientas más modernas y bonitas del mundo Android, para que todo funcione a la perfección:

- **Kotlin:** El lenguaje que nos permite escribir código elegante y seguro.
- **Retrofit y OkHttp:** Nuestros mensajeros mágicos, que se comunican con el cerebro de la tienda en Xano.
- **Coroutines:** Para que la app sea súper rápida y nunca te haga esperar.
- **Glide:** La varita mágica que hace que las imágenes de los perfumes se vean espectaculares.
- **ViewBinding:** Para que cada pantalla y cada botoncito estén siempre en su lugar.

## 🌳 La Arquitectura de Nuestro Jardín (Estructura Detallada) 🌳

Así es como está organizado nuestro pequeño universo de código, para que no te pierdas ningún detalle:

```
Perfumitos/
├─ app/
│  ├─ src/main/java/com/miapp/xanostorekotlin
│  │  ├─ api/         # 💌 Los mensajeros que hablan con la nube (Xano).
│  │  │  ├─ RetrofitClient.kt  # El constructor de nuestros mensajeros.
│  │  │  ├─ TokenManager.kt    # El guardián de nuestra llave secreta (el token).
│  │  │  └─ ...y un servicio para cada tipo de recado (UserService, ProductService, etc.).
│  │  │
│  │  ├─ model/      # 🏷️ Las "etiquetas" que nos dicen cómo es cada dato.
│  │  │  ├─ User.kt
│  │  │  ├─ Product.kt
│  │  │  ├─ Order.kt
│  │  │  └─ Cart.kt
│  │  │
│  │  ├─ ui/         # 🎨 El corazón visual de la app, donde ocurre la magia.
│  │  │  ├─ adapter/   # 🧵 Los hilos que tejen los datos en listas bonitas.
│  │  │  │  ├─ ProductAdapter.kt
│  │  │  │  ├─ CartAdapter.kt
│  │  │  │  └─ UserAdapter.kt
│  │  │  │
│  │  │  ├─ fragments/ # 🧩 Pedacitos de pantalla que se unen para formar la app.
│  │  │  │  ├─ ProductsFragment.kt     # El escaparate de nuestros perfumes.
│  │  │  │  ├─ CartFragment.kt         # El carrito de tus deseos.
│  │  │  │  ├─ ProfileFragment.kt      # Tu rincón personal.
│  │  │  │  ├─ UsersFragment.kt        # El panel de usuarios para el admin.
│  │  │  │  └─ PendingOrdersFragment.kt # El panel de órdenes para el admin.
│  │  │  │
│  │  │  ├─ LoginActivity.kt     # La puerta de entrada a nuestro jardín.
│  │  │  ├─ SignupActivity.kt    # Donde te unes a nuestra familia.
│  │  │  ├─ HomeActivity.kt      # El salón principal, con sus distintas estancias.
│  │  │  └─ ProductDetailActivity.kt # La lupa para ver cada perfume de cerca.
│  │  │
│  │  └─ manager/    # 🧠 El cerebro que recuerda qué tienes en el carrito mientras paseas por la tienda.
│  │
│  ├─ src/main/res
│  │  ├─ layout/     # 🏰 Los planos de cada pantalla y cada piececita.
│  │  ├─ drawable/   # ✨ Iconos y adornos para que todo se vea precioso.
│  │  └─ values/     # 🎨 La paleta de colores, textos y estilos de nuestra app.
│  │
│  └─ build.gradle.kts # 📜 La receta secreta que une todos los ingredientes.
│
└─ ...y otros archivos de configuración del proyecto.
```

## 🚀 ¿Cómo empezar a soñar? 🚀

Simplemente abre el proyecto en Android Studio, dale al botón de play, ¡y déjate llevar por la magia de los aromas! No necesitas ninguna configuración complicada, todo está listo para que empieces a explorar.

---

Hecho con ❤️ y un montón de ilusión. Espero que disfrutes de este pequeño universo de fragancias tanto como yo he disfrutado creándolo para ti.
