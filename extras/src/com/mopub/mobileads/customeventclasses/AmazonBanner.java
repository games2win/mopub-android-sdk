package com.mopub.mobileads.customeventclasses;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.*;

import android.widget.RelativeLayout;
import android.widget.FrameLayout;
import android.view.Gravity;
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.mopub.mobileads.AdViewController;

public class AmazonBanner extends CustomEventBanner implements AdListener {
    private CustomEventBannerListener mBannerListener;
    private AdLayout adView;
    private RelativeLayout adLayoutView;
    private static final String LOG_TAG = "AmazonAds"; // Tag used to prefix all log messages
    /*
     * Abstract methods from CustomEventBanner
     */
    @Override
    public void loadBanner(Context context, CustomEventBannerListener bannerListener,
            Map<String, Object> localExtras, Map<String, String> serverExtras) {
        mBannerListener = bannerListener;
        
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            // You may also pass in an Activity Context in the localExtras map and retrieve it here.
        }
        
        if (activity == null) {
            mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
            return;
        }
        // For debugging purposes enable logging, but disable for production builds
        AdRegistration.enableLogging( true);
        // For debugging purposes flag all ad requests as tests, but set to false for production builds
        AdRegistration.enableTesting( MoPubView.TEST_MODE);

        /*
         * You may also pass this String down in the serverExtras Map by specifying Custom Event Data
         * in MoPub's web interface.
         */
        String amazonAppId = serverExtras.get("AMAZON_AD_ID");
        
        Log.i(LOG_TAG, "Loading Amazon Ad: " +amazonAppId);
        
         try {
            AdRegistration.setAppKey( amazonAppId);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception thrown: " + e.toString());
            return;
        }
        
		adView = new AdLayout(activity, AdSize.SIZE_320x50);
	  
	    float scale = activity.getApplicationContext().getResources().getDisplayMetrics().density;
	    adView.setLayoutParams(new FrameLayout.LayoutParams((int) (320 * scale),(int) (50 * scale),Gravity.CENTER));
   
		adView.setListener(this);
		//AdViewController.setShouldHonorServerDimensions(adView);
		adView.loadAd(new AdTargetingOptions());
    }

    @Override
    public void onInvalidate() {
       adView.setListener(null);
    }

    
    /**
     * This event is called after a rich media ads has collapsed from an expanded state.
     */
    @Override
    public void onAdCollapsed(AdLayout view) {
        Log.d(LOG_TAG, "Ad collapsed.");
    }

    /**
     * This event is called if an ad fails to load.
     */
    @Override
    public void onAdFailedToLoad(AdLayout view, AdError error) {
        Log.w(LOG_TAG, "Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
          mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    /**
     * This event is called once an ad loads successfully.
     */
    @Override
    public void onAdLoaded(AdLayout view, AdProperties adProperties) {
        Log.d(LOG_TAG, adProperties.getAdType().toString() + " Ad loaded successfully.");
         if (adView != null) {
            Log.d("MoPub", "Amazon banner ad loaded successfully. Showing ad...");
            mBannerListener.onBannerLoaded(adView);
          //  mBannerListener.setAdContentView(adView);
        } else {
            mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
        }
    }

    /**
     * This event is called after a rich media ad expands.
     */
    @Override
    public void onAdExpanded(AdLayout view) {
        Log.d(LOG_TAG, "Ad expanded.");
          mBannerListener.onBannerClicked();
    }
}
