package com.Saiful.SampleApp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Saiful.SampleApp.databinding.ActivityMainBinding;
import com.sslcommerz.library.payment.model.datafield.MandatoryFieldModel;
import com.sslcommerz.library.payment.model.dataset.TransactionInfo;
import com.sslcommerz.library.payment.model.util.CurrencyType;
import com.sslcommerz.library.payment.model.util.ErrorKeys;
import com.sslcommerz.library.payment.model.util.SdkCategory;
import com.sslcommerz.library.payment.model.util.SdkType;
import com.sslcommerz.library.payment.viewmodel.listener.OnPaymentResultListener;
import com.sslcommerz.library.payment.viewmodel.management.PayUsingSSLCommerz;

import java.util.UUID;

public class PaymentInputActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        getSupportActionBar().setTitle("Payment");

        activityMainBinding.pay.setOnClickListener(v -> {

            String amount = activityMainBinding.Amount.getEditText().getText().toString().trim();

            if(amount.length() > 0) {
                doPay(amount);

            }
            else {
                activityMainBinding.Amount.setError("Please give proper amount");
            }

        });



    }

    private void doPay(String amount) {

        String TransactionNo = TransId();

        MandatoryFieldModel mandatoryFieldModel = new MandatoryFieldModel(
                "abc5ecb99d98cb70",
                "abc5ecb99d98cb70@ssl",
                amount +".00",
                TransactionNo,
                CurrencyType.BDT,
                SdkType.TESTBOX,
                SdkCategory.BANK_LIST);


        PayUsingSSLCommerz.getInstance().setData(getApplicationContext(), mandatoryFieldModel, new OnPaymentResultListener() {
            @Override
            public void transactionSuccess(TransactionInfo transactionInfo) {
                // If payment is success and risk label is 0 get payment details from here
                if (transactionInfo.getRiskLevel().equals("0")) {

                    Log.e("ValidationID", transactionInfo.getValId());
                    /*Toast.makeText(getApplicationContext(),transactionInfo.getValId(),Toast.LENGTH_LONG).show();*/
                    /* After successful transaction send this val id to your server and from
                       your server you can call this api

                     https://sandbox.sslcommerz.com/validator/api/validationserverAPI.php?val_id=yourvalid&store_id=yourstoreid&store_passwd=yourpassword
                     if you call this api from your server side you will get all the details of the transaction.
                     for more details visit:   www.tashfik.me */
                    ;

                }

                // Payment is success but payment is not complete yet. Card on hold now.
                else {
                    Log.e("RiskTitle", "Transaction in risk. Risk Title : " + transactionInfo.getRiskTitle());
                }

                Toast.makeText(PaymentInputActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                ClearText();

            }

            @Override
            public void transactionFail(String s) {
                //Log.e("transactionFail", TransactionNo);
            }


            @SuppressLint("LongLogTag")
            @Override
            public void error(int errorCode) {
                switch (errorCode) {
                    // Your provides information is not valid.
                    case ErrorKeys.USER_INPUT_ERROR:
                        Log.e("USER_INPUT_ERROR", "User Input Error");
                        break;
                    // Internet is not connected.
                    case ErrorKeys.INTERNET_CONNECTION_ERROR:
                        Log.e("INTERNET_CONNECTION_ERROR", "Internet Connection Error");
                        break;
                    // Server is not giving valid data.
                    case ErrorKeys.DATA_PARSING_ERROR:
                        Log.e("DATA_PARSING_ERROR", "Data Parsing Error");
                        break;
                    // User press back button or canceled the transaction.
                    case ErrorKeys.CANCEL_TRANSACTION_ERROR:
                        Log.e("CANCEL_TRANSACTION_ERROR", "User Cancel The Transaction");
                        break;
                    // Server is not responding.
                    case ErrorKeys.SERVER_ERROR:
                        Log.e("SERVER_ERROR", "Server Error");
                        break;
                    // For some reason network is not responding
                    case ErrorKeys.NETWORK_ERROR:
                        Log.e("NETWORK_ERROR", "Network Error");
                        break;
                }
            }
        });

    }

    private void ClearText() {
        activityMainBinding.Amount.getEditText().getText().clear();
        activityMainBinding.Amount.setError(null);
    }

    private String TransId(){

        UUID uuid = UUID.randomUUID();
        return uuid.toString();

    }
}
