package com.dondika.juraganbatik.ui.user.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetailCatalogViewModel : ViewModel() {

    var stock = 1

    private var _totalStock = MutableLiveData<Int>()
    val totalStock: LiveData<Int> = _totalStock

    private var _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> = _totalPrice

    private var _stateButtonPlus = MutableLiveData<Boolean>()
    val stateButtonPlus: LiveData<Boolean> = _stateButtonPlus

    private var _stateButtonMinus = MutableLiveData<Boolean>()
    val stateButtonMinus: LiveData<Boolean> = _stateButtonMinus




    fun plus(productPrice: String){
        /*if (totalStock >= 4){
            _stateButtonPlus.value = false
        }*/
        stock++
        _totalStock.value = stock
        _totalPrice.value = stock * productPrice.toInt()

        //Log.e("TAG", totalStock.toString() )
    }

    fun minus(productPrice: String){
        stock--
        _totalStock.value = stock
        _totalPrice.value = stock * productPrice.toInt()
        /*if (totalStock == 0){
            _stateButtonMinus.value = false
        }else if (totalStock == 1) {
            _stateButtonMinus.value = true
        }*/
    }


}