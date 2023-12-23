package com.hxh19950701.webviewtvlive.misc

fun adjustValue(value: Int, size: Int, next: Boolean): Int {
    return if (next) {
        if (value + 1 >= size) 0 else value + 1
    } else {
        if (value - 1 < 0) size - 1 else value - 1
    }
}

