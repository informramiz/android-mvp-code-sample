package com.hypercubesoft.boatdisplay.signin;

import android.support.annotation.NonNull;
import android.util.Log;

import com.hypercubesoft.boatdisplay.api.AuthenticationEngine;
import com.hypercubesoft.boatdisplay.dependencyinjection.components.AppComponent;
import com.hypercubesoft.boatdisplay.entity.SignInResponse;
import com.hypercubesoft.boatdisplay.entity.Squad;
import com.hypercubesoft.boatdisplay.util.CommonUtils;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by ramiz on 9/24/16.
 * This class is presenter for SignInView (Activity). This handles event handling and rendering
 * logic for SignInView
 */

public class SignInPresenter implements SignInContract.UserActionListener {

    public static final String TAG = SignInPresenter.class.getSimpleName();
    private SignInContract.View mSignInView;
    /**
     * Inject here means that this field will be created and injected automatically
     * by dagger
     */
    @Inject
    AuthenticationEngine mAuthenticationEngine;

    public SignInPresenter(SignInContract.View signInView) {
        mSignInView = signInView;
        mSignInView.setUserActionListener(this);
        //calling this tells dagger that this class needs injection for some objects
        mSignInView.provideAppComponent().inject(this);
    }

    /**
     * This method will be called by class implementing SignInContract.View
     * for user to sign in. It will initiate the API call for verifying user credentials
     * and upon completion will notify view
     * @param username
     * @param password
     */
    @Override
    public void signIn(String username, String password) {
        if (!validateInput(username, password)) {
            return;
        }

        mSignInView.setProgressIndicator(true);
        mAuthenticationEngine.signIn(username, password, new AuthenticationEngine.AuthenticationEngineCallback<SignInResponse>() {
            @Override
            public void onSuccess(SignInResponse signInResponse) {
                mSignInView.setProgressIndicator(false);
                handleSuccessfulSignIn(signInResponse);
            }

            @Override
            public void onFailure(String errorMsg) {
                mSignInView.setProgressIndicator(false);
                mSignInView.showSignInFailureMsg(errorMsg);
            }
        });
    }

    private void handleSuccessfulSignIn(SignInResponse signInResponse) {
        if (signInResponse.result.equals("success")) {
            mSignInView.displaySquadsSelectionDialog(signInResponse.squads);
        } else {
            mSignInView.showSignInFailureMsg(signInResponse.result);
        }
    }

    /**
     * This method is called by View when a squad is selected. This in return
     * informs the view to goto to main activity
     * @param squad selected squad
     */
    @Override
    public void squadSelected(Squad squad) {
        mSignInView.gotoMainActivity(squad);
    }

    /**
     * This method will be called by class implementing SignInContract.View
     * to indicate that user is not sign up. This method will in return notify view
     * to take user to sign up screen
     */
    @Override
    public void signUp() {

    }

    /**
     * This method will be called by class implementing SignInContract.View
     * to indicate that user has forgotten password. This method will in return notify view
     * to take user to forgot password screen
     */
    @Override
    public void forgotPassword() {

    }

    /**
     * this method given Squad objects as input extracts squad names
     * @param squads list of squads to return name of
     * @return list of names extract from squads list
     */
    private ArrayList<String> extractSquadNames(ArrayList<Squad> squads) {
        ArrayList<String> squadNames = new ArrayList<>();
        for (Squad squad : squads) {
            squadNames.add(squad.squadName);
        }

        return squadNames;
    }

    private boolean validateInput(@NonNull String email, @NonNull String password) {
        if (!CommonUtils.isEmailIdValid(email)) {
            mSignInView.showInvalidEmailMsg();
            return false;
        } else if (!CommonUtils.isPasswordValid(password)) {
            mSignInView.showInvalidPassword();
            return false;
        }

        return true;
    }
}
