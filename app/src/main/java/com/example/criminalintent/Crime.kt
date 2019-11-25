package com.example.criminalintent

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*
@Entity
data class Crime    (@PrimaryKey val id : UUID = UUID.randomUUID(),
                     var title: String = "",
                     var date : Date = Date(),
                     //var requiresPolice : Boolean = false,
                     var isSolved : Boolean = false)
