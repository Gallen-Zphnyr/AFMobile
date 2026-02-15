package com.example.afmobile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.example.afmobile.adapters.ProductAdapter
import com.example.afmobile.data.Product
import com.example.afmobile.viewmodels.ProductViewModel
import com.example.afmobile.workers.ProductSyncWorker
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private lateinit var productViewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter

    private lateinit var searchEditText: EditText
    private lateinit var categoryChipGroup: ChipGroup
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var loadingProgressBar: ProgressBar

    private var selectedCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        searchEditText = view.findViewById(R.id.searchEditText)
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)

        // Initialize ViewModel
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        // Setup RecyclerView
        setupRecyclerView()

        // Setup search
        setupSearch()

        // Setup swipe refresh
        setupSwipeRefresh()

        // Observe products
        observeProducts()

        // Observe categories
        observeCategories()

        // Setup periodic sync
        setupPeriodicSync()

        // Initial sync
        syncProducts()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            onProductClick(product)
        }
        productsRecyclerView.adapter = productAdapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    searchProducts(query)
                } else {
                    // Show all products or filtered by category
                    if (selectedCategory != null) {
                        filterByCategory(selectedCategory!!)
                    } else {
                        observeProducts()
                    }
                }
            }
        })
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            syncProducts()
        }
    }

    private fun observeProducts() {
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            updateUI(products)
        }
    }

    private fun observeCategories() {
        productViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            updateCategoryChips(categories)
        }
    }

    private fun updateCategoryChips(categories: List<String>) {
        categoryChipGroup.removeAllViews()

        // Add "All" chip
        val allChip = Chip(requireContext())
        allChip.text = "All"
        allChip.isCheckable = true
        allChip.isChecked = selectedCategory == null
        allChip.setOnClickListener {
            selectedCategory = null
            observeProducts()
        }
        categoryChipGroup.addView(allChip)

        // Add category chips
        categories.forEach { category ->
            val chip = Chip(requireContext())
            chip.text = category
            chip.isCheckable = true
            chip.isChecked = selectedCategory == category
            chip.setOnClickListener {
                selectedCategory = category
                filterByCategory(category)
            }
            categoryChipGroup.addView(chip)
        }
    }

    private fun filterByCategory(category: String) {
        productViewModel.getProductsByCategory(category).observe(viewLifecycleOwner) { products ->
            updateUI(products)
        }
    }

    private fun searchProducts(query: String) {
        productViewModel.searchProducts(query).observe(viewLifecycleOwner) { products ->
            updateUI(products)
        }
    }

    private fun updateUI(products: List<Product>) {
        if (products.isEmpty()) {
            productsRecyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            productsRecyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
            productAdapter.submitList(products)
        }
        swipeRefreshLayout.isRefreshing = false
        loadingProgressBar.visibility = View.GONE
    }

    private fun syncProducts() {
        loadingProgressBar.visibility = View.VISIBLE
        productViewModel.syncProducts()

        // Hide loading after a delay (real sync happens in background)
        view?.postDelayed({
            loadingProgressBar.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(requireContext(), "Products synced!", Toast.LENGTH_SHORT).show()
        }, 1500)
    }

    private fun setupPeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<ProductSyncWorker>(
            15, TimeUnit.MINUTES // Sync every 15 minutes
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "ProductSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    private fun onProductClick(product: Product) {
        // Show product detail bottom sheet
        val bottomSheet = ProductDetailBottomSheet.newInstance(product)
        bottomSheet.show(parentFragmentManager, "ProductDetailBottomSheet")
    }
}
