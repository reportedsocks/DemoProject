package com.reportedsocks.demoproject.ui.details

import androidx.lifecycle.ViewModel
import com.reportedsocks.demoproject.data.source.DataRepository
import javax.inject.Inject

class UserDetailsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {
}