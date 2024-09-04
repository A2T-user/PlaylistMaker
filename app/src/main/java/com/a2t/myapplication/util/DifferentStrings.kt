package com.a2t.myapplication.util

class DifferentStrings {
    companion object {
        fun countTracks(count: Int): String {
            val str = when (count % 10) {
                1 -> " трек"
                2, 3, 4 -> " трека"
                else -> " треков"
            }
            return count.toString() + str
        }

        fun countMinutes(count: Int): String {
            val str = when (count % 10) {
                1 -> " минута"
                2, 3, 4 -> " минуты"
                else -> " минут"
            }
            return count.toString() + str
        }

    }
}