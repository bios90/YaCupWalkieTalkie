package com.test.yacupwalkietalkie.base

fun Toast(text: String) {
    android.widget.Toast.makeText(App.app, text, android.widget.Toast.LENGTH_LONG).show()
}

fun Boolean?.safe(): Boolean = this == true

fun <T> T.toSet() = setOf(this)
