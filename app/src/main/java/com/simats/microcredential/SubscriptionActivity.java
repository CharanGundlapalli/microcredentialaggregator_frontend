package com.simats.microcredential;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private static final String TAG = "SubscriptionActivity";
    private static final String PREFS_NAME = "MicroCredentialPrefs";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String SUBSCRIPTION_SKU = "microcredential_premium_subscription";
    private static final String TEST_SUBSCRIPTION_SKU = "android.test.purchased";

    private TextView btnSkip;
    private Button btnSubscribe;
    private BillingClient billingClient;
    private ProductDetails productDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        addDebugInformation();
        initializeViews();
        setupBillingClient();
        setupClickListeners();
    }

    private void addDebugInformation() {
        Log.d(TAG, "=== DEBUG INFORMATION ===");
        Log.d(TAG, "Package name: " + getPackageName());

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Log.d(TAG, "Version code: " + packageInfo.versionCode);
            Log.d(TAG, "Version name: " + packageInfo.versionName);
        } catch (Exception e) {
            Log.w(TAG, "Unable to get package info: " + e.getMessage());
        }

        Log.d(TAG, "Product ID: " + SUBSCRIPTION_SKU);
        Log.d(TAG, "Test Product ID: " + TEST_SUBSCRIPTION_SKU);
        Log.d(TAG, "=========================");
    }

    private void initializeViews() {
        btnSkip = findViewById(R.id.btnSkipForNow);
        btnSubscribe = findViewById(R.id.btnSubscribe);
    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing setup finished successfully");
                    querySubscriptionDetails();
                } else {
                    Log.e(TAG, "Billing setup failed: " + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d(TAG, "Billing service disconnected");
            }
        });
    }

    private void querySubscriptionDetails() {
        querySpecificProduct(SUBSCRIPTION_SKU, BillingClient.ProductType.SUBS, success -> {
            if (!success) {
                Log.w(TAG, "Real subscription product not found, trying test products...");
                querySpecificProduct(TEST_SUBSCRIPTION_SKU, BillingClient.ProductType.INAPP, testSuccess -> {
                    if (!testSuccess) {
                        Log.e(TAG, "Both real and test products failed");
                        showNoProductsAvailable();
                    }
                });
            }
        });
    }

    private void querySpecificProduct(String productId, String productType, ProductQueryCallback callback) {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if (!productDetailsList.isEmpty()) {
                    productDetails = productDetailsList.get(0);
                    Log.d(TAG, "Product details retrieved successfully for: " + productId);

                    if (productType.equals(BillingClient.ProductType.SUBS)) {
                        List<ProductDetails.SubscriptionOfferDetails> offers = productDetails
                                .getSubscriptionOfferDetails();
                        if (offers != null) {
                            Log.d(TAG, "Available subscription offers: " + offers.size());
                            for (int i = 0; i < offers.size(); i++) {
                                Log.d(TAG, "Offer " + i + ": basePlanId=" + offers.get(i).getBasePlanId());
                            }
                        }
                    }
                    callback.onResult(true);
                } else {
                    Log.e(TAG, "No product details found for: " + productId);
                    callback.onResult(false);
                }
            } else {
                Log.e(TAG, "Failed to query product details for " + productId + ": " + billingResult.getDebugMessage());
                callback.onResult(false);
            }
        });
    }

    private void showNoProductsAvailable() {
        runOnUiThread(() -> Toast
                .makeText(SubscriptionActivity.this,
                        "No subscription products available. Check your setup in Play Console.", Toast.LENGTH_LONG)
                .show());
    }

    private void setupClickListeners() {
        btnSkip.setOnClickListener(v -> navigateNext());
        btnSubscribe.setOnClickListener(v -> launchSubscriptionFlow());
    }

    private void launchSubscriptionFlow() {
        if (!billingClient.isReady()) {
            Log.e(TAG, "Billing client is not ready");
            Toast.makeText(this, "Billing service not ready. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productDetails != null) {
            List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();

            if (productDetails.getProductType().equals(BillingClient.ProductType.SUBS)) {
                List<ProductDetails.SubscriptionOfferDetails> subscriptionOfferDetails = productDetails
                        .getSubscriptionOfferDetails();

                if (subscriptionOfferDetails == null || subscriptionOfferDetails.isEmpty()) {
                    Log.e(TAG, "No subscription offers available");
                    Toast.makeText(this, "No subscription offers available", Toast.LENGTH_SHORT).show();
                    return;
                }

                ProductDetails.SubscriptionOfferDetails selectedOffer = subscriptionOfferDetails.get(0);
                Log.d(TAG, "Using subscription offer: basePlanId=" + selectedOffer.getBasePlanId());

                productDetailsParamsList.add(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(selectedOffer.getOfferToken())
                                .build());
            } else {
                Log.d(TAG, "Using in-app product: " + productDetails.getProductId());
                productDetailsParamsList.add(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build());
            }

            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();

            BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Billing flow launched successfully");
            } else {
                Log.e(TAG, "Failed to launch billing flow: " + billingResult.getDebugMessage());
                Toast.makeText(this, "Failed to start subscription process: " + billingResult.getDebugMessage(),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e(TAG, "No product details available");
            Toast.makeText(this, "Subscription not available. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        Log.d(TAG, "onPurchasesUpdated called - Response Code: " + billingResult.getResponseCode());
        Log.d(TAG, "Debug Message: " + billingResult.getDebugMessage());

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "User canceled the purchase");
            Toast.makeText(this, "Purchase canceled", Toast.LENGTH_SHORT).show();
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.d(TAG, "Item already owned");
            Toast.makeText(this, "You already have an active subscription", Toast.LENGTH_SHORT).show();
            navigateNext();
        } else {
            Log.e(TAG, "Purchase failed with code: " + billingResult.getResponseCode());
            Toast.makeText(this, "Purchase failed: " + getResponseCodeMessage(billingResult.getResponseCode()),
                    Toast.LENGTH_LONG).show();
        }
    }

    private String getResponseCodeMessage(int responseCode) {
        switch (responseCode) {
            case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                return "Service timeout";
            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                return "Feature not supported";
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                return "Service disconnected";
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                return "Billing unavailable";
            case BillingClient.BillingResponseCode.NETWORK_ERROR:
                return "Network error";
            default:
                return "Unknown error (Code: " + responseCode + ")";
        }
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Purchase acknowledged successfully");
                        runOnUiThread(this::onSubscriptionSuccess);
                    } else {
                        Log.e(TAG, "Failed to acknowledge purchase: " + billingResult.getDebugMessage());
                    }
                });
            } else {
                onSubscriptionSuccess();
            }
        }
    }

    private void onSubscriptionSuccess() {
        Toast.makeText(this, "Subscription successful! Welcome to Premium!", Toast.LENGTH_LONG).show();

        SharedPreferences sharedPref = getSharedPreferences("subscription_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("is_premium_user", true);
        editor.putLong("subscription_time", System.currentTimeMillis());
        editor.apply();

        navigateNext();
    }

    private void navigateNext() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean(IS_FIRST_TIME_LAUNCH, true);

        if (isFirstTime) {
            startActivity(new Intent(SubscriptionActivity.this, OnboardingActivity.class));
        } else {
            startActivity(new Intent(SubscriptionActivity.this, LoginActivity.class));
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }

    interface ProductQueryCallback {
        void onResult(boolean success);
    }
}
