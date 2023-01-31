package com.nexusinfinity.electronicsportal.repository

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.nexusinfinity.electronicsportal.model.Product
import com.nexusinfinity.electronicsportal.singletons.VolleySingleton
import org.json.JSONObject

class ProductsRepo(val context: Context) {

    interface OnProductsResponse {
        fun onSuccess(products: ArrayList<Product>?)
        fun onFail(message: String?, responseCode: String?)
    }

    interface OnProductAddResponse {
        fun onSuccess(product: Product?)
        fun onFail(message: String?, responseCode: String?)
    }

    interface OnProductUpdateResponse {
        fun onUpdateSuccess(id: String?, productName: String?)
        fun onUpdateFail(message: String?, responseCode: String?)
    }

    interface OnDeleteResponse {
        fun onDeleteSuccess(msg: String?)
        fun onDeleteFail(msg: String?, response: String?)
    }

    companion object {
        const val PRODUCTS_GET_AND_POST = "${LoginCheck.TEMP_URL}api/products/"

        fun getDelete(id: String) = "${LoginCheck.TEMP_URL}api/products/${id}"
        fun getPut(id: String) = "${LoginCheck.TEMP_URL}api/products/${id}"
    }

    fun addProductsWithDetails(
        pId: String,
        pBrand: String,
        pName: String,
        pQuantity: String,
        pTarget: String,
        pBuyingPrice: String,
        pSellingPrice: String,
        listener: OnProductAddResponse,
        token: String
    ) {
        val productAddingRequest = object : JsonObjectRequest(
            Request.Method.POST,
            PRODUCTS_GET_AND_POST,
            JSONObject(
                "{\n" +
                        "\"ProductId\": \"$pId\",\n" +
                        "\"ProductModel\": \"$pBrand\",\n" +
                        "\"ProductName\": \"$pName\",\n" +
                        "\"ProductQnt\": \"$pQuantity\",\n" +
                        "\"ProductTarget\": \"$pTarget\",\n" +
                        "\"BuyingPrice\": \"$pBuyingPrice\",\n" +
                        "\"SellPrice\": \"$pSellingPrice\"\n" +
                        "}"
            ),
            {
                if (it.getString("Status") == "Done") {
                    val productObject = it.getJSONObject("products")
                    listener.onSuccess(
                        Product(
                            productId = productObject.getString("ProductId"),
                            productModel = productObject.getString("ProductModel"),
                            productName = productObject.getString("ProductName"),
                            productQnt = productObject.getString("ProductQnt"),
                            productTarget = productObject.getString("ProductTarget"),
                            buyingPrice = productObject.getString("BuyingPrice"),
                            sellingPrice = productObject.getString("SellPrice")
                        )
                    )
                } else {
                    listener.onFail(
                        "Product either already exists or fields are missing out!!",
                        "401"
                    )
                }
            },
            {
                listener.onFail(it.localizedMessage, it.networkResponse.statusCode.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["authorization"] = token

                return headers
            }
        }

        VolleySingleton.getInstance(context).addToRequestQueue(productAddingRequest)
    }

    fun getProductsByToken(token: String, response: OnProductsResponse) {
        val productArrayRequest = object : JsonArrayRequest(
            Request.Method.GET,
            PRODUCTS_GET_AND_POST,
            null,
            {
                val productsList = ArrayList<Product>()

                for (i in 0 until it.length()) {
                    val currentObj = it.getJSONObject(i)
                    val product = Product(
                        productId = currentObj.getString("ProductId"),
                        productModel = currentObj.getString("ProductModel"),
                        productName = currentObj.getString("ProductName"),
                        productQnt = currentObj.getString("ProductQnt"),
                        productTarget = currentObj.getString("ProductTarget"),
                        buyingPrice = currentObj.getString("BuyingPrice"),
                        sellingPrice = currentObj.getString("SellPrice")
                    )
                    productsList.add(product)
                }

                response.onSuccess(productsList)
            },
            {
                response.onFail(it.localizedMessage, it.networkResponse.statusCode.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["authorization"] = token

                return headers
            }
        }

        VolleySingleton.getInstance(context).addToRequestQueue(productArrayRequest)
    }

    fun updateProductUsingId(
        id: String,
        pBrand: String,
        pName: String,
        pQuantity: String,
        pTarget: String,
        pBuyingPrice: String,
        pSellingPrice: String,
        listener: OnProductUpdateResponse,
        token: String
    ) {

        val productAddingRequest = object : JsonObjectRequest(
            Request.Method.PUT,
            getPut(id),
            JSONObject(
                "{\n" +
                        "\"ProductId\": \"$id\",\n" +
                        "\"ProductModel\": \"$pBrand\",\n" +
                        "\"ProductName\": \"$pName\",\n" +
                        "\"ProductQnt\": \"$pQuantity\",\n" +
                        "\"ProductTarget\": \"$pTarget\",\n" +
                        "\"BuyingPrice\": \"$pBuyingPrice\",\n" +
                        "\"SellPrice\": \"$pSellingPrice\"\n" +
                        "}"
            ),
            {
                listener.onUpdateSuccess(id, "$id updated successfully!!")
            },
            {
                listener.onUpdateFail(it.localizedMessage, it.networkResponse.statusCode.toString())
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["authorization"] = token

                return headers
            }
        }

    }

    fun getProductUsingId(id: String, token: String,  response: OnProductsResponse) {
        val productArrayRequest = object : JsonArrayRequest(
            Request.Method.GET,
            PRODUCTS_GET_AND_POST,
            null,
            {
                val productsList = ArrayList<Product>()

                for (i in 0 until it.length()) {
                    val currentObj = it.getJSONObject(i)
                    val product = Product(
                        productId = currentObj.getString("ProductId"),
                        productModel = currentObj.getString("ProductModel"),
                        productName = currentObj.getString("ProductName"),
                        productQnt = currentObj.getString("ProductQnt"),
                        productTarget = currentObj.getString("ProductTarget"),
                        buyingPrice = currentObj.getString("BuyingPrice"),
                        sellingPrice = currentObj.getString("SellPrice")
                    )
                    productsList.add(product)
                }

                response.onSuccess(productsList)
            },
            {
                response.onFail(it.localizedMessage, it.networkResponse.statusCode.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["authorization"] = token

                return headers
            }
        }
    }

    fun deleteProductUsingId(id: String, token: String, listener: OnDeleteResponse) {
        val request = object : JsonObjectRequest(
            Request.Method.DELETE, getDelete(id), null,
            {
                listener.onDeleteSuccess(it.toString())
            }, { vlyE ->
                vlyE.networkResponse?.let {
                    listener.onDeleteFail(vlyE.localizedMessage, it.statusCode.toString())
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["authorization"] = token
                return headers
            }
        }

        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }
}