package com.dondika.juraganbatik.ui.user.detail

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.dondika.juraganbatik.R
import com.dondika.juraganbatik.data.local.PreferenceManager
import com.dondika.juraganbatik.data.model.ProductsResponse
import com.dondika.juraganbatik.data.model.Transaction
import com.dondika.juraganbatik.data.model.TransactionResponse
import com.dondika.juraganbatik.data.remote.ApiConfig
import com.dondika.juraganbatik.databinding.ActivityDetailCatalogBinding
import com.dondika.juraganbatik.utility.Utils
import com.dondika.juraganbatik.utility.constant.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.snap.Gopay
import com.midtrans.sdk.corekit.models.snap.Shopeepay
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import kotlinx.android.synthetic.main.dialog_payment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailCatalogActivity : AppCompatActivity(), TransactionFinishedCallback {

    private lateinit var binding: ActivityDetailCatalogBinding
    private lateinit var productData: ProductsResponse

    private lateinit var totalPriceObserve: String
    private lateinit var totalStockObserver: String

    private val viewModel: DetailCatalogViewModel by viewModels()

    private val db by lazy { Firebase.firestore }
    private val pref by lazy { PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setData()
        setListener()
        initMidtransSdk()
    }


    private fun setData() {
        productData = intent.getParcelableExtra(EXTRA_PRODUCT)!!
        binding.apply {
            tvBatikName.text = productData.batikName
            tvBatikPrice.text = Utils.amountFormat(productData.batikPrice.toInt())
            tvBatikSeller.text = productData.sellerName
            Glide.with(this@DetailCatalogActivity)
                .load(productData.batikImg)
                .into(imgBatik)
        }
    }


    private fun setListener() {
        binding.buyButton.setOnClickListener {
            setupDialog()
        }
    }


    private fun setupDialog(){
        val dialogBinding = layoutInflater.inflate(R.layout.dialog_payment, null)
        val myDialog = Dialog(this@DetailCatalogActivity)
        myDialog.setContentView(dialogBinding)
        myDialog.setCancelable(true)
        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()

        val price = productData.batikPrice
        val tvTotalPrice = dialogBinding.totalPrice
        val tvTotalStock = dialogBinding.totalStock
        val buttonMinus = dialogBinding.minusButton
        val buttonPlus = dialogBinding.plusButton
        val buttonPay = dialogBinding.button_pay

        buttonMinus.setOnClickListener {
            viewModel.minus(price)
            tvTotalStock.text = viewModel.stock.toString()
        }

        buttonPlus.setOnClickListener {
            viewModel.plus(price)
            tvTotalStock.text = viewModel.stock.toString()
        }

        //observe stock
        totalStockObserver = viewModel.stock.toString()
        viewModel.totalStock.observe(this){
            totalStockObserver = it.toString()
        }

        //observer price
        tvTotalPrice.text = Utils.amountFormat(price.toInt())
        totalPriceObserve = price
        viewModel.totalPrice.observe(this){
            tvTotalPrice.text = Utils.amountFormat(it.toInt())
            totalPriceObserve = it.toString()
        }

        //payment to midtrans
        buttonPay.setOnClickListener {
            MidtransSDK.getInstance().apply {
                transactionRequest = initTransactionRequest(totalPriceObserve.toDouble())
                startPaymentUiFlow(this@DetailCatalogActivity)
            }
        }

    }


    private fun updateTransaction(){
        val transactionId = pref.getString(Utils.PREF_TRANSACTION_ID)
        if (transactionId?.isEmpty() == false){
            val client = ApiConfig.getApiService().getTransaction(transactionId)
            client.enqueue(object: Callback<TransactionResponse>{
                override fun onResponse(
                    call: Call<TransactionResponse>,
                    response: Response<TransactionResponse>
                ) {
                    if (response.isSuccessful){
                        val responseBody = response.body()!!
                        val transactionData = Transaction(
                            transactionId = transactionId,
                            orderId = responseBody.orderId,
                            batikName = productData.batikName,
                            batikPrice = responseBody.grossAmount,
                            amount = totalStockObserver,
                            buyer = pref.getString(Utils.PREF_EMAIL)!!,
                            sellerEmail = productData.sellerEmail,
                            sellerName = productData.sellerName,
                            status = responseBody.transactionStatus,
                            transactionTime = responseBody.transactionTime
                        )
                        db.collection(Firestore.TRANSACTIONS).add(transactionData)
                    }
                }
                override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }



    private fun initMidtransSdk() {
        val clientKey: String = Midtrans.MERCHANT_CLIENT_KEY
        val baseUrl: String = Midtrans.MERCHANT_BASE_CHECKOUT_URL

        val sdkUIFlowBuilder: SdkUIFlowBuilder = SdkUIFlowBuilder.init()
            .setClientKey(clientKey)
            .setContext(this)
            .setTransactionFinishedCallback(this)
            .setMerchantBaseUrl(baseUrl)
            .setUIkitCustomSetting(uiKitCustomSetting())
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
        sdkUIFlowBuilder.buildSDK()
    }
    private fun uiKitCustomSetting(): UIKitCustomSetting {
        val uiKitCustomSetting = UIKitCustomSetting()
        uiKitCustomSetting.isSkipCustomerDetailsPages = true
        uiKitCustomSetting.isShowPaymentStatus = true
        return uiKitCustomSetting
    }

    private fun initCustomerDetails(): CustomerDetails {
        val customerDetails = CustomerDetails()
        customerDetails.phone = pref.getString(Utils.PREF_PHONE_NUM)
        customerDetails.firstName = pref.getString(Utils.PREF_NAME)
        customerDetails.email = pref.getString(Utils.PREF_EMAIL)
        customerDetails.customerIdentifier = pref.getString(Utils.PREF_EMAIL)
        return customerDetails
    }

    private fun initTransactionRequest(payment: Double): TransactionRequest {
        val batikName = productData.batikName.lowercase().replace(" ","")
        val orderId = batikName + System.currentTimeMillis().toString()
        val transactionRequest = TransactionRequest(orderId, payment)
        transactionRequest.customerDetails = initCustomerDetails()
        transactionRequest.gopay = Gopay("mysamplesdk:://midtrans")
        transactionRequest.shopeepay = Shopeepay("mysamplesdk:://midtrans")
        return transactionRequest
    }

    override fun onTransactionFinished(result: TransactionResult) {
        val transactionId = result.response.transactionId
        pref.putString(Utils.PREF_TRANSACTION_ID, transactionId)
        if (result.response != null){
            if (result.response.transactionStatus == "settlement"){
                //Toast.makeText(this, "Terima kasih atas donasinya", Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(this, "Selesaikan pembayaran anda!", Toast.LENGTH_SHORT).show()
            }
            /*when(result.status){
                TransactionResult.STATUS_SUCCESS -> Toast.makeText(this, "Transaction Finished. ID: " + result.response.transactionId, Toast.LENGTH_LONG).show()
                TransactionResult.STATUS_PENDING-> Toast.makeText(this, "Transaction Pending. ID: " + result.response.transactionId, Toast.LENGTH_LONG).show()
                TransactionResult.STATUS_FAILED -> Toast.makeText(this, "Transaction Failed. ID: " + result.response.transactionId.toString() + result.response.statusMessage, Toast.LENGTH_LONG).show()
            }
            result.response.validationMessages*/
        } else if (result.isTransactionCanceled) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()
        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, true)){
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Transaction Finished with Failure.", Toast.LENGTH_SHORT).show()
            }
        }
        updateTransaction()
        //worstScenario()
        //updateAmountDonation()
        //getAmountTransactionDonation()
    }



    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }
}