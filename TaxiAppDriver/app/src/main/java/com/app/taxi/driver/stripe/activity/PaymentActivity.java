package com.app.taxi.driver.stripe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.app.taxi.driver.R;
import com.app.taxi.driver.stripe.PaymentForm;
import com.app.taxi.driver.stripe.dialog.ErrorDialogFragment;
import com.app.taxi.driver.stripe.dialog.ProgressDialogFragment;
import com.app.taxi.driver.volleyapi.OnApihit;
import com.app.taxi.driver.volleyapi.VolleyBase;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;




public class PaymentActivity extends FragmentActivity implements OnApihit {

    String CardNumber, Cvc, Currency , id;
    Integer ExpMonth, ExpYear;

    Charge createdCharge;

    String token_id, dilverydate, location_name = "", street = "", zipcode = "", city = "", state = "", neighborhood = "", payment_type, latitude, longitude;
    /*
     * Change this to your publishable key.
     *
     * You can get your key here: https://manage.stripe.com/account/apikeys
     */
    public static final String PUBLISHABLE_KEY ="pk_test_5uutN3fbaxiMIzObbN2iHTZk";
    private ProgressDialogFragment progressFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        progressFragment = ProgressDialogFragment.newInstance(R.string.progressMessage);
        Intent intent = getIntent();
        if (getIntent().getExtras() != null) {
            Log.e("intentttttt" , intent.toString());
            location_name = intent.getStringExtra("location");
            street = intent.getStringExtra("street");
            zipcode = intent.getStringExtra("pcode");
            city = intent.getStringExtra("city");
            neighborhood = intent.getStringExtra("neighborhood");
            state = intent.getStringExtra("state");
            payment_type = "stp";
            latitude = intent.getStringExtra("latitude");
            longitude = intent.getStringExtra("longitude");
        }
    }
    public void saveCreditCard(PaymentForm form) {
        CardNumber = form.getCardNumber();
        ExpMonth = form.getExpMonth();
        ExpYear = form.getExpYear();
        Cvc = form.getCvc();
        Currency = "usd";
        Card card = new Card(
                form.getCardNumber(),
                form.getExpMonth(),
                form.getExpYear(),
                form.getCvc());
        card.setCurrency(Currency);

       boolean validation = card.validateCard();
        if (validation) {
            startProgress();
            new Stripe(PaymentActivity.this).createToken(
                    card,
                    PUBLISHABLE_KEY,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            Log.e("token_id", "t  " + token.getId());
                                Log.e("token" , token.toString());
                            Log.e("bank account" ,token.getCard().toString());
                                Log.e("tt" , token.getCard().getId().toString());
                                String card_id = token.getCard().getId();

                            Map<String, String> params = new HashMap<>();

                            params.put("card_id", card_id);

                                new VolleyBase(PaymentActivity.this).main(params, "GetDeviceToken", 1);

                       /* nextscreen = new Intent(LoginScreen.this, HomeScreen.class);
                        nextscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(nextscreen);*/
                          // payment(token);
                            //getTokenList().addToList(token);
                            finishProgress();
                        }
                        public void onError(Exception error) {
                            handleError(error.getLocalizedMessage());
                            finishProgress();
                        }
                    });
        } else if (!card.validateNumber()) {
            handleError("The card number that you entered is invalid");
        } else if (!card.validateExpiryDate()) {
            handleError("The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
            handleError("The CVC code that you entered is invalid");
        } else {
            handleError("The card details that you entered are invalid");
        }
    }

    private void startProgress() {
        progressFragment.show(getSupportFragmentManager(), "progress");
    }

    private void finishProgress() {
        progressFragment.dismiss();
    }

    private void handleError(String error) {
        DialogFragment fragment = ErrorDialogFragment.newInstance(R.string.validationErrors, error);
        fragment.show(getSupportFragmentManager(), "error");
    }


    public String payment(Token token) {


        com.stripe.Stripe.apiKey = "sk_test_bpPKvsPcncsEGz92yWB7YzXj";
        String res = "";
        Map<String, Object> chargeMap = new HashMap<String, Object>();
        String val = "110,00";
        String array[] = val.split(" ");
        String Final_val[] = array[0].split(",");
        double dollar = Integer.parseInt(Final_val[0] + Final_val[1]) * 0.00066 * 100;
        System.out.println("dollar_value" + dollar);
        int i = (int) Math.round(dollar);
        System.out.println(i);
        chargeMap.put("amount", i);//500=$5.00
        Map<String, Object> cardMap = new HashMap<String, Object>();
        // chargeMap.put("source", token);
        chargeMap.put("description", "Charge for test@example.com");
        cardMap.put("number", token.getId());
        cardMap.put("exp_month", ExpMonth);
        cardMap.put("exp_year", ExpYear);
        cardMap.put("cvc", String.valueOf(Cvc));
        chargeMap.put("card", token.getId());
        chargeMap.put("currency", "USD");

        Log.e("ChargeMap", "" + chargeMap.toString());

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {

            createdCharge = Charge.create(chargeMap);

            Log.e("CHARGED", "" + createdCharge);
            Log.e("CHARGE ID", "" + createdCharge.getId());
            System.out.println("you have been charged " + createdCharge.getAmount() + " of this tranaction id " + createdCharge.getId() + "status" + createdCharge.getStatus());
            Charge c = Charge.retrieve(createdCharge.getId().toString());
            Log.e("RETRIEVE", "" + c);

            Boolean status = createdCharge.getPaid();
            if (status) {
                Log.e("PAYMENT", "STATUS = " + status);
                // new Thread(null, placeordernow, "").start();
            } else {
                res = createdCharge.getFailureMessage();
                Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
            }

        } catch (StripeException e) {
            // Display a very generic error to the user, and maybe send
            // yourself an email
            Log.e("TAG", "e.getCode() ...." + e.getCause());
            Log.e("TAG", "e.getCode() ...." + e.getMessage());
            res = e.getMessage();
        }
        return res;
    }



    @Override
    public void success(String Response, int index) {
        Log.e("RESPONSE", "TRANSACTION" + Response);
        //{"status":"1","message":"Transaction completed successfylly"}


    }

    @Override
    public void error(VolleyError error, int index) {

    }



}
