package com.reportedsocks.demoproject.ui.main

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.reportedsocks.demoproject.MyApp
import com.reportedsocks.demoproject.R
import com.reportedsocks.demoproject.databinding.FragmentMainBinding
import com.reportedsocks.demoproject.di.viewmodel.ViewModelFactory
import com.reportedsocks.demoproject.util.EventObserver
import com.reportedsocks.demoproject.util.setupRefreshLayout
import com.reportedsocks.demoproject.util.setupSnackbar
import javax.inject.Inject

class MainFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MainViewModel
    private lateinit var viewDataBinding: FragmentMainBinding
    private lateinit var listAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().applicationContext as MyApp).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewDataBinding = FragmentMainBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner
        setupListAdapter()
        setupSnackbar()
        setupNavigation()
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.usersList)
        setupErrorHandling()

    }

    private fun setupErrorHandling() {
        viewModel.loadingError.observe(viewLifecycleOwner, Observer {
            viewModel.showError(it)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_filter -> {
                showFilteringPopupMenu()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu)
    }

    private fun showFilteringPopupMenu() {
        val view: View = requireActivity().findViewById<View>(R.id.menu_filter)
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_users, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(when (it.itemId) {
                    R.id.user -> UsersFilterType.USER
                    R.id.organisation -> UsersFilterType.ORGANISATION
                    else -> UsersFilterType.ALL
                })
                true
            }
            show()
        }
    }

    private fun setupListAdapter() {
        listAdapter = UsersAdapter(viewModel)
        viewDataBinding.usersList.adapter = listAdapter
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupNavigation() {
        viewModel.openUserEvent.observe(viewLifecycleOwner, EventObserver {
            val action = MainFragmentDirections.actionMainFragmentToUserDetailsFragment(it)
            findNavController().navigate(action)
        })
    }

}
