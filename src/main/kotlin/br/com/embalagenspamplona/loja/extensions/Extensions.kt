package br.com.embalagenspamplona.loja.extensions

fun String.Companion.randomAlphanumeric(length: Int): String {
    val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%&*()-_=+[]{};:,.<>?/\\|~"
    return (0..length).map { source.random() }.joinToString("")
}