package com.example.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListVIewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()
}