/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.car.window;

import static android.view.WindowInsets.Type.statusBars;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowInsets;

/**
 * Owns a {@link View} that is present in SystemUIOverlayWindow.
 */
public class OverlayViewController {
    private final int mStubId;
    private final OverlayViewGlobalStateController mOverlayViewGlobalStateController;

    private View mLayout;

    public OverlayViewController(int stubId,
            OverlayViewGlobalStateController overlayViewGlobalStateController) {
        mLayout = null;
        mStubId = stubId;
        mOverlayViewGlobalStateController = overlayViewGlobalStateController;
    }

    /**
     * Shows content of {@link OverlayViewController}.
     *
     * Should be used to show view externally and in particular by {@link OverlayViewMediator}.
     */
    public final void start() {
        mOverlayViewGlobalStateController.showView(/* viewController= */ this, this::show);
    }

    /**
     * Hides content of {@link OverlayViewController}.
     *
     * Should be used to hide view externally and in particular by {@link OverlayViewMediator}.
     */
    public final void stop() {
        mOverlayViewGlobalStateController.hideView(/* viewController= */ this, this::hide);
    }

    /**
     * Inflate layout owned by controller.
     */
    public final void inflate(ViewGroup baseLayout) {
        ViewStub viewStub = baseLayout.findViewById(mStubId);
        mLayout = viewStub.inflate();
        onFinishInflate();
    }

    /**
     * Called once inflate finishes.
     */
    protected void onFinishInflate() {
        // no-op
    }

    /**
     * Returns {@code true} if layout owned by controller has been inflated.
     */
    public final boolean isInflated() {
        return mLayout != null;
    }

    private void show() {
        if (mLayout == null) {
            // layout must be inflated before show() is called.
            return;
        }
        showInternal();
    }

    /**
     * Subclasses should override this method to implement reveal animations and implement logic
     * specific to when the layout owned by the controller is shown.
     *
     * Should only be overridden by Superclass but not called by any {@link OverlayViewMediator}.
     */
    protected void showInternal() {
        mLayout.setVisibility(View.VISIBLE);
    }

    private void hide() {
        if (mLayout == null) {
            // layout must be inflated before hide() is called.
            return;
        }
        hideInternal();
    }

    /**
     * Subclasses should override this method to implement conceal animations and implement logic
     * specific to when the layout owned by the controller is hidden.
     *
     * Should only be overridden by Superclass but not called by any {@link OverlayViewMediator}.
     */
    protected void hideInternal() {
        mLayout.setVisibility(View.GONE);
    }

    /**
     * Provides access to layout owned by controller.
     */
    protected final View getLayout() {
        return mLayout;
    }

    /** Returns the {@link OverlayViewGlobalStateController}. */
    protected final OverlayViewGlobalStateController getOverlayViewGlobalStateController() {
        return mOverlayViewGlobalStateController;
    }

    /**
     * Returns {@code true} if heads up notifications should be displayed over this view.
     */
    protected boolean shouldShowHUN() {
        return true;
    }

    /**
     * Returns {@code true} if navigation bar insets should be displayed over this view. Has no
     * effect if {@link #shouldFocusWindow} returns {@code false}.
     */
    protected boolean shouldShowNavigationBarInsets() {
        return false;
    }

    /**
     * Returns {@code true} if status bar insets should be displayed over this view. Has no
     * effect if {@link #shouldFocusWindow} returns {@code false}.
     */
    protected boolean shouldShowStatusBarInsets() {
        return false;
    }

    /**
     * Returns {@code true} if this view should be hidden during the occluded state.
     */
    protected boolean shouldShowWhenOccluded() {
        return false;
    }

    /**
     * Returns {@code true} if the window should be focued when this view is visible. Note that
     * returning {@code false} here means that {@link #shouldShowStatusBarInsets} and
     * {@link #shouldShowNavigationBarInsets} will have no effect.
     */
    protected boolean shouldFocusWindow() {
        return true;
    }

    /**
     * Returns the insets types to fit to the sysui overlay window when this
     * {@link OverlayViewController} is in the foreground.
     */
    @WindowInsets.Type.InsetsType
    protected int getInsetTypesToFit() {
        return statusBars();
    }
}
