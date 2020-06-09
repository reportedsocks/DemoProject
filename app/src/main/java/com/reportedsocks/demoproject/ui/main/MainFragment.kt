package com.reportedsocks.demoproject.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.reportedsocks.demoproject.MyApp
import com.reportedsocks.demoproject.R
import com.reportedsocks.demoproject.databinding.FragmentMainBinding
import com.reportedsocks.demoproject.di.viewmodel.ViewModelFactory
import com.reportedsocks.demoproject.ui.util.setupRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MainViewModel
    private lateinit var viewDataBinding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().applicationContext as MyApp).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewDataBinding = FragmentMainBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().toolbar.title = resources.getString(R.string.app_name)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.text)

        viewModel.items.observe(viewLifecycleOwner, Observer { response ->
            Log.d("MyLogs", "new value in fragment: $response")
            Toast.makeText(activity, "got data: $response", Toast.LENGTH_LONG).show()
        })

    }

}
