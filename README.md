# Catálogo Express (Android · Kotlin)

Aplicación móvil nativa para Android desarrollada en **Kotlin** con interfaces en **XML**,
para el módulo Herramientas de Programación Móvil I (Politécnico Grancolombiano, grupo G10).

**Integrantes:** Andres Felipe Buelvas Rivera, Yair Alejandro Castaneda Vargas,
Alexis Carmona Pelaez, Daniel Castelblanco.

---

## 1. Qué hace la aplicación

El usuario inicia sesión (o se registra) y llega a una pantalla dividida en **dos
fragmentos**: a la izquierda un menú vertical de opciones y a la derecha el contenido de
la opción seleccionada, como pide el enunciado del proyecto.

| Opción del menú | Requisito del enunciado | Qué muestra |
|---|---|---|
| Perfil | Perfil | Foto, nombre, datos de contacto, estudios y experiencia. Los textos largos tienen su propia barra de desplazamiento. Botón para editar el perfil. |
| Catálogo | Fotos | Lista de imágenes de productos con barra de desplazamiento. Al tocar una se muestra su descripción, precio y disponibilidad. Incluye búsqueda y filtro por categoría. |
| Carrito | Botones | Uso de Button, ImageButton, Switch, RadioButton y CheckBox: cambiar cantidades, eliminar, envío express, método de pago, aceptar términos y finalizar compra. |
| Web | Web | Caja de texto para escribir una URL, botón *Ir* y la página cargada en un WebView, con botones de atrás, recargar e inicio. |
| Salir | — | Cierra la sesión y vuelve al inicio. |

La opción **Video** del enunciado no se implementó por indicación del tutor; se explica en
la sustentación.

**Cuenta de prueba:** `demo@catalogo.com` / `123456`

### Elementos de Android que se usan

Actividades (`LoginActivity`, `RegistroActivity`, `MainActivity`), fragmentos (`MenuFragment`,
`PerfilFragment`, `CatalogoFragment`, `CarritoFragment`, `WebFragment`), imágenes
(`ImageView`), vistas de texto (`TextView`, `EditText`), botones (`Button`, `ImageButton`),
listas (`ListView` con adaptadores propios), eventos (clics, selección de lista y de
categoría, cambio de texto, cambio de interruptores) y widgets (`Spinner`, `CheckBox`,
`Switch`, `RadioGroup`, `ProgressBar`, `WebView`).

---

## 2. Historia del proyecto: de Flutter a Kotlin

### Primera versión en Flutter

El proyecto empezó en **Flutter**. Lo elegimos porque permitía avanzar rápido sin instalar
herramientas pesadas: probábamos la app en el navegador con GitHub Codespaces y GitHub
Actions generaba el APK automáticamente. Con esa versión construimos un prototipo
funcional basado en el mockup: login y registro, catálogo con búsqueda y filtros, carrito,
compra simulada, historial de pedidos, notificaciones y un módulo de administración.
Esa versión sigue disponible en la rama `version-flutter` del repositorio.

### Cambio de tecnología

En la clase del **10 de octubre de 2026** el instructor descartó Flutter para el proyecto,
porque el módulo evalúa el desarrollo nativo de Android con actividades, fragmentos y
vistas definidas en XML. Indicó que **Kotlin** sí era una opción aceptable. Al revisar de
nuevo el enunciado también vimos que la entrega final pide una estructura concreta: una
pantalla con dos fragmentos, un menú a la izquierda y el contenido a la derecha. Nuestra
versión en Flutter no seguía esa estructura.

### Cómo se hizo la migración

No se tradujo el código línea por línea. Se rehízo la app para Android nativo con un orden
claro:

1. **Guardar lo hecho.** La versión Flutter se movió a la rama `version-flutter` antes de
   tocar `main`, para no perder el trabajo ni el historial.
2. **Relacionar el enunciado con lo que ya teníamos.** Cada módulo exigido se cruzó con una
   función existente: *Fotos* pasó a ser el catálogo de productos, *Botones* el carrito y
   *Perfil* el perfil del usuario. *Web* se construyó desde cero. Por indicación del tutor,
   *Video* se explica en la sustentación y no se implementó.
3. **Reutilizar lo que no dependía de la tecnología.** Se conservaron los requerimientos,
   los casos de uso, el mockup (logo, fotos de productos y colores), los datos de ejemplo y
   las reglas de negocio: correos no repetidos al registrarse, control de inventario al
   agregar al carrito y formato de precios.
4. **Rehacer la capa visual.** Las pantallas de Flutter se convirtieron en actividades y
   fragmentos con su XML. El estado global de Flutter (`AppState`) pasó a ser el objeto
   `Repositorio` en Kotlin, y la navegación entre pantallas se reemplazó por el cambio de
   fragmentos dentro de `MainActivity`.
5. **Mantener la compilación automática.** El flujo de GitHub Actions se adaptó de Flutter a
   Gradle, así que el APK se sigue generando con cada cambio en `main`.

Para enfocarnos en lo que evalúa el módulo, algunas funciones de la versión Flutter
(historial de pedidos, notificaciones, administración del catálogo y favoritos) quedaron
como mejoras futuras.

### Lo que aprendimos

- **Validar la tecnología antes de construir.** Empezamos por la herramienta más cómoda
  para el equipo sin confirmar que encajaba con lo que el módulo evalúa. Cambiarla a mitad
  del semestre costó tiempo. La próxima vez lo confirmaríamos con el instructor desde la
  primera entrega.
- **Separar la lógica de la interfaz facilita el cambio.** Lo que estaba bien separado (datos,
  reglas de negocio y diseño) se reutilizó casi completo. Lo que hubo que rehacer fue sobre
  todo la parte visual.
- **Dos formas de construir interfaces.** Flutter describe la pantalla con código de forma
  declarativa. En Android con XML la interfaz se diseña aparte y se conecta al código
  mediante identificadores y eventos. Pasar de una a otra nos ayudó a entender mejor el
  ciclo de vida de actividades y fragmentos.
- **Git y la automatización ahorraron trabajo.** La rama de respaldo nos dejó migrar sin
  miedo a perder nada, y la compilación en GitHub Actions nos permitió generar el APK sin
  configurar Android Studio en cada computador.

---

## 3. Estructura del proyecto

```
app/src/main/
  AndroidManifest.xml            Actividades y permiso de Internet
  java/co/edu/poligran/catalogoexpress/
    LoginActivity.kt             Inicio de sesión
    RegistroActivity.kt          Registro de usuario
    MainActivity.kt              Pantalla con los dos fragmentos
    MenuFragment.kt              Fragmento izquierdo (menú)
    MenuAdapter.kt               Diseño de cada opción del menú
    PerfilFragment.kt            Perfil
    CatalogoFragment.kt          Catálogo (Fotos)
    ProductoAdapter.kt           Filas de la lista de productos
    CarritoFragment.kt           Carrito (Botones)
    WebFragment.kt               Navegador web
    Modelos.kt                   Usuario, Producto, ItemCarrito, opciones del menú
    Repositorio.kt               Datos de ejemplo y lógica de negocio
  res/layout/                    Un XML por actividad, fragmento, fila y diálogo
  res/drawable/                  Íconos y fondos
  res/drawable-nodpi/            Logo y fotos de productos (tomados del mockup)
  res/values/                    Colores, textos y estilos
.github/workflows/build-apk.yml  Compilación automática del APK
```

Cada pantalla sigue el mismo orden, para que sea fácil de explicar: primero se declaran
las variables de las vistas, luego `vincularVistas()` las une con sus identificadores
(`findViewById`) y después `configurarEventos()` declara qué pasa en cada interacción.

---

## 4. Cómo obtener y probar la app

**APK:** se compila solo con GitHub Actions cada vez que se suben cambios a `main`. Entra a
**Actions → Build APK → última ejecución → Artifacts → CatalogoExpress-apk**, descomprime el
.zip e instala `CatalogoExpress.apk` en un celular Android (permite "instalar apps de origen
desconocido" si lo pide).

**Android Studio (opcional):** *File → Open* y selecciona la carpeta del repositorio. Android
Studio descarga lo necesario y permite ejecutar la app en un emulador o en un celular
conectado por USB con el botón ▶.

---

## 5. Mejoras futuras

- Base de datos real (Room o Firebase) para que usuarios, productos y pedidos se guarden.
- Módulo de video con un video promocional de la tienda.
- Historial de pedidos y notificaciones de cambio de estado.
- Módulo de administración del catálogo (crear, editar y eliminar productos).
- Favoritos, calificaciones y comentarios de productos.
- Pasarela de pago real y recuperación de contraseña.
- Foto de perfil tomada con la cámara o desde la galería.
- Migrar la interfaz de XML a Jetpack Compose, el sistema de interfaces más reciente de Android.
