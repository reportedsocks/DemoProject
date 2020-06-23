package com.reportedsocks.demoproject.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.reportedsocks.demoproject.MyApp
import com.reportedsocks.demoproject.databinding.FragmentUserDetailsBinding
import com.reportedsocks.demoproject.di.viewmodel.ViewModelFactory
import javax.inject.Inject


class UserDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: UserDetailsViewModel
    private lateinit var viewDataBinding: FragmentUserDetailsBinding
    private val args: UserDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().applicationContext as MyApp).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(UserDetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentUserDetailsBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
    }
}