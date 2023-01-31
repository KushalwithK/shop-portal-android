package com.nexusinfinity.electronicsportal.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nexusinfinity.electronicsportal.R
import com.nexusinfinity.electronicsportal.adapters.ButtonClicks
import com.nexusinfinity.electronicsportal.adapters.ProductClickResponse
import com.nexusinfinity.electronicsportal.adapters.ProductsViewAdapter
import com.nexusinfinity.electronicsportal.constants.Constant
import com.nexusinfinity.electronicsportal.databinding.BottomSheetBinding
import com.nexusinfinity.electronicsportal.databinding.FragmentProductsBinding
import com.nexusinfinity.electronicsportal.model.Product
import com.nexusinfinity.electronicsportal.notification.model.NotificationData
import com.nexusinfinity.electronicsportal.notification.model.PushNotification
import com.nexusinfinity.electronicsportal.notification.utility.ApiInterface
import com.nexusinfinity.electronicsportal.notification.utility.ApiUtility
import com.nexusinfinity.electronicsportal.repository.ProductsRepo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

fun sendNotification(title: String, message: String, api: ApiInterface) {
    api.sendNotification(
        PushNotification(
            NotificationData(
                title,
                message
            ), Constant.TOPIC
        )
    ).enqueue(object : Callback<PushNotification> {
        override fun onResponse(
            call: Call<PushNotification>,
            response: Response<PushNotification>
        ) {
            Log.d(
                "ADD_NOTIFICATION",
                "success: ${response.body().toString()}"
            )
        }

        override fun onFailure(
            call: Call<PushNotification>,
            t: Throwable
        ) {
            t.localizedMessage?.let {
                Log.d("ADD_NOTIFICATION", "failed: $it")
            }

        }
    })
}

class ModalBottomSheet(
    val productId: String,
    val productBrand: String,
    val productName: String,
    val productQuantity: String,
    val productTarget: String,
    val buyingPrice: String,
    val sellingPrice: String
) : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var productsLoader: ProductsRepo
    private var token: String? = null
    private lateinit var progressDialog: RelativeLayout
    private lateinit var api: ApiInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BottomSheetBinding.inflate(inflater, container, false)
        productsLoader = ProductsRepo(requireContext())
        val pref = requireContext().getSharedPreferences("isLogin", 0)
        token = pref.getString("token", null)
        api = ApiUtility.getClient()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productId_et: EditText = view.findViewById(R.id.ProductId_et)
        val productBrand_et: EditText = view.findViewById(R.id.ProductBrand_et)
        val productName_et: EditText = view.findViewById(R.id.ProductName_et)

        val productQuantity_et: EditText = view.findViewById(R.id.ProductQuantity_et)
        val productTarget_et: EditText = view.findViewById(R.id.ProductTarget_et)

        val buyingPrice_et: EditText = view.findViewById(R.id.BuyingPrice_et)
        val sellingPrice_et: EditText = view.findViewById(R.id.SellingPrice_et)

        val applyChanges_btn: Button = view.findViewById(R.id.ApplyChanges_btn)

        progressDialog = view.findViewById(R.id.progressLay)

        productId_et.setText(productId)
        productBrand_et.setText(productBrand)
        productName_et.setText(productName)
        productQuantity_et.setText(productQuantity)
        productTarget_et.setText(productTarget)
        buyingPrice_et.setText(buyingPrice)
        sellingPrice_et.setText(sellingPrice)

        applyChanges_btn.setOnClickListener(this)

    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.ApplyChanges_btn -> {
                    token?.let { tok ->
                        progressDialog.visibility = View.VISIBLE
                        productsLoader.updateProductUsingId(
                            productId,
                            productBrand,
                            productName,
                            productQuantity,
                            productTarget,
                            buyingPrice,
                            sellingPrice,
                            object : ProductsRepo.OnProductUpdateResponse {
                                override fun onUpdateSuccess(id: String?, productName: String?) {
                                    productName?.let {
                                        Toast.makeText(
                                            requireContext(),
                                            "$productId was successfully updated!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        sendNotification("$productId Updated!", "Product $productId was updated successfully on ${Date().time}", api)
                                        progressDialog.visibility = View.GONE
                                        dismiss()
                                    }
                                }

                                override fun onUpdateFail(message: String?, responseCode: String?) {
                                    message?.let {
                                        Toast.makeText(
                                            requireContext(),
                                            "Something went wrong, pls try again!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        sendNotification("$productId Update failed!", "Product $productId was not been able to update which was requested to be updated on ${Date().time}", api)
                                        progressDialog.visibility = View.GONE
                                        dismiss()
                                    }
                                }
                            }, tok
                        )
                    }

                }
                else -> {
                    dismiss()
                }
            }
        }
    }
}

class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentProductsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var productsLoader: ProductsRepo
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var api: ApiInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductsBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView
        progress = binding.progress
        refreshLayout = binding.refreshLayout

        api = ApiUtility.getClient()

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.hasFixedSize()
        progress.visibility = View.VISIBLE

        activity?.let { context ->
            val shared = context.getSharedPreferences("isLogin", AppCompatActivity.MODE_PRIVATE)
            val token = shared.getString("token", null)

            productsLoader = ProductsRepo(context)

            token?.let { tok ->
                loadProducts(tok, context)

                refreshLayout.setOnRefreshListener {
                    loadProducts(tok, context)
                }
            }

        }

        return binding.root
    }

    fun share(product: Product) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT, "Information regarding product ${product.productId}\n" +
                    "Name: ${product.productName}\n" +
                    "Model: ${product.productModel}\n" +
                    "Quantity: ${product.productQnt}, Target: ${product.productTarget}\n" +
                    "Sold: ${product.sold}\n" +
                    "BuyingPrice: ${product.buyingPrice}, " +
                    "SellingPrice: ${product.sellingPrice}"
        )

        val chooser = Intent.createChooser(intent, "Share product using...")
        startActivity(chooser)
    }

    fun loadProducts(tok: String, context: Activity) {
        productsLoader.getProductsByToken(tok, object : ProductsRepo.OnProductsResponse {
            override fun onSuccess(products: ArrayList<Product>?) {
                products?.let {
                    val adapter =
                        ProductsViewAdapter(object : ProductClickResponse {
                            override fun onClick(product: Product, view: View) {
                                val bundle = Bundle()
                                bundle.putInt("productId", Integer.valueOf(product.productId))
                                bundle.putString("productName", product.productName)
                                bundle.putString("brand", product.productModel)
                                bundle.putString("buyingPrice", product.buyingPrice)
                                bundle.putString("sellingPrice", product.sellingPrice)
                                bundle.putString("available", product.productQnt)
                                bundle.putString("target", product.productTarget)
                                bundle.putString("sold", product.sold)
                            }

                        }, object : ButtonClicks {
                            override fun onEditClick(product: Product) {
//                                Toast.makeText(
//                                    context,
//                                    "Edit clicked for ${product.productName}",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                                val modalBottomSheet =
                                    ModalBottomSheet(
                                        product.productId,
                                        product.productModel,
                                        product.productName,
                                        product.productQnt,
                                        product.productTarget,
                                        product.buyingPrice,
                                        product.sellingPrice
                                    )
                                modalBottomSheet.show(parentFragmentManager, ModalBottomSheet.TAG)
                            }

                            override fun onDeleteClick(
                                product: Product,
                                progressDialog: RelativeLayout
                            ) {
                                productsLoader.deleteProductUsingId(
                                    product.productId,
                                    tok,
                                    object : ProductsRepo.OnDeleteResponse {
                                        override fun onDeleteSuccess(msg: String?) {
                                            msg?.let {
                                                Toast.makeText(
                                                    context,
                                                    it,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                sendNotification("${product.productName} was deleted Successfully!", "Product ${product.productName} was deleted from the inventory on ${Date().time}", api)
                                                progressDialog.visibility = View.GONE
                                                loadProducts(tok, context)
                                            }
                                        }

                                        override fun onDeleteFail(msg: String?, response: String?) {
                                            response?.let {
                                                Toast.makeText(
                                                    context,
                                                    "$msg - $it",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                progressDialog.visibility = View.GONE
                                            }
                                        }

                                    })
                            }

                            override fun onShareClick(product: Product) {
                                share(product)
                            }

                        })
                    recyclerView.adapter = adapter
                    adapter.submitList(it)
                    progress.visibility = View.GONE
                }
            }

            override fun onFail(message: String?, responseCode: String?) {
                progress.visibility = View.GONE
                MaterialAlertDialogBuilder(context)
                    .setTitle("Something isn't right!")
                    .setCancelable(false)
                    .setMessage(
                        "Sorry merchant, it seems the server is facing some critical issues currently so we won't be able to fulfil your request." +
                                "\nYou can try the following:" +
                                "\n\n1. Restart the application" +
                                "\n2. Keep trying" +
                                "\n3. Try after sometime" +
                                "\n\nif the problem continues please contact support at -> kushal@cleverstudio.in\n"
                    )
                    .setNeutralButton("Exit") { dialog, which ->
                        context.finish()
                    }
                    .setPositiveButton("Try again") { dialog, which ->
                        dialog.cancel()
                        loadProducts(tok, requireActivity())
                    }
                    .show()

            }

        })

        refreshLayout.isRefreshing = false
    }

}