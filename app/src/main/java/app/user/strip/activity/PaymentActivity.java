package app.user.strip.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.taxiapp.user.R;
import app.user.activity.Feedback;
import app.user.activity.Home;
import app.user.global.Global;
import app.user.global.NetworkUtil;
import app.user.strip.PaymentForm;
import app.user.strip.dialog.ErrorDialogFragment;
import app.user.strip.dialog.ProgressDialogFragment;
import app.user.volleyapi.OnApihit;
import app.user.volleyapi.VolleyBase;


public class PaymentActivity extends FragmentActivity implements OnApihit {

    String CardNumber, Cvc, Currency;
    Integer ExpMonth, ExpYear;

    Charge createdCharge;
    SharedPreferences sharedPreferences;
    String token_id, dilverydate, location_name = "", street = "", zipcode = "", city = "", state = "", neighborhood = "", payment_type, latitude, longitude;
    /*
     * Change this to your publishable key.
     *
     * You can get your key here: https://manage.stripe.com/account/apikeys
     */
    public static final String PUBLISHABLE_KEY = "pk_test_XertrXjSyuuHWWZDI1REq8cB";

    private ProgressDialogFragment progressFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        sharedPreferences = getSharedPreferences(Global.pref_name, Context.MODE_PRIVATE);
        progressFragment = ProgressDialogFragment.newInstance(R.string.progressMessage);

        Intent intent = getIntent();
        if (getIntent().getExtras() != null) {
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
        Currency = "inr";
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
                            Log.e("token", "t  " + token.getId());
                            // payment(token);
                            //getTokenList().addToList(token);
                            finishProgress();

                            Map<String, String> params = new HashMap<>();

                            params.put("user_id", sharedPreferences.getString("user_id", ""));
                            params.put("driver_id", sharedPreferences.getString("driver_id", ""));
                            params.put("request_id", sharedPreferences.getString("request_id", ""));
                            params.put("stripe_token", token.getId());

                            Log.e("Transaction params", " print  " + params);
                            if (NetworkUtil.getConnectivityStatus(PaymentActivity.this)) {

                                new VolleyBase(PaymentActivity.this).main(params, "Transaction", 1);


                            } else {
                                NetworkUtil.openAlert(PaymentActivity.this);
                            }
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
            handleError("The CVV code that you entered is invalid");
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


        com.stripe.Stripe.apiKey = "sk_test_UKJBC7pnuUJ6ZssmV86D4rpQ";
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(PaymentActivity.this, Home.class);
        i.putExtra("i", "");
        startActivity(i);
        finish();
    }

    @Override
    public void success(String Response, int index) {
        Log.e("RESPONSE", "TRANSACTION" + Response);
        //{"status":"1","message":"Transaction completed successfylly"}

        try {
            JSONObject jsonObject = new JSONObject(Response);
            String status = jsonObject.getString("status");
            String message = jsonObject.getString("message");


            if (status.equalsIgnoreCase("1")) {
                String result = jsonObject.getString("result");
                Log.e("TRANSACTION", "1");
                JSONObject j=new JSONObject(result);
                Log.e("TRANSACTION", "2");
                String request_id = j.getString("request_id");
                String source_location = j.getString("source_location");
                String dest_location = j.getString("dest_location");
                String created = j.getString("created");
                Log.e("TRANSACTION", "3");
                Intent i = new Intent(PaymentActivity.this, Feedback.class);
                i.putExtra("date", created);
                i.putExtra("start", source_location);
                i.putExtra("end", dest_location);
                i.putExtra("request_id", request_id);
                startActivity(i);
                finish();
                Log.e("TRANSACTION", "4");
            }
            Global.toast(PaymentActivity.this, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("request_id", "");
        editor.putString("driver_id", "");
        editor.commit();
    }

    @Override
    public void error(VolleyError error, int index) {

    }
}
