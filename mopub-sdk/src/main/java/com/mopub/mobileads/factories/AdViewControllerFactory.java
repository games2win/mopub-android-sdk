/*
 * Copyright (c) 2010-2013, MoPub Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 *  Neither the name of 'MoPub Inc.' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.mopub.mobileads.factories;

import android.content.Context;
import android.util.Log;
import com.mopub.mobileads.AdViewController;
import com.mopub.mobileads.MoPubView;

import java.lang.reflect.Constructor;

import static com.mopub.mobileads.util.VersionCode.ECLAIR_MR1;
import static com.mopub.mobileads.util.VersionCode.currentApiLevel;

public class AdViewControllerFactory {
    protected static AdViewControllerFactory instance = new AdViewControllerFactory();

    @Deprecated // for testing
    public static void setInstance(AdViewControllerFactory factory) {
        instance = factory;
    }

    public static AdViewController create(Context context, MoPubView moPubView) {
        return instance.internalCreate(context, moPubView);
    }

    protected AdViewController internalCreate(Context context, MoPubView moPubView) {
        if (currentApiLevel().isBelow(ECLAIR_MR1)) {
            return new AdViewController(context, moPubView);
        }

        Class<?> HTML5AdViewClass = null;
        try {
            HTML5AdViewClass = Class.forName("com.mopub.mobileads.HTML5AdView");
        } catch (ClassNotFoundException e) {
            return new AdViewController(context, moPubView);
        }

        try {
            Constructor<?> constructor = HTML5AdViewClass.getConstructor(Context.class, MoPubView.class);
            return (AdViewController) constructor.newInstance(context, moPubView);
        } catch (Exception e) {
            Log.e("MoPub", "Could not load HTML5AdView.");
        }

        return new AdViewController(context, moPubView);
    }
}
