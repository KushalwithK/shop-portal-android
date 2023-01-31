package com.nexusinfinity.electronicsportal.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.nexusinfinity.electronicsportal.R
import com.nexusinfinity.electronicsportal.constants.Constant
import com.nexusinfinity.electronicsportal.databinding.FragmentAddProductBinding
import com.nexusinfinity.electronicsportal.model.Product
import com.nexusinfinity.electronicsportal.notification.model.NotificationData
import com.nexusinfinity.electronicsportal.notification.model.PushNotification
import com.nexusinfinity.electronicsportal.notification.utility.ApiUtility
import com.nexusinfinity.electronicsportal.repository.ProductsRepo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class AddProductFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentAddProductBinding.inflate(inflater, container, false)

        val api = ApiUtility.getClient()
        val date = Date()

        binding.addProductButton.setOnClickListener {
            if (binding.etProductId.text.toString().isNotEmpty() &&
                binding.etProductBrand.text.toString().isNotEmpty() &&
                binding.etProductName.text.toString().isNotEmpty() &&
                binding.etProductQuantity.text.toString().isNotEmpty() &&
                binding.etProductTarget.text.toString().isNotEmpty() &&
                binding.etBuyingPrice.text.toString().isNotEmpty() &&
                binding.etSellingPrice.text.toString().isNotEmpty()
            ) {
                binding.progress.visibility = View.VISIBLE
                val productRepo = ProductsRepo(requireActivity().applicationContext)
                val id = binding.etProductId.text.toString()
                val brand = binding.etProductBrand.text.toString()
                val name = binding.etProductName.text.toString()
                val quantity = binding.etProductQuantity.text.toString()
                val target = binding.etProductTarget.text.toString()
                val buyingPrice = binding.etBuyingPrice.text.toString()
                val sellingPrice = binding.etSellingPrice.text.toString()

                val token = requireActivity().getSharedPreferences(
                    "isLogin",
                    AppCompatActivity.MODE_PRIVATE
                )
                    .getString("token", null)

                token?.let {
                    productRepo.addProductsWithDetails(
                        pId = id,
                        pBrand = brand,
                        pName = name,
                        pQuantity = quantity,
                        pTarget = target,
                        pBuyingPrice = buyingPrice,
                        pSellingPrice = sellingPrice,
                        token = token,
                        listener = object : ProductsRepo.OnProductAddResponse {
                            override fun onSuccess(product: Product?) {
                                product?.let {
                                    binding.progress.visibility = View.INVISIBLE
                                    Toast.makeText(
                                        requireActivity().applicationContext,
                                        "${product.productName} has been added to your inventory!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.etProductId.text = null
                                    binding.etProductBrand.text = null
                                    binding.etProductName.text = null
                                    binding.etProductQuantity.text = null
                                    binding.etProductTarget.text = null
                                    binding.etBuyingPrice.text = null
                                    binding.etSellingPrice.text = null

                                    binding.etProductId.requestFocus()

                                    api.sendNotification(
                                        PushNotification(
                                            NotificationData(
                                                "Product added to inventory!",
                                                "${product.productName} was added to your inventory successfully on ${date.time}"
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
                                                Log.d("ADD_NOTIFICATION", "failed: ${it}")
                                            }

                                        }

                                    })
                                }
                            }

                            override fun onFail(message: String?, responseCode: String?) {
                                message?.let {
                                    Toast.makeText(
                                        requireActivity().applicationContext,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        }
                    )
                }

            } else {
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Some fields are seems to be missing, please fill it out!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return binding.root
    }

}